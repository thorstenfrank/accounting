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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.ui.AbstractModalDialog;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 * @see de.tfsw.accounting.AccountingService#copyInvoice(Invoice, String)
 */
public class CopyInvoiceFromSelectionHandler extends AbstractInvoiceHandler implements Constants {
	
	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(CopyInvoiceFromSelectionHandler.class);
	
	private String copyInvoiceNumber;
	
	/**
     * {@inheritDoc}.
     * @see de.tfsw.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
    	Invoice original = getInvoiceFromSelection(event);
    	
    	AbstractModalDialog dialog = new AbstractModalDialog(
    			getShell(event), 
    			Messages.CopyInvoiceCommand_dialogTitle,
    			Messages.CopyInvoiceCommand_dialogMessage) {
		
    		Text invoiceNumber;
    		
			@Override
			protected void createMainContents(Composite parent) {
	        	Composite composite = new Composite(parent, SWT.NONE);
	        	composite.setLayout(new GridLayout(2, false));
	        	WidgetHelper.grabBoth(composite);
	        		        	
	        	WidgetHelper.createLabel(composite, Messages.labelInvoiceNo);
	        	invoiceNumber = WidgetHelper.createSingleBorderText(
	        			composite, AccountingUI.getAccountingService().getNextInvoiceNumber());
			}
			
	        @Override
	        protected void buttonPressed(int buttonId) {
	        	if (buttonId == IDialogConstants.OK_ID) {
	        		if (invoiceNumber.getText().isEmpty()) {
	        			setErrorMessage(Messages.CopyInvoiceCommand_errorEmptyInvoiceNumber);
	        		} else if (AccountingUI.getAccountingService().getInvoice(invoiceNumber.getText()) != null) {
	        			setErrorMessage(Messages.CopyInvoiceCommand_errorExistingInvoice);
	        		} else {
	        			copyInvoiceNumber = invoiceNumber.getText();
	        			super.buttonPressed(buttonId);
	        		}
	        	} else {
	        		super.buttonPressed(buttonId);
	        	}
	        }
		};
    	
    	if (dialog.show()) {
    		LOG.info("Creating copy of invoice with new number: " + copyInvoiceNumber); //$NON-NLS-1$
    		Invoice copy = AccountingUI.getAccountingService().copyInvoice(original, copyInvoiceNumber);
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
	 * @see de.tfsw.accounting.ui.AbstractInvoiceHandler.invoice.AbstractInvoiceCommand#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}