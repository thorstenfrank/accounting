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

/**
 * Deletes the currently edited invoice. This handler should be active when the currently active part is an
 * {@link InvoiceEditor} and only if the edited invoice may be deleted ({@link Invoice#canBeDeleted()}).
 * 
 * @author tfrank1
 * @see DeleteInvoiceFromSelectionHandler
 */
public class DeleteInvoiceFromEditorHandler extends AbstractInvoiceHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(DeleteInvoiceFromEditorHandler.class);
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		deleteInvoice(getInvoiceFromEditor(event), event);
	}
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

}
