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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
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

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.AnnualDepreciation;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.ui.AbstractAccountingEditor;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.expense.editing.ExpenseEditHelper;
import de.tfsw.accounting.ui.expense.editing.ExpenseEditingHelperClient;
import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class ExpenseEditor extends AbstractAccountingEditor implements ExpenseEditingHelperClient {

	private static final Logger LOG = LogManager.getLogger(ExpenseEditor.class);
	
	// form 
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private ExpenseEditHelper editHelper;
	
	private TableViewer deprecTableViewer;
	
	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractAccountingEditor#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractAccountingEditor#getForm()
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
		
		this.editHelper = new ExpenseEditHelper(getEditorInput().getExpense(), this);
		
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
		
		if (getEditorInput().getExpense().getDepreciationSchedule() != null) {
			updateDepreciationTable(false);
		} else {
			updateDepreciationTable(true);
		}
		
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
		
		editHelper.createBasicSection(sectionClient);
		
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
		
		editHelper.createPriceSection(sectionClient);
		
		section.setClient(sectionClient);
	}
	
	/**
	 * 
	 */
	private void createDepreciationSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText(Messages.ExpenseEditor_Depreciation);
		
		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
		grabHorizontal.applyTo(section);
		
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		
		editHelper.createDepreciationSection(sectionClient);
		
		section.setClient(sectionClient);
	}
	
	/**
	 * 
	 */
	private void createDepreciationTableSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText(Messages.ExpenseEditor_DepreciationSchedule);
		
		GridDataFactory.fillDefaults().grab(true, true).applyTo(section);
		
		Composite sectionClient = toolkit.createComposite(section);
		TableColumnLayout tcl = new TableColumnLayout();
		sectionClient.setLayout(tcl);
		
		deprecTableViewer = new TableViewer(sectionClient);
		
		deprecTableViewer.getTable().setHeaderVisible(true);
		deprecTableViewer.getTable().setLinesVisible(true);
		
		addColumn(0, Messages.labelYear, tcl, SWT.LEFT);
		addColumn(1, Messages.ExpenseEditor_BeginningBookValue, tcl, SWT.RIGHT);
		addColumn(2, Messages.ExpenseEditor_Depreciation, tcl, SWT.RIGHT);
		addColumn(3, Messages.ExpenseEditor_EndBookValue, tcl, SWT.RIGHT);
		
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
	private void updateDepreciationTable(boolean recalculate) {
		Expense expense = getEditorInput().getExpense();
		if (checkDepreciationPreConditions(expense)) {
			if (recalculate) {
				LOG.debug("Recalculating depreciation schedule"); //$NON-NLS-1$
				expense.setDepreciationSchedule(CalculationUtil.calculateDepreciationSchedule(expense));
				// this should only happen when opening an expense without a saved schedule for the first time
				if (!isDirty()) {
					setIsDirty(true);
					MessageDialog.openInformation(
							getSite().getShell(), 
							Messages.ExpenseEditor_DepreciationCalculatedTitle, 
							Messages.ExpenseEditor_DepreciationCalculatedMessage);
				}
			} else {
				LOG.debug("Using data from Expense model"); //$NON-NLS-1$
			}
			
			deprecTableViewer.setInput(expense.getDepreciationSchedule());
		} else {
			deprecTableViewer.setInput(null);
		}
		
		deprecTableViewer.refresh();
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkDepreciationPreConditions(Expense expense) {
		if (expense.getExpenseType() != null && expense.getExpenseType().isDepreciationPossible()) {
			if (expense.getDepreciationMethod() != null && expense.getDepreciationPeriodInYears() != null) {
				return expense.getDepreciationPeriodInYears() >= 0;
			}
		}
		
		return false; // default
	}
	
	/**
	 * {@inheritDoc}
	 * @see ExpenseEditingHelperCallback#modelHasChanged()
	 */
	@Override
	public void modelHasChanged() {
		updateDepreciationTable(true);
		setIsDirty(true);
	}

	/**
	 * {@inheritDoc}
	 * @see ExpenseEditingHelperCallback#createLabel(org.eclipse.swt.widgets.Composite, java.lang.String)
	 */
	@Override
	public Label createLabel(Composite parent, String text) {
		return toolkit.createLabel(parent, text);
	}

	/**
	 * {@inheritDoc}
	 * @see ExpenseEditingHelperCallback#createText(org.eclipse.swt.widgets.Composite, int)
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
	
	/**
	 * 
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractAccountingEditor#setIsDirty(boolean)
	 */
	@Override
	protected void setIsDirty(boolean dirty) {
		super.setIsDirty(dirty);
	}
	
	/**
	 * 
	 * @author thorsten
	 *
	 */
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