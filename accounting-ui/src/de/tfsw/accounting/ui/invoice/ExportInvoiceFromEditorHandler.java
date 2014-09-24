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

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;

import de.tfsw.accounting.model.Invoice;

/**
 * @author thorsten
 *
 */
public class ExportInvoiceFromEditorHandler extends ExportInvoiceFromSelectionHandler {

	private static final Logger LOG = Logger.getLogger(ExportInvoiceFromEditorHandler.class);
	
	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.invoice.ExportInvoiceFromSelectionHandler#doGetInvoice(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
    protected Invoice doGetInvoice(ExecutionEvent event) {
	    return getInvoiceFromEditor(event);
    }

	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.invoice.ExportInvoiceFromSelectionHandler#getLogger()
	 */
	@Override
    protected Logger getLogger() {
	    return LOG;
    }
}
