/*
 *  Copyright 2012 , 2014 Thorsten Frank (accounting@tfsw.de).
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

import java.time.LocalDate;

import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.expense.editing.BaseExpenseEditHelper;
import de.tfsw.accounting.ui.expense.editing.ExpenseEditHelper;

/**
 * @author thorsten
 *
 */
class ExpenseWizardPage extends AbstractExpenseWizardPage {
	
	/**
	 * 
	 */
	ExpenseWizardPage() {
		super(ExpenseWizardPage.class.getName(), new Expense());
		((Expense)getExpense()).setPaymentDate(LocalDate.now());
		setTitle(Messages.EditExpenseWizard_newTitle);
		setDescription(Messages.EditExpenseWizard_newDesc);
	}
	
	/**
	 * 
	 * @param expense
	 */
	ExpenseWizardPage(Expense expense) {
		super(ExpenseWizardPage.class.getName(), expense);
		setTitle(Messages.EditExpenseWizard_editTitle);
		setDescription(Messages.EditExpenseWizard_editDesc);
	}
	
	/**
	 * @see de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage#getHelper()
	 */
	@Override
	protected BaseExpenseEditHelper getHelper() {
		return new ExpenseEditHelper(getExpense(), this);
	}

	/**
	 * @see de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage#getExpense()
	 */
	@Override
	protected Expense getExpense() {
		return (Expense)super.getExpense();
	}

	/**
	 * 
	 */
	@Override
	protected boolean checkIfPageComplete() {
		Expense expense = getExpense();
		if (expense.getDescription() != null && !expense.getDescription().isEmpty() &&
			expense.getPaymentDate() != null && expense.getNetAmount() != null &&
			expense.getExpenseType() != null) {
			
			return true;
		} else {
			return false;
		}
	}
}
