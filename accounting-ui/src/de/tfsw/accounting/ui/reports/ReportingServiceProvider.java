/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.reports;

import org.apache.log4j.Logger;

import de.tfsw.accounting.reporting.ReportingService;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class ReportingServiceProvider {

	private static final Logger LOG = Logger.getLogger(ReportingServiceProvider.class);
	
	private static ReportingServiceProvider instance;
	
	private ReportingService reportingService;
		
	/**
	 * 
	 */
	public ReportingServiceProvider() {
		LOG.debug("Provider created and is now the default instance");
		instance = this;
	}
	
	/**
	 * @return the reportingService
	 */
	public ReportingService getReportingService() {
		return reportingService;
	}
	
	/**
	 * OSGI DS
	 * @param reportingService
	 */
	public synchronized void setReportingService(ReportingService reportingService) {
		LOG.debug("reporting service instance was injected");
		this.reportingService = reportingService;
	}
	
	/**
	 * 
	 * @param reportingService
	 */
	public synchronized void unsetReportingService(ReportingService reportingService) {
		LOG.debug("Unsetting reporting service...");
		if (this.reportingService != null && this.reportingService == reportingService) {
			this.reportingService = null;
			LOG.debug("done");
		} else {
			LOG.debug("Unknown service instance, will skip...");
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static ReportingServiceProvider getReportingServiceProvider() {
		return instance;
	}
}
