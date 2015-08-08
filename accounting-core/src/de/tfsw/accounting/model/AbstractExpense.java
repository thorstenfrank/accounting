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
package de.tfsw.accounting.model;

import java.math.BigDecimal;

/**
 * Abstract base class for expenses containing common properties.
 * 
 * @author Thorsten Frank
 *
 */
public abstract class AbstractExpense extends AbstractBaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_NET_AMOUNT = "netAmount";
	public static final String FIELD_TAX_RATE = "taxRate";
    public static final String FIELD_TYPE = "expenseType";
    public static final String FIELD_CATEGORY = "category";
    
    private String description;
    private BigDecimal netAmount;
    private TaxRate taxRate;
    private ExpenseType expenseType;
    private String category;
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the netAmount
	 */
	public BigDecimal getNetAmount() {
		return netAmount;
	}
	/**
	 * @param netAmount the netAmount to set
	 */
	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}
	/**
	 * @return the taxRate
	 */
	public TaxRate getTaxRate() {
		return taxRate;
	}
	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(TaxRate taxRate) {
		this.taxRate = taxRate;
	}
	/**
	 * @return the expenseType
	 */
	public ExpenseType getExpenseType() {
		return expenseType;
	}
	/**
	 * @param expenseType the expenseType to set
	 */
	public void setExpenseType(ExpenseType expenseType) {
		this.expenseType = expenseType;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
}
