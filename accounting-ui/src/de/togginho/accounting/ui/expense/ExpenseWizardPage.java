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

import java.util.Date;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.model.Expense;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
class ExpenseWizardPage extends WizardPage implements ExpenseEditingHelperCallback {
	
	/**
	 * 
	 */
	private Expense expense;
	
	/**
	 * 
	 */
	ExpenseWizardPage() {
		super(ExpenseWizardPage.class.getName());
		this.expense = new Expense();
		expense.setPaymentDate(new Date());
		setTitle(Messages.EditExpenseWizard_newTitle);
		setDescription(Messages.EditExpenseWizard_newDesc);
	}
	
	/**
	 * 
	 * @param expense
	 */
	ExpenseWizardPage(Expense expense) {
		super(ExpenseWizardPage.class.getName());
		this.expense = expense;
		setTitle(Messages.EditExpenseWizard_editTitle);
		setDescription(Messages.EditExpenseWizard_editDesc);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		ExpenseEditingHelper expenseEditingHelper = new ExpenseEditingHelper(expense, this);
		
		expenseEditingHelper.createBasicSection(composite);
		
		expenseEditingHelper.createPriceSection(composite);
				
		setControl(composite);
		
		checkIfPageComplete();
	}	
	
	/**
	 * {@inheritDoc}
	 * @see ExpenseEditingHelperCallback#modelHasChanged()
	 */
	@Override
	public void modelHasChanged() {
		checkIfPageComplete();
	}

	/**
	 * {@inheritDoc}
	 * @see ExpenseEditingHelperCallback#createLabel(org.eclipse.swt.widgets.Composite, java.lang.String)
	 */
	@Override
	public Label createLabel(Composite parent, String text) {
		return WidgetHelper.createLabel(parent, text);
	}

	/**
	 * {@inheritDoc}
	 * @see ExpenseEditingHelperCallback#createText(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public Text createText(Composite parent, int style) {
		return WidgetHelper.createSingleBorderText(parent, null);
	}

	/**
	 * 
	 * @return
	 */
	protected Expense getExpense() {
		return expense;
	}
	
	/**
	 * 
	 */
	private void checkIfPageComplete() {
		if (expense.getDescription() != null && !expense.getDescription().isEmpty() &&
			expense.getPaymentDate() != null && expense.getNetAmount() != null &&
			expense.getExpenseType() != null) {
			
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}
}
