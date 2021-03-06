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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;

/**
 * @author tfrank1
 *
 */
public class FindInvoiceHandler extends EditInvoiceHandler {

	/** */
	private static final Logger LOG = LogManager.getLogger(FindInvoiceHandler.class);
	
	@Override
	protected Invoice getInvoiceToEdit(ExecutionEvent event) {
		InputDialog inputDialog = new InputDialog(
				getShell(event), 
				Messages.FindInvoiceCommand_inputTitle, 
				Messages.FindInvoiceCommand_inputMessage, 
				Constants.EMPTY_STRING, 
				null);
		
		if (InputDialog.OK == inputDialog.open()) {
			final String invoiceNumber = inputDialog.getValue();
			LOG.debug("Searching for invoice: " + invoiceNumber); //$NON-NLS-1$
			final Invoice invoice = AccountingUI.getAccountingService().getInvoice(invoiceNumber);
			if (invoice == null) {
				LOG.debug("Invoice not found: " + invoiceNumber); //$NON-NLS-1$
				MessageDialog.openWarning(
						getShell(event), 
						Messages.FindInvoiceCommand_noresultTitle, 
						Messages.bind(Messages.FindInvoiceCommand_noresultMessage, invoiceNumber));
			}
			
			return invoice;
		}
		
		return null;
	}



	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
