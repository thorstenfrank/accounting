/*
 *  Copyright 2012 thorsten frank (thorsten.frank@tfsw.de).
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
package de.tfsw.accounting.ui.expense;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.wizard.WizardDialog;

import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.ui.AbstractAccountingHandler;

/**
 * Abstract base class for command handlers dealing with {@link Expense} entitites.
 * Mainly provides easy access to expense selection and opening the creation/edit wizard.
 * 
 * @author Thorsten Frank - thorsten.frank@tfsw.de
 *
 */
abstract class AbstractExpenseHandler extends AbstractAccountingHandler {

	/**
	 * Extracts and returns the {@link Expense} that is the current selection of the active selection provider.
	 * Mostly, that will be the expense for which a command was fired, through either a double-click or context menu
	 * selection in the {@link ExpensesView}.
	 * 
	 * @param event	the {@link ExecutionEvent} provided through {@link #doExecute(ExecutionEvent)}
	 * 
	 * @return	the currently selected {@link Expense}, or <code>null</code> if this handler was called without such a 
	 * 			selection
	 */
	protected Expense getExpenseFromSelection(ExecutionEvent event) {
		return ((ExpenseWrapper)getCurrentSelection(event, ExpenseWrapper.class)).getExpense();
	}

	/**
	 * Opens an {@link ExpenseWizard} without an existing {@link Expense} to edit.
	 * 
	 * @param event the {@link ExecutionEvent} provided through {@link #doExecute(ExecutionEvent)}
	 */
	protected void openExpenseWizard(ExecutionEvent event) {
		getLogger().debug("Opening NEW expense wizard");
		openExpenseWizard(event, new ExpenseWizard());
	}
	
	/**
	 * Opens the {@link ExpenseWizard} with the supplied {@link Expense} for editing.
	 * 
	 * @param event the {@link ExecutionEvent} provided through {@link #doExecute(ExecutionEvent)}
	 * @param expense the {@link Expense} to edit
	 */
	protected void openExpenseWizard(ExecutionEvent event, Expense expense) {
		getLogger().debug("Opening EDIT expense wizard for " + expense.toString());
		openExpenseWizard(event, new ExpenseWizard(expense));
	}
	
	/**
	 * 
	 * @param event
	 * @param wizard
	 */
	private void openExpenseWizard(ExecutionEvent event, ExpenseWizard wizard) {
		WizardDialog dialog = new WizardDialog(getShell(event), wizard);
		
		int returnCode = dialog.open();
		
		if (returnCode == WizardDialog.CANCEL) {
			getLogger().debug("Editing Expense was cancelled"); //$NON-NLS-1$
		} else {
			getLogger().debug("Edited Expense was saved"); //$NON-NLS-1$
		}		
	}
}
