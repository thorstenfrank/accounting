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
import java.math.BigDecimal;

import de.togginho.accounting.util.TimeFrame;

/**
 * This is a non-persistent entity.
 * 
 * @author thorsten
 *
 */
public class CashFlowStatement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -205470386888149896L;

	private TimeFrame timeFrame;
	
	private Revenue revenue;
	
	private ExpenseCollection expenses;
	
	private BigDecimal grossProfit;
	
	private BigDecimal taxSum;
	
	/**
	 * @param timeFrame
	 */
	public CashFlowStatement(TimeFrame timeFrame) {
		this.timeFrame = timeFrame;
	}

	/**
	 * @return the timeFrame
	 */
	public TimeFrame getTimeFrame() {
		return timeFrame;
	}

	/**
	 * @param timeFrame the timeFrame to set
	 */
	public void setTimeFrame(TimeFrame timeFrame) {
		this.timeFrame = timeFrame;
	}

	/**
	 * @param revenue the revenue to set
	 */
	public void setRevenue(Revenue revenue) {
		this.revenue = revenue;
		calculateTotals();
	}

	/**
	 * @param expenses the expenses to set
	 */
	public void setExpenses(ExpenseCollection expenses) {
		this.expenses = expenses;
		calculateTotals();
	}

	/**
	 * 
	 * @return
	 */
	public Price getTotalRevenue() {
		if (revenue != null) {
			return revenue.getTotalRevenue();
		}
		return new Price();
	}
	
	public Price getTotalCosts() {
		if (expenses != null) {
			return expenses.getTotalCost();
		}
		return new Price();
	}
	
	public Price getOperatingCosts() {
		if (expenses != null) {
			return expenses.getOperatingCosts();
		}
		return new Price();
	}
	
	/**
	 * @return the grossProfit
	 */
	public BigDecimal getGrossProfit() {
		return grossProfit;
	}
	
	/**
	 * @return the taxSum
	 */
	public BigDecimal getTaxSum() {
		return taxSum;
	}

	private void calculateTotals() {
		if (revenue != null && revenue.getRevenueNet() != null) {
			if (expenses != null) {
				if(expenses.getOperatingCosts() != null) {
					grossProfit = revenue.getRevenueNet().subtract(expenses.getOperatingCosts().getNet());
				}
				
				taxSum = revenue.getRevenueTax() != null ? revenue.getRevenueTax() : BigDecimal.ZERO;
				if (expenses.getTotalCost().getTax() != null) {
					taxSum = taxSum.subtract(expenses.getTotalCost().getTax());
				}
			}
		}
	}
}
