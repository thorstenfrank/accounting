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

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.ModelHelper;

/**
 * Sends the currently edited invoice. This handler is active when the active view part is {@link InvoiceEditor}, and
 * is enabled only when the {@link Invoice#canBeSent()} returns <code>true</code>.
 *  
 * @author thorsten
 * @see SendInvoiceFromSelectionHandler
 * @see ModelHelper#sendInvoice(Invoice)
 */
public class SendInvoiceFromEditorHandler extends AbstractInvoiceHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(SendInvoiceFromEditorHandler.class);
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.invoice.AbstractInvoiceHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
		ModelHelper.sendInvoice(getInvoiceFromEditor(event));
    }
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractInvoiceHandler.invoice.AbstractInvoiceCommand#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}