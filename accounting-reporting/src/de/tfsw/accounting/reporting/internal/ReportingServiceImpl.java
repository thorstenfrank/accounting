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
package de.tfsw.accounting.reporting.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.tfsw.accounting.reporting.ReportGenerationMonitor;
import de.tfsw.accounting.reporting.ReportingService;

/**
 * @author thorsten
 *
 */
public class ReportingServiceImpl implements ReportingService {

	/**
	 * 
	 */
	private static final Logger LOG = LogManager.getLogger(ReportingServiceImpl.class);

	private static final String REPORT_EXTENSION_ID = "de.tfsw.accounting.reporting.reports"; //$NON-NLS-1$
	
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
				LOG.debug(String.format("Starting Report Generation: [%s] to file [%s]", reportId, fileLocation));
			}
			
			@Override
			public void loadingTemplate() {
				LOG.debug("Loading template: " + reportId);
			}
			
			@Override
			public void fillingReport() {
				LOG.debug("Filling report: " + reportId);
			}
			
			@Override
			public void exportingReport() {
				LOG.debug("Exporting report: " + reportId);
			}
			
			@Override
			public void exportFinished() {
				LOG.debug("Finished export: " + reportId);
			}
			
			@Override
			public void addingReportParameters() {
				LOG.debug("Adding report params: " + reportId);
			}
		});
	}
    
    
}
