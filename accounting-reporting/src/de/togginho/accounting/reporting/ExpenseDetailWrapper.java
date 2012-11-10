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
package de.togginho.accounting.reporting;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class ExpenseDetailWrapper {

	private Expense expense;

	private Price totalPrice;
	
	/**
	 * @param expense
	 */
	public ExpenseDetailWrapper(Expense expense) {
		this.expense = expense;
		this.totalPrice = CalculationUtil.calculatePrice(expense);
	}
	
	/**
     * @return the expense
     */
    protected Expense getExpense() {
    	return expense;
    }

	/**
	 * 
	 * @return
	 */
	public String getPaymentDate() {
		return FormatUtil.formatDate(expense.getPaymentDate());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getNetPrice() {
		return FormatUtil.formatCurrency(expense.getNetAmount());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTaxAmount() {
		if (totalPrice.getTax() != null) {
			return FormatUtil.formatCurrency(totalPrice.getTax());
		}
		return Constants.HYPHEN;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getGrossPrice() {
		return FormatUtil.formatCurrency(totalPrice.getGross());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getExpenseDescription() {
		return expense.getDescription();
	}
	
	/**
	 * @return
	 */
	public String getExpenseCategory() {
		return expense.getCategory();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTaxRate() {
		return expense.getTaxRate() != null ? FormatUtil.formatPercentValue(expense.getTaxRate().getRate()) : Constants.HYPHEN;
	}
}
