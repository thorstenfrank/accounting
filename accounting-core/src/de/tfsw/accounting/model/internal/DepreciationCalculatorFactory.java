/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.model.internal;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.model.Expense;

/**
 * @author thorsten
 *
 */
public final class DepreciationCalculatorFactory {

	/**
	 * 
	 */
	private DepreciationCalculatorFactory() {
		
	}
	
	/**
	 * 
	 * @param expense
	 * @return
	 */
	public static Depreciation getDepreciationCalculator(Expense expense) {
		
		if (expense.getDepreciationMethod() != null) {
			switch (expense.getDepreciationMethod()) {
			case STRAIGHTLINE:
				return new StraightlineDepreciation(expense);
			case FULL:
				return new ImmediateWriteOff(expense);
			}			
		}

		throw new AccountingException("No depreciation method defined!");
	}	
}
