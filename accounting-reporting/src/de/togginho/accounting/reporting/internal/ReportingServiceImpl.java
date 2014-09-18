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
package de.togginho.accounting.reporting.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.togginho.accounting.reporting.ReportGenerationMonitor;
import de.togginho.accounting.reporting.ReportingService;

/**
 * @author thorsten
 *
 */
public class ReportingServiceImpl implements ReportingService {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(ReportingServiceImpl.class);

	private static final String REPORT_EXTENSION_ID = "de.togginho.accounting.reporting.reports"; //$NON-NLS-1$
	
	private Map<String, String> availableReports;
	private Map<String, IConfigurationElement> internalReportsMap;
		
	/**
     * 
     */
    public ReportingServiceImpl() {
    	LOG.info("Initialising service implementation. Now looking for available reports"); //$NON-NLS-1$
    	
    	IConfigurationElement[] elements = 
    			Platform.getExtensionRegistry().getConfigurationElementsFor(REPORT_EXTENSION_ID);
    	
    	LOG.info(String.format("Found %d reports", elements.length)); //$NON-NLS-1$
    	
    	availableReports = new HashMap<String, String>(elements.length);
    	internalReportsMap = new HashMap<String, IConfigurationElement>(elements.length);
    	
    	for (IConfigurationElement ce : elements) {
    		String id = ce.getAttribute("id");
    		String name = ce.getAttribute("name");
    		String template = ce.getAttribute("template");
    		LOG.debug(String.format("Found extension ID [%s], Name: [%s], Template: [%s]", id, name, template));
    		availableReports.put(id, name);
    		internalReportsMap.put(id, ce);
    	}
    }

	/**
     * @return the availableReports
     */
    public Map<String, String> getAvailableReports() {
    	return availableReports;
    }
    
	/**
     * {@inheritDoc}.
     * @see ReportingService#generateReport(String, Object, String, ReportGenerationMonitor)
     */
    @Override
    public void generateReport(String reportId, Object model, String fileLocation, ReportGenerationMonitor monitor) {
    	
	    JasperReportGenerator generator = new JasperReportGenerator(internalReportsMap.get(reportId).getAttribute("template"), model);
	    generator.generateReport(fileLocation, monitor);
    }

	/**
	 * @see ReportingService#generateReport(String, Object, String)
	 */
	@Override
	public void generateReport(String reportId, Object model, String fileLocation) {
		generateReport(reportId, model, fileLocation, new ReportGenerationMonitor() {
			
			@Override
			public void startingReportGeneration() {
			}
			
			@Override
			public void loadingTemplate() {
			}
			
			@Override
			public void fillingReport() {
			}
			
			@Override
			public void exportingReport() {
			}
			
			@Override
			public void exportFinished() {
			}
			
			@Override
			public void addingReportParameters() {
			}
		});
	}
    
    
}
