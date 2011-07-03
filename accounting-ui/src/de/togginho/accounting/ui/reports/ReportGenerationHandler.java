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
package de.togginho.accounting.ui.reports;

import de.togginho.accounting.ReportGenerationMonitor;
import de.togginho.accounting.ReportingService;

/**
 * @author thorsten
 *
 */
public interface ReportGenerationHandler {

	/**
	 * 
	 * @return
	 */
	String getTargetFileNameSuggestion();
	
	/**
	 * 
	 * @param reportingService
	 * @param targetFileName
	 * @param monitor
	 */
	void handleReportGeneration(ReportingService reportingService, String targetFileName, ReportGenerationMonitor monitor);
}
