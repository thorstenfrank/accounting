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

import de.tfsw.accounting.model.AbstractExpense;
import de.tfsw.accounting.model.RecurringExpense;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage;
import de.tfsw.accounting.ui.expense.editing.BaseExpenseEditHelper;

/**
 * @author Thorsten Frank
 *
 */
class RecurringExpenseWizardPage extends AbstractExpenseWizardPage {
	
	private RecurringExpenseEditHelper helper;
	
	/**
	 * 
	 */
	RecurringExpenseWizardPage(RecurringExpense recurringExpense) {
		super(RecurringExpenseWizardPage.class.getName(), recurringExpense);
		this.helper = new RecurringExpenseEditHelper(recurringExpense, this);
		setTitle(Messages.RecurringExpenseWizardPage_Title);
		setDescription(Messages.RecurringExpenseWizardPage_Desc);
		setPageComplete(checkIfPageComplete());
	}
	
	/**
	 * @see de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage#getHelper()
	 */
	@Override
	protected BaseExpenseEditHelper getHelper() {
		return helper;
	}
	
	/**
	 * @see de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage#getExpense()
	 */
	@Override
	protected RecurringExpense getExpense() {
		return (RecurringExpense) super.getExpense();
	}

	/**
	 * @see de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage#checkIfPageComplete()
	 */
	@Override
	protected boolean checkIfPageComplete() {
		AbstractExpense expense = getExpense();
		
		if (isNotEmpty(expense.getDescription()) && expense.getExpenseType() != null && expense.getNetAmount() != null) {
			return true;
		}
		
		return false;
	}

	private boolean isNotEmpty(String string) {
		return string != null && string.isEmpty() == false;
	}
}
