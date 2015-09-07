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
package de.tfsw.accounting.model;

import java.io.Serializable;

import de.tfsw.accounting.Messages;

/**
 * An {@link Expense} can be grouped by its type: operational, capital or other.
 * This type also drives whether an expense may provide tax incentives or must be depreciated or amortized over its
 * usage period.
 * 
 * @author thorsten
 *
 */
public enum ExpenseType implements Serializable {

	/**
	 * Operating Expense.
	 */
	OPEX(false), 
	
	/**
	 * Capital Expense.
	 */
	CAPEX(true),
	
	/**
	 * Other expenses.
	 */
	OTHER(false);
	
	/**
	 * Flag whether depreciation is possible with this type of expense.
	 */
	private boolean depreciationPossible;
	
	/**
	 * Creates a new ExpenseType.
	 * 
	 * @param depreciationPossible whether or not expenses of this type can be depreciated/amortized
	 */
	private ExpenseType(boolean depreciationPossible) {
		this.depreciationPossible = depreciationPossible;
	}
	
	/**
	 * Returns a translated String describing this expense type. 
	 * The locale used is the default locale derived from the framework.
	 * 
	 * @return a locale-sensitive string representation of this expense type
	 */
	public String getTranslatedString() {
		return Messages.translate(this);
	}

	/**
	 * Returns whether depreciation is possible for this type of expense.
	 * 
	 * @return <code>true</code> if depreciation is possible with this expense type, <code>false</code> if not
	 */
	public boolean isDepreciationPossible() {
		return depreciationPossible;
	}
}
