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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.tfsw.accounting.ui.AbstractAccountingEditor;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.expense.editing.ExpenseEditingHelperClient;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author Thorsten Frank
 *
 */
public class ExpenseTemplateEditor extends AbstractAccountingEditor implements ExpenseEditingHelperClient {

	private FormToolkit toolkit;
	private ScrolledForm form;
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		ExpenseTemplateEditHelper helper = new ExpenseTemplateEditHelper(getEditorInput().getExpenseTemplate(), this);
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Form Title");
		form.getBody().setLayout(new GridLayout(2, true));
		
		createTemplateSection(helper);
		createRecurrenceSection(helper);
		
		form.getToolBarManager().update(true);
		
		toolkit.decorateFormHeading(form.getForm());
		form.reflow(true);
	}
	
	/**
	 * 
	 */
	private void createTemplateSection(ExpenseTemplateEditHelper helper) {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Template Section Text");
		WidgetHelper.grabHorizontal(section);
		
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		
		helper.createBasicSection(sectionClient);
		helper.createPriceSection(sectionClient);
		
		section.setClient(sectionClient);
	}
	
	/**
	 * 
	 */
	private void createRecurrenceSection(ExpenseTemplateEditHelper helper) {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Recurrence Section Text");
		WidgetHelper.grabHorizontal(section);
		
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		
		helper.createRecurrenceSection(sectionClient);
		
		section.setClient(sectionClient);
	}
	
	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			AccountingUI.getAccountingService().saveExpenseTemplate(getEditorInput().getExpenseTemplate());
			setIsDirty(false);
		} catch (Exception e) {
			showError(e);
		}
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public ExpenseTemplateEditorInput getEditorInput() {
		return (ExpenseTemplateEditorInput) super.getEditorInput();
	}
	
	/**
	 * @see de.tfsw.accounting.ui.expense.editing.ExpenseEditingHelperClient#modelHasChanged()
	 */
	@Override
	public void modelHasChanged() {
		setIsDirty(true);
	}

	/**
	 * @see de.tfsw.accounting.ui.expense.editing.ExpenseEditingHelperClient#createLabel(org.eclipse.swt.widgets.Composite, java.lang.String)
	 */
	@Override
	public Label createLabel(Composite parent, String text) {
		return toolkit.createLabel(parent, text);
	}

	/**
	 * @see de.tfsw.accounting.ui.expense.editing.ExpenseEditingHelperClient#createText(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public Text createText(Composite parent, int style) {
		return createText(parent, null);
	}

	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingEditor#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingEditor#getForm()
	 */
	@Override
	protected Form getForm() {
		return form.getForm();
	}
}
