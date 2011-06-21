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
package de.togginho.accounting.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Set;

import de.togginho.accounting.AccountingContext;
import de.togginho.accounting.AccountingService;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.User;

/**
 * @author thorsten
 *
 */
public final class ModelHelper {
	
	/** Singleton instance. */
	private static final ModelHelper INSTANCE = new ModelHelper();
	
	/** */
	public static final String MODEL_CURRENT_USER = "model.current.user"; //$NON-NLS-1$
	
	/** */
	public static final String MODEL_INVOICES = "model.invoices"; //$NON-NLS-1$
	
	/** */
	private AccountingContext context;
	
	/** */
	private AccountingService accountingService;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	private User currentUser;
	
	/**
	 * Singleton -> private.
	 */
	private ModelHelper() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * 
	 * @param context
	 * @param service
	 */
	protected static final void init(AccountingContext context, AccountingService service) {
		INSTANCE.context = context;
		INSTANCE.accountingService = service;
		//INSTANCE.propertyChangeSupport = new PropertyChangeSupport(INSTANCE);
	}

	/**
	 * 
	 * @return
	 */
	public static User getCurrentUser() {
		if (INSTANCE.currentUser == null) {
			INSTANCE.currentUser = INSTANCE.accountingService.getCurrentUser();
		}
		return INSTANCE.currentUser;
	}

	/**
	 * 
	 */
	public static void saveCurrentUser() {
		saveCurrentUser(INSTANCE.currentUser);
	}

	/**
	 * 
	 * @param newUser
	 */
	public static void saveCurrentUser(User newUser) {
		INSTANCE.currentUser = INSTANCE.accountingService.saveCurrentUser(newUser);
		// old value needs to be null to make sure the change is actually broadcast
		INSTANCE.propertyChangeSupport.firePropertyChange(MODEL_CURRENT_USER, null, INSTANCE.currentUser);		
	}
	
	/**
	 * 
	 * @return
	 */
	public static Set<Invoice> getOpenInvoices() {
		return INSTANCE.accountingService.findInvoices(getCurrentUser(), InvoiceState.CREATED, InvoiceState.SENT);
	}
	
	/**
	 * 
	 * @param invoiceNumber
	 * @return
	 */
	public static Invoice getInvoice(String invoiceNumber) {
		return INSTANCE.accountingService.getInvoice(invoiceNumber);
	}
	
	/**
	 * 
	 * @param invoice
	 * @return
	 */
	public static Invoice saveInvoice(Invoice invoice) {
		Invoice saved = INSTANCE.accountingService.saveInvoice(invoice);
		INSTANCE.propertyChangeSupport.firePropertyChange(MODEL_INVOICES, null, saved);
		return saved;
	}

	/**
	 * @param invoice
	 * @return
	 * @see de.togginho.accounting.AccountingService#sendInvoice(de.togginho.accounting.model.Invoice)
	 */
	public static Invoice sendInvoice(Invoice invoice) {
		Invoice sent = INSTANCE.accountingService.sendInvoice(invoice);
		INSTANCE.propertyChangeSupport.firePropertyChange(MODEL_INVOICES, null, sent);
		return sent;
	}

	/**
	 * 
	 * @param invoice
	 */
	public static void deleteInvoice(Invoice invoice) {
		INSTANCE.accountingService.deleteInvoice(invoice);
		INSTANCE.propertyChangeSupport.firePropertyChange(MODEL_INVOICES, invoice, null);
	}

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
	 */
	public static void addPropertyChangeListener(PropertyChangeListener listener) {
		INSTANCE.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
	 * @see #MODEL_CURRENT_USER
	 * @see #MODEL_INVOICES
	 */
	public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		INSTANCE.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
	 */
	public static void removePropertyChangeListener(PropertyChangeListener listener) {
		INSTANCE.propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
	 * @see #MODEL_CURRENT_USER
	 * @see #MODEL_INVOICES
	 */
	public static void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		INSTANCE.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
