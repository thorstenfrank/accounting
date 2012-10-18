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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.menus.MenuUtil;

import de.togginho.accounting.ui.AbstractAccountingEditor;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
public class ExpenseEditor extends AbstractAccountingEditor implements ExpenseEditingHelperCallback {
		
	// form 
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private ExpenseEditingHelper expenseEditingHelper;
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingEditor#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingEditor#getForm()
	 */
	@Override
	protected Form getForm() {
		return form.getForm();
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public ExpenseEditorInput getEditorInput() {
		return (ExpenseEditorInput) super.getEditorInput();
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		this.expenseEditingHelper = new ExpenseEditingHelper(getEditorInput().getExpense(), this);
		
		// init toolkit and overall layout manager
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.ExpenseEditor_title);
		form.getBody().setLayout(new GridLayout(2, true));
		
		// create the editor sections...
		createBasicSection();
		createPriceSection();
		createDepreciationSection();
		
		// paint the menu bar
		IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
		menuService.populateContributionManager(
				(ToolBarManager) form.getToolBarManager(), MenuUtil.toolbarUri(IDs.EDIT_EXPENSE_ID));
		
		form.getToolBarManager().update(true);
		
		toolkit.decorateFormHeading(form.getForm());
		form.reflow(true);
	}
	
	/**
	 * 
	 */
	private void createBasicSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText(Messages.labelBasicInformation);
		
		GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
		
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		
		expenseEditingHelper.createBasicSection(sectionClient);
		
		section.setClient(sectionClient);
	}
	
	/**
	 * 
	 */
	private void createPriceSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText(Messages.labelExpenses);
		
		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
		grabHorizontal.applyTo(section);
		
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		
		expenseEditingHelper.createPriceSection(sectionClient);
		
		section.setClient(sectionClient);
	}
	
	/**
	 * 
	 */
	private void createDepreciationSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Depreciation");
		
		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
		grabHorizontal.applyTo(section);
		
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		
		expenseEditingHelper.createDepreciationSection(sectionClient);
		
		section.setClient(sectionClient);
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.expense.ExpenseEditingHelperCallback#modelHasChanged()
	 */
	@Override
	public void modelHasChanged() {
		setIsDirty(true);
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.expense.ExpenseEditingHelperCallback#createLabel(org.eclipse.swt.widgets.Composite, java.lang.String)
	 */
	@Override
	public Label createLabel(Composite parent, String text) {
		return toolkit.createLabel(parent, text);
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.expense.ExpenseEditingHelperCallback#createText(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public Text createText(Composite parent, int style) {
		return createText(parent, null);
		//return toolkit.createText(parent, null, style);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			AccountingUI.getAccountingService().saveExpense(getEditorInput().getExpense());
			setIsDirty(false);
		} catch (Exception e) {
			showError(e);
		}
	}
}