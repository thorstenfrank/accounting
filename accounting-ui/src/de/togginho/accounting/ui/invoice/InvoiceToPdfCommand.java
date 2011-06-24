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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.togginho.accounting.ReportingService;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;

/**
 * @author tfrank1
 *
 */
public class InvoiceToPdfCommand extends AbstractInvoiceCommand {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(InvoiceToPdfCommand.class);
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.invoice.AbstractInvoiceCommand#handleInvoice(de.togginho.accounting.model.Invoice, org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void handleInvoice(Invoice invoice, ExecutionEvent event) throws ExecutionException {
		ReportingService reportingService = AccountingUI.getDefault().getReportingService();
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
					reportingService.generateInvoiceToPdf(invoice, selected);
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
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}