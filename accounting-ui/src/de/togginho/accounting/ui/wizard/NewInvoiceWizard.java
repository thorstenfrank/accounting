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
package de.togginho.accounting.ui.wizard;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;
import de.togginho.accounting.ui.invoice.InvoiceEditorInput;

/**
 * @author thorsten
 *
 */
public class NewInvoiceWizard extends Wizard implements IWorkbenchWizard {
	
	/**
	 * 
	 */
	protected static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".NewInvoiceWizard"; //$NON-NLS-1$
	
	/** Logger. */
	private static final Logger LOG = Logger.getLogger(NewInvoiceWizard.class);
	
	/**
	 * 
	 */
	private NewInvoiceWizardPage invoiceNumberPage;
	
	/**
	 * 
	 */
	public NewInvoiceWizard() {
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.NewInvoiceWizard_windowTitle);
		setHelpAvailable(true);
	}

	/**
	 * {@inheritDoc}
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		invoiceNumberPage = new NewInvoiceWizardPage();
		addPage(invoiceNumberPage);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		
		final Invoice newInvoice = new Invoice();
		newInvoice.setUser(ModelHelper.getCurrentUser());
		
		// update data from wizard pages
		newInvoice.setNumber(invoiceNumberPage.getInvoiceNumber());
		newInvoice.setClient(invoiceNumberPage.getSelectedClient());
		
		// set the default invoice date to today
		newInvoice.setInvoiceDate(new Date());
		
		// check if an invoice with that number already exists
		if (ModelHelper.getInvoice(newInvoice.getNumber()) != null) {
			LOG.warn(String.format("Invoice with number [%s] already exists!", newInvoice.getNumber())); //$NON-NLS-1$
			MessageBox msgBox = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK);
			msgBox.setMessage(Messages.NewInvoiceWizardPage_alreadyExistsMsg);
			msgBox.setText(Messages.NewInvoiceWizardPage_alreadyExistsText);
			msgBox.open();
			return false;
		}
		
		// save the invoice
		LOG.info("Saving new invoice " + newInvoice); //$NON-NLS-1$
		ModelHelper.saveInvoice(newInvoice);
		
		LOG.debug("Setting selection to new invoice"); //$NON-NLS-1$
		ISelectionProvider selectionProvider = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite().getSelectionProvider();
		if (selectionProvider != null) {
			selectionProvider.setSelection(new StructuredSelection(newInvoice));
		} else {
			LOG.debug("no selection provider"); //$NON-NLS-1$
		}
		
		LOG.debug("Opening editor for newly created invoice"); //$NON-NLS-1$
		InvoiceEditorInput input = new InvoiceEditorInput(newInvoice);
		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, IDs.EDIT_INVOIDCE_ID);
		} catch (PartInitException e) {
			LOG.error("Error opening editor for newly created invoice", e); //$NON-NLS-1$
		}
		
		return true;
	}
}
