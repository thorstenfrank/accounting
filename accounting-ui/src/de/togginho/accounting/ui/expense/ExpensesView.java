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

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelChangeListener;
import de.togginho.accounting.util.FormatUtil;
import de.togginho.accounting.util.TimeFrame;
import de.togginho.accounting.util.TimeFrameType;

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
		
		final TableColumn colDate = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_DATE).getColumn();
		colDate.setText(Messages.labelDate);
		tcl.setColumnData(colDate, new ColumnWeightData(1, true));
		sorter.addSortingSupport(tableViewer, colDate, ExpenseTableSorter.COL_INDEX_DATE);
		
		final TableColumn colDesc = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_DESC).getColumn();
		colDesc.setText(Messages.labelDescription);
		tcl.setColumnData(colDesc, new ColumnWeightData(2, true));
		sorter.addSortingSupport(tableViewer, colDesc, ExpenseTableSorter.COL_INDEX_DESC);
		
		final TableColumn colCategory = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_CATEGORY).getColumn();
		colCategory.setText(Messages.labelCategory);
		tcl.setColumnData(colCategory, new ColumnWeightData(1, true));
		sorter.addSortingSupport(tableViewer, colCategory, ExpenseTableSorter.COL_INDEX_CATEGORY);
		
		final TableColumn colNet = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_NET).getColumn();
		colNet.setText(Messages.labelNet);
		colNet.setAlignment(SWT.RIGHT);
		tcl.setColumnData(colNet, new ColumnWeightData(1, true));
		sorter.addSortingSupport(tableViewer, colNet, ExpenseTableSorter.COL_INDEX_NET);
		
		final TableColumn colTax = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_TAX).getColumn();
		colTax.setText(Messages.labelTaxes);
		colTax.setAlignment(SWT.RIGHT);
		tcl.setColumnData(colTax, new ColumnWeightData(1, true));
		sorter.addSortingSupport(tableViewer, colTax, ExpenseTableSorter.COL_INDEX_TAX);

		final TableColumn colGross = new TableViewerColumn(tableViewer, SWT.NONE, ExpenseTableSorter.COL_INDEX_GROSS).getColumn();
		colGross.setText(Messages.labelGross);
		colGross.setAlignment(SWT.RIGHT);
		tcl.setColumnData(colGross, new ColumnWeightData(1, true));
		sorter.addSortingSupport(tableViewer, colGross, ExpenseTableSorter.COL_INDEX_GROSS);
		
		tableViewer.setLabelProvider(new ExpenseTableLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		this.currentTimeFrame = TimeFrame.currentMonth();
		sorter.setSortColumnIndex(ExpenseTableSorter.COL_INDEX_DATE);
		tableViewer.setComparator(sorter);
		table.setSortColumn(colDate);
		
		tableViewer.addDoubleClickListener(this);
		
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
		
		modelChanged();
	}
	
	/**
	 * 
	 * @return
	 */
	private Set<ExpenseWrapper> getExpenses() {
		final ExpenseCollection ec = AccountingUI.getAccountingService().findExpenses(currentTimeFrame);
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
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.ModelChangeListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		tableViewer.setInput(getExpenses());
		tableViewer.refresh();
		
		String titlePart = currentTimeFrame.getType().getTranslatedName();
		if (currentTimeFrame.getType() == TimeFrameType.CUSTOM) {
			StringBuilder sb = new StringBuilder(FormatUtil.formatDate(currentTimeFrame.getFrom()));
			sb.append(Constants.HYPHEN);
			sb.append(FormatUtil.formatDate(currentTimeFrame.getUntil()));
			titlePart = sb.toString();
		}
		
		setPartName(Messages.bind(Messages.ExpensesView_title, titlePart));
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
