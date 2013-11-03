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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.togginho.accounting.reporting.internal.ReportingServiceImpl;
import de.togginho.accounting.reporting.xml.generated.AccountingReports;

/**
 * Bundle Activator for the reporting plugin/bundle.
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
		
		ReportingServiceImpl service = new ReportingServiceImpl();
		
		try {
	        service.setAvailableReports(readConfig());
        } catch (Exception e) {
        	LOG.error("Error reading XML report configuration", e); //$NON-NLS-1$
        	throw e;
        }
		
		LOG.info("Registering service implementation"); //$NON-NLS-1$
		
		context.registerService(
				ReportingService.class, 
				service, 
				new Hashtable<String, String>());
		
		LOG.info("Reporting Plugin startup complete"); //$NON-NLS-1$
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
    private AccountingReports readConfig() throws IOException, JAXBException {
		URL url = getContext().getBundle().getEntry("/config/reporting_config.xml");
		LOG.debug("Looking for URL: " + url.toString());
		InputStream inputStream = url.openStream();
		LOG.debug("Opened input stream, now creating JAXB context"); //$NON-NLS-1$
		
		try {
			Unmarshaller unmarshaller = JAXBContext.newInstance("de.togginho.accounting.reporting.xml.generated").createUnmarshaller();
			LOG.debug("Unmarshalling configuration from XML"); //$NON-NLS-1$
			
			JAXBElement<AccountingReports> result = (JAXBElement<AccountingReports>) unmarshaller.unmarshal(inputStream);
			LOG.debug("Done reading XML config");//$NON-NLS-1$
			return result.getValue();			
		} finally {
			inputStream.close();
		}

	}
	
	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		LOG.info("STOP REPORTING: " + bundleContext.getBundle().getSymbolicName());
		ReportingPlugin.context = null;
	}
}