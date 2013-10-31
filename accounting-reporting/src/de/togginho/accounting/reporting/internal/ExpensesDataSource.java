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
package de.togginho.accounting.reporting.internal;

import java.util.Map;
import java.util.Set;

import de.togginho.accounting.model.ExpenseType;

/**
 * @author thorsten
 *
 */
public class ExpensesDataSource extends AbstractReportDataSource {

	private static final String EXPENSES_KEY = "expenses";
	
	private ExpensesWrapper wrapper;
		
	/**
	 * @param wrapper
	 */
	public ExpensesDataSource(ExpensesWrapper wrapper) {
		this.wrapper = wrapper;
	}



	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.reporting.internal.AbstractReportDataSource#addFieldsToMap(java.util.Map)
	 */
	@Override
	protected void addFieldsToMap(Map<String, Object> fieldMap) {
		fieldMap.put(EXPENSES_KEY, wrapper);
		
		Set<ExpenseType> includedTypes = wrapper.getExpenseCollection().getIncludedTypes();
		if (includedTypes == null || includedTypes.isEmpty() || includedTypes.size() > 1) {
			fieldMap.put("expenses.title", Messages.ExpensesDataSource_expensesTitle); //$NON-NLS-1$
		} else {
			ExpenseType onlyType = includedTypes.iterator().next();
			fieldMap.put("expenses.title", onlyType.getTranslatedString()); //$NON-NLS-1$
		}
		
		fieldMap.put("expense.description.title", Messages.descriptionTitle); //$NON-NLS-1$
		fieldMap.put("expense.category.title", Messages.ExpensesDataSource_expenseCategory); //$NON-NLS-1$
		fieldMap.put("payment.date.title", Messages.dateTitle); //$NON-NLS-1$
		fieldMap.put("from.title", Messages.fromTitle); //$NON-NLS-1$
		fieldMap.put("until.title", Messages.untilTitle); //$NON-NLS-1$
		fieldMap.put("net.price.title", Messages.netPriceTitle); //$NON-NLS-1$
		fieldMap.put("taxRate.title", Messages.taxRateTitle); //$NON-NLS-1$
		fieldMap.put("tax.amount.title", Messages.taxAmountTitle); //$NON-NLS-1$
		fieldMap.put("gross.price.title", Messages.grossPriceTitle); //$NON-NLS-1$
		fieldMap.put("sum.title", Messages.sumTitle); //$NON-NLS-1$
	}

}
