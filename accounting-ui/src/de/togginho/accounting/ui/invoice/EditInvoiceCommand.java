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
import org.eclipse.ui.PartInitException;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.IDs;

/**
 * @author thorsten
 *
 */
public class EditInvoiceCommand extends AbstractInvoiceCommand {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(EditInvoiceCommand.class);
		
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.rcp.invoice.AbstractInvoiceCommand#handleInvoice(de.togginho.accounting.model.Invoice)
	 */
	@Override
	protected void handleInvoice(Invoice invoice, ExecutionEvent event) throws ExecutionException {
		LOG.debug("Opening invoice " + invoice.getNumber()); //$NON-NLS-1$
		InvoiceEditorInput input = new InvoiceEditorInput(invoice);
		
		try {
			getActivePage().openEditor(input, IDs.EDIT_INVOIDCE_ID);
		} catch (PartInitException e) {
			LOG.error("Error opening invoice editor", e); //$NON-NLS-1$
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
}