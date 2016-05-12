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
package de.tfsw.accounting.ui.util;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tfsw.accounting.Constants;

/**
 * @author Thorsten Frank
 *
 */
public class GenericLabelProvider extends StringLabelProvider {
	
	private static final Logger LOG = LogManager.getLogger(GenericLabelProvider.class);
	
	private Class<?> targetClass;
	
	private String methodName;
	
	private String unknownElement = Constants.HYPHEN;
	
	/**
	 * @param targetClass
	 * @param methodName full name of the method to use retrieving a label string
	 */
	public GenericLabelProvider(Class<?> targetClass, String methodName) {
		this.targetClass = targetClass;
		this.methodName = methodName;
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element != null && targetClass.isInstance(element)) {
			try {
				Method method = element.getClass().getMethod(methodName);
				if (String.class.isAssignableFrom(method.getReturnType())) {
					return (String) method.invoke(element);
				}
			} catch (Exception e) {
				LOG.error("Error retrieving label from method " + methodName, e);
			}			
		}

		
		return unknownElement;
	}
}
