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

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;
import de.togginho.accounting.ui.WidgetHelper;
import de.togginho.accounting.util.FormatUtil;

/**
 * Sends the currently edited invoice. This handler is active when the active view part is {@link InvoiceView} and 
 * is enabled only when an invoice is selected and the {@link Invoice#canBeSent()} returns <code>true</code>. 
 * 
 * @author thorsten
 * @see SendInvoiceFromEditorHandler
 * @see ModelHelper#sendInvoice(Invoice)
 */
public class SendInvoiceFromSelectionHandler extends AbstractInvoiceHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(SendInvoiceFromSelectionHandler.class);
	
	private Date sentDate;
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.invoice.AbstractInvoiceHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
		Invoice invoice = getInvoice(event);
		
		SentDateSelectionDialog dialog = new SentDateSelectionDialog(getShell(event), invoice);
		
		if (dialog.open() == TitleAreaDialog.OK) {
			getLogger().info(String.format("Now marking invoice [%s] as sent on [%s]", //$NON-NLS-1$
					invoice.getNumber(), FormatUtil.formatDate(sentDate)));
			ModelHelper.sendInvoice(invoice, sentDate);
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
	 * @see de.togginho.accounting.ui.AbstractInvoiceHandler.invoice.AbstractInvoiceCommand#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
	
	/**
	 * 
	 * @author thorsten
	 *
	 */
	private class SentDateSelectionDialog extends TitleAreaDialog {
		/** */
		private DateTime dateTime;
		
		/** */
		private Invoice invoice;
		
		/**
		 * 
		 * @param shell
		 * @param invoice
		 */
		private SentDateSelectionDialog(Shell shell, Invoice invoice) {
	        super(shell);
	        this.invoice = invoice;
        }
		
		/**
		 * {@inheritDoc}.
		 * @see org.eclipse.jface.dialogs.Dialog#create()
		 */
		@Override
		public void create() {
		    super.create();
		    setTitle(Messages.SendInvoiceCommand_title);
		    setMessage(Messages.bind(Messages.SendInvoiceCommand_message, invoice.getNumber()));
		}
		
		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createDialogArea(Composite parent) {
        	Composite composite = new Composite(parent, SWT.NONE);
        	GridLayout layout = new GridLayout(2, false);
        	layout.marginHeight = 0;
        	layout.marginWidth = 0;
        	
        	composite.setLayout(layout);
        	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
        	
    		final Label topSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
    		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(topSeparator);
        	
        	final Label dateLabel = WidgetHelper.createLabel(composite, Messages.SendInvoiceCommand_sentDateLabel);
        	GridDataFactory.fillDefaults().indent(5, 5).applyTo(dateLabel);
        	
        	dateTime = new DateTime(composite, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
        	WidgetHelper.dateToWidget(invoice.getInvoiceDate(), dateTime);
        	
    		final Label fillToBottom = WidgetHelper.createLabel(composite, Constants.EMPTY_STRING);
    		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(fillToBottom);
    		
    		final Label bottomSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
    		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(bottomSeparator);
        	
            return composite;
        }
        
        /**
         * {@inheritDoc}.
         * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
         */
        @Override
        protected void buttonPressed(int buttonId) {
        	if (buttonId == IDialogConstants.OK_ID) {
        		sentDate = WidgetHelper.widgetToDate(dateTime);
        	}
        	
        	super.buttonPressed(buttonId);
        }
	}
}