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
package de.tfsw.accounting.service;

import com.db4o.query.Predicate;

import de.tfsw.accounting.model.ExpenseTemplate;

/**
 * @author Thorsten Frank
 *
 */
class FindExpenseTemplatePredicate extends Predicate<ExpenseTemplate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean active;
	
	private Boolean applicable;
	
	/**
	 * @param active
	 * @param applicable
	 */
	FindExpenseTemplatePredicate(Boolean active, Boolean applicable) {
		super();
		this.active = active;
		this.applicable = applicable;
	}
	
	/**
	 * @see com.db4o.query.Predicate#match(java.lang.Object)
	 */
	@Override
	public boolean match(ExpenseTemplate template) {
		boolean match = true;
		
		if (active != null) {
			match = template.isApplicable();
		}
		
		if (match && applicable != null) {
			match = template.isApplicable();
		}
		
		return match;
	}
	
}
