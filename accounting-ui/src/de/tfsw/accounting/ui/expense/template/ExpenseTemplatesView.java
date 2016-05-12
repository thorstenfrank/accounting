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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.tfsw.accounting.ui.AbstractTableSorter;
import de.tfsw.accounting.ui.AbstractTableView;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.ModelChangeListener;

/**
 * @author Thorsten Frank
 *
 */
public class ExpenseTemplatesView extends AbstractTableView implements ModelChangeListener {

	private static final Logger LOG = LogManager.getLogger(ExpenseTemplatesView.class);
	
	private TableViewer tableViewer;
	private ExpenseTemplateTableSorter sorter;
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		AccountingUI.addModelChangeListener(this);
		
		Composite tableComposite = new Composite(parent, SWT.NONE);
		TableColumnLayout tcl = new TableColumnLayout();
		tableComposite.setLayout(tcl);
		
		tableViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION | SWT.MULTI);
		getSite().setSelectionProvider(tableViewer);
		
		sorter = new ExpenseTemplateTableSorter();
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		addColumn(ExpenseTemplateTableSorter.COL_ACTIVE, Messages.ExpenseTemplateEditHelper_active, tcl, 20);
		TableColumn colFirstAppl = addColumn(ExpenseTemplateTableSorter.COL_FIRST_APPLICATION, Messages.ExpenseTemplateEditHelper_validFrom, tcl, 20);
		addColumn(ExpenseTemplateTableSorter.COL_DESC, Messages.labelDescription, tcl, 20);
		addColumn(ExpenseTemplateTableSorter.COL_NET, Messages.labelNet, tcl, 20);
		addColumn(ExpenseTemplateTableSorter.COL_FREQUENCY, Messages.ExpenseTemplateEditHelper_frequency, tcl, 20);
		
		tableViewer.setLabelProvider(new ExpenseTemplateLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		sorter.setSortColumnIndex(ExpenseTemplateTableSorter.COL_FIRST_APPLICATION);
		tableViewer.setComparator(sorter);
		table.setSortColumn(colFirstAppl);
		
		tableViewer.addDoubleClickListener(this);
		
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
		
		modelChanged();
	}
	
	@Override
	public void dispose() {
		AccountingUI.removeModelChangeListener(this);
		super.dispose();
	}
	
	/**
	 * @see de.tfsw.accounting.ui.ModelChangeListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		tableViewer.setInput(AccountingUI.getAccountingService().getExpenseTemplates());
		tableViewer.refresh();
	}
	
	/**
	 * @see de.tfsw.accounting.ui.AbstractTableView#getTableViewer()
	 */
	@Override
	protected TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * @see de.tfsw.accounting.ui.AbstractTableView#getTableSorter()
	 */
	@Override
	protected AbstractTableSorter<?> getTableSorter() {
		return sorter;
	}

	/**
	 * @see de.tfsw.accounting.ui.AbstractTableView#getDoubleClickCommand()
	 */
	@Override
	protected String getDoubleClickCommand() {
		return IDs.CMD_EDIT_EXPENSE_TEMPLATE;
	}
	
	/**
	 * @see de.tfsw.accounting.ui.AbstractTableView#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
