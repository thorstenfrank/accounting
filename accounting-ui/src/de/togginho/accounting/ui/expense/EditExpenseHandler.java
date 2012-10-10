/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.expense;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.togginho.accounting.model.Expense;
import de.togginho.accounting.ui.AbstractAccountingHandler;


/**
 * @author thorsten
 *
 */
public class EditExpenseHandler extends AbstractAccountingHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(EditExpenseHandler.class);
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		LOG.debug("Opening wizard for existing expense"); //$NON-NLS-1$
		
		ISelectionProvider selectionProvider = getActivePage(event).getActivePart().getSite().getSelectionProvider();
		
		if (selectionProvider != null && !selectionProvider.getSelection().isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
						
			if (structuredSelection.getFirstElement() instanceof ExpenseWrapper) {
				Shell shell = getShell(event);
				Expense expense = ((ExpenseWrapper) structuredSelection.getFirstElement()).getExpense();
				
				ExpenseWizard wizard = new ExpenseWizard(expense);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				
				int returnCode = dialog.open();
				
				if (returnCode == WizardDialog.CANCEL) {
					LOG.debug("Editing Expense was cancelled"); //$NON-NLS-1$
				} else {
					LOG.debug("Edited Expense was saved"); //$NON-NLS-1$
				}
			} else {
				getLogger().warn("Current selection is not of type Ivoice: "  //$NON-NLS-1$
						+ structuredSelection.getFirstElement().getClass());
			}
		} else {
			getLogger().warn("Current selection is empty, cannot run this command!"); //$NON-NLS-1$
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}	
}
