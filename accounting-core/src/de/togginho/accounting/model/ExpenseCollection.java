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

import java.util.Set;

import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class ExpenseCollection {

	private TimeFrame timeFrame;
	
	private Price totalCost;
	
	private Price operatingCosts;
	
	private Set<Expense> expenses;
	
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
	 * @return the totalPrice
	 */
	public Price getTotalCost() {
		return totalCost;
	}

	/**
	 * @return the operatingCosts
	 */
	public Price getOperatingCosts() {
		return operatingCosts;
	}

	/**
	 * @return the expenses
	 */
	public Set<Expense> getExpenses() {
		return expenses;
	}

	/**
	 * @param expenses the expenses to set
	 */
	public void setExpenses(Set<Expense> expenses) {
		this.expenses = expenses;

		totalCost = new Price();
		operatingCosts = new Price();
			
		for (Expense expense : expenses) {
			final Price price = CalculationUtil.calculatePrice(expense);
			totalCost.add(price);
			if (ExpenseType.OPEX.equals(expense.getExpenseType())) {
				operatingCosts.add(price);
			}
		}
	}
}
