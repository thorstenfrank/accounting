/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui;

import java.text.Collator;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author thorsten
 *
 */
public abstract class AbstractTableSorter<ModelClass> extends ViewerSorter {
	
	private int column = -1;

	private int direction = 1;

	private Class<? extends ModelClass> modelClass;
	
	/**
	 * 
	 * @param modelClass
	 */
	public AbstractTableSorter(Class<? extends ModelClass> modelClass) {
		super();
		this.modelClass = modelClass;
	}

	/**
	 * 
	 * @param modelClass
	 * @param collator
	 */
	public AbstractTableSorter(Class<? extends ModelClass> modelClass, Collator collator) {
		super(collator);
		this.modelClass = modelClass;
	}

	/**
	 * 
	 * @param index
	 */
	public void setSortColumnIndex(int index) {
		if (column == index) {
			// switch direction
			direction *= -1;
		} else {
			column = index;
			direction = 1;
		}
	}

	/**
     * {@inheritDoc}.
     * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
		if (viewer instanceof TableViewer) {
			((TableViewer) viewer).getTable().setSortDirection(direction < 0 ? SWT.DOWN : SWT.UP);
		}
		
		try {
			ModelClass mc1 = modelClass.cast(e1);
			ModelClass mc2 = modelClass.cast(e2);
			final int result = doCompare(mc1, mc2, column);
			return result * direction; // make sure the current direction is used
		} catch (ClassCastException e) {
			getLogger().warn("Couldn't cast input to model object class", e);
			return 0;
		}
    }

    /**
     * Can be called by views to enable sorting for the supplied column.
     * 
     * <p>
     * This method will add a {@link SelectionListener} to the supplied {@link TableColumn}, which in turn will
     * notify the {@link Table} contained in the supplied {@link TableViewer} that this column is now the sorting one.
     * Also, this sorter will be adapted.
     * </p>
     * 
     * @param tableViewer	the viewer containing a table to be sorted
     * @param col			the column in the table that should be sorted
     * @param columnIndex	the index of the sorting column - this is necessary because there's no way to retrieve this
     * 						information from the column itself
     */
    public void addSortingSupport(final TableViewer tableViewer, final TableColumn col, final int columnIndex) {
		col.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.getTable().setSortColumn(col);
				setSortColumnIndex(columnIndex);
				tableViewer.refresh();
			}
		});
    }
    
    /**
     * Called by {@link #compare(Viewer, Object, Object)} to handle the actual comparison of the two supplied objects.
     * 
     * @param e1			the first element
     * @param e2			the second element
     * @param columnIndex	the index within the table that points to the attribute to be compared
     * 
     * @return	<code>-1</code> if the attribute signified by <code>columnIndex</code> of <code>e1</code>is less than 
     * 			the value of the same attribute of <code>e2</code>, <code>0</code> if they are equal, <code>1</code> if
     * 			is more. 
     */
	protected abstract int doCompare(ModelClass e1, ModelClass e2, int columnIndex);
	
	/**
	 * Returns an implementation-specific {@link Logger}.
	 * 
	 * @return the logger to use
	 */
	protected abstract Logger getLogger();
}
