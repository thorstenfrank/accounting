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
	
	/**
	 * 
	 */
	private AccountingServiceConsumer accountingServiceConsumer;
	
	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		LOG.info("Starting ELSTER plugin");
		super.start(context);
		plugin = this;
	}

	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		LOG.info("Stopping ELSTER plugin");
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
	 * @param accountingService
	 */
	protected static void registerAccountingServiceConsumer(AccountingServiceConsumer consumer) {
		LOG.debug("AccountingServiceConsumer has registered");
		plugin.accountingServiceConsumer = consumer;
	}
	
	/**
	 * 
	 * @param consumer
	 */
	protected static void unregisterAccountingServiceConsumer(AccountingServiceConsumer consumer) {
		if (plugin.accountingServiceConsumer == consumer) {
			LOG.debug("AccountingServiceConsumer is unregistering...");
			plugin.accountingServiceConsumer = null;
		} else {
			LOG.debug("Unknown AccountingServiceConsumer is unregistering - will ignore");
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static AccountingService getAccountingService() {
		return plugin.accountingServiceConsumer.getAccountingService();
	}
}
