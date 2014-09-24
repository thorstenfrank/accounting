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
package de.tfsw.accounting.ui.conversion;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author thorsten
 *
 */
public class FromStructuredSelectionConverter implements IConverter {

	private Class<?> toClass;
	
	/**
	 * @param toClass
	 */
	public FromStructuredSelectionConverter(Class<?> toClass) {
		this.toClass = toClass;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		return StructuredSelection.class;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		return toClass;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		if (fromObject instanceof StructuredSelection) {
			StructuredSelection selection = (StructuredSelection) fromObject;
			if (!selection.isEmpty() && toClass.isInstance(selection.getFirstElement().getClass())) {
				return selection.getFirstElement();
			} else {
				return null;
			}
		}
		return fromObject;
	}

}
