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
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;

/**
 * Deletes the invoice currently being the active selection. This handler should be active when the {@link InvoiceView}
 * is the currently active part and only enabled if the selected invoice can be deleted ({@link Invoice#canBeDeleted()}).
 * 
 * @author tfrank1
 * @see DeleteInvoiceFromEditorHandler
 * @see de.togginho.accounting.AccountingService#deleteInvoice(Invoice)
 */
public class DeleteInvoiceFromSelectionHandler extends AbstractInvoiceHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(DeleteInvoiceFromSelectionHandler.class);
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
		Invoice invoice = getInvoiceFromSelection(event);
		if (showWarningMessage(
				event, 
				Messages.bind(Messages.DeleteInvoiceCommand_confirmMessage, invoice.getNumber()), 
				Messages.DeleteInvoiceCommand_confirmText,
				true)) {
			getLogger().info("Deleting invoice " + invoice.getNumber()); //$NON-NLS-1$
			
			// do the actual work
			AccountingUI.getAccountingService().deleteInvoice(invoice);
			
			// close any open editors for the deleted invoice
			closeOpenEditorForInvoice(invoice, event);
		} else {
			getLogger().info(String.format("Deleting invoice [%s] was cancelled by user", invoice.getNumber())); //$NON-NLS-1$
		}
    }
	
	/**
	 * 
	 * @param invoice
	 * @param event
	 */
	private void closeOpenEditorForInvoice(Invoice invoice, ExecutionEvent event) {
		getLogger().debug("Checking for open editors for invoice " + invoice.getNumber()); //$NON-NLS-1$
		IWorkbenchPage page = getActivePage(event);
		
		for (IEditorReference editorRef : page.findEditors(null, IDs.EDIT_INVOIDCE_ID, IWorkbenchPage.MATCH_ID)) {
			if (editorRef.getName().equals(invoice.getNumber())) {
				getLogger().debug("Closing editor for deleted invoice: " + editorRef.getName()); //$NON-NLS-1$
				page.closeEditor(editorRef.getEditor(false), false);
			}
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
