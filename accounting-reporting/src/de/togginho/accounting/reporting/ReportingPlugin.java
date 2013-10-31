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
package de.togginho.accounting.reporting;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.togginho.accounting.reporting.internal.ReportingServiceImpl;

/**
 * 
 * @author thorsten
 *
 */
public class ReportingPlugin implements BundleActivator {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(ReportingPlugin.class);
	
	/**
	 * 
	 */
	private static BundleContext context;

	/**
	 * 
	 * @return
	 */
	static BundleContext getContext() {
		return context;
	}

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		LOG.info("START REPORTING " + bundleContext.getBundle().getSymbolicName()); //$NON-NLS-1$
		ReportingPlugin.context = bundleContext;

		// register the service
		LOG.info("Registering ReportingService"); //$NON-NLS-1$
		context.registerService(
				ReportingService.class, 
				new ReportingServiceImpl(), 
				new Hashtable<String, String>());
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		LOG.info("STOP REPORTING: " + bundleContext.getBundle().getSymbolicName());
		ReportingPlugin.context = null;
	}
}