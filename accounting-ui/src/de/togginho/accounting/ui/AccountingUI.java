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
	
	private static final String KEY_USER_NAME = "accounting.user.name"; //$NON-NLS-1$
	
	private static final String KEY_USER_DB_FILE = "accounting.db.file"; //$NON-NLS-1$

	
	/** The shared instance. */
	private static AccountingUI plugin;
	
	/** Application context. */
	private AccountingContext accountingContext;
	
	/** */
	private AccountingServiceInvocationHandler accountingServiceProxy;
	
	private ServiceTracker<ReportingService, ReportingService> reportingServiceTracker;
	
	/** */
	private boolean firstRun = false;
	
	
	/**
	 * The constructor
	 */
	public AccountingUI() {
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		LOG.debug("START UI"); //$NON-NLS-1$
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
		LOG.debug("STOP UI"); //$NON-NLS-1$
		
		saveContext();
		
		plugin = null;
		super.stop(context);
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
		
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getBundle().getSymbolicName());
		
		if (prefs != null) {
			final String userName = prefs.get(KEY_USER_NAME, null);
			final String dbFileLocation = prefs.get(KEY_USER_DB_FILE, null);
			
			initContext(userName, dbFileLocation);
		} else {
			LOG.warn("No preferences to be found in instance scope with name " + getBundle().getSymbolicName()); //$NON-NLS-1$
		}
		
		return (accountingContext != null);
	}
	
	/**
	 * 
	 * @param xmlFile
	 * @param dbFileLocation
	 */
	protected void initContextFromImport(final String xmlFile, final String dbFileLocation) {
		//accountingContext = accountingService.importModelFromXml(xmlFile, dbFileLocation);
		
		//ModelHelper.init(accountingContext, accountingService);
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

		// build the context
		accountingContext = AccountingContextFactory.buildContext(userName, dbFileLocation);
		
		// immediately init the AccountingService
		LOG.debug("Initialising accounting service..."); //$NON-NLS-1$
		
		accountingServiceProxy.setAccountingContext(accountingContext);
	}
	
	/**
	 * Saves the application context to preferences.
	 */
	private void saveContext() {
		if (accountingContext == null) {
			LOG.warn("No accounting context present, no user preferences to save..."); //$NON-NLS-1$
			return;
		} else {
			LOG.info("Saving context to preferences..."); //$NON-NLS-1$
		}
		
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getBundle().getSymbolicName());
		if (prefs == null) {
			LOG.warn("Preferences not found: " + getBundle().getSymbolicName()); //$NON-NLS-1$
			return;
		}
		
		prefs.put(KEY_USER_NAME, accountingContext.getUserName());
		prefs.put(KEY_USER_DB_FILE, accountingContext.getDbFileName());
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			LOG.error("Error saving context!", e); //$NON-NLS-1$
		}
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
}
