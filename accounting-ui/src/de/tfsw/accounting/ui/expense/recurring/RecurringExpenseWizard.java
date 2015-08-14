/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.expense.recurring;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.tfsw.accounting.model.RecurringExpense;

/**
 * @author Thorsten Frank
 *
 */
public class RecurringExpenseWizard extends Wizard implements IWorkbenchWizard {
	
	private RecurringExpense recurringExpense;
	
	/**
	 * 
	 */
	public RecurringExpenseWizard() {
		setNeedsProgressMonitor(false);
		setWindowTitle("Recurring Expense Wizard Window Title");
		recurringExpense = new RecurringExpense();
		recurringExpense.setActive(true);
	}

	@Override
	public void addPages() {
		addPage(new RecurringExpenseWizardPage(recurringExpense));
		addPage(new RecurrenceRuleWizardPage(recurringExpense));
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return false;
	}
}
