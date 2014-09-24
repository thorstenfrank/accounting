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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;

import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;

/**
 * Opens the selected invoice in its own editor.
 * @author thorsten
 *
 */
public class EditInvoiceHandler extends AbstractInvoiceHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(EditInvoiceHandler.class);
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.invoice.AbstractInvoiceHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
		Invoice invoice = getInvoiceToEdit(event);
		if (invoice == null) {
			LOG.warn("No invoice to edit!"); //$NON-NLS-1$
			return;
		}
		getLogger().debug("Opening invoice " + invoice.getNumber()); //$NON-NLS-1$
		InvoiceEditorInput input = new InvoiceEditorInput(invoice);
		try {
			getActivePage(event).openEditor(input, IDs.EDIT_INVOIDCE_ID);
			
			if (!invoice.canBeEdited()) {
				MessageDialog.openInformation(
						getShell(event), 
						Messages.EditInvoiceCommand_uneditableText, 
						Messages.EditInvoiceCommand_uneditableMessage);				
			}
		} catch (PartInitException e) {
			getLogger().error("Error opening editor for invoice " + invoice.getNumber(), e); //$NON-NLS-1$
			throw new ExecutionException(Messages.bind(Messages.EditInvoiceCommand_errorOpeningEditor, invoice), e);
		}
    }
	
	/**
	 * 
	 * @param event
	 * @return
	 */
	protected Invoice getInvoiceToEdit(ExecutionEvent event) {
		return getInvoiceFromSelection(event);
	}
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}