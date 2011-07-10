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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.togginho.accounting.AccountingContext;
import de.togginho.accounting.AccountingService;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.Revenue;
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
	public static final String MODEL_INVOICE_FILTER = "model.invoice.filter"; //$NON-NLS-1$
	
	/** */
	private AccountingContext context;
	
	/** */
	private AccountingService accountingService;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	private User currentUser;
	
	private Set<InvoiceState> invoiceFilter;
	
	/**
	 * Singleton -> private.
	 */
	private ModelHelper() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		invoiceFilter = new HashSet<InvoiceState>();
		invoiceFilter.add(InvoiceState.CREATED);
		invoiceFilter.add(InvoiceState.SENT);
		invoiceFilter.add(InvoiceState.OVERDUE);
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
	public static Set<Invoice> findInvoices() {
		return INSTANCE.doFindInvoices();
	}
	
	/**
	 * 
	 * @return
	 */
	private Set<Invoice> doFindInvoices() {
		if (invoiceFilter == null || invoiceFilter.isEmpty()) {
			return accountingService.findInvoices();
		}
		final InvoiceState[] states = new InvoiceState[invoiceFilter.size()];
		Iterator<InvoiceState> iter = invoiceFilter.iterator();
		int counter = 0;
		while (iter.hasNext()) {
			states[counter] = iter.next();
			counter++;
		}
		
		return accountingService.findInvoices(states);
	}
	
	/**
	 * 
	 * @return
	 */
	public static Set<InvoiceState> getInvoiceFilter() {
		return INSTANCE.invoiceFilter;
	}
	
	/**
	 * 
	 * @param filter
	 */
	public static void setInvoiceFilter(Set<InvoiceState> filter) {
		INSTANCE.invoiceFilter = filter;
		INSTANCE.propertyChangeSupport.firePropertyChange(MODEL_INVOICE_FILTER, null, INSTANCE.invoiceFilter);
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
	 * @see de.togginho.accounting.AccountingService#sendInvoice(Invoice, Date)
	 */
	public static Invoice sendInvoice(Invoice invoice, Date date) {
		Invoice sent = INSTANCE.accountingService.sendInvoice(invoice, date);
		INSTANCE.propertyChangeSupport.firePropertyChange(MODEL_INVOICES, null, sent);
		return sent;
	}

	/**
     * @param invoice
     * @param paymentDate
     * @return
     * @see AccountingService#markAsPaid(Invoice, Date)
     */
    public static Invoice markAsPaid(Invoice invoice, Date paymentDate) {
	    Invoice paid = INSTANCE.accountingService.markAsPaid(invoice, paymentDate);
	    INSTANCE.propertyChangeSupport.firePropertyChange(MODEL_INVOICES, null, paid);
	    return paid;
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
	 * 
	 * @param invoice
	 * @return
	 * @see AccountingService#cancelInvoice(Invoice)
	 */
	public static Invoice cancelInvoice(Invoice invoice) {
		Invoice cancelled = INSTANCE.accountingService.cancelInvoice(invoice);
	    INSTANCE.propertyChangeSupport.firePropertyChange(MODEL_INVOICES, null, cancelled);
	    return cancelled;
	}
	
	/**
     * @param from
     * @param until
     * @return
     * @see de.togginho.accounting.AccountingService#getRevenue(java.util.Date, java.util.Date)
     */
    public static Revenue getRevenue(Date from, Date until) {
	    return INSTANCE.accountingService.getRevenue(from, until);
    }

	/**
	 * 
	 * @param invoice
	 * @param newInvoiceNumber
	 * @return
	 * @see AccountingService#copyInvoice(Invoice, String)
	 */
	public static Invoice copyInvoice(Invoice invoice, String newInvoiceNumber) {
		return INSTANCE.accountingService.copyInvoice(invoice, newInvoiceNumber);
	}
	
	/**
	 * 
	 * @param targetFileName
	 */
	public static void exportModelToXml(String targetFileName) {
		INSTANCE.accountingService.exportModelToXml(targetFileName);
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
