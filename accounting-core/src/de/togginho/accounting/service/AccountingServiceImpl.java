/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.config.ObjectField;
import com.db4o.config.encoding.StringEncodings;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.constraints.UniqueFieldValueConstraintViolationException;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oException;
import com.db4o.ext.Db4oIOException;
import com.db4o.ext.IncompatibleFileFormatException;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

import de.togginho.accounting.AccountingContext;
import de.togginho.accounting.AccountingContextFactory;
import de.togginho.accounting.AccountingException;
import de.togginho.accounting.AccountingService;
import de.togginho.accounting.Messages;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.Revenue;
import de.togginho.accounting.model.User;
import de.togginho.accounting.model.internal.InvoiceSequencer;
import de.togginho.accounting.util.FormatUtil;
import de.togginho.accounting.util.TimeFrame;
import de.togginho.accounting.xml.ImportResult;
import de.togginho.accounting.xml.ModelMapper;

/**
 * Implementation of the {@link AccountingService} that used DB4o for
 * persistence.
 * 
 * @author thorsten frank
 */
public class AccountingServiceImpl implements AccountingService {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(AccountingServiceImpl.class);
	
	private static final String INVOICE_SEQUENCER_SEMAPHORE = "INVOICE_SEQUENCER_SEMAPHORE";
	private static final int SEMAPHORE_WAIT_TIMEOUT = 1000;
	
	private boolean initialised;

	private AccountingContext context;
	private Db4oService db4oService;
	private ObjectContainer objectContainer;

	/**
	 * Creates a new instance of this service implementation.
	 * 
	 * @param db4oService
	 */
	public AccountingServiceImpl(Db4oService db4oService) {
		this.db4oService = db4oService;
		this.initialised = false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.togginho.accounting.AccountingService#init(de.togginho.accounting.AccountingContext)
	 */
	@Override
	public void init(AccountingContext context) {
		if (context == null) {
			LOG.error("Context was [null]!"); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorNoContext);
		}
		if (initialised) {
			LOG.info("Service is already initialised, nothing to do here."); //$NON-NLS-1$
			return;
		}

		LOG.info("service init"); //$NON-NLS-1$
		this.context = context;

		try {
			objectContainer = db4oService.openFile(createConfiguration(), context.getDbFileName());
		} catch (DatabaseFileLockedException e) {
			LOG.error(String.format(
					"DB file [%s] is locked by another process - application is probably already running", //$NON-NLS-1$
					context.getDbFileName()), e); 
			
			throw new AccountingException(
			        Messages.bind(Messages.AccountingService_errorFileLocked, context.getDbFileName()), e);
		} catch (IncompatibleFileFormatException e) {
			LOG.error(String.format("File [%s] is not a valid data file format!", e)); //$NON-NLS-1$
			throw new AccountingException(
					Messages.bind(Messages.AccountingService_errorIllegalFileFormat, context.getDbFileName()), e);
		} catch (Exception e) {
			LOG.error("Error setting up DB " + context.getDbFileName(), e); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorServiceInit, e);
		}

		// set this service to initialised - only after all necessary processing
		// finished successfully
		initialised = true;
	}

	/**
	 * 
	 * @return
	 */
	private Configuration createConfiguration() {
		Configuration config = db4oService.newConfiguration();

		// make sure to use UTF-8
		config.stringEncoding(StringEncodings.utf8());

		// allow version upgrades...
		// common.allowVersionUpdates(true);

		// config for User object graph cascade
		ObjectClass userClass = config.objectClass(User.class);
		userClass.cascadeOnUpdate(true);
		userClass.cascadeOnDelete(true);
		userClass.objectField(User.FIELD_NAME).indexed(true);
		config.add(new UniqueFieldValueConstraint(User.class, User.FIELD_NAME));

		// config for Client object graph cascade
		ObjectClass clientClass = config.objectClass(Client.class);
		clientClass.cascadeOnUpdate(true);
		clientClass.cascadeOnDelete(true);
		clientClass.objectField(Client.FIELD_NAME).indexed(true);
		clientClass.objectField(Client.FIELD_CLIENT_NUMBER).indexed(true);
		config.add(new UniqueFieldValueConstraint(Client.class, Client.FIELD_NAME));
		config.add(new UniqueFieldValueConstraint(Client.class, Client.FIELD_CLIENT_NUMBER));

		// config for Invoice object graph
		ObjectClass invoiceClass = config.objectClass(Invoice.class);
		ObjectField invoicePositionsField = invoiceClass.objectField(Invoice.FIELD_INVOICE_POSITIONS);
		invoicePositionsField.cascadeOnDelete(true);
		invoicePositionsField.cascadeOnUpdate(true);
		invoiceClass.objectField(Invoice.FIELD_NUMBER).indexed(true);
		config.add(new UniqueFieldValueConstraint(Invoice.class, Invoice.FIELD_NUMBER));

		return config;
	}

	/**
	 * Properly shuts down the service.
	 */
	public void shutDown() {
		LOG.info("shutDown"); //$NON-NLS-1$

		if (!initialised) {
			LOG.info("Service not initialised, nothing to do here!"); //$NON-NLS-1$
			return;
		}

		try {
			LOG.info("Closing object container."); //$NON-NLS-1$
			objectContainer.close();
		} catch (Db4oIOException e) {
			LOG.warn("Error closing DB file, will ignore", e); //$NON-NLS-1$
		} finally {
			initialised = false;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.togginho.accounting.AccountingService#saveCurrentUser(de.togginho.accounting.model.User)
	 */
	@Override
	public User saveCurrentUser(User user) {
		LOG.debug("saveCurrentUser"); //$NON-NLS-1$
		doStoreEntity(user);
		return user;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see de.togginho.accounting.AccountingService#getCurrentUser()
	 */
	@Override
	public User getCurrentUser() {
		LOG.debug("getCurrentUser"); //$NON-NLS-1$
		User user = null;

		try {
			ObjectSet<User> userList = objectContainer.query(new FindCurrentUserPredicate(context));
			if (userList != null && userList.size() == 1) {
				user = userList.get(0);
			} else {
				LOG.warn("Cannot identify current user, list size is " + userList.size()); //$NON-NLS-1$
			}
		} catch (Db4oIOException e) {
			throwDb4oIoException(e);
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
		}

		return user;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.AccountingService#getClients()
	 */
	@Override
	public Set<Client> getClients() {
		
		Set<Client> clients = new HashSet<Client>();
		
		try {
			clients.addAll(objectContainer.query(Client.class));
		} catch (Db4oIOException e) {
			throwDb4oIoException(e);
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
		}
		
		return clients;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.AccountingService#saveClient(de.togginho.accounting.model.Client)
	 */
	@Override
	public Client saveClient(Client client) {
		try {
			doStoreEntity(client);
		} catch (UniqueFieldValueConstraintViolationException e) {
			LOG.error("A client with this name already exists: " + client.getName()); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorClientExists);
		}
		
		return client;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.AccountingService#deleteClient(de.togginho.accounting.model.Client)
	 */
	@Override
	public void deleteClient(Client client) {
		// TODO make sure a client can only be deleted when there are no invoices left...
		doDeleteEntity(client);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * @see de.togginho.accounting.AccountingService#getNextInvoiceNumber()
	 */
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
		
		try {
			doStoreEntity(sequencer);
			
			return String.format("RE%d-%02d", currentYear, sequencer.getCurrentSequenceNumber());
		} finally {
			objectContainer.ext().releaseSemaphore(INVOICE_SEQUENCER_SEMAPHORE);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private InvoiceSequencer getInvoiceSequencer() {
		if (objectContainer.ext().setSemaphore(INVOICE_SEQUENCER_SEMAPHORE, SEMAPHORE_WAIT_TIMEOUT)) {
			Query query = objectContainer.query();
			query.constrain(InvoiceSequencer.class);
			
			ObjectSet<InvoiceSequencer> set = query.execute();
			
			InvoiceSequencer sequencer = null;
			
			if (set.size() == 1) {
				sequencer = set.next();
				LOG.debug("Sequencer found: " + sequencer.toString()); //$NON-NLS-1$
			} else {
				sequencer = new InvoiceSequencer();
				LOG.info("No sequencer found in storage, creating new"); //$NON-NLS-1$
			}
				
			return sequencer;
		} else {
			throw new AccountingException("Busy!");
		}
	}
	
	/**
	 * 
	 */
	private void updateInvoiceSequencer(String invoiceNumber) {
		final int year = new Integer(invoiceNumber.substring(2, 6));
		final int number = new Integer(invoiceNumber.substring(7));
		
		InvoiceSequencer sequencer = getInvoiceSequencer();
		LOG.debug("Current sequencer: " + sequencer.toString()); //$NON-NLS-1$
		
		try {
			if (year == sequencer.getYear() && number == sequencer.getCurrentSequenceNumber()) {
				LOG.debug("Nothing to change, sequencer is up to date!"); //$NON-NLS-1$
			} else {
				sequencer.setYear(year);
				sequencer.setCurrentSequenceNumber(number);
				LOG.info("Updating sequencer: "+sequencer.toString()); //$NON-NLS-1$
				doStoreEntity(sequencer);
				
			}			
		} finally {
			objectContainer.ext().releaseSemaphore(INVOICE_SEQUENCER_SEMAPHORE);
		}
	}
	
	/**
	 * Creates a new but unsaved invoice.
	 * 
	 * @param invoiceNumber
	 * @param client
	 * @return
	 */
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
		invoice.setInvoiceDate(new Date());
		if (client.getDefaultPaymentTerms() != null) {
			LOG.debug("Using default payment terms of client"); //$NON-NLS-1$
			invoice.setPaymentTerms(client.getDefaultPaymentTerms());
		} else {
			LOG.debug("Using global default payment terms"); //$NON-NLS-1$
			invoice.setPaymentTerms(PaymentTerms.getDefault());
		}
		
		// assign payment terms
		return invoice;
	}
	
	/**
	 * 
	 * @param invoiceNumber
	 */
	private void validateInvoiceNumberNotYetUsed(String invoiceNumber) {
		if (getInvoice(invoiceNumber) != null) {
			LOG.error("An invoice with this number already exists: " + invoiceNumber); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorInvoiceNumberExists);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.AccountingService#saveInvoice(de.togginho.accounting.model.Invoice)
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
		
		InvoiceState state = invoice.getState();
		if (!InvoiceState.UNSAVED.equals(state) && !InvoiceState.CREATED.equals(state)) {
			LOG.error("Cannot save an invoice that is beyond state CREATED. Was: " + state); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorCannotSaveInvoice);
		}

		if (invoice.getCreationDate() == null) {
			LOG.info("Saving invoice for the first time: " + invoice.getNumber()); //$NON-NLS-1$
			validateInvoiceNumberNotYetUsed(invoice.getNumber());
			invoice.setCreationDate(new Date());
			updateInvoiceSequencer(invoice.getNumber());
		}

		doStoreEntity(invoice);

		return invoice;
	}

	/**
	 * {@inheritDoc}.
	 * 
	 * @see de.togginho.accounting.AccountingService#sendInvoice(Invoice, Date)
	 */
	@Override
	public Invoice sendInvoice(Invoice invoice, Date sentDate) {
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
		} else if (invoice.getDueDate().before(sentDate)) {
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
     * @see de.togginho.accounting.AccountingService#markAsPaid(de.togginho.accounting.model.Invoice, java.util.Date)
     */
    @Override
    public Invoice markAsPaid(Invoice invoice, Date paymentDate) {
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
     * @see de.togginho.accounting.AccountingService#cancelInvoice(de.togginho.accounting.model.Invoice)
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
    	
    	invoice.setCancelledDate(new Date());
    	doStoreEntity(invoice);
    	
	    return invoice;
    }

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.togginho.accounting.AccountingService#deleteInvoice(de.togginho.accounting.model.Invoice)
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
	 * @see de.togginho.accounting.AccountingService#getInvoice(String)
	 */
	@SuppressWarnings("serial")
	@Override
	public Invoice getInvoice(final String invoiceNumber) {
		LOG.debug("getInvoice: " + invoiceNumber); //$NON-NLS-1$
		Invoice invoice = null;

		try {
			ObjectSet<Invoice> invoiceList = objectContainer.query(new Predicate<Invoice>() {
				@Override
				public boolean match(Invoice invoice) {
					return invoice.getNumber().equals(invoiceNumber);
				}
			});

			if (invoiceList == null || invoiceList.size() < 1) {
				invoice = null;
			} else if (invoiceList.size() == 1) {
				invoice = invoiceList.get(0);
			} else if (invoiceList.size() > 1) {
				LOG.warn("Should have found 1 invoice, but found instead: " + invoiceList.size()); //$NON-NLS-1$
			}
		} catch (Db4oIOException e) {
			throwDb4oIoException(e);
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
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
	 * {@inheritDoc}
	 * 
	 * @see AccountingService#findInvoices(InvoiceState...)
	 */
	@Override
	public Set<Invoice> findInvoices(InvoiceState... states) {
		StringBuilder sb = new StringBuilder("findInvoices for user"); //$NON-NLS-1$
		sb.append(" [").append(context.getUserName()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		if (states != null) {
			sb.append(" in state "); //$NON-NLS-1$
			for (InvoiceState state : states) {
				sb.append(" [").append(state).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		LOG.debug(sb.toString()); 
		
		Set<Invoice> invoices = new HashSet<Invoice>();

		try {
			invoices.addAll(objectContainer.query(new FindInvoicesPredicate(context, states)));
		} catch (Db4oIOException e) {
			throwDb4oIoException(e);
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
		}

		return invoices;
	}

	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.AccountingService#getRevenue(de.togginho.accounting.util.TimeFrame)
     */
    @Override
    public Revenue getRevenue(TimeFrame timeFrame) {
    	Revenue revenue = new Revenue();
    	revenue.setTimeFrame(timeFrame);
    	
    	try {
    		List<Invoice> invoices = new ArrayList<Invoice>(
    				objectContainer.query(new FindInvoicesForRevenuePredicate(timeFrame)));
    		Collections.sort(invoices, new Comparator<Invoice>() {
    				/**
                     * {@inheritDoc}.
                     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                     */
                    @Override
                    public int compare(Invoice o1, Invoice o2) {
    	                return o1.getPaymentDate().compareTo(o2.getPaymentDate());
                    }
    	        	
    			});
	        revenue.setInvoices(invoices);
        } catch (Db4oIOException e) {
        	throwDb4oIoException(e);
        } catch (DatabaseClosedException e) {
        	throwDbClosedException(e);
        }
    	
	    return revenue;
    }

    
    /**
     * {@inheritDoc}.
     * @see de.togginho.accounting.AccountingService#saveExpense(de.togginho.accounting.model.Expense)
     */
    @Override
    public Expense saveExpense(Expense expense) {
    	doStoreEntity(expense);
	    return expense;
    }

	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.AccountingService#getExpenses(de.togginho.accounting.util.TimeFrame)
     */
    @Override
    public Set<Expense> getExpenses(TimeFrame timeFrame) {
    	Set<Expense> expenses = null;
    	try {
	        expenses = new HashSet<Expense>(objectContainer.query(new FindExpensesPredicate(timeFrame)));
        } catch (Db4oIOException e) {
        	throwDb4oIoException(e);
        } catch (DatabaseClosedException e) {
        	throwDbClosedException(e);
        }
    	return expenses;
    }

	/**
     * 
     * {@inheritDoc}.
     * @see AccountingService#exportModelToXml(java.lang.String)
     */
    @Override
    public void exportModelToXml(String targetFileName) {
    	ModelMapper.modelToXml(getCurrentUser(), getClients(), findInvoices(), targetFileName);
    }
    
    /**
     * 
     * {@inheritDoc}.
     * @see AccountingService#importModelFromXml(String, String)
     */
    @Override
    public AccountingContext importModelFromXml(String sourceXmlFile, String dbFileLocation) {
    	if (initialised) {
    		throw new AccountingException("Cannot import into an existing DB!");
    	}
    	
    	LOG.info("Importing from " + sourceXmlFile); //$NON-NLS-1$
    	ImportResult importResult = ModelMapper.xmlToModel(sourceXmlFile);
    	
    	final String userName = importResult.getImportedUser().getName();
    	
    	LOG.info("Building context for imported user " + userName); //$NON-NLS-1$
    	this.context = AccountingContextFactory.buildContext(userName, dbFileLocation);
    	
    	init(context);
    	
    	// save all entities imported from XML...
    	LOG.info("Now saving imported user to DB file");
    	objectContainer.store(importResult.getImportedUser());
    	
    	final Set<Client> importedClients = importResult.getImportedClients();
    	if (importedClients != null && !importedClients.isEmpty()) {
    		LOG.info("Now saving imported clients: " + importedClients.size()); //$NON-NLS-1$
    		for (Client client : importedClients) {
    			objectContainer.store(client);
    		}
    	} else {
    		LOG.info("No clients to import"); //$NON-NLS-1$
    	}
    	
    	final Set<Invoice> importedInvoices = importResult.getImportedInvoices();
    	if (importedInvoices != null && !importedInvoices.isEmpty()) {
    		LOG.info("Now saving imported Invoices to DB file: " + importedInvoices.size()); //$NON-NLS-1$
    		for (Invoice invoice : importedInvoices) {
    			objectContainer.store(invoice);
    		}
    	} else {
    		LOG.info("No invoices to import"); //$NON-NLS-1$
    	}
    	
    	objectContainer.commit();
    	
    	return context;
    }
    
	/**
	 * 
	 * @param entity
	 * @throws AccountingException
	 * @throws Db4oException
	 */
	private void doStoreEntity(Object entity) {
		if (entity == null) {
			LOG.warn("Call to doStoreEntity with param [null]!"); //$NON-NLS-1$
			return;
		}
		
		try {
			objectContainer.store(entity);
			objectContainer.commit();
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
		} catch (DatabaseReadOnlyException e) {
			throwDbReadOnlyException(e);
		} catch(Db4oIOException e) {
			throwDb4oIoException(e);
		} catch (Db4oException e) {
			LOG.error("Error while trying so store entity: " + entity, e); //$NON-NLS-1$
			objectContainer.rollback();
			throw e;
		}
	}

	/**
	 * 
	 * @param entity
	 * @throws AccountingException
	 * @throws Db4oException
	 */
	private void doDeleteEntity(Object entity) {
		if (entity == null) {
			LOG.warn("Call to doDeleteEntity with param [null]!"); //$NON-NLS-1$
			return;
		}
		
		try {
			objectContainer.delete(entity);
			objectContainer.commit();
		} catch (Db4oIOException e) {
			throwDb4oIoException(e);
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
		} catch (DatabaseReadOnlyException e) {
			throwDbReadOnlyException(e);
		} catch (Db4oException e) {
			LOG.error("Error while deleting entity: " + entity, e); //$NON-NLS-1$
			objectContainer.rollback();
			throw e;
		}
	}
	
	/**
	 * 
	 * @param e
	 * @param rollback
	 */
	private void throwDbClosedException(DatabaseClosedException e) {
		LOG.error("Database is closed!", e); //$NON-NLS-1$
		throw new AccountingException(Messages.AccountingService_errorDatabaseClosed, e);
	}
	
	/**
	 * 
	 * @param e
	 */
	private void throwDb4oIoException(Db4oIOException e) {
		LOG.error("I/O error during DB operation", e); //$NON-NLS-1$		
		throw new AccountingException(Messages.AccountingService_errorIO, e);
	}
	
	/**
	 * 
	 * @param e
	 */
	private void throwDbReadOnlyException(DatabaseReadOnlyException e) {
		LOG.error("Database is read-only!", e);
		throw new AccountingException(Messages.AccountingService_errorDatabaseReadOnly, e);
	}
}
