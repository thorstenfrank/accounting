/*
 *  Copyright 2011, 2014 Thorsten Frank (accounting@tfsw.de).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.tfsw.accounting.service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.db4o.constraints.UniqueFieldValueConstraintViolationException;
import com.db4o.ext.Db4oException;
import com.db4o.osgi.Db4oService;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingContextFactory;
import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.EventIds;
import de.tfsw.accounting.Messages;
import de.tfsw.accounting.io.AccountingXmlImportExport;
import de.tfsw.accounting.io.ExpenseImporter;
import de.tfsw.accounting.io.XmlModelDTO;
import de.tfsw.accounting.model.AbstractBaseEntity;
import de.tfsw.accounting.model.AnnualDepreciation;
import de.tfsw.accounting.model.CVEntry;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.CurriculumVitae;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.model.ExpenseImportParams;
import de.tfsw.accounting.model.ExpenseImportResult;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.IncomeStatement;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoicePosition;
import de.tfsw.accounting.model.InvoiceState;
import de.tfsw.accounting.model.ModelMetaInformation;
import de.tfsw.accounting.model.PaymentTerms;
import de.tfsw.accounting.model.Revenue;
import de.tfsw.accounting.model.User;
import de.tfsw.accounting.model.internal.InvoiceSequencer;
import de.tfsw.accounting.model.internal.ModelMetaInformationImpl;
import de.tfsw.accounting.service.BusinessLogger.Operation;
import de.tfsw.accounting.util.FormatUtil;
import de.tfsw.accounting.util.TimeFrame;

/**
 * Implementation of the {@link AccountingService} that uses DB4o for
 * persistence.
 * 
 * @author thorsten frank
 */
public class AccountingServiceImpl implements AccountingService {

	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(AccountingServiceImpl.class);
	
	private boolean initialised;

	private AccountingContext context;
	
	private Persistence persistence;
	
	private EventAdmin eventAdmin;
	
	private ModelMetaInformationImpl modelMetaInformation;
	
	/**
	 * 
	 */
	public AccountingServiceImpl() {
		LOG.info("AccountingServiceImpl created"); //$NON-NLS-1$
		this.initialised = false;
	}
	
	/**
	 * @param db4oService the db4oService to set
	 */
	protected synchronized void bindDb4oService(Db4oService db4oService) {
		if (db4oService != null) {		
			setPersistence(new Db4oWrapper(db4oService));
		}
	}
	
	/**
	 * @param the db4oService
	 */
	protected synchronized void unbindDb4oService(Db4oService db4oService) {
		LOG.info("Db4o service is being unbound");
		if (initialised) {
			persistence.shutDown();
			this.initialised = false;
		} else {
			LOG.warn("Unkonwn Db4o service instance, will ignore unbind");
		}
	}
	
	protected synchronized void bindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
		LOG.info("Binding EventAdmin");
		final Map<String, Object> properties = new HashMap<>();
		properties.put(EVENT_PROPERTY_INIT_SERVICE, this);
		eventAdmin.postEvent(new Event(EVENT_TOPIC_SERVICE_INIT, properties));
	}
	
	/** Visible for testing purposes. */
	protected void setPersistence(Persistence persistence) {
		this.persistence = persistence;
		initialised = false;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see de.tfsw.accounting.AccountingService#init(de.tfsw.accounting.AccountingContext)
	 */
	@Override
	public void init(AccountingContext context) {
		if (context == null) {
			LOG.error("Context was [null]!"); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorNoContext);
		}
		if (initialised) {
			LOG.debug("Service is already initialised, nothing to do here."); //$NON-NLS-1$
			return;
		}

		LOG.info("service init"); //$NON-NLS-1$
		this.context = context;

		persistence.init(context.getDbFileName());

		buildModelMetaInfo();
		
		LOG.info("Service is now initialised"); //$NON-NLS-1$
		// set this service to initialised - only after all necessary processing
		// finished successfully
		initialised = true;
		eventAdmin.postEvent(ContextInitialisedEventImpl.create(context));
	}

	/**
     * 
     */
    private void buildModelMetaInfo() {
	    LOG.info("Building model meta information..."); //$NON-NLS-1$
		this.modelMetaInformation = new ModelMetaInformationImpl();
		
		Set<Expense> expenses = persistence.runFindQuery(Expense.class);
		LOG.debug("Traversing all known expenses, total found: " + expenses.size()); //$NON-NLS-1$
		modelMetaInformation.setNumberOfExpenses(expenses.size());
		
		LOG.debug("Looking for oldest expense and all expense categories"); //$NON-NLS-1$
		
		LocalDate oldestExpenseDate = LocalDate.now();
		Set<String> expenseCategories = new TreeSet<String>();
		for (Expense expense : expenses) {
			if (expense.getPaymentDate() != null && oldestExpenseDate.isAfter(expense.getPaymentDate())) {
				oldestExpenseDate = expense.getPaymentDate();
			}
			
			if (expense.getCategory() != null && !expenseCategories.contains(expense.getCategory())) {
				LOG.debug("Adding category to known list: " + expense.getCategory()); //$NON-NLS-1$
				expenseCategories.add(expense.getCategory());
			}
		}

		LOG.debug("Number of categories found: " + expenseCategories.size()); //$NON-NLS-1$
		modelMetaInformation.setExpenseCategories(expenseCategories);
		
		LOG.debug("Oldest known expense is from: " + oldestExpenseDate.toString()); //$NON-NLS-1$
		modelMetaInformation.setOldestExpense(oldestExpenseDate);
		
		Set<Invoice> invoices = persistence.runFindQuery(Invoice.class);
		LOG.debug("Searching for oldest invoice, total found: " + invoices.size()); //$NON-NLS-1$
		modelMetaInformation.setNumberOfInvoices(invoices.size());
		LocalDate oldestInvoiceDate = LocalDate.now();
		for (Invoice invoice : invoices) {
			if (invoice.getInvoiceDate() != null && oldestInvoiceDate.isAfter(invoice.getInvoiceDate())) {
				oldestInvoiceDate = invoice.getInvoiceDate();
			}
		}
		LOG.debug("Oldest invoice is from: " + oldestInvoiceDate.toString()); //$NON-NLS-1$
		modelMetaInformation.setOldestInvoice(oldestInvoiceDate);
    }

	/**
	 * 
	 */
	@Override
	public User saveCurrentUser(User user) {
		LOG.debug("saveCurrentUser"); //$NON-NLS-1$
		doStoreEntity(user);
		return user;
	}

	/**
	 * 
	 */
	@Override
	public User getCurrentUser() {
		LOG.debug("getCurrentUser"); //$NON-NLS-1$
		User user = null;
		Set<User> userList = persistence.runFindQuery(new FindCurrentUserPredicate(context));
		if (userList != null && userList.size() == 1) {
			user = userList.iterator().next();
		} else {
			LOG.warn("No user found for context name: {}", context.getUserName()); //$NON-NLS-1$
		}

		return user;
	}

	/**
	 * 
	 */
	@Override
	public Set<String> getClientNames() {
		return persistence.runFindQuery(Client.class).stream().map(Client::getName).collect(Collectors.toSet());
	}
	
	@Override
	public Client getClient(String name) {
		Set<Client> clients = persistence.runFindQuery(new FindClientByNamePredicate(name));
		
		if (clients != null && clients.size() == 1) {
			return clients.iterator().next();
		}
		
		return null;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.AccountingService#getClients()
	 */
	@Override
	public Set<Client> getClients() {
		return new HashSet<Client>(persistence.runFindQuery(Client.class));
	}

	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.AccountingService#saveClient(de.tfsw.accounting.model.Client)
	 */
	@Override
	public Client saveClient(Client client) {
		
		try {
			doStoreEntity(client);
		} catch (UniqueFieldValueConstraintViolationException e) {
			if (e.getMessage().endsWith(Client.FIELD_NAME)) {
				LOG.error("A client with this name already exists: " + client.getName()); //$NON-NLS-1$
				throw new AccountingException(
						Messages.bind(Messages.AccountingService_errorClientNameExists, client.getName()));
			} else if (e.getMessage().endsWith(Client.FIELD_CLIENT_NUMBER)) {
				LOG.error("A client with this number already exists: " + client.getClientNumber()); //$NON-NLS-1$
				throw new AccountingException(
						Messages.bind(Messages.AccountingService_errorClientNumberExists, client.getClientNumber()));
			}
			LOG.error(String.format("Client [%s], number: [%s] already exists!", client.getName(), client.getClientNumber()), e); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorClientExists);
		}
		
		return client;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.AccountingService#deleteClient(de.tfsw.accounting.model.Client)
	 */
	@Override
	public void deleteClient(Client client) {
		// TODO make sure a client can only be deleted when there are no invoices left...
		LOG.info(String.format("Deleting client [%s]", client.getName()));
		doDeleteEntity(client);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.AccountingService#getNextInvoiceNumber()
	 */
	@Override
	public String getNextInvoiceNumber() {		
		Calendar cal = Calendar.getInstance();
		final int currentYear = cal.get(Calendar.YEAR);
		
		InvoiceSequencer sequencer = getInvoiceSequencer();
		
		if (currentYear != sequencer.getYear()) {
			sequencer.setYear(currentYear);
			sequencer.setCurrentSequenceNumber(1);
		} else {
			sequencer.setCurrentSequenceNumber(sequencer.getCurrentSequenceNumber() + 1);
		}
		
		doStoreEntity(sequencer);
		LOG.info("Invoice sequencer was updated: " + sequencer.toString()); //$NON-NLS-1$
		// FIXME this is hardcoded and should be configurable...
		return String.format("RE%d-%02d", currentYear, sequencer.getCurrentSequenceNumber());
	}
	
	/**
	 * 
	 * @return
	 */
	private InvoiceSequencer getInvoiceSequencer() {			
		Set<InvoiceSequencer> set = persistence.runFindQuery(InvoiceSequencer.class);
		
		InvoiceSequencer sequencer = null;
		
		if (set.size() == 1) {
			sequencer = set.iterator().next();
			LOG.debug("Sequencer found: " + sequencer.toString()); //$NON-NLS-1$
		} else {
			sequencer = new InvoiceSequencer();
			LOG.info("No sequencer found in storage, creating new"); //$NON-NLS-1$
		}
			
		return sequencer;
	}
		
	/**
	 * 
	 */
	private void updateInvoiceSequencer(String invoiceNumber) {
		// FIXME make this flexible
		final int year = new Integer(invoiceNumber.substring(2, 6));
		final int number = new Integer(invoiceNumber.substring(7));
		
		InvoiceSequencer sequencer = getInvoiceSequencer();
		LOG.debug("Current sequencer: " + sequencer.toString()); //$NON-NLS-1$
		
		if (year == sequencer.getYear() && number == sequencer.getCurrentSequenceNumber()) {
			LOG.debug("Nothing to change, sequencer is up to date!"); //$NON-NLS-1$
		} else {
			sequencer.setYear(year);
			sequencer.setCurrentSequenceNumber(number);
			LOG.info("Updating sequencer: "+sequencer.toString()); //$NON-NLS-1$
			doStoreEntity(sequencer);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see AccountingService#createNewInvoice(String, Client)
	 */
	@Override
	public Invoice createNewInvoice(String invoiceNumber, Client client) {
		LOG.debug(String.format("Creating new invoice [%s]", invoiceNumber)); //$NON-NLS-1$
		
		// validate invoice no: not empty, not yet used
		if (invoiceNumber == null || invoiceNumber.isEmpty()) {
			throw new AccountingException(Messages.AccountingService_errorMissingInvoiceNumber);
		}

		// check if client exists
		if (client == null) {
			throw new AccountingException(Messages.AccountingService_errorMissingClient);
		}
		
		// check if an invoice with that number already exists
		validateInvoiceNumberNotYetUsed(invoiceNumber);
		
		// create invoice
		Invoice invoice = new Invoice();
		
		// assign values
		invoice.setNumber(invoiceNumber);
		invoice.setUser(getCurrentUser());
		invoice.setClient(client);
		invoice.setInvoiceDate(LocalDate.now());

		// assign payment terms
		if (client.getDefaultPaymentTerms() != null) {
			LOG.debug("Using default payment terms of client"); //$NON-NLS-1$
			invoice.setPaymentTerms(client.getDefaultPaymentTerms());
		} else {
			LOG.debug("Using global default payment terms"); //$NON-NLS-1$
			invoice.setPaymentTerms(PaymentTerms.getDefault());
		}
		
		return invoice;
	}
	
	/**
	 * 
	 * @param invoiceNumber
	 */
	private void validateInvoiceNumberNotYetUsed(String invoiceNumber) {
		if (getInvoice(invoiceNumber) != null) {
			LOG.error("An invoice with this number already exists: []", invoiceNumber); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorInvoiceNumberExists);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.AccountingService#saveInvoice(de.tfsw.accounting.model.Invoice)
	 */
	@Override
	public Invoice saveInvoice(Invoice invoice) {
		if (invoice == null) {
			LOG.warn("Trying to save Invoice [null]"); //$NON-NLS-1$
			return null;
		}
		LOG.debug("saveInvoice: " + invoice.getNumber()); //$NON-NLS-1$

		if (invoice.getNumber() == null || invoice.getNumber().isEmpty()) {
			LOG.error("Cannot save an invoice without a number!"); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorMissingInvoiceNumber);
		} else if (invoice.getUser() == null || false == context.getUserName().equals(invoice.getUser().getName())) {
			LOG.error("Saving an invoice is only possible for a known user"); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorUnknownUser);
		}
		
		final InvoiceState state = invoice.getState();
		if (!InvoiceState.UNSAVED.equals(state) && !InvoiceState.CREATED.equals(state)) {
			LOG.error("Cannot save an invoice that is beyond state CREATED. Was: {]", state); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorCannotSaveInvoice);
		}

		if (invoice.getCreationDate() == null) {
			LOG.info("Saving invoice for the first time: {}", invoice.getNumber()); //$NON-NLS-1$
			validateInvoiceNumberNotYetUsed(invoice.getNumber());
			invoice.setCreationDate(LocalDate.now());
			updateInvoiceSequencer(invoice.getNumber());
		}

		doStoreEntity(invoice);
		
		// update model meta info if necessary
		if (invoice.getInvoiceDate() != null && modelMetaInformation.getOldestKnownInvoiceDate().isAfter(invoice.getInvoiceDate())) {
			modelMetaInformation.setOldestInvoice(invoice.getInvoiceDate());
		}
		
		
		return invoice;
	}

	/**
	 * {@inheritDoc}.
	 * 
	 * @see de.tfsw.accounting.AccountingService#sendInvoice(Invoice, LocalDate)
	 */
	@Override
	public Invoice sendInvoice(Invoice invoice, LocalDate sentDate) {
		if (invoice == null) {
			LOG.warn("Trying to send invoice which is [null]. Aborting..."); //$NON-NLS-1$
			return null;
		}

		LOG.debug("sendInvoice: " + invoice.getNumber()); //$NON-NLS-1$

		// Invoice is CANCELLED -> cannot re-send
		if (invoice.getCancelledDate() != null) {
			LOG.error("Cannot re-send cancelled invoice " + invoice.getNumber()); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorCannotSendCancelledInvoice);
		}
		
		// Invoice has no due date, or a due date in the past -> cannot send
		if (invoice.getDueDate() == null) {
			LOG.error("Cannot send an invoice without a due date: " + invoice.getNumber());
			throw new AccountingException(Messages.AccountingService_errorCannotSendInvoiceWithoutDueDate);
		} else if (invoice.getDueDate().isBefore(sentDate)) {
			final String dueDate = FormatUtil.formatDate(invoice.getDueDate());
			LOG.error(String.format("Invoice [%s] has a due date in the past (%s), cannot send!",  //$NON-NLS-1$
					invoice.getNumber(), dueDate));
			throw new AccountingException(
					Messages.bind(Messages.AccountingService_errorCannotSendInvoiceWithDueDateInPast, dueDate));
		}

		if (invoice.getState() == InvoiceState.UNSAVED) {
			LOG.warn("Trying to send an unsaved invoice - will save first"); //$NON-NLS-1$
			invoice = saveInvoice(invoice);
		}

		invoice.setSentDate(sentDate);
		doStoreEntity(invoice);
		
		return invoice;
	}
		
	/**
     * {@inheritDoc}.
     * @see de.tfsw.accounting.AccountingService#markAsPaid(Invoice, LocalDate)
     */
    @Override
    public Invoice markAsPaid(Invoice invoice, LocalDate paymentDate) {
    	if (invoice == null) {
    		LOG.warn("Trying to mark invoice [null] as paid, ignoring this service call"); //$NON-NLS-1$
    		return null;
    	} else if (paymentDate == null) {
    		LOG.warn(String.format("Trying to mark invoice [%s] as paid with date [null], aborting...", //$NON-NLS-1$
    				invoice.getNumber()));
    		return invoice;
    	}
    	
    	if (!invoice.canBePaid()) {
    		LOG.error("Cannot mark this invoice as paid!");
    		throw new AccountingException(Messages.AccountingService_errorCannotMarkInvoiceAsPaid);
    	}
    	
    	invoice.setPaymentDate(paymentDate);
    	doStoreEntity(invoice);
    	
	    return invoice;
    }

	/**
     * {@inheritDoc}.
     * @see de.tfsw.accounting.AccountingService#cancelInvoice(de.tfsw.accounting.model.Invoice)
     */
    @Override
    public Invoice cancelInvoice(Invoice invoice) {
    	if (invoice == null) {
    		LOG.warn("Trying to cancel invoice [null], ignoring this service call"); //$NON-NLS-1$
    		return null;
    	}
    	
    	if (!invoice.canBeCancelled()) {
    		LOG.error(String.format("Cannot cancel Invoice [%s], since it is in state [%s]", //$NON-NLS-1$
    				invoice.getNumber(), invoice.getState()));
    		
    		throw new AccountingException("This invoice cannot be cancelled");
    	}
    	
    	invoice.setCancelledDate(LocalDate.now());
    	doStoreEntity(invoice);
    	
	    return invoice;
    }

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.tfsw.accounting.AccountingService#deleteInvoice(de.tfsw.accounting.model.Invoice)
	 */
	@Override
	public void deleteInvoice(Invoice invoice) {
		if (invoice == null) {
			LOG.warn("Trying to delete an invoice [null]!"); //$NON-NLS-1$
			return;
		}

		LOG.debug("Delete invoice: " + invoice.getNumber()); //$NON-NLS-1$

		InvoiceState state = invoice.getState();

		if (InvoiceState.UNSAVED.equals(state)) {
			LOG.info(String.format("Don't have to delete an invoice in state [%s]", state.name())); //$NON-NLS-1$
			return;
		}

		// Invoice is in an advanced state and cannot be deleted
		if (!InvoiceState.CREATED.equals(state)) {
			final Object[] params = { invoice.getNumber(), state };

			LOG.error(String.format("Invoice [%s] is in state [%s] and cannot be deleted", params)); //$NON-NLS-1$
			throw new AccountingException(
					Messages.bind(Messages.AccountingService_errorInvoiceCannotBeDeleted, params));
		}

		doDeleteEntity(invoice);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.tfsw.accounting.AccountingService#getInvoice(String)
	 */
	@Override
	public Invoice getInvoice(final String invoiceNumber) {
		LOG.debug("getInvoice: " + invoiceNumber); //$NON-NLS-1$
		Invoice invoice = null;

		Set<Invoice> invoiceList = persistence.runFindQuery(new GetInvoicePredicate(invoiceNumber));
		
		if (invoiceList == null || invoiceList.size() < 1) {
			invoice = null;
		} else if (invoiceList.size() == 1) {
			invoice = invoiceList.iterator().next();
		} else if (invoiceList.size() > 1) {
			LOG.warn("Should have found 1 invoice, but found instead: " + invoiceList.size()); //$NON-NLS-1$
			invoiceList.forEach(i -> LOG.debug("Found invoice: {}", i.getNumber()));
		}

		return invoice;
	}
	
	/**
     * {@inheritDoc}.
     * @see AccountingService#copyInvoice(Invoice, String)
     */
    @Override
    public Invoice copyInvoice(Invoice invoice, String invoiceNumber) {
    	Invoice copy = createNewInvoice(invoiceNumber, invoice.getClient());
    	
    	if (invoice.getPaymentTerms() != null) {
    		copy.setPaymentTerms(invoice.getPaymentTerms());
    	}
    	
    	if (invoice.getInvoicePositions() != null) {
    		List<InvoicePosition> positions = new ArrayList<InvoicePosition>();
    		
    		for (InvoicePosition origIP : invoice.getInvoicePositions()) {
    			try {
	                InvoicePosition copiedIP = (InvoicePosition) BeanUtils.cloneBean(origIP);
	                positions.add(copiedIP);
                } catch (Exception e) {
                	LOG.error("Error while cloning invoice position", e); //$NON-NLS-1$
                }
    		}
    		
    		copy.setInvoicePositions(positions);
    	}
    	
	    return copy;
    }
    
	/**
	 * @see AccountingService#findInvoices(InvoiceState...)
	 */
	@Override
	public Set<Invoice> findInvoices(InvoiceState... states) {
		return findInvoices(null, states);
	}

	/**
	 * 
	 */
	@Override
	public Set<Invoice> findInvoices(TimeFrame timeFrame, InvoiceState... states) {
		return new HashSet<Invoice>(persistence.runFindQuery(new FindInvoicesPredicate(context, timeFrame, states)));
	}

	/**
	 * 
     */
    @Override
    public Revenue getRevenue(TimeFrame timeFrame) {
    	Revenue revenue = new Revenue();
    	revenue.setTimeFrame(timeFrame);
    	
		List<Invoice> invoices = new ArrayList<Invoice>(persistence.runFindQuery(new FindInvoicesForRevenuePredicate(timeFrame)));
		sortInvoicesByPaymentDateAscending(invoices);
        revenue.setInvoices(invoices);
    	
	    return revenue;
    }

    /**
     * 
     */
    @Override
    public List<Revenue> getRevenueByYears() {
    	List<Revenue> totalRevenue = new ArrayList<Revenue>();
    	
    	// get all paid invoices
    	List<Invoice> allInvoices = new ArrayList<Invoice>(findInvoices(InvoiceState.PAID));
    	
    	if (allInvoices.isEmpty()) {
    		return totalRevenue;
    	}
    	
    	// sort ascending
    	sortInvoicesByPaymentDateAscending(allInvoices);
    	
    	int currentYear = -1;
    	Revenue currentRevenue = null;
    	List<Invoice> currentInvoices = new ArrayList<Invoice>();
    	
    	for (Invoice invoice : allInvoices) {
    		// get year
    		int tempCurrentYear = invoice.getPaymentDate().getYear();
    		
    		if (tempCurrentYear != currentYear) {
    			if (currentYear > 0) {
        			// wrap up the last year
        			currentRevenue.setInvoices(currentInvoices);
        			totalRevenue.add(currentRevenue);
        			LOG.debug("Finishing year " + currentYear);
        			LOG.debug("Total net revenue: " + FormatUtil.formatCurrency(currentRevenue.getRevenueNet()));
    			}
    			
    			LOG.debug("Collecting revenue for new year: " + tempCurrentYear);
    			// start up the new year
    			currentRevenue = new Revenue();
    			currentRevenue.setTimeFrame(TimeFrame.ofYear(invoice.getPaymentDate().getYear()));
    			currentInvoices = new ArrayList<Invoice>();
    			currentYear = tempCurrentYear;
    		}
    		
    		currentInvoices.add(invoice);
    	}
    	
    	// need to wrap up the final year...
		currentRevenue.setInvoices(currentInvoices);
		totalRevenue.add(currentRevenue);
		LOG.debug("Finishing year " + currentYear);
		LOG.debug("Total net revenue: " + FormatUtil.formatCurrency(currentRevenue.getRevenueNet()));
		
    	return totalRevenue;
    }
    
    /**
     * 
     */
    private void sortInvoicesByPaymentDateAscending(List<Invoice> invoices) {
		Collections.sort(invoices, (i1, i2) ->  i1.getPaymentDate().compareTo(i2.getPaymentDate()));    	
    }
    
    /**
     * 
     */
    @Override
    public Expense saveExpense(Expense expense) {
    	doStoreEntity(expense);
    	checkAndUpdateMetaInfo(expense);
	    return expense;
    }
        
	/**
	 * 
     */
    @Override
    public Collection<Expense> saveExpenses(Collection<Expense> expenses) {
    	doStoreEntities(expenses);
    	for (Expense expense : expenses) {
    		checkAndUpdateMetaInfo(expense);
    	}
	    return expenses;
    }

    /**
     * 
     */
    private void checkAndUpdateMetaInfo(Expense expense) {
    	if (expense.getPaymentDate() != null && modelMetaInformation.getOldestKnownExpenseDate().isAfter(expense.getPaymentDate())) {
    		modelMetaInformation.setOldestExpense(expense.getPaymentDate());
    	}
    	
    	if (expense.getCategory() != null) {
    		modelMetaInformation.getExpenseCategories().add(expense.getCategory());
    	}
    }
    
	/**
     * {@inheritDoc}.
     * @see AccountingService#findExpenses(TimeFrame, ExpenseType...)
     */
    @Override
    public ExpenseCollection findExpenses(TimeFrame timeFrame, ExpenseType...types) {
    	ExpenseCollection ec = new ExpenseCollection();
    	ec.setTimeFrame(timeFrame);
    	ec.setExpenses(getExpensesAsSet(timeFrame, types));
    	return ec;
    }
    
    /**
     * 
     * @param timeFrame
     * @return
     */
    private Set<Expense> getExpensesAsSet(TimeFrame timeFrame, ExpenseType...types) {
    	Set<Expense> expenses = null;
		expenses = new TreeSet<Expense>(new Comparator<Expense>() {
			@Override
			public int compare(Expense o1, Expense o2) {
				int result = o1.getPaymentDate().compareTo(o2.getPaymentDate());
				if (result == 0) {
					result = o1.getDescription().compareTo(o2.getDescription());
					if (result == 0) {
						result = o1.equals(o2) ? 0 : -1;
					}
				}
				return result;
			}
		});
		expenses.addAll(persistence.runFindQuery(new FindExpensesPredicate(timeFrame, types)));
    	return expenses;
    }
    
	/**
	 * 
	 */
	@Override
	public void deleteExpense(Expense expense) {
		doDeleteEntity(expense);
	}
	
	/**
	 * 
	 */
	@Override
	public List<AnnualDepreciation> getDepreciationForYear(int year) {
		Set<Expense> expenses = persistence.runFindQuery(new FindDepreciationPredicate(year));
		
		List<AnnualDepreciation> returnMe = new ArrayList<AnnualDepreciation>();
		
		for (Expense expense : expenses) {
			for (AnnualDepreciation annual : expense.getDepreciationSchedule()) {
				if (annual.getYear() == year) {
					returnMe.add(annual);
				}
			}
		}
		
		return returnMe;
	}

	/**
	 * 
     */
    @Override
    public IncomeStatement getIncomeStatement(TimeFrame timeFrame) {
    	IncomeStatement incomeStatement = new IncomeStatement();
    	incomeStatement.setTimeFrame(timeFrame);
    	incomeStatement.setRevenue(getRevenue(timeFrame));
    	incomeStatement.setOperatingExpenses(findExpenses(timeFrame, ExpenseType.OPEX));
    	incomeStatement.setCapitalExpenses(findExpenses(timeFrame, ExpenseType.CAPEX));
    	incomeStatement.setOtherExpenses(findExpenses(timeFrame, ExpenseType.OTHER));
	    return incomeStatement;
    }

	/**
     * 
     * {@inheritDoc}.
     * @see AccountingService#exportModelToXml(java.lang.String)
     */
    @Override
    public void exportModelToXml(String targetFileName) {
    	XmlModelDTO model = new XmlModelDTO();
    	model.setUser(getCurrentUser());
    	model.setClients(getClients());
    	model.setInvoices(findInvoices());
    	model.setExpenses(getExpensesAsSet(null));
    	model.setExpenseTemplates(getExpenseTemplates());
    	AccountingXmlImportExport.exportModelToXml(model, targetFileName);
    }
    
    /**
     * 
     */
    @Override
    public AccountingContext importModelFromXml(String sourceXmlFile, String dbFileLocation) {
    	if (initialised) {
    		throw new AccountingException("Cannot import into an existing DB!");
    	}
    	
    	LOG.info("Importing data from XML file:" + sourceXmlFile); //$NON-NLS-1$
    	XmlModelDTO importResult = AccountingXmlImportExport.importModelFromXml(sourceXmlFile);
    	
    	final String userName = importResult.getUser().getName();
    	
    	LOG.info("Building context for imported user " + userName); //$NON-NLS-1$
    	this.context = AccountingContextFactory.buildContext(userName, dbFileLocation);
    	
    	init(context);
    	
    	// save all entities imported from XML...
    	LOG.info("Now saving imported user to DB file");
    	doStoreEntity(importResult.getUser());
    	
    	final Set<Client> importedClients = importResult.getClients();
    	if (importedClients != null && !importedClients.isEmpty()) {
    		LOG.info("Now saving imported clients: " + importedClients.size()); //$NON-NLS-1$
    		doStoreEntities(importedClients);
    	} else {
    		LOG.info("No clients to import"); //$NON-NLS-1$
    	}
    	
    	final Set<Invoice> importedInvoices = importResult.getInvoices();
    	if (importedInvoices != null && !importedInvoices.isEmpty()) {
    		LOG.info("Now saving imported Invoices to DB file: " + importedInvoices.size()); //$NON-NLS-1$
    		doStoreEntities(importedInvoices);
    	} else {
    		LOG.info("No invoices to import"); //$NON-NLS-1$
    	}
    	
    	final Set<Expense> importedExpenses = importResult.getExpenses();
    	if (importedExpenses != null && !importedExpenses.isEmpty()) {
    		LOG.info("Now saving imported Expenses to DB file: " + importedExpenses.size()); //$NON-NLS-1$
    		doStoreEntities(importedExpenses);
    	} else {
    		LOG.info("No expenses to import"); //$NON-NLS-1$
    	}
    	
    	final Set<ExpenseTemplate> importedTemplates = importResult.getExpenseTemplates();
    	if (importedTemplates != null && !importedTemplates.isEmpty()) {
    		LOG.info("Now saving imported ExpenseTEmplates: " + importedTemplates.size()); //$NON-NLS-1$
    		doStoreEntities(importedTemplates);
    	}
    	
    	LOG.info("Successfully imported data, now building meta information");
    	
    	buildModelMetaInfo();
    	
    	return context;
    }
    
	/**
	 * 
     */
    @Override
    public ExpenseImportResult importExpenses(String sourceFile, ExpenseImportParams params) {
    	ExpenseImporter importer = new ExpenseImporter(new File(sourceFile), getCurrentUser().getTaxRates(), params);
	    return importer.parse();
    }

    /**
     * 
     */
    @Override
    public ModelMetaInformation getModelMetaInformation() {
    	return modelMetaInformation;
    }
    
	/**
	 * 
	 */
	@Override
	public CurriculumVitae saveCurriculumVitae(CurriculumVitae cv) {
		LOG.debug("Saving Curriculum Vitae with number of entries: " + (cv.getReferences() != null ? cv.getReferences().size() : "NULL"));
		doStoreEntity(cv);
		return cv;
	}

	/**
	 * 
	 */
	@Override
	public CurriculumVitae getCurriculumVitae() {
		CurriculumVitae cv = null;
		
		Set<CurriculumVitae> cvSet = persistence.runFindQuery(CurriculumVitae.class);
		
		if (cvSet.size() == 1) {
			cv = cvSet.iterator().next();
			cleanupCvEntries(cv);
//			doDeleteEntities(cv.getReferences(), true);
//			doDeleteEntity(cv);
//			return null;
		} else { 
			LOG.error("Cannot uniquely identify CV, size of found elements is: " + cvSet.size());
		}
		
		return cv;
	}
	
	/**
	 * 
	 * @param cv
	 */
	private void cleanupCvEntries(CurriculumVitae cv) {
		Collection<CVEntry> known = cv.getReferences() != null ? cv.getReferences() : new ArrayList<CVEntry>();
		for (CVEntry entry : persistence.runFindQuery(CVEntry.class)) {
			if (known.contains(entry)) {
				LOG.debug("CleanupCVEntries - KNOWN: " + entry.getTitle());
			} else {
				LOG.debug("CleanupCVEntries - ORPHAN: " + entry.getTitle());
				doDeleteEntity(entry);
			}
		}
	}
	
	/**
	 * @see de.tfsw.accounting.AccountingService#getExpenseTemplates()
	 */
	@Override
	public Set<ExpenseTemplate> getExpenseTemplates() {
		return new HashSet<ExpenseTemplate>(persistence.runFindQuery(ExpenseTemplate.class));
	}

	/**
	 * @see de.tfsw.accounting.AccountingService#findApplicableExpenseTemplates()
	 */
	@Override
	public Set<ExpenseTemplate> findApplicableExpenseTemplates() {
		return new HashSet<ExpenseTemplate>(persistence.runFindQuery(new FindExpenseTemplatePredicate(true, true)));
	}
	
	/**
	 * @see de.tfsw.accounting.AccountingService#saveExpenseTemplate(de.tfsw.accounting.model.ExpenseTemplate)
	 */
	@Override
	public ExpenseTemplate saveExpenseTemplate(ExpenseTemplate template) {
		doStoreEntity(template);
		return template;
	}

	/**
	 * @see de.tfsw.accounting.AccountingService#deleteExpenseTemplate(de.tfsw.accounting.model.ExpenseTemplate)
	 */
	@Override
	public void deleteExpenseTemplate(ExpenseTemplate template) {
		LOG.info(String.format("Deleting expense template [%s / %s]", template.getDescription(), template.getRule().toString()));
		doDeleteEntity(template);
	}
	
	/**
	 * Shortcut for <code>doStoreEntity(entity, true)</code>
	 * @param entity
	 */
	private void doStoreEntity(final AbstractBaseEntity entity) {
		persistence.storeEntity(entity);
		postModelChangeEvent(entity.getClass());
		BusinessLogger.log(Operation.SAVE, entity);
	}
	
	/**
	 * 
	 * @param entities
	 */
	private void doStoreEntities(Collection<? extends AbstractBaseEntity> entities) {
		if (entities != null && entities.isEmpty() == false) {
			persistence.storeEntities(entities);
			postModelChangeEvent(entities.iterator().next().getClass());
		}
	}
	
	/**
	 * 
	 * @param entity
	 * @throws AccountingException
	 * @throws Db4oException
	 */
	private void doDeleteEntity(AbstractBaseEntity entity) {
		if (entity == null) {
			LOG.warn("Call to doDeleteEntity with param [null]!"); //$NON-NLS-1$
			return;
		}

		persistence.deleteEntity(entity);
		BusinessLogger.log(Operation.DELETE, entity);
		postModelChangeEvent(entity.getClass());
	}
	
	@SuppressWarnings("unchecked")
	private void postModelChangeEvent(Class<? extends AbstractBaseEntity> type) {
		eventAdmin.postEvent(new Event(EventIds.modelChangeTopicFor(type), (Map) null));
	}
}
