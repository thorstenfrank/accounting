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
package de.tfsw.accounting.ui.expense;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.model.AbstractExpense;
import de.tfsw.accounting.ui.expense.editing.AbstractExpenseEditHelper;
import de.tfsw.accounting.ui.expense.editing.ExpenseEditingHelperClient;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author Thorsten Frank
 *
 */
public abstract class AbstractExpenseWizardPage extends WizardPage implements ExpenseEditingHelperClient {

	private AbstractExpense expense;
	
	/**
	 * @param pageName
	 */
	public AbstractExpenseWizardPage(String pageName, AbstractExpense expense) {
		super(pageName);
		this.expense = expense;
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public AbstractExpenseWizardPage(String pageName, String title, ImageDescriptor titleImage, AbstractExpense expense) {
		super(pageName, title, titleImage);
		this.expense = expense;
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract boolean checkIfPageComplete();
	
	/**
	 * @return the expense
	 */
	protected AbstractExpense getExpense() {
		return expense;
	}

	/**
	 * By default creates a {@link Composite} that includes the basic and price sections. Also calls 
	 * {@link #postCreateControl(Composite)} afterwards, so subclasses need only override this method if they need a
	 * completely different layout.
	 *  
	 * @see AbstractExpenseEditHelper#createBasicSection(Composite)
	 * @see AbstractExpenseEditHelper#createPriceSection(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		AbstractExpenseEditHelper editHelper = getHelper();
		
		editHelper.createBasicSection(composite);
		
		editHelper.createPriceSection(composite);
		
		postCreateControl(composite);
		
		setControl(composite);
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract AbstractExpenseEditHelper getHelper();
	
	/**
	 * Called by {@link #createControl(Composite)} after the common UI elements have been built.
	 * Subclasses may use this to add specific elements.
	 * 
	 * @param composite the main area composite
	 */
	protected void postCreateControl(Composite composite) {
		
	}
	
	/**
	 * {@inheritDoc}
	 * @see ExpenseEditingHelperClient#modelHasChanged()
	 */
	@Override
	public void modelHasChanged() {
		setPageComplete(checkIfPageComplete());
	}

	/**
	 * {@inheritDoc}
	 * @see ExpenseEditingHelperClient#createLabel(org.eclipse.swt.widgets.Composite, java.lang.String)
	 */
	@Override
	public Label createLabel(Composite parent, String text) {
		return WidgetHelper.createLabel(parent, text);
	}

	/**
	 * {@inheritDoc}
	 * @see ExpenseEditingHelperClient#createText(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public Text createText(Composite parent, int style) {
		return WidgetHelper.createSingleBorderText(parent, null);
	}
}
