/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

/**
 * @author thorsten
 *
 */
public abstract class AbstractTableView extends ViewPart implements IDoubleClickListener {

	/**
	 * 
	 * @return
	 */
	protected abstract Logger getLogger();
	
	/**
	 * 
	 * @return
	 */
	protected abstract TableViewer getTableViewer();
	
	/**
	 * 
	 * @return
	 */
	protected abstract AbstractTableSorter<?> getTableSorter();

	/**
	 * 
	 * @return
	 */
	protected abstract String getDoubleClickCommand();
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		getTableViewer().getControl().setFocus();
	}
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		final String command = getDoubleClickCommand();
		
		if (command == null) {
			getLogger().warn("Double Click Command was <null>, aborting..."); //$NON-NLS-1$
		} else {
			IHandlerService handlerService = 
					(IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
				
			try {
				handlerService.executeCommand(getDoubleClickCommand(), new Event());
			} catch (Exception e) {
				getLogger().error(String.format("Error while running double-click command [%s]", getDoubleClickCommand()), e); //$NON-NLS-1$
			}			
		}
	}
	
	/**
	 * 
	 * @param index
	 * @param label
	 * @param tcl
	 * @param weight
	 * @return
	 */
	protected TableColumn addColumn(int index, String label, TableColumnLayout tcl, int weight) {
		return addColumn(index, label, SWT.LEFT, tcl, weight, true);
	}
	
	/**
	 * 
	 * @param index
	 * @param label
	 * @param alignment
	 * @param tcl
	 * @param weight
	 * @param addSortingSupport
	 * @return
	 */
	protected TableColumn addColumn(int index, String label, int alignment, TableColumnLayout tcl, int weight, boolean addSortingSupport) {
		final TableColumn col = new TableViewerColumn(getTableViewer(), SWT.NONE, index).getColumn();
		col.setText(label);
		col.setAlignment(alignment);
		tcl.setColumnData(col, new ColumnWeightData(weight, true));
		if (addSortingSupport) {
			addSortingSupport(col, index);
		}
		return col;
	}
	
	/**
	 * 
	 * @param col
	 * @param columnIndex
	 */
	private void addSortingSupport(final TableColumn col, final int columnIndex) {
		col.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getTableViewer().getTable().setSortColumn(col);
				getTableSorter().setSortColumnIndex(columnIndex);
				getTableViewer().refresh();
			}
		});
	}
}
