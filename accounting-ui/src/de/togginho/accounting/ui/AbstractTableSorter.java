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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;

/**
 * @author thorsten
 *
 */
public abstract class AbstractTableSorter extends ViewerSorter {
	
	private int column = -1;

	private int direction = 1;

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
		
		return doCompare(e1, e2, column) * direction;
    }
	
    /**
     * 
     * @param e1
     * @param e2
     * @param columnIndex
     * @return
     */
	protected abstract int doCompare(Object e1, Object e2, int columnIndex);
}
