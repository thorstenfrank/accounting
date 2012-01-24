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
package de.togginho.accounting.model;

import java.io.Serializable;

import de.togginho.accounting.Messages;

/**
 * An {@link Expense} is either operational ("OPEX") or capital ("CAPEX").
 * 
 * @author thorsten
 *
 */
public enum ExpenseType implements Serializable {

	/**
	 * Operating Expense.
	 */
	OPEX(Messages.ExpenseType_OPEX, false), 
	
	/**
	 * Capital Expense.
	 */
	CAPEX(Messages.ExpenseType_CAPEX, true);
	
	/**
	 * Translated name of this type.
	 */
	private String translated;
	
	/**
	 * Flag whether depreciation is possible with this type of expense.
	 */
	private boolean depreciationPossible;
	
	/**
	 * Creates a new ExpenseType.
	 * @param translation
	 */
	private ExpenseType(String translation, boolean depreciationPossible) {
		this.translated = translation;
		this.depreciationPossible = depreciationPossible;
	}
	
	/**
	 * Returns a translated String describing this expense type. 
	 * The locale used is the default locale derived from the framework.
	 * 
	 * @return a locale-sensitive string representation of this expense type
	 */
	public String getTranslatedString() {
		return translated != null ? translated : this.name();
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
