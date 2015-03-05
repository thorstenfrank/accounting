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
package de.tfsw.accounting.ui.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
class ExpenseWrapper {

	private Expense expense;

	private Price price;
	
	ExpenseWrapper(Expense expense) {
		this.expense = expense;
		this.price = CalculationUtil.calculatePrice(expense);
	}
	
	/**
	 * 
	 * @return
	 */
	protected Expense getExpense() {
		return expense;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getPaymentDateFormatted() {
		return FormatUtil.formatDate(expense.getPaymentDate());
	}
	
	/**
	 * @return
	 * @see de.tfsw.accounting.model.Expense#getPaymentDate()
	 */
	protected LocalDate getPaymentDate() {
		return expense.getPaymentDate();
	}
	
	/**
	 * @return
	 * @see de.tfsw.accounting.model.Expense#getNetAmount()
	 */
	protected BigDecimal getNetAmount() {
		return expense.getNetAmount();
	}
	
	/**
	 * @return
	 * @see de.tfsw.accounting.model.Expense#getDescription()
	 */
	protected String getDescription() {
		return expense.getDescription();
	}

	/**
     * @return
     * @see de.tfsw.accounting.model.Expense#getCategory()
     */
    protected String getCategory() {
	    return expense.getCategory();
    }

	/**
	 * 
	 * @return
	 */
	protected String getNetAmountFormatted() {
		return FormatUtil.formatCurrency(price.getNet());
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getTaxRateFormatted() {
		return expense.getTaxRate() != null ? expense.getTaxRate().toShortString() : Constants.HYPHEN;
	}
	
	/**
	 * 
	 * @return
	 */
	protected BigDecimal getTaxAmount() {
		return price.getTax();
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getTaxAmountFormatted() {
		if (price.getTax() == null) {
			return Constants.HYPHEN;
		}
		
		StringBuilder sb = new StringBuilder(FormatUtil.formatPercentValue(expense.getTaxRate().getRate()));
		sb.append(Constants.BLANK_STRING).append(Constants.HYPHEN).append(Constants.BLANK_STRING);
		sb.append(FormatUtil.formatCurrency(price.getTax()));
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	protected BigDecimal getGrossAmount() {
		return price.getGross();
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getGrossAmountFormatted() {
		return FormatUtil.formatCurrency(price.getGross());
	}
}
