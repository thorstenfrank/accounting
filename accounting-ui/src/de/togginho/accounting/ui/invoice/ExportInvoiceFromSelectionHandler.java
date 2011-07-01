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

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.togginho.accounting.ReportGenerationMonitor;
import de.togginho.accounting.ReportingService;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;

/**
 * @author tfrank1
 *
 */
public class ExportInvoiceFromSelectionHandler extends AbstractInvoiceHandler {

	/** */
	private static final Logger LOG = Logger.getLogger(ExportInvoiceFromSelectionHandler.class);	
	
	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
		ReportingService reportingService = AccountingUI.getDefault().getReportingService();
		Invoice invoice = getInvoiceFromSelection(event);

		if (reportingService != null) {
			Shell shell = getShell(event);
			FileDialog fd = new FileDialog(shell, SWT.SAVE);
			fd.setFileName(invoice.getNumber());
			fd.setFilterExtensions(new String[]{"*.pdf"}); //$NON-NLS-1$
			fd.setFilterNames(new String[]{Messages.InvoiceToPdfCommand_labelPdfFiles});
			
			String selected = fd.open();
			LOG.debug("Target file: " + selected); //$NON-NLS-1$
			
			if (selected != null) {
				try {
					// TODO progress monitor dialog...
					LOG.info("Starting PDF generation to file " + selected); //$NON-NLS-1$
					ReportGeneration generation = new ReportGeneration(reportingService, invoice, selected);
					new ProgressMonitorDialog(getShell(event)).run(true, false, generation);
				} catch (Exception e) {
					LOG.error("Error creating PDF", e); //$NON-NLS-1$
					throw new ExecutionException(Messages.InvoiceToPdfCommand_errorGeneratingInvoice, e);
				}
				
				// show some success message.
				MessageBox msgBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				msgBox.setMessage(Messages.bind(Messages.InvoiceToPdfCommand_successMsg, selected));
				msgBox.setText(Messages.InvoiceToPdfCommand_successText);
				msgBox.open();
			}
		} else {
			LOG.error("No reporting service!"); //$NON-NLS-1$
			throw new ExecutionException(Messages.InvoiceToPdfCommand_errorNoReportingService);
		}
    }
    
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
	
	/**
	 * A runnable that tracks report generation progress.
	 */
	private class ReportGeneration implements IRunnableWithProgress, ReportGenerationMonitor {

		private IProgressMonitor monitor;
		private ReportingService service;
		private Invoice invoice;
		private String fileName;
				
		/**
         * @param service
         * @param invoice
         * @param fileName
         */
        private ReportGeneration(ReportingService service, Invoice invoice, String fileName) {
	        this.service = service;
	        this.invoice = invoice;
	        this.fileName = fileName;
        }

		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        	this.monitor = monitor;
        	monitor.worked(1);
        	
        	service.generateInvoiceToPdf(invoice, fileName, this);
        	
        	monitor.done();
        }

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ReportGenerationMonitor#startingReportGeneration()
         */
        @Override
        public void startingReportGeneration() {
        	monitor.beginTask(Messages.ExportInvoiceCommand_startingReportGeneration, 5);
        }

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ReportGenerationMonitor#loadingTemplate()
         */
        @Override
        public void loadingTemplate() {
	        monitor.subTask(Messages.ExportInvoiceCommand_loadingTemplate);
	        monitor.worked(1);
        }

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ReportGenerationMonitor#addingReportParameters()
         */
        @Override
        public void addingReportParameters() {
	        monitor.subTask(Messages.ExportInvoiceCommand_addingReportParameters);
	        monitor.worked(1);
        }

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ReportGenerationMonitor#fillingReport()
         */
        @Override
        public void fillingReport() {
        	monitor.subTask(Messages.ExportInvoiceCommand_fillingReport);
        	monitor.worked(1);
        }

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ReportGenerationMonitor#exportingReport()
         */
        @Override
        public void exportingReport() {
	        monitor.subTask(Messages.ExportInvoiceCommand_exportingReportToFile);
	        monitor.worked(1);
        }
		
        
	}
}