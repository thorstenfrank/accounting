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

/**
 * An interface that provides detailed callback to reporting clients during generation of reports.
 * 
 * @author thorsten
 *
 */
public interface ReportGenerationMonitor {

	/**
	 * Step 1 - initial setup of the report generator.
	 */
	void startingReportGeneration();
	
	/**
	 * Step 2 - the report template is loaded into memory.
	 */
	void loadingTemplate();
	
	/**
	 * Step 3 - parameters such as internationalised labels are added to the report.
	 */
	void addingReportParameters();
	
	/**
	 * Step 4 - the report is filled with the actual source data.
	 */
	void fillingReport();
	
	/**
	 * Step 5 - export of the report into its target format and file starts.
	 */
	void exportingReport();
	
	/**
	 * Step 6 - exporting has finished successfully.
	 */
	void exportFinished();
}
