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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.ui.AbstractTableSorter;
import de.togginho.accounting.ui.AbstractTableView;
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
public class ExpensesView extends AbstractTableView implements ModelChangeListener {
	
	private static final Logger LOG = Logger.getLogger(ExpensesView.class);
	
	private TableViewer tableViewer;
	private ExpenseTableSorter sorter;
	private TimeFrame currentTimeFrame;
	private Set<ExpenseType> selectedTypes;
	private ExpenseCollection expenseCollection;
	private IContextActivation contextActivation;
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		AccountingUI.addModelChangeListener(this);
		
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextActivation = contextService.activateContext(getClass().getPackage().getName());
		
		Composite tableComposite = new Composite(parent, SWT.NONE);
		TableColumnLayout tcl = new TableColumnLayout();
		tableComposite.setLayout(tcl);
		
		tableViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION | SWT.MULTI);
		getSite().setSelectionProvider(tableViewer);
		
		sorter = new ExpenseTableSorter();
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn colDate = addColumn(ExpenseTableSorter.COL_INDEX_DATE, Messages.labelDate, tcl, 1);
		addColumn(ExpenseTableSorter.COL_INDEX_DESC, Messages.labelDescription, tcl, 2);
		addColumn(ExpenseTableSorter.COL_INDEX_CATEGORY, Messages.labelCategory, tcl, 1);
		addColumn(ExpenseTableSorter.COL_INDEX_NET, Messages.labelNet, SWT.RIGHT, tcl, 1, true);
		addColumn(ExpenseTableSorter.COL_INDEX_TAX, Messages.labelTaxes, SWT.RIGHT, tcl, 1, true);
		addColumn(ExpenseTableSorter.COL_INDEX_GROSS, Messages.labelGross, SWT.RIGHT, tcl, 1, true);
				
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
		
		// by default, select all known expense types
		selectedTypes = new HashSet<ExpenseType>();
		for (ExpenseType type : ExpenseType.values()) {
			selectedTypes.add(type);
		}
		
		modelChanged();
	}
	
	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.ui.AbstractTableView#getLogger()
     */
    @Override
    protected Logger getLogger() {
    	return LOG;
    }

	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.ui.AbstractTableView#getDoubleClickCommand()
     */
    @Override
    protected String getDoubleClickCommand() {
	    return IDs.CMD_EDIT_EXPENSE;
    }

	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.ui.AbstractTableView#getTableViewer()
     */
    @Override
    protected TableViewer getTableViewer() {
	    return tableViewer;
    }

	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.ui.AbstractTableView#getTableSorter()
     */
    @Override
    protected AbstractTableSorter<?> getTableSorter() {
	    return sorter;
    }
    
	/**
	 * 
	 */
	@Override
	public void dispose() {
		// unregister myself as a listener
		AccountingUI.removeModelChangeListener(this);
		
		// unregister the context
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.deactivateContext(contextActivation);
		
		super.dispose();
	}
	
	/**
	 * 
	 * @param timeFrame
	 */
	protected void updateExpenses(TimeFrame timeFrame) {
		if (timeFrame != null) {
			this.currentTimeFrame = timeFrame;
		}
		
		ExpenseType[] types = null;
		if (selectedTypes != null && !selectedTypes.isEmpty()) {
			types = (ExpenseType[]) selectedTypes.toArray(new ExpenseType[selectedTypes.size()]);
		}
		
		expenseCollection = AccountingUI.getAccountingService().findExpenses(timeFrame, types);
		final Set<ExpenseWrapper> wrappers = new HashSet<ExpenseWrapper>(expenseCollection.getExpenses().size());
		
		for (Expense expense : expenseCollection.getExpenses()) {
			wrappers.add(new ExpenseWrapper(expense));
		}
		
		tableViewer.setInput(wrappers);
		tableViewer.refresh();
		
		StringBuilder title = new StringBuilder();
		if (timeFrame == null) {
			title.append(Messages.ExpensesView_allExpenses);
		} else if (timeFrame.getType() == TimeFrameType.CUSTOM) {
			title.append(FormatUtil.formatDate(timeFrame.getFrom()));
			title.append(Constants.HYPHEN);
			title.append(FormatUtil.formatDate(timeFrame.getUntil()));
		} else {
			title.append(timeFrame.getType().getTranslatedName());
		}
		
		setPartName(Messages.bind(Messages.ExpensesView_title, title.toString()));
		getViewSite().getActionBars().getStatusLineManager().setMessage(
				AccountingUI.getImageDescriptor(Messages.iconsExpenses).createImage(),
				Messages.bind(Messages.ExpensesView_statusLine, wrappers.size()));
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.ModelChangeListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		updateExpenses(currentTimeFrame);
	}	
	
	/**
	 * @return the currentTimeFrame
	 */
	protected TimeFrame getCurrentTimeFrame() {
		return currentTimeFrame;
	}

	/**
	 * @return the selectedTypes
	 */
	protected Set<ExpenseType> getSelectedTypes() {
		return selectedTypes;
	}
	
	/**
	 * 
	 * @return
	 */
	protected ExpenseCollection getExpenseCollection() {
		return expenseCollection;
	}
}
