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

import java.io.File;
import java.lang.reflect.Proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingService;

/**
 * The activator class controls the plug-in life cycle
 */
public class AccountingUI extends AbstractUIPlugin {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "de.tfsw.accounting.ui"; //$NON-NLS-1$

	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(AccountingUI.class);
		
	/** The shared instance. */
	private static AccountingUI plugin;
		
	/** */
	private AccountingServiceInvocationHandler accountingServiceProxy;
	
	/** */
	private boolean firstRun = false;
	
	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		LOG.debug("START UI: " + context.getBundle().getSymbolicName()); //$NON-NLS-1$
		super.start(context);
		plugin = this;
		
		LOG.info("Creating service trackers"); //$NON-NLS-1$
		
		ServiceTracker<AccountingService, AccountingService> accountingServiceTracker = 
				new ServiceTracker<AccountingService, AccountingService>(context, AccountingService.class, null);
		LOG.debug("Opening service tracker for AccountingService"); //$NON-NLS-1$
		accountingServiceTracker.open();
		LOG.debug("Done"); //$NON-NLS-1$
		
		accountingServiceProxy = new AccountingServiceInvocationHandler(accountingServiceTracker);
		
		LOG.info("AccountingUI startup complete"); //$NON-NLS-1$
	}

	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		LOG.debug("STOP UI: " + context.getBundle().getSymbolicName()); //$NON-NLS-1$
		
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AccountingUI getDefault() {
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
	public static String buildDefaultDbFileLocation() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.home")); //$NON-NLS-1$
		sb.append(File.separatorChar);
		sb.append("accounting.data"); //$NON-NLS-1$
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public static AccountingService getAccountingService() {
		final Class<AccountingService> serviceClazz = AccountingService.class;
		
		return (AccountingService) Proxy.newProxyInstance(
				serviceClazz.getClassLoader(), 
				new Class[]{serviceClazz}, 
				plugin.accountingServiceProxy);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public static void addModelChangeListener(ModelChangeListener listener) {
		plugin.accountingServiceProxy.addModelChangeListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public static void removeModelChangeListener(ModelChangeListener listener) {
		plugin.accountingServiceProxy.removeModelChangeListener(listener);
	}

	/**
	 * @return the firstRun
	 */
	public boolean isFirstRun() {
		return firstRun;
	}

	/**
	 * @param firstRun the firstRun to set
	 */
	protected void setFirstRun(boolean firstRun) {
		this.firstRun = firstRun;
	}
	
	/**
	 * 
	 * @param context
	 */
	protected void initServiceWithContext(AccountingContext context) {
		accountingServiceProxy.setAccountingContext(context);
	}
	
	/**
	 * 
	 * @return
	 */
	protected boolean isServiceContextInitialised() {
		return accountingServiceProxy.getAccountingContext() != null;
	}
}
