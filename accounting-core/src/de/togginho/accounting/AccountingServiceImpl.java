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
package de.togginho.accounting;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.config.ObjectField;
import com.db4o.config.encoding.StringEncodings;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oIOException;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.User;
import de.togginho.accounting.util.FormatUtil;

/**
 * Implementation of the {@link AccountingService} that used DB4o for
 * persistence.
 * 
 * @author thorsten frank
 */
class AccountingServiceImpl implements AccountingService {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(AccountingServiceImpl.class);
	
	private boolean initialised;

	private AccountingContext context;
	private Db4oService db4oService;
	private ObjectContainer objectContainer;

	/**
	 * Creates a new instance of this service implementation.
	 * 
	 * @param db4oService
	 */
	AccountingServiceImpl(Db4oService db4oService) {
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

		LOG.info("init");
		this.context = context;

		try {
			objectContainer = db4oService.openFile(createConfiguration(), context.getDbFileName());
		} catch (DatabaseFileLockedException e) {
			LOG.error(String.format(
					"DB file [%s] is locked by another process - application is probably already running", context.getDbFileName()), e); //$NON-NLS-1$
			
			throw new AccountingException(
			        Messages.bind(Messages.AccountingService_errorFileLocked, context.getDbFileName()), e);
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
		config.add(new UniqueFieldValueConstraint(Client.class, Client.FIELD_NAME));

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
	 * Propery shuts down the service.
	 */
	protected void shutDown() {
		LOG.info("shutDown"); //$NON-NLS-1$

		if (!initialised) {
			return;
		}

		try {
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
	 * {@inheritDoc}
	 * 
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
			invoice.setCreationDate(new Date());
		}

		doStoreEntity(invoice);

		return invoice;
	}

	/**
	 * {@inheritDoc}.
	 * 
	 * @see de.togginho.accounting.AccountingService#sendInvoice(de.togginho.accounting.model.Invoice)
	 */
	@Override
	public Invoice sendInvoice(Invoice invoice) {
		if (invoice == null) {
			LOG.warn("Trying to send invoice which is [null]. Aborting..."); //$NON-NLS-1$
			return null;
		}

		LOG.debug("sendInvoice: " + invoice.getNumber()); //$NON-NLS-1$
		final Date today = new Date();

		// Invoice is CANCELLED -> cannot re-send
		if (invoice.getCancelledDate() != null) {
			LOG.error("Cannot re-send cancelled invoice " + invoice.getNumber()); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorCannotSendCancelledInvoice);
		}
		
		// Invoice has no due date, or a due date in the past -> cannot send
		if (invoice.getDueDate() == null) {
			LOG.error("Cannot send an invoice without a due date: " + invoice.getNumber());
			throw new AccountingException(Messages.AccountingService_errorCannotSendInvoiceWithoutDueDate);
		} else if (invoice.getDueDate().before(today)) {
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

		invoice.setSentDate(new Date());
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

		try {
			objectContainer.delete(invoice);
			objectContainer.commit();
		} catch (Db4oIOException e) {
			throwDb4oIoException(e);
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
		} catch (DatabaseReadOnlyException e) {
			throwDbReadOnlyException(e);
		}
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

			if (invoiceList.size() == 1) {
				invoice = invoiceList.get(0);
			} else {
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
	 * 
	 * @param entity
	 * @throws AccountingPersistenceException
	 */
	private void doStoreEntity(Object entity) {
		if (entity == null) {
			LOG.warn("Call to doStoreEntity with param [null]!");
			return;
		}
		try {
			objectContainer.store(entity);
			objectContainer.commit();
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
		} catch (DatabaseReadOnlyException e) {
			throwDbReadOnlyException(e);
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
