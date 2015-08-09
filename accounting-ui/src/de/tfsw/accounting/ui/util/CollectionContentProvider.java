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
package de.tfsw.accounting.ui.util;

import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;

/**
 * A content provider for arrays and {@link Collection}s very similar to {@link ArrayContentProvider}, with the
 * exception that this class can also add an empty element at the top of the list. This is necessary e.g. for 
 * {@link ComboViewer} instances where a "non-selection" is needed.
 * 
 * @author thorsten
 *
 */
public class CollectionContentProvider implements IStructuredContentProvider {
	
	public static final CollectionContentProvider NO_EMPTY = new CollectionContentProvider(false);
	
	public static final CollectionContentProvider WITH_EMPTY = new CollectionContentProvider(true);
	
	/**
	 * 
	 */
	private boolean includeEmptyElement;
	
	/**
	 * @param includeEmptyElement
	 */
	public CollectionContentProvider(boolean includeEmptyElement) {
		this.includeEmptyElement = includeEmptyElement;
	}

	/**
	 * {@inheritDoc}
	 * @see IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// nothing to do here

	}

	/**
	 * {@inheritDoc}
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// nothing to do here
	}

	/**
	 * {@inheritDoc}
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		Object[] workOnMe = null;
		if (inputElement instanceof Object[]) {
			workOnMe = (Object[]) inputElement;
		} else if (inputElement instanceof Collection) {
			workOnMe = ((Collection<?>) inputElement).toArray();
		} else {
			workOnMe = new Object[0];
		}
		
		if (includeEmptyElement) {
			Object[] returnMe = new Object[workOnMe.length + 1];
			returnMe[0] = StructuredSelection.EMPTY;
			for (int index = 0; index < workOnMe.length; index++) {
				returnMe[index + 1] = workOnMe[index];
			}
			return returnMe;
		} else {
			return workOnMe;
		}
	}

}
