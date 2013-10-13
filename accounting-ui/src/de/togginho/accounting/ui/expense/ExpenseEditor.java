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

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.menus.MenuUtil;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.AnnualDepreciation;
import de.togginho.accounting.ui.AbstractAccountingEditor;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class ExpenseEditor extends AbstractAccountingEditor implements ExpenseEditingHelperCallback {
	
	private static final Logger LOG = Logger.getLogger(ExpenseEditor.class);
	
	// form 
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private ExpenseEditingHelper expenseEditingHelper;
	
	private TableViewer deprecTableViewer;
	
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
		createDepreciationTableSection();
		
		// paint the menu bar
		IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
		menuService.populateContributionManager(
				(ToolBarManager) form.getToolBarManager(), MenuUtil.toolbarUri(IDs.EDIT_EXPENSE_ID));
		
		form.getToolBarManager().update(true);
		
		toolkit.decorateFormHeading(form.getForm());
		form.reflow(true);
		
		updateDepreciationTable();
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
	 * 
	 */
	private void createDepreciationTableSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Depreciation Table");
		
		GridDataFactory.fillDefaults().grab(true, true).applyTo(section);
		
		Composite sectionClient = toolkit.createComposite(section);
		TableColumnLayout tcl = new TableColumnLayout();
		sectionClient.setLayout(tcl);
		
		deprecTableViewer = new TableViewer(sectionClient);
		
		deprecTableViewer.getTable().setHeaderVisible(true);
		deprecTableViewer.getTable().setLinesVisible(true);
		
		addColumn(0, "Year", tcl, SWT.LEFT);
		addColumn(1, "Book Value", tcl, SWT.RIGHT);
		addColumn(2, "Annual Amount", tcl, SWT.RIGHT);
		addColumn(3, "End of Year Value", tcl, SWT.RIGHT);
		
		deprecTableViewer.setLabelProvider(new DeprecTableLabelProvider());
		deprecTableViewer.setContentProvider(new ArrayContentProvider());
		section.setClient(sectionClient);
	}
	
	/**
	 * 
	 * @param index
	 * @param label
	 * @param tcl
	 * @param alignment
	 */
	private void addColumn(int index, String label, TableColumnLayout tcl, int alignment) {
		final TableColumn col = new TableViewerColumn(deprecTableViewer, SWT.NONE, index).getColumn();
		col.setText(label);
		col.setAlignment(alignment);
		tcl.setColumnData(col, new ColumnWeightData(10));
	}
	
	/**
	 * 
	 */
	private void updateDepreciationTable() {
		deprecTableViewer.setInput(CalculationUtil.calculateDepreciationSchedule(getEditorInput().getExpense()));
		deprecTableViewer.refresh();
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.expense.ExpenseEditingHelperCallback#modelHasChanged()
	 */
	@Override
	public void modelHasChanged() {
		updateDepreciationTable();
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
	
	@Override
	protected void setIsDirty(boolean dirty) {
		super.setIsDirty(dirty);
	}
	
	private class DeprecTableLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
	        return null;
        }

		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
        	AnnualDepreciation ad = (AnnualDepreciation) element;
        	
        	switch (columnIndex) {
			case 0:
				return Constants.EMPTY_STRING + ad.getYear();
			case 1:
				return FormatUtil.formatCurrency(ad.getBeginningOfYearBookValue());
			case 2:
				return FormatUtil.formatCurrency(ad.getDepreciationAmount());
			case 3:
				return FormatUtil.formatCurrency(ad.getEndOfYearBookValue());
			default:
				return Constants.HYPHEN;
			}
        }
		
	}
}