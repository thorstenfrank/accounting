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
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.AbstractAccountingHandler;
import de.togginho.accounting.ui.IDs;

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
abstract class AbstractInvoiceCommand extends AbstractAccountingHandler {

	
	
	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
    	getLogger().debug("Getting invoice from current selection"); //$NON-NLS-1$
    	
    	IWorkbenchPage activePage = getActivePage(event);
	    
		IWorkbenchPart part = activePage.getActivePart();
		ISelectionProvider selectionProvider = part.getSite().getSelectionProvider();
		
		Invoice theInvoice = null;
		
		if (selectionProvider != null && !selectionProvider.getSelection().isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
						
			if (structuredSelection.getFirstElement() instanceof Invoice) {
				theInvoice = (Invoice) structuredSelection.getFirstElement();			
			}
		} else {
			getLogger().warn("No selection to be found, will try active editor"); //$NON-NLS-1$
			
			if (activePage.getActiveEditor().getEditorSite().getId().equals(IDs.EDIT_INVOIDCE_ID)) {
				getLogger().debug("Active editor is invoice editor, will handle this invoice"); //$NON-NLS-1$
				
				InvoiceEditorInput input = (InvoiceEditorInput) activePage.getActiveEditor().getEditorInput();
				theInvoice = input.getInvoice();
			} else {
				getLogger().warn("No invoice in selection or active editor - cannot run this command!");  //$NON-NLS-1$
				return;
			}
		}
		
		if (theInvoice != null) {
			getLogger().info(String.format("Now running command [%s] for invoice [%s].",  //$NON-NLS-1$
					getClass().getSimpleName(), theInvoice.getNumber()));
			
			handleInvoice(theInvoice, event);
		}
    }
    
	/**
	 * 
	 * @param invoice
	 * @param event
	 * @throws ExecutionException
	 */
	protected abstract void handleInvoice(Invoice invoice, ExecutionEvent event) throws ExecutionException;

}
