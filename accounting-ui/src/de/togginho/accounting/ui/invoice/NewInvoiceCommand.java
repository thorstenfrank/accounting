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
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.ui.wizard.NewInvoiceWizard;

/**
 * @author thorsten
 *
 */
public class NewInvoiceCommand extends AbstractHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(NewInvoiceCommand.class);
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LOG.debug("Creating new invoice"); //$NON-NLS-1$
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		NewInvoiceWizard wizard = new NewInvoiceWizard();
		
		WizardDialog dialog = new WizardDialog(shell, wizard);
		
		int returnCode = dialog.open();
		
		if (returnCode == WizardDialog.CANCEL) {
			LOG.debug("NewInvoiceWizard was cancelled"); //$NON-NLS-1$
		} else {
			LOG.debug("Opening editor for newly created invoice"); //$NON-NLS-1$
			
			// this is done by the wizard...
//			InvoiceEditorInput input = new InvoiceEditorInput(wizard.getNewInvoice());
//			
//			try {
//				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, IDs.EDIT_INVOIDCE_ID);
//			} catch (PartInitException e) {
//				LOG.error("Error opening editor for newly created invoice", e); //$NON-NLS-1$
//			}
		}
		
		return null;
	}

}
