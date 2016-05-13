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

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.db4o.osgi.Db4oService;

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
	private static final Logger LOG = LogManager.getLogger(AccountingCore.class);
	
	/** Bundle context. */
	private static BundleContext context;
	
	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		LOG.info("START CORE: " + bundleContext.getBundle().getSymbolicName()); //$NON-NLS-1$
		
		LOG.debug("Install Location: " + Platform.getInstallLocation().getURL().toString());
		LOG.debug("Config Location: " + Platform.getConfigurationLocation().getURL().toString());
		LOG.debug("Instance Location: " + Platform.getInstanceLocation().getURL().toString() );
		LOG.debug("User Location: " + Platform.getUserLocation().getURL().toString() );
		
		LOG.warn("**************HEPPES: " + System.getProperty("accounting.log.path"));
		
		// for debug purposes...
		if (System.getProperty("accounting.dump.env") != null) {
			dumpEnvironment();
		}
		
		AccountingCore.context = bundleContext;
		makeSureDb4oGetsStarted();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		LOG.info("STOP CORE: " + bundleContext.getBundle().getSymbolicName()); //$NON-NLS-1$
		AccountingCore.context = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static AccountingService getAccountingService() {
		ServiceReference<AccountingService> ref = context.getServiceReference(AccountingService.class);
		return context.getService(ref);
	}
	
	/**
	 * This needs to be done in order for the Db4o plugin to be started, otherwise the service never gets
	 * registered.
	 */
	private void makeSureDb4oGetsStarted() {
		ServiceReference<Db4oService> reference = context.getServiceReference(Db4oService.class);
		if (reference == null) {
			LOG.error("Cannot find DB4o service reference!"); //$NON-NLS-1$
			throw new AccountingException("Cannot find Db4oService, cannot start AccountingCore!");
		}
	}
	
	/**
	 * Simple blurts out all system properties.
	 */
	private void dumpEnvironment() {
		StringBuilder sb = new StringBuilder("System Properties:\n");
		Properties sysProps = System.getProperties();
		for (Object key : sysProps.keySet()) {
			sb.append(key).append("=").append(sysProps.get(key)).append("\n");
		}
		LOG.debug(sb.toString());
	}
}
