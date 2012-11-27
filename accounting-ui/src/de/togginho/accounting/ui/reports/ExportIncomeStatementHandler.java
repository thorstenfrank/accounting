/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.togginho.accounting.ReportGenerationMonitor;
import de.togginho.accounting.ReportingService;
import de.togginho.accounting.ui.AbstractAccountingHandler;

/**
 * @author thorsten
 *
 */
public class ExportIncomeStatementHandler extends AbstractAccountingHandler {

	private static final Logger LOG = Logger.getLogger(ExportIncomeStatementHandler.class);
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		ReportGenerationUtil.executeReportGeneration(new ReportGenerationHandler() {
			
			@Override
			public void handleReportGeneration(ReportingService reportingService, String targetFileName,
			        ReportGenerationMonitor monitor) {
				reportingService.generateIncomeStatementToPdf(targetFileName, monitor);
				
			}
			
			@Override
			public String getTargetFileNameSuggestion() {
				return "IncomeStatement.pdf";
			}
		}, getShell(event));
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
