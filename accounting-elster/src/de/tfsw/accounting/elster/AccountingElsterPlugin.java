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
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.elster.adapter.ElsterAdapterFactory;
import de.tfsw.accounting.elster.adapter.ServiceProvider;

/**
 * Plugin activator for the accounting ELSTER plugin (German revenue service interface, specifically the VAT
 * reporting subsystem).
 */
public class AccountingElsterPlugin extends AbstractUIPlugin implements ServiceProvider {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(AccountingElsterPlugin.class);
	
	/** The shared instance */
	private static AccountingElsterPlugin plugin;
	
	/**
	 * 
	 */
	private ElsterAdapterFactory elsterAdapterFactory;
	
	/**
	 * 
	 */
	private AccountingService accountingService;
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AccountingElsterPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		LOG.info("Starting ELSTER plugin"); //$NON-NLS-1$
		super.start(context);
		plugin = this;
		
		this.elsterAdapterFactory = new ElsterAdapterFactory(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		LOG.info("Stopping ELSTER plugin"); //$NON-NLS-1$
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * @return the elsterAdapterFactory
	 */
	public ElsterAdapterFactory getElsterAdapterFactory() {
		return elsterAdapterFactory;
	}

	/**
	 * 
	 * @see de.tfsw.accounting.elster.adapter.ServiceProvider#getAccountingService()
	 */
	@Override
	public AccountingService getAccountingService() {
		return accountingService;
	}

	/**
	 * @see de.tfsw.accounting.elster.adapter.ServiceProvider#getExtensionRegistry()
	 */
	@Override
	public IExtensionRegistry getExtensionRegistry() {
		return Platform.getExtensionRegistry();
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(IDs.PLUGIN_ID, path);
	}
	
	/**
	 * Allows the {@link AccountingServiceConsumer} to register an {@link AccountingService} instance received through
	 * the OSGi declarative services.
	 * 
	 * @param accountingService the service instance of register
	 */
	protected void registerAccountingService(AccountingService accountingService) {
		LOG.debug("AccountingService has registered"); //$NON-NLS-1$
		this.accountingService = accountingService;
	}
	
	/**
	 * Allows the {@link AccountingServiceConsumer} to unregister a previously registered {@link AccountingService}
	 * instance.
	 * <p>
	 * After calling this method with a known instance, {@link #getAccountingService()} will return <code>null</code>
	 * until a new instance is registered. If the concrete service instance is unkonwn, this method does nothing.
	 * </p>
	 * 
	 * @param accountingService the service instance to unregister
	 */
	protected void unregisterAccountingService(AccountingService accountingService) {
		if (this.accountingService == accountingService) {
			LOG.debug("AccountingService is unregistering..."); //$NON-NLS-1$
			this.accountingService = null;
		} else {
			LOG.debug("Unknown AccountingService is unregistering - will ignore"); //$NON-NLS-1$
		}
	}	
}
