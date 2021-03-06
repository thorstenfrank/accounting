/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.util.tracker.ServiceTracker;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.ModelChanging;

/**
 * @author tfrank1
 *
 */
final class AccountingServiceInvocationHandler implements InvocationHandler {

	private static final Logger LOG = LogManager.getLogger(AccountingServiceInvocationHandler.class);
	
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
			ModelChanging modelChanging = method.getAnnotation(ModelChanging.class);
			if (modelChanging != null) {
				LOG.debug("Broadcasting model changes to registered listeners"); //$NON-NLS-1$
				for (ModelChangeListener listener : modelChangeListeners) {
					listener.modelChanged();
				}
			}
			
			if (result instanceof AccountingContext) {
				LOG.info("New context was read by service, will re-init now...");
				setAccountingContext((AccountingContext) result);
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
    	if (this.accountingContext != null) {
    		LOG.warn("Overwriting accounting context"); //$NON-NLS-1$
    	}
    	
    	this.accountingContext = accountingContext;
    	
    	LOG.info("Calling init on AccountingService"); //$NON-NLS-1$
    	
    	// try initialising the service with the new context
    	getService();
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
