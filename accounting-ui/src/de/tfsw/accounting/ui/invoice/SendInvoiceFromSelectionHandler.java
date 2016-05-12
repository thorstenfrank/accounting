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

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.ui.AbstractModalDialog;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.FormatUtil;

/**
 * Sends the currently edited invoice. This handler is active when the active view part is {@link InvoiceView} and 
 * is enabled only when an invoice is selected and the {@link Invoice#canBeSent()} returns <code>true</code>. 
 * 
 * @author thorsten
 * @see SendInvoiceFromEditorHandler
 * @see de.tfsw.accounting.AccountingService#sendInvoice(Invoice)
 */
public class SendInvoiceFromSelectionHandler extends AbstractInvoiceHandler {

	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(SendInvoiceFromSelectionHandler.class);
	
	private LocalDate sentDate;
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.invoice.AbstractInvoiceHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
		final Invoice invoice = getInvoice(event);
		
		sentDate = invoice.getInvoiceDate();
		
		AbstractModalDialog dialog = new AbstractModalDialog(
				getShell(event), 
				Messages.SendInvoiceCommand_title, 
				Messages.bind(Messages.SendInvoiceCommand_message, invoice.getNumber())) {
			
			@Override
			protected void createMainContents(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(2, false));
				
		       	WidgetHelper.createLabel(composite, Messages.SendInvoiceCommand_sentDateLabel);
	        	
	        	final DateTime dateTime = new DateTime(composite, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
	        	WidgetHelper.dateToWidget(invoice.getInvoiceDate(), dateTime);
	        	dateTime.addSelectionListener(new SelectionAdapter() {
	        		@Override
	        		public void widgetSelected(SelectionEvent e) {
	        			sentDate = WidgetHelper.widgetToDate(dateTime);
	        		}
				});
				
			}
		};
		
		if (dialog.show()) {
			getLogger().info(String.format("Now marking invoice [%s] as sent on [%s]", //$NON-NLS-1$
					invoice.getNumber(), FormatUtil.formatDate(sentDate)));
			AccountingUI.getAccountingService().sendInvoice(invoice, sentDate);
		} else {
			getLogger().debug("Payment was cancelled..."); //$NON-NLS-1$
		}
    }
	
	/**
	 * 
	 * @param event
	 * @return
	 */
	protected Invoice getInvoice(ExecutionEvent event) {
		return getInvoiceFromSelection(event);
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractInvoiceHandler.invoice.AbstractInvoiceCommand#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}