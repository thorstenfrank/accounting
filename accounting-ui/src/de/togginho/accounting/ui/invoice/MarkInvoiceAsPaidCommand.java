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

import java.util.Calendar;
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

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;
import de.togginho.accounting.ui.WidgetHelper;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class MarkInvoiceAsPaidCommand extends AbstractInvoiceCommand {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(MarkInvoiceAsPaidCommand.class);
	
	private Date paymentDate;
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.invoice.AbstractInvoiceCommand#handleInvoice(de.togginho.accounting.model.Invoice, org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void handleInvoice(final Invoice invoice, ExecutionEvent event) throws ExecutionException {
		// show date selection dialog
		TitleAreaDialog tad = new TitleAreaDialog(getShell(event)) {

			private DateTime dateTime;
			
			@Override
			public void create() {
			    super.create();
			    setTitle(Messages.MarkInvoiceAsPaidCommand_title);
			    setMessage(Messages.MarkInvoiceAsPaidCommand_message);
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
            	
            	final Label dateLabel = WidgetHelper.createLabel(composite, Messages.MarkInvoiceAsPaidCommand_paymentDateLabel);
            	GridDataFactory.fillDefaults().indent(5, 5).applyTo(dateLabel);
            	
            	dateTime = new DateTime(composite, SWT.DATE | SWT.DROP_DOWN);
            	
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
    				Calendar cal = Calendar.getInstance();
    				
    				Date rightNow = cal.getTime();
    				
    				cal.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
    				cal.set(Calendar.MONTH, dateTime.getMonth());
    				cal.set(Calendar.YEAR, dateTime.getYear());
    				
    				paymentDate = cal.getTime();
    				
    				if (paymentDate.after(rightNow)) {
    					setErrorMessage(Messages.MarkInvoiceAsPaidCommand_errorPaymentDateInTheFuture);
    				} else if (paymentDate.before(invoice.getSentDate())) {
    					setErrorMessage(Messages.MarkInvoiceAsPaidCommand_errorPaymentDateBeforeSentDate);
    				}
    				else {
    					super.buttonPressed(buttonId);
    				}
            		
            	} else {
            		super.buttonPressed(buttonId);
            	}
            }
		};
		
		int result = tad.open();
		
		if (result == TitleAreaDialog.OK) {
			LOG.info(String.format("Now marking invoice [%s] as paid on [%s]", //$NON-NLS-1$
					invoice.getNumber(), FormatUtil.formatDate(paymentDate)));
			
			ModelHelper.markAsPaid(invoice, paymentDate);
		} else {
			LOG.debug("Payment was cancelled..."); //$NON-NLS-1$
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