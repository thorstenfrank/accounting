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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPage;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.ui.AbstractAccountingHandler;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;

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
	 * Returns the current selection using the active workbench selection provider.
	 * 
	 * @param event
	 * @return
	 * @throws AccountingException if no {@link Invoice} is selected
	 */
	protected Invoice getInvoiceFromSelection(ExecutionEvent event) {
		return getCurrentSelection(event, Invoice.class);
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
		
		if (invoice == null) {
			throw new AccountingException(Messages.AbstractInvoiceHandler_errorNoInvoiceInCurrentEditor);
		}
		
		return invoice;
	}
}