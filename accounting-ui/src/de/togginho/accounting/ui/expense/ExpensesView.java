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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelChangeListener;
import de.togginho.accounting.ui.wizard.EditExpenseWizard;
import de.togginho.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class ExpensesView extends ViewPart implements IDoubleClickListener, ModelChangeListener {
	
	protected static final int COL_INDEX_DATE = 0;
	protected static final int COL_INDEX_DESC = 1;
	protected static final int COL_INDEX_NET = 2;
	protected static final int COL_INDEX_TAX = 3;
	protected static final int COL_INDEX_GROSS = 4;
	
	private TableViewer tableViewer;
	private ExpensesViewTableSorter sorter;
	private TimeFrame currentTimeFrame;
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		AccountingUI.addModelChangeListener(this);
		
		Composite tableComposite = new Composite(parent, SWT.NONE);
		TableColumnLayout tcl = new TableColumnLayout();
		tableComposite.setLayout(tcl);
		
		tableViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);
		getSite().setSelectionProvider(tableViewer);
		
		sorter = new ExpensesViewTableSorter();
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableViewerColumn col1 = new TableViewerColumn(tableViewer, SWT.NONE, COL_INDEX_DATE);
		final TableColumn col1col = col1.getColumn();
		col1col.setText(Messages.labelDate);
		tcl.setColumnData(col1col, new ColumnWeightData(20, true));
		sorter.addSortingSupport(tableViewer, col1col, COL_INDEX_DATE);
		
		TableViewerColumn col2 = new TableViewerColumn(tableViewer, SWT.NONE, COL_INDEX_DESC);
		final TableColumn col2col = col2.getColumn();
		col2col.setText(Messages.labelDescription);
		tcl.setColumnData(col2col, new ColumnWeightData(20, true));
		sorter.addSortingSupport(tableViewer, col2col, COL_INDEX_DESC);
		
		TableViewerColumn col3 = new TableViewerColumn(tableViewer, SWT.NONE, COL_INDEX_NET);
		final TableColumn col3col = col3.getColumn();
		col3col.setText(Messages.labelNet);
		tcl.setColumnData(col3col, new ColumnWeightData(20, true));
		sorter.addSortingSupport(tableViewer, col3col, COL_INDEX_NET);
		
		TableViewerColumn col4 = new TableViewerColumn(tableViewer, SWT.NONE, COL_INDEX_TAX);
		final TableColumn col4col = col4.getColumn();
		col4col.setText(Messages.labelTaxRate);
		tcl.setColumnData(col4col, new ColumnWeightData(20, true));
		sorter.addSortingSupport(tableViewer, col4col, COL_INDEX_TAX);

		TableViewerColumn col5 = new TableViewerColumn(tableViewer, SWT.NONE, COL_INDEX_GROSS);
		final TableColumn col5col = col5.getColumn();
		col5col.setText(Messages.labelGross);
		tcl.setColumnData(col5col, new ColumnWeightData(20, true));
		sorter.addSortingSupport(tableViewer, col5col, COL_INDEX_GROSS);
		
		tableViewer.setLabelProvider(new ExpenseLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		this.currentTimeFrame = TimeFrame.thisMonth();
		tableViewer.setInput(getExpenses());
		sorter.setSortColumnIndex(COL_INDEX_DATE);
		tableViewer.setComparator(sorter);
		table.setSortColumn(col1col);
		
		tableViewer.addDoubleClickListener(this);
		
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
	}
	
	/**
	 * 
	 * @return
	 */
	private Set<ExpenseWrapper> getExpenses() {
		final ExpenseCollection ec = AccountingUI.getAccountingService().getExpenses(currentTimeFrame);
		final Set<ExpenseWrapper> wrappers = new HashSet<ExpenseWrapper>(ec.getExpenses().size());
		
		for (Expense expense : ec.getExpenses()) {
			wrappers.add(new ExpenseWrapper(expense));
		}
		
		return wrappers;
	}
	
	/**
	 * 
	 */
	@Override
	public void dispose() {
		// unregister myself as a listener
		AccountingUI.removeModelChangeListener(this);
		super.dispose();
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
		ExpenseWrapper wrapper = (ExpenseWrapper) structuredSelection.getFirstElement();
		EditExpenseWizard wizard = new EditExpenseWizard(wrapper.getExpense());
		WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
		dialog.open();
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.ModelChangeListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		tableViewer.setInput(getExpenses());
		tableViewer.refresh();
	}	
	
	/**
	 * @return the currentTimeFrame
	 */
	protected TimeFrame getCurrentTimeFrame() {
		return currentTimeFrame;
	}

	/**
	 * @param currentTimeFrame the currentTimeFrame to set
	 */
	protected void setCurrentTimeFrame(TimeFrame newTimeFrame) {
		if (newTimeFrame != null) {
			this.currentTimeFrame = newTimeFrame;
		}
	}

	/**
	 *
	 */
	private class ExpenseLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

		/**
		 * {@inheritDoc}
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			ExpenseWrapper wrapper = (ExpenseWrapper) element;
			switch (columnIndex) {
			case COL_INDEX_DATE:
				return wrapper.getPaymentDateFormatted();
			case COL_INDEX_DESC:
				return wrapper.getDescription();
			case COL_INDEX_NET:
				return wrapper.getNetAmountFormatted();
			case COL_INDEX_TAX:
				return wrapper.getTaxAmountFormatted();
			case COL_INDEX_GROSS:
				return wrapper.getGrossAmountFormatted();
			default:
				return Constants.HYPHEN;
			}
		}	
	}
}
