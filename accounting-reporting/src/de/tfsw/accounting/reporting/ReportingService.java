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
package de.tfsw.accounting.reporting;

import java.util.Map;

import de.tfsw.accounting.AccountingException;

/**
 * Service interface and single point of entry into the reporting subsystem.
 * 
 * <p>
 * The plugin will search reports defined through the extension point 
 * <code>de.tfsw.accounting.reporting.reports</code> and make them available via {@link #getAvailableReports()}. 
 * Actual document generation is available both with or without a monitor callback interface to track progress.
 * </p>
 * 
 * @author thorsten
 *
 */
public interface ReportingService {
	
	/**
	 * Returns an ID to localized name map of reports available during startup of this plugin.
	 * 
	 * <p>
	 * The keys of this map represent the respective ID of each report, which must be used when requesting generation
	 * of a document through the {@link #generateReport(String, Object, String)} or 
	 * {@link #generateReport(String, Object, String, ReportGenerationMonitor)} methods.
	 * </p>
	 * 
	 * <p>
	 * Reports are defined via the plugin extension mechanism using the extension id 
	 * <code>de.tfsw.accounting.reporting.reports</code>.
	 * </p>
	 * 
	 * @return	a map of available reports, where the key is the ID and the value the localized name of the report
	 */
	Map<String, String> getAvailableReports();
	
	/**
	 * Generates the requested report from the supplied model data to the specified file location without status updates.
	 * 
	 * <p>
	 * If you want to be notified of the various stages of the document generation steps, use 
	 * {@link #generateReport(String, Object, String, ReportGenerationMonitor)}.
	 * </p>
	 * 
	 * @param reportId		id of the report as defined in the plugin extension
	 * @param model			object from which data is sourced into the report 
	 * @param fileLocation	pathname of the target file
	 * @throws AccountingException if the supplied model class does not match the class defined by the report in 
	 * 							   the plugin extension 
	 */
	void generateReport(String reportId, Object model, String fileLocation);
	
	/**
	 * Generates the requested report from the supplied model data to the specified file location without status updates. 
	 * 
	 * @param reportId		id of the report as defined in the plugin extension
	 * @param model			object from which data is sourced into the report 
	 * @param fileLocation	pathname of the target file
	 * @param monitor		callback interface for status updates
	 * 
	 * @throws AccountingException if the supplied model class does not match the class defined by the report in 
	 * 							   the plugin extension
	 */
	void generateReport(String reportId, Object model, String fileLocation, ReportGenerationMonitor monitor);
}
