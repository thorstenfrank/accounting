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
import java.util.List;
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

/**
 * Implementation of the {@link AccountingService} that used DB4o for persistence.
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
	 * @param db4oService
	 */
	AccountingServiceImpl(Db4oService db4oService) {
		this.db4oService = db4oService;
		this.initialised = false;
	}
	
	/** 
	 * {@inheritDoc}
	 * @see de.togginho.accounting.AccountingService#init(de.togginho.accounting.AccountingContext)
	 */
	@Override
	public void init(AccountingContext context) {
		if (context == null) {
			throwAccountingException(Messages.AccountingService_errorNoContext, null);
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
			throwAccountingException(Messages.bind(Messages.AccountingService_errorFileLocked, context.getDbFileName()), e);
		} catch (Exception e) {
			throwAccountingException(Messages.AccountingService_errorServiceInit, e);
		}
		
		// set this service to initialised - only after all necessary processing finished successfully
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
			LOG.warn("Error closing DB file", e); //$NON-NLS-1$
		} finally {
			initialised = false;
		}
	}

	/**
	 * {@inheritDoc}
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
			throwAccountingException(Messages.AccountingService_errorIO, e);
		} catch (DatabaseClosedException e) {
			throwAccountingException(Messages.AccountingService_errorDatabaseClosed, e);
		}
		
		return user;
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
			throwAccountingException(Messages.AccountingService_errorMissingInvoiceNumber, null);
		} else if (invoice.getUser() == null || false == context.getUserName().equals(invoice.getUser().getName())) {
			throwAccountingException(Messages.AccountingService_errorUnknownUser, null);
		} 
		InvoiceState state = invoice.getState();
		if (!InvoiceState.UNSAVED.equals(state) && !InvoiceState.CREATED.equals(state)) {
			throwAccountingException(Messages.AccountingService_errorCannotSaveInvoice, null);
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
	 * @see de.togginho.accounting.AccountingService#sendInvoice(de.togginho.accounting.model.Invoice)
	 */
	@Override
	public Invoice sendInvoice(Invoice invoice) {
		LOG.debug("sendInvoice: " + invoice.getNumber()); //$NON-NLS-1$
		final Date today = new Date();
		
		if (invoice.getCancelledDate() != null) {
			throwAccountingException(Messages.AccountingService_errorCannotSendCancelledInvoice, null);
		}
		if (invoice.getDueDate() == null) {
			throwAccountingException(Messages.AccountingService_errorCannotSendInvoiceWithoutDueDate, null);
		} else if (invoice.getDueDate().before(today)) {
			throwAccountingException(Messages.AccountingService_errorCannotSendInvoiceWithDueDateInPast, null);
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
	 * {@inheritDoc}
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
			final Object[] params = {invoice.getNumber(), state};
			
			LOG.error(String.format("Invoice [%s] is in state [%s] and cannot be deleted", params));
			throw new AccountingException(Messages.bind(Messages.AccountingService_errorInvoiceCannotBeDeleted, params));
		}
		
		try {
			objectContainer.delete(invoice);
			objectContainer.commit();
		} catch (Db4oIOException e) {
			objectContainer.rollback();
			throwAccountingException(Messages.AccountingService_errorIO, e);
		} catch (DatabaseClosedException e) {
			objectContainer.rollback();
			throwAccountingException(Messages.AccountingService_errorDatabaseClosed, e);
		} catch (DatabaseReadOnlyException e) {
			objectContainer.rollback();
			throwAccountingException(Messages.AccountingService_errorDatabaseReadOnly, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.AccountingService#getInvoice(String)
	 */
	@SuppressWarnings("serial")
	@Override
	public Invoice getInvoice(final String invoiceNumber) {
		LOG.debug("getInvoice: " + invoiceNumber);
		Invoice invoice = null;
		
		try {
			List<Invoice> invoiceList = objectContainer.query(new Predicate<Invoice>() {
				@Override
				public boolean match(Invoice invoice) {
					return invoice.getNumber().equals(invoiceNumber);
				}
			});
			
			
			if (invoiceList.size() == 1) {
				invoice = invoiceList.get(0);
			} else {
				LOG.warn("Should have found 1 invoice, but found instead: " + invoiceList.size());
			}
		} catch (Db4oIOException e) {
			objectContainer.rollback();
			throwAccountingException(Messages.AccountingService_errorIO, e);
		} catch (DatabaseClosedException e) {
			objectContainer.rollback();
			throwAccountingException(Messages.AccountingService_errorDatabaseClosed, e);
		}
		
		return invoice;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.AccountingService#findInvoices(de.togginho.accounting.model.User, de.togginho.accounting.model.InvoiceState[])
	 */
	@Override
	public Set<Invoice> findInvoices(User user, InvoiceState... states) {
		LOG.debug("findInvoices");
		Set<Invoice> invoices = new HashSet<Invoice>();
		
		try {
			invoices.addAll(objectContainer.query(new FindInvoicesPredicate(user, states)));
		} catch (Db4oIOException e) {
			objectContainer.rollback();
			throwAccountingException(Messages.AccountingService_errorIO, e);
		} catch (DatabaseClosedException e) {
			objectContainer.rollback();
			throwAccountingException(Messages.AccountingService_errorDatabaseClosed, e);
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
			objectContainer.rollback();
			throwAccountingException(Messages.AccountingService_errorDatabaseClosed, e);
		} catch (DatabaseReadOnlyException e) {
			objectContainer.rollback();
			throwAccountingException(Messages.AccountingService_errorDatabaseReadOnly, e);
		}
	}
	
	/**
	 * 
	 * @param errorMsg
	 * @param cause
	 */
	private void throwAccountingException(final String errorMsg, Exception cause) {
		if (cause == null) {
			LOG.error(errorMsg);
			throw new AccountingException(errorMsg);
		} else {
			LOG.error(errorMsg, cause);
			throw new AccountingException(errorMsg, cause);
		}
		
	}
}
