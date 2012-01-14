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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;

/**
 * @author thorsten
 * @see de.togginho.accounting.AccountingService#copyInvoice(Invoice, String)
 */
public class CopyInvoiceFromSelectionHandler extends AbstractInvoiceHandler implements Constants {
	
	/** Logger. */
	private static final Logger LOG = Logger.getLogger(CopyInvoiceFromSelectionHandler.class);
	
	private String invoiceNumber;
	
	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
    	Invoice original = getInvoiceFromSelection(event);
    	CopyInvoiceDialog dialog = new CopyInvoiceDialog(getShell(event));
    	invoiceNumber = null;
    	if (dialog.open() == TitleAreaDialog.OK) {
    		LOG.info("Creating copy of invoice with new number: " + invoiceNumber); //$NON-NLS-1$
    		Invoice copy = AccountingUI.getAccountingService().copyInvoice(original, invoiceNumber);
    		InvoiceEditorInput input = new InvoiceEditorInput(copy);
    		
    		try {
	            getActivePage(event).openEditor(input, IDs.EDIT_INVOIDCE_ID);
            } catch (PartInitException e) {
            	LOG.error("Couldn't open editor for copied invoice", e); //$NON-NLS-1$
            	throw new ExecutionException(Messages.CopyInvoiceCommand_errorOpeningEditor, e);
            }
    	} else {
    		LOG.debug("Copy invoice cancelled by user"); //$NON-NLS-1$
    	}
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
	private class CopyInvoiceDialog extends TitleAreaDialog {
		
		private Text invoiceNumberText;
		
		/**
         * @param parentShell
         */
        private CopyInvoiceDialog(Shell parentShell) {
	        super(parentShell);
        }
        
		/**
		 * {@inheritDoc}.
		 * @see org.eclipse.jface.dialogs.Dialog#create()
		 */
		@Override
		public void create() {
		    super.create();
		    setTitle(Messages.CopyInvoiceCommand_dialogTitle);
		    setMessage(Messages.CopyInvoiceCommand_dialogMessage);
		}
		
		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createDialogArea(Composite parent) {
        	Composite composite = new Composite(parent, SWT.NONE);
        	GridLayout layout = new GridLayout(2, false);
//        	layout.marginHeight = 0;
//        	layout.marginWidth = 0;
        	
        	composite.setLayout(layout);
        	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
        	
    		final Label topSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
    		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(topSeparator);
        	
        	final Label dateLabel = WidgetHelper.createLabel(composite, Messages.labelInvoiceNo);
        	GridDataFactory.fillDefaults().indent(5, 5).applyTo(dateLabel);
        	
        	final String invoiceNumber = AccountingUI.getAccountingService().getNextInvoiceNumber();
        	invoiceNumberText = WidgetHelper.createSingleBorderText(composite, invoiceNumber);
        	GridDataFactory.fillDefaults().grab(true, false).applyTo(invoiceNumberText);
        	invoiceNumberText.selectAll();
        	
    		final Label fillToBottom = WidgetHelper.createLabel(composite, Constants.EMPTY_STRING);
    		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(fillToBottom);
    		
    		final Label bottomSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
    		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(bottomSeparator);   		
    		
            return composite;
        }
        
        @Override
        protected void buttonPressed(int buttonId) {
        	if (buttonId == IDialogConstants.OK_ID) {
        		invoiceNumber = invoiceNumberText.getText();
        		if (invoiceNumber == null || invoiceNumber.isEmpty()) {
        			setErrorMessage(Messages.CopyInvoiceCommand_errorEmptyInvoiceNumber);
        		} else if (AccountingUI.getAccountingService().getInvoice(invoiceNumber) != null) {
        			setErrorMessage(Messages.CopyInvoiceCommand_errorExistingInvoice);
        		} else {
        			super.buttonPressed(buttonId);
        		}
        	} else {
        		super.buttonPressed(buttonId);
        	}
        }
	}
}