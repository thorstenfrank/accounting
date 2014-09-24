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
package de.tfsw.accounting;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.db4o.osgi.Db4oService;

import de.tfsw.accounting.service.AccountingServiceImpl;

/**
 * The activator for the <code>accounting-core</code> plugin.
 * 
 * <p>When started, this plugin will register an instance of the {@link AccountingService}.</p>
 * 
 * @author thorsten frank
 * @see AccountingService
 */
public class AccountingCore implements BundleActivator {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "de.tfsw.accounting.core"; //$NON-NLS-1$
	
	/** Logger. */
	private static final Logger LOG = Logger.getLogger(AccountingCore.class);
	
	/** Bundle context. */
	private static BundleContext context;

	private static AccountingServiceImpl accountingServiceImpl;
	
	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		LOG.info("START CORE: " + bundleContext.getBundle().getSymbolicName()); //$NON-NLS-1$
		AccountingCore.context = bundleContext;
		
		ServiceReference<Db4oService> reference = context.getServiceReference(Db4oService.class);
		if (reference == null) {
			LOG.error("Cannot find DB4o service!"); //$NON-NLS-1$
			// FIXME the bundle cannot properly start without the persistence service, so don't swallow this silently!
		} else {
			Db4oService service = context.getService(reference);
			
			accountingServiceImpl = new AccountingServiceImpl(service);
						
			LOG.info("Now registering new AccountingService implementation for DB4o service");
			context.registerService(
					AccountingService.class.getName(), 
					accountingServiceImpl, 
					new Hashtable<String, String>());
		}
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		LOG.info("STOP CORE: " + bundleContext.getBundle().getSymbolicName()); //$NON-NLS-1$
		if (accountingServiceImpl != null) {
			accountingServiceImpl.shutDown();
		}
		AccountingCore.context = null;
	}

	/**
	 * 
	 * @return
	 */
	public static AccountingService getAccountingService() {
		return accountingServiceImpl;
	}
}
