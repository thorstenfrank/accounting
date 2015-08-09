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

import org.eclipse.jface.viewers.LabelProvider;

import de.tfsw.accounting.Constants;

/**
 * @author Thorsten Frank
 *
 */
public class StringLabelProvider extends LabelProvider {

	public static StringLabelProvider DEFAULT = new StringLabelProvider();
	
	private String unkonwnString = Constants.HYPHEN;
	
	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			return (String) element;
		}
		
		return unkonwnString;
	}

	/**
	 * The string returned by {@link #getText(Object)} if a label text cannot be retrieved from the supplied element.
	 * 
	 * <p>The default is {@link Constants#HYPHEN}</p>
	 * 
	 * @return default string for unkonwn elements returned by {@link #getText(Object)} 
	 */
	public String getUnkonwnString() {
		return unkonwnString;
	}

	/**
	 * Define the string returned by {@link #getText(Object)} if a label text cannot be retrieved from the supplied 
	 * element.
	 * 
	 * <p>The default is {@link Constants#HYPHEN}</p>
	 * 
	 * @param unkonwnString the new default label string for unkonwn elements
	 */
	public void setUnkonwnString(String unkonwnString) {
		this.unkonwnString = unkonwnString;
	}
}
