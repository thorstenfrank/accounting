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
package de.tfsw.accounting.ui.invoice;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.ui.AbstractAccountingHandler;
import de.tfsw.accounting.ui.reports.ReportGenerationHandler;
import de.tfsw.accounting.ui.reports.ReportGenerationUtil;

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
    	currentInvoice = doGetInvoice(event);
    	ReportGenerationUtil.executeReportGeneration(this, getShell(event));
    }
    
	/**
	 * 
	 * @param event
	 * @return
	 */
	protected Invoice doGetInvoice(ExecutionEvent event) {
		return getInvoiceFromSelection(event);
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
     * {@inheritDoc}.
     * @see de.tfsw.accounting.ui.reports.ReportGenerationHandler#getModelObject()
     */
    @Override
    public Object getModelObject() {
	    return currentInvoice;
    }

	/**
     * {@inheritDoc}.
     * @see de.tfsw.accounting.ui.reports.ReportGenerationHandler#getReportId()
     */
    @Override
    public String getReportId() {
	    return "de.tfsw.accounting.reporting.Invoice";
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