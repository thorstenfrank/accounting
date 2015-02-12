/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.elster;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.tfsw.accounting.AccountingService;

/**
 * Plugin activator for the accounting ELSTER plugin.
 * (German revenue service interface).
 */
public class AccountingElsterPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.tfsw.accounting.elster"; //$NON-NLS-1$

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(AccountingElsterPlugin.class);
	
	/** The shared instance */
	private static AccountingElsterPlugin plugin;

	/** Service Tracker for the {@link AccountingService}. */
	private ServiceTracker<AccountingService, AccountingService> accountingServiceTracker;
	
	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		LOG.info("Starting ELSTER plugin");
		super.start(context);
		plugin = this;
		
		LOG.debug("Opening service tracker for accounting service...");
		accountingServiceTracker = 
				new ServiceTracker<AccountingService, AccountingService>(context, AccountingService.class, null);
		accountingServiceTracker.open();
		LOG.debug("Done");
	}

	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		LOG.info("Stopping ELSTER plugin");
		
		LOG.debug("Closing service tracker");
		accountingServiceTracker.close();
		LOG.debug("Done");
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AccountingElsterPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * 
	 * @return
	 */
	public static AccountingService getAccountingService() {
		AccountingService as = plugin.accountingServiceTracker.getService();
		return as;//plugin.accountingServiceTracker.getService();
	}
}
