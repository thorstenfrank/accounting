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
package de.tfsw.accounting.ui.expense.template;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage;
import de.tfsw.accounting.ui.expense.editing.BaseExpenseEditHelper;

/**
 * @author Thorsten Frank
 *
 */
public class RecurrenceRuleWizardPage extends AbstractExpenseWizardPage {

	private ExpenseTemplateEditHelper helper;
	
	/**
	 * 
	 */
	public RecurrenceRuleWizardPage(ExpenseTemplate expense) {
		super(RecurrenceRuleWizardPage.class.getName(), expense);
		this.helper = new ExpenseTemplateEditHelper(expense, this);
		setTitle(Messages.RecurrenceRuleWizardPage_Title);
		setDescription(Messages.RecurrenceRuleWizardPage_Desc);
	}
	
	/**
	 * @see de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		
		helper.createRecurrenceSection(composite);
		
		setControl(composite);
	}



	/**
	 * @see de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage#checkIfPageComplete()
	 */
	@Override
	protected boolean checkIfPageComplete() {
		return true;
	}

	/**
	 * @see de.tfsw.accounting.ui.expense.AbstractExpenseWizardPage#getHelper()
	 */
	@Override
	protected BaseExpenseEditHelper getHelper() {
		return helper;
	}
}
