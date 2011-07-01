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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.AbstractAccountingHandler;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;

/**
 * Abstract base class for all invoice-centric command handlers.
 * 
 * <p>The {@link #execute(ExecutionEvent)} method will try to get the currently selected {@link Invoice} from the
 * active workbench page's {@link ISelectionProvider}, and then call {@link #handleInvoice(Invoice)} for the actual
 * work to happen.</p>
 * 
 * @author thorsten
 *
 */
abstract class AbstractInvoiceHandler extends AbstractAccountingHandler {
	
	/**
	 * 
	 * @param event
	 * @return
	 */
	protected Invoice getInvoiceFromSelection(ExecutionEvent event) {
		ISelectionProvider selectionProvider = getActivePage(event).getActivePart().getSite().getSelectionProvider();
		
		Invoice invoice = null;
		if (selectionProvider != null && !selectionProvider.getSelection().isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
						
			if (structuredSelection.getFirstElement() instanceof Invoice) {
				invoice = (Invoice) structuredSelection.getFirstElement();			
			} else {
				getLogger().warn("Current selection is not of type Ivoice: "  //$NON-NLS-1$
						+ structuredSelection.getFirstElement().getClass());
			}
		} else {
			getLogger().warn("Current selection is empty, cannot run this command!"); //$NON-NLS-1$
		}
		
		// TODO refine this
		if (invoice == null) {
			throw new AccountingException("No invoice in current selection!");
		}
		
		return invoice;
	}
    
	/**
	 * 
	 * @param event
	 * @return
	 */
	protected Invoice getInvoiceFromEditor(ExecutionEvent event) {
		Invoice invoice = null;
		IWorkbenchPage activePage = getActivePage(event);
		
		if (activePage.getActiveEditor().getEditorSite().getId().equals(IDs.EDIT_INVOIDCE_ID)) {
			getLogger().debug("Active editor is invoice editor, will handle this invoice"); //$NON-NLS-1$
			
			InvoiceEditorInput input = (InvoiceEditorInput) activePage.getActiveEditor().getEditorInput();
			invoice = input.getInvoice();
		} else {
			getLogger().warn("No invoice in active editor - cannot run this command!");  //$NON-NLS-1$
		}
		
		// TODO refine this
		if (invoice == null) {
			throw new AccountingException("No invoice in current editor!");
		}
		
		return invoice;
	}
	
	/**
	 * 
	 * @param invoice
	 * @param event
	 */
	protected void deleteInvoice(Invoice invoice, ExecutionEvent event) {
		final boolean areYouSure =  showWarningMessage(
				invoice, 
				event, 
				Messages.bind(Messages.DeleteInvoiceCommand_confirmMessage, invoice.getNumber()), 
				Messages.DeleteInvoiceCommand_confirmText);
				
		if (areYouSure) {
			getLogger().info("Deleting invoice " + invoice.getNumber()); //$NON-NLS-1$
			
			// do the actual work
			ModelHelper.deleteInvoice(invoice);
			
			// close any open editors for the deleted invoice
			removeOpenEditorForInvoice(invoice, event);
		} else {
			getLogger().info("Delete was cancelled by user"); //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 * @param invoice
	 * @param event
	 */
	protected void cancelInvoice(Invoice invoice, ExecutionEvent event) {
		final boolean areYouSure = showWarningMessage(
				invoice, 
				event, 
				Messages.bind(Messages.CancelInvoiceCommand_confirmMessage, invoice.getNumber()), 
				Messages.CancelInvoiceCommand_confirmText);
		
		if (areYouSure) {
			getLogger().info("Cancelling invoice " + invoice.getNumber()); //$NON-NLS-1$
			// do the actual work
			ModelHelper.cancelInvoice(invoice);
		} else {
			getLogger().info("CancelInvoice was cancelled by user"); //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 * @param invoice
	 * @param event
	 * @return
	 */
	private boolean showWarningMessage(Invoice invoice, ExecutionEvent event, final String message, final String text) {
		MessageBox msgBox = new MessageBox(
				getShell(event), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		msgBox.setMessage(message);
		msgBox.setText(text);
		
		return (msgBox.open() == SWT.OK);
	}
	
	/**
	 * 
	 * @param toBeDeleted
	 * @param event
	 */
	private void removeOpenEditorForInvoice(Invoice toBeDeleted, ExecutionEvent event) {
		getLogger().debug("Checking for open editors for invoice " + toBeDeleted.getNumber()); //$NON-NLS-1$
		IWorkbenchPage page = getActivePage(event);
		
		for (IEditorReference editorRef : page.findEditors(null, IDs.EDIT_INVOIDCE_ID, IWorkbenchPage.MATCH_ID)) {
			if (editorRef.getName().equals(toBeDeleted.getNumber())) {
				getLogger().debug("Closing editor for deleted invoice: " + editorRef.getName()); //$NON-NLS-1$
				page.closeEditor(editorRef.getEditor(false), false);
			}
		}		
	}
}