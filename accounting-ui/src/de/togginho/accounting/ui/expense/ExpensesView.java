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

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelChangeListener;
import de.togginho.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class ExpensesView extends ViewPart implements IDoubleClickListener, ModelChangeListener {
	
	private static final Logger LOG = Logger.getLogger(ExpensesView.class);
	
	private TableViewer tableViewer;
	private ExpenseTableSorter sorter;
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
		
		sorter = new ExpenseTableSorter();
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableViewerColumn col1 = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_DATE);
		final TableColumn col1col = col1.getColumn();
		col1col.setText(Messages.labelDate);
		tcl.setColumnData(col1col, new ColumnWeightData(18, true));
		sorter.addSortingSupport(tableViewer, col1col, ExpenseTableSorter.COL_INDEX_DATE);
		
		TableViewerColumn col2 = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_DESC);
		final TableColumn col2col = col2.getColumn();
		col2col.setText(Messages.labelDescription);
		tcl.setColumnData(col2col, new ColumnWeightData(28, true));
		sorter.addSortingSupport(tableViewer, col2col, ExpenseTableSorter.COL_INDEX_DESC);
		
		TableViewerColumn col3 = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_NET);
		final TableColumn col3col = col3.getColumn();
		col3col.setText(Messages.labelNet);
		col3col.setAlignment(SWT.RIGHT);
		tcl.setColumnData(col3col, new ColumnWeightData(18, true));
		sorter.addSortingSupport(tableViewer, col3col, ExpenseTableSorter.COL_INDEX_NET);
		
		TableViewerColumn col4 = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_TAX);
		final TableColumn col4col = col4.getColumn();
		col4col.setText(Messages.labelTaxes);
		col4col.setAlignment(SWT.RIGHT);
		tcl.setColumnData(col4col, new ColumnWeightData(18, true));
		sorter.addSortingSupport(tableViewer, col4col, ExpenseTableSorter.COL_INDEX_TAX);

		TableViewerColumn col5 = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_GROSS);
		final TableColumn col5col = col5.getColumn();
		col5col.setText(Messages.labelGross);
		col5col.setAlignment(SWT.RIGHT);
		tcl.setColumnData(col5col, new ColumnWeightData(18, true));
		sorter.addSortingSupport(tableViewer, col5col, ExpenseTableSorter.COL_INDEX_GROSS);
		
		tableViewer.setLabelProvider(new ExpenseTableLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		this.currentTimeFrame = TimeFrame.currentMonth();
		tableViewer.setInput(getExpenses());
		sorter.setSortColumnIndex(ExpenseTableSorter.COL_INDEX_DATE);
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
		IHandlerService handlerService = 
			(IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		
		try {
			handlerService.executeCommand(IDs.CMD_EDIT_EXPENSE, new Event());
		} catch (Exception e) {
			LOG.error("Error opening invoice editor", e); //$NON-NLS-1$
		}
		
//		IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
//		ExpenseWrapper wrapper = (ExpenseWrapper) structuredSelection.getFirstElement();
//		ExpenseWizard wizard = new ExpenseWizard(wrapper.getExpense());
//		WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
//		dialog.open();
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
}
