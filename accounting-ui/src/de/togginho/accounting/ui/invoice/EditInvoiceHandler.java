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
import org.eclipse.ui.PartInitException;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;

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
	 * @see de.togginho.accounting.ui.invoice.AbstractInvoiceHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
		Invoice invoice = getInvoiceFromSelection(event);
		getLogger().debug("Opening invoice " + invoice.getNumber()); //$NON-NLS-1$
		InvoiceEditorInput input = new InvoiceEditorInput(invoice);
		try {
			getActivePage(event).openEditor(input, IDs.EDIT_INVOIDCE_ID);
			
			if (!invoice.canBeEdited()) {
				MessageBox box = new MessageBox(getShell(event), SWT.ICON_INFORMATION | SWT.OK);
				box.setMessage(Messages.EditInvoiceCommand_uneditableMessage);
				box.setText(Messages.EditInvoiceCommand_uneditableText);
				box.open();				
			}
		} catch (PartInitException e) {
			getLogger().error("Error opening editor for invoice " + invoice.getNumber(), e); //$NON-NLS-1$
			throw new ExecutionException(Messages.bind(Messages.EditInvoiceCommand_errorOpeningEditor, invoice), e);
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