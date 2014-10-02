/*
 *  Copyright 2012, 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.model;

import de.tfsw.accounting.Messages;

/**
 * Calculation method for depreciation/amortization.
 * 
 * @author Thorsten Frank
 * @since  1.0
 * @see	   AnnualDepreciation
 * @see	   Expense#setDepreciationMethod(DepreciationMethod)
 */
public enum DepreciationMethod {
	
	/**
	 * Straightline depreciation/amortisation.
	 */
	STRAIGHTLINE(Messages.DepreciationMethod_STRAIGHTLINE),
	
	/**
	 * Immediate Write-Off
	 */
	FULL(Messages.DepreciationMethod_FULL);
	
	/**
	 * Translated name.
	 */
	private String translated;

	/**
	 * @param translated
	 */
	private DepreciationMethod(String translated) {
		this.translated = translated;
	}
	
	/**
	 * Returns a translated String describing this depreciation methods. 
	 * The locale used is the default locale derived from the framework.
	 * 
	 * @return a locale-sensitive string representation of this expense type
	 */
	public String getTranslatedString() {
		return translated != null ? translated : this.name();
	}
}
