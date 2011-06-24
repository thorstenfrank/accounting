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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;

/**
 * @author tfrank1
 *
 */
public class DeleteInvoiceCommand extends AbstractInvoiceCommand {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(DeleteInvoiceCommand.class);
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see AbstractInvoiceCommand#handleInvoice(Invoice, ExecutionEvent)
	 */
	@Override
	protected void handleInvoice(Invoice invoice, ExecutionEvent event) throws ExecutionException {
		LOG.info("Deleting invoice + " + invoice.getNumber()); //$NON-NLS-1$
		
		if (areYouSure(invoice, event)) {
			// do the actual work
			ModelHelper.deleteInvoice(invoice);
			
			// close any open editors for the deleted invoice
			removeOpenEditorForInvoice(invoice, event);
		} else {
			LOG.info("Delete was cancelled by user"); //$NON-NLS-1$
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.rcp.invoice.AbstractInvoiceCommand#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	/**
	 * 
	 * @param invoice
	 * @param event
	 * @return
	 */
	private boolean areYouSure(Invoice invoice, ExecutionEvent event) {
		MessageBox msgBox = new MessageBox(
				getShell(event), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		msgBox.setMessage(Messages.bind(Messages.DeleteInvoiceCommand_confirmMessage, invoice.getNumber()));
		msgBox.setText(Messages.DeleteInvoiceCommand_confirmText);
		
		return (msgBox.open() == SWT.OK);
	}
	
	/**
	 * 
	 * @param toBeDeleted
	 * @param event
	 */
	private void removeOpenEditorForInvoice(Invoice toBeDeleted, ExecutionEvent event) {
		LOG.debug("Checking for open editors for invoice " + toBeDeleted.getNumber()); //$NON-NLS-1$
		IWorkbenchPage page = getActivePage(event);
		
		for (IEditorReference editorRef : page.findEditors(null, IDs.EDIT_INVOIDCE_ID, IWorkbenchPage.MATCH_ID)) {
			if (editorRef.getName().equals(toBeDeleted.getNumber())) {
				LOG.debug("Closing editor for deleted invoice: " + editorRef.getName()); //$NON-NLS-1$
				page.closeEditor(editorRef.getEditor(false), false);
			}
		}		
	}
}