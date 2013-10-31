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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class ExpensesWrapper {

	/**
	 * 
	 */
	private ExpenseCollection expenseCollection;

	/**
	 * @param expenseCollection
	 */
	public ExpensesWrapper(ExpenseCollection expenseCollection) {
		this.expenseCollection = expenseCollection;
	}

	/**
	 * 
	 * @return
	 */
	public List<ExpenseDetailWrapper> getExpenseDetails() {
		List<ExpenseDetailWrapper> wrappers = new ArrayList<ExpenseDetailWrapper>();
		for (Expense expense : expenseCollection.getExpenses()) {
			wrappers.add(new ExpenseDetailWrapper(expense));
		}
		Collections.sort(wrappers, new Comparator<ExpenseDetailWrapper>() {

			/**
             * {@inheritDoc}.
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            @Override
            public int compare(ExpenseDetailWrapper o1, ExpenseDetailWrapper o2) {
	            return o1.getExpense().getPaymentDate().compareTo(o2.getExpense().getPaymentDate());
            }
			
		});
		return wrappers;
	}
	
	/**
     * @return the expenseCollection
     */
    protected ExpenseCollection getExpenseCollection() {
    	return expenseCollection;
    }

	/**
	 * @return
	 */
	public String getFromDate() {
		return FormatUtil.formatDate(expenseCollection.getTimeFrame().getFrom());
	}
	
	/**
	 * @return
	 */
	public String getUntilDate() {
		return FormatUtil.formatDate(expenseCollection.getTimeFrame().getUntil());
	}
	
	/**
	 * @return
	 */
	public String getTotalGross() {
		return FormatUtil.formatCurrency(expenseCollection.getTotalCost().getGross());
	}

	/**
	 * @return
	 */
	public String getTotalNet() {
		return FormatUtil.formatCurrency(expenseCollection.getTotalCost().getNet());
	}

	/**
	 * @return
	 */
	public String getTotalTaxAmount() {
		return FormatUtil.formatCurrency(expenseCollection.getTotalCost().getTax());
	}
}
