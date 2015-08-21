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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.model.AbstractExpense;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;

/**
 * @author Thorsten Frank
 *
 */
public class ExpenseTemplateWizard extends Wizard implements IWorkbenchWizard {
	
	private ExpenseTemplate expenseTemplate;
	
	/**
	 * 
	 */
	public ExpenseTemplateWizard() {
		this(null);
	}
	
	/**
	 * 
	 * @param copyFrom
	 */
	public ExpenseTemplateWizard(AbstractExpense copyFrom) {
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.ExpenseTemplateWizard_Title);
		expenseTemplate = new ExpenseTemplate();
		if (copyFrom != null) {
			expenseTemplate.setCategory(copyFrom.getCategory());
			expenseTemplate.setDescription(copyFrom.getDescription());
			expenseTemplate.setExpenseType(copyFrom.getExpenseType());
			expenseTemplate.setNetAmount(copyFrom.getNetAmount());
			expenseTemplate.setTaxRate(copyFrom.getTaxRate());
		}
	}
	
	@Override
	public void addPages() {
		addPage(new ExpenseTemplateWizardPage(expenseTemplate));
		addPage(new RecurrenceRuleWizardPage(expenseTemplate));
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
		try {
			AccountingUI.getAccountingService().saveExpenseTemplate(expenseTemplate);
			return true;
		} catch (AccountingException e) {
        	MessageBox msgBox = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK);
        	msgBox.setMessage(Messages.labelError);
        	msgBox.setText(e.getMessage());
			msgBox.open();
			return false;
		}
	}
}
