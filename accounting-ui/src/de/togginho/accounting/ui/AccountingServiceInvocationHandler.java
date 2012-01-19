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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.util.tracker.ServiceTracker;

import de.togginho.accounting.AccountingContext;
import de.togginho.accounting.AccountingException;
import de.togginho.accounting.AccountingService;

/**
 * @author tfrank1
 *
 */
final class AccountingServiceInvocationHandler implements InvocationHandler {

	private static final Logger LOG = Logger.getLogger(AccountingServiceInvocationHandler.class);
	
	private static final Set<String> MODEL_CHANGING_METHODS = new HashSet<String>();

	static {
		MODEL_CHANGING_METHODS.add("saveCurrentUser"); //$NON-NLS-1$
		MODEL_CHANGING_METHODS.add("saveClient"); //$NON-NLS-1$
		MODEL_CHANGING_METHODS.add("deleteClient"); //$NON-NLS-1$
		MODEL_CHANGING_METHODS.add("saveInvoice"); //$NON-NLS-1$
		MODEL_CHANGING_METHODS.add("sendInvoice"); //$NON-NLS-1$
		MODEL_CHANGING_METHODS.add("markAsPaid"); //$NON-NLS-1$
		MODEL_CHANGING_METHODS.add("cancelInvoice"); //$NON-NLS-1$
		MODEL_CHANGING_METHODS.add("deleteInvoice"); //$NON-NLS-1$
		MODEL_CHANGING_METHODS.add("importModelFromXml"); //$NON-NLS-1$
		MODEL_CHANGING_METHODS.add("saveExpense"); //$NON-NLS-1$
	}
	
	/** */
	private Set<ModelChangeListener> modelChangeListeners; 
	
	/** ServiceTracker for the AccountingService. */
	private ServiceTracker<AccountingService, AccountingService> accountingServiceTracker;	
	
	/** Accounting context. */
	private AccountingContext accountingContext;
	
	/**
	 * 
	 * @param accountingServiceTracker
	 */
	protected AccountingServiceInvocationHandler(
			ServiceTracker<AccountingService, AccountingService> accountingServiceTracker) {
		this(accountingServiceTracker, null);
	}
	
	/**
	 * 
	 * @param accountingServiceTracker
	 * @param accountingContext
	 */
    protected AccountingServiceInvocationHandler(
    		ServiceTracker<AccountingService, AccountingService> accountingServiceTracker,
    		AccountingContext accountingContext) {
	    this.accountingServiceTracker = accountingServiceTracker;
	    this.modelChangeListeners = new HashSet<ModelChangeListener>();
	    this.accountingContext = accountingContext;
    }
	
	/**
	 * {@inheritDoc}.
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws AccountingException {
		LOG.debug("invoking method: " + method.getName()); //$NON-NLS-1$
		
		try {
			Object result = method.invoke(getService(), args);
			if (MODEL_CHANGING_METHODS.contains(method.getName())) {
				LOG.debug("Broadcasting model changes to registered listeners"); //$NON-NLS-1$
				for (ModelChangeListener listener : modelChangeListeners) {
					listener.modelChanged();
				}
			}
				
			return result;
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof AccountingException) {
				throw (AccountingException) e.getTargetException();
			}
			LOG.error("Unknown error during processing of service request", e.getTargetException()); //$NON-NLS-1$
			throw new AccountingException(Messages.labelUnknownError, e.getTargetException());
		} catch (Throwable t) {
			LOG.error("Error while calling service", t); //$NON-NLS-1$
			throw new AccountingException(Messages.labelUnknownError, t);
		}
	}

	/**
     * @return the accountingContext
     */
    protected AccountingContext getAccountingContext() {
    	return accountingContext;
    }

	/**
     * @param accountingContext the accountingContext to set
     */
    protected void setAccountingContext(AccountingContext accountingContext) {
    	this.accountingContext = accountingContext;
    }

	/**
	 * 
	 * @param listener
	 */
	protected void addModelChangeListener(ModelChangeListener listener) {
		modelChangeListeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	protected void removeModelChangeListener(ModelChangeListener listener) {
		modelChangeListeners.remove(listener);
	}
	
	/**
	 * 
	 * @return
	 */
	private AccountingService getService() {
		AccountingService service = accountingServiceTracker.getService();
		
		if (service == null) {
			throw new AccountingException("AccountingService is not available at this time");
		} else if (accountingContext != null) {
			service.init(accountingContext);
		}
		
		return service;
	}
}
