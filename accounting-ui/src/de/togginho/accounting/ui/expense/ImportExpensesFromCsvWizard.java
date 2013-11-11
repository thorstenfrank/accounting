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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.togginho.accounting.model.ExpenseImportResult;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
public class ImportExpensesFromCsvWizard extends Wizard implements IImportWizard {
	
	private ImportWizardPageOne pageOne;
	private ImportWizardPageTwo pageTwo;

	private ExpenseImportResult result;
	
	/**
     * {@inheritDoc}.
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    	setWindowTitle(Messages.ImportExpensesWizard_title);
    	setNeedsProgressMonitor(true);
    }

	/**
     * {@inheritDoc}.
     * @see Wizard#addPages()
     */
    @Override
    public void addPages() {
    	pageOne = new ImportWizardPageOne();
    	pageTwo = new ImportWizardPageTwo();
	    addPage(pageOne);
	    addPage(pageTwo);
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @Override
    public boolean canFinish() {
    	return pageTwo.isPageComplete();
    }
    
	/**
	 * {@inheritDoc}.
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (pageTwo.getSelectedExpenses() != null) {
			AccountingUI.getAccountingService().saveExpenses(pageTwo.getSelectedExpenses());
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected ExpenseImportResult getImportResult() {
		if (result == null) {
			result = AccountingUI.getAccountingService().importExpenses(pageOne.getSourceFile(), pageOne.getParams());
		}
		return result;
	}
}
