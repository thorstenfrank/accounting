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

import de.togginho.accounting.model.Invoice;

/**
 * @author tfrank1
 *
 */
public class PayInvoiceFromEditorHandler extends PayInvoiceFromSelectionHandler {

	/** */
	private static final Logger LOG = Logger.getLogger(PayInvoiceFromEditorHandler.class);
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.invoice.PayInvoiceFromSelectionHandler#doGetInvoice(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
    protected Invoice doGetInvoice(ExecutionEvent event) {
	    return getInvoiceFromEditor(event);
    }

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.invoice.PayInvoiceFromSelectionHandler#getLogger()
	 */
	@Override
    protected Logger getLogger() {
	    return LOG;
    }
}