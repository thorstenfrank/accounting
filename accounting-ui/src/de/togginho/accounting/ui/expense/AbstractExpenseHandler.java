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

import org.eclipse.core.commands.ExecutionEvent;
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
abstract class AbstractExpenseHandler extends AbstractAccountingHandler {

	/**
	 * 
	 * @param event
	 * @return
	 */
	protected Expense getExpenseFromSelection(ExecutionEvent event) {
		Expense expense = null;
		
		ISelectionProvider selectionProvider = getSelectionProvider(event);
		if (selectionProvider != null && !selectionProvider.getSelection().isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
						
			if (structuredSelection.getFirstElement() instanceof ExpenseWrapper) {
				expense = ((ExpenseWrapper) structuredSelection.getFirstElement()).getExpense();
			} else {
				getLogger().warn("Current selection is not of type Expense");
			}
		} 
		else { 
			getLogger().warn("Current selection is empty, cannot run this command!"); //$NON-NLS-1$ 
		}
		
		return expense;
	}

	/**
	 * 
	 * @param event
	 */
	protected void openExpenseWizard(ExecutionEvent event) {
		openExpenseWizard(event, new ExpenseWizard());
	}
	
	/**
	 * @param event
	 * @param expense
	 */
	protected void openExpenseWizard(ExecutionEvent event, Expense expense) {
		openExpenseWizard(event, new ExpenseWizard(expense));
	}
	
	/**
	 * 
	 * @param event
	 * @param wizard
	 */
	private void openExpenseWizard(ExecutionEvent event, ExpenseWizard wizard) {
		getLogger().debug("Opening expense editor");
		Shell shell = getShell(event);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		
		int returnCode = dialog.open();
		
		if (returnCode == WizardDialog.CANCEL) {
			getLogger().debug("Editing Expense was cancelled"); //$NON-NLS-1$
		} else {
			getLogger().debug("Edited Expense was saved"); //$NON-NLS-1$
		}		
	}
}
