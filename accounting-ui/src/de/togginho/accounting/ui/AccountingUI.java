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

import java.io.File;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.util.tracker.ServiceTracker;

import de.togginho.accounting.AccountingContext;
import de.togginho.accounting.AccountingContextFactory;
import de.togginho.accounting.AccountingService;
import de.togginho.accounting.ReportingService;

/**
 * The activator class controls the plug-in life cycle
 */
public class AccountingUI extends AbstractUIPlugin {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "de.togginho.accounting.ui"; //$NON-NLS-1$

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(AccountingUI.class);
	
	/** */
	private static final String KEY_USER_NAME = "accounting.user.name"; //$NON-NLS-1$
	
	/** */
	private static final String KEY_USER_DB_FILE = "accounting.db.file"; //$NON-NLS-1$
	
	/** The shared instance. */
	private static AccountingUI plugin;
		
	/** */
	private AccountingServiceInvocationHandler accountingServiceProxy;
	
	/** */
	private ServiceTracker<ReportingService, ReportingService> reportingServiceTracker;
	
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
		accountingServiceTracker.open();
		
		accountingServiceProxy = new AccountingServiceInvocationHandler(accountingServiceTracker);
		
		reportingServiceTracker = new ServiceTracker<ReportingService, ReportingService>(
				context, ReportingService.class, null);
		reportingServiceTracker.open();
	}

	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		LOG.debug("STOP UI: " + context.getBundle().getSymbolicName()); //$NON-NLS-1$
		
		saveContext();
		
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
		// TODO cache the proxy instance?
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
	 * @return the reportingService
	 */
	public ReportingService getReportingService() {
		return reportingServiceTracker.getService();
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
	 * Initialises the context from preferences and system properties.
	 * 
	 * @return <code>true</code> if the context was properly initialised, <code>false</code> if not
	 */
	protected boolean initContext() {
		// TODO throw an exception if the context is already initialised...
		
		LOG.info("Initialising AccountingContext"); //$NON-NLS-1$
				
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
		
		if (prefs != null) {
			final String userName = prefs.get(KEY_USER_NAME, null);
			final String dbFileLocation = prefs.get(KEY_USER_DB_FILE, null);
			
			initContext(userName, dbFileLocation);
		} else {
			LOG.warn("No preferences to be found in instance scope with name " + PLUGIN_ID); //$NON-NLS-1$
		}
		
		return (accountingServiceProxy.getAccountingContext() != null);
	}
	
	/**
	 * 
	 * @param xmlFile
	 * @param dbFileLocation
	 */
	protected void initContextFromImport(final String xmlFile, final String dbFileLocation) {
		accountingServiceProxy.setAccountingContext(
				getAccountingService().importModelFromXml(xmlFile, dbFileLocation));
	}
	
	/**
	 * Initialises the context from the supplied values. This method should be called only during the first run of
	 * the application!
	 * 
	 * @param userName
	 * @param dbFileLocation
	 */
	protected void initContext(final String userName, final String dbFileLocation) {
		if (userName == null || userName.isEmpty() || dbFileLocation == null || dbFileLocation.isEmpty()) {
			LOG.warn("None or insufficient preferences found, cannot build context"); //$NON-NLS-1$
			return;
		}
		
		// get the Locale that OSGi was started with
		//final String locale = getBundle().getBundleContext().getProperty("osgi.nl.user"); //$NON-NLS-1$
		
		// build the context
		LOG.debug(String.format(
				"Building accounting context for user [%s] with DB file [%s]",  //$NON-NLS-1$
				userName, dbFileLocation)); //$NON-NLS-1$
		
		// build the context and immediately init the AccountingService
		LOG.debug("Propagating context to AccountingService proxy..."); //$NON-NLS-1$
		accountingServiceProxy.setAccountingContext(AccountingContextFactory.buildContext(userName, dbFileLocation));
	}
	
	/**
	 * Saves the application context to preferences.
	 */
	private void saveContext() {
		final AccountingContext accCtx = accountingServiceProxy.getAccountingContext();
		if (accCtx == null) {
			LOG.warn("No accounting context present, no user preferences to save..."); //$NON-NLS-1$
			return;
		} else {
			LOG.info("Saving context to preferences..."); //$NON-NLS-1$
		}
		
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
		if (prefs == null) {
			LOG.warn("Preferences not found: " + PLUGIN_ID); //$NON-NLS-1$
			return;
		}
		
		prefs.put(KEY_USER_NAME, accCtx.getUserName());
		prefs.put(KEY_USER_DB_FILE, accCtx.getDbFileName());
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			LOG.error("Error saving context!", e); //$NON-NLS-1$
		}
	}
}
