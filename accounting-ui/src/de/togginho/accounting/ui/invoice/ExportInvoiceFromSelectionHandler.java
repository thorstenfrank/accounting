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
package de.togginho.accounting.ui.invoice;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.togginho.accounting.ReportGenerationMonitor;
import de.togginho.accounting.ReportingService;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.reports.ReportGenerationHandler;
import de.togginho.accounting.ui.reports.ReportGenerationUtil;

/**
 * @author tfrank1
 *
 */
public class ExportInvoiceFromSelectionHandler extends AbstractInvoiceHandler implements ReportGenerationHandler {

	/** */
	private static final Logger LOG = Logger.getLogger(ExportInvoiceFromSelectionHandler.class);	
	
	private Invoice currentInvoice;
	
	/**
     * {@inheritDoc}.
     * @see AbstractAccountingHandler#doExecute(ExecutionEvent)
     */
    @Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
    	currentInvoice = getInvoiceFromSelection(event);
    	ReportGenerationUtil.executeReportGeneration(this, getShell(event));
    }
        
	/**
     * {@inheritDoc}.
     * @see ReportGenerationHandler#getTargetFileNameSuggestion()
     */
    @Override
    public String getTargetFileNameSuggestion() {
	    return currentInvoice.getNumber();
    }

    /**
     * 
     * {@inheritDoc}.
     * @see ReportGenerationHandler#handleReportGeneration(ReportingService, String, ReportGenerationMonitor)
     */
    @Override
    public void handleReportGeneration(ReportingService reportingService, String targetFileName, ReportGenerationMonitor monitor) {
    	reportingService.generateInvoiceToPdf(currentInvoice, targetFileName, monitor);
    }

	/**
	 * {@inheritDoc}.
	 * @see AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}