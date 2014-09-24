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
package de.tfsw.accounting.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class ExpenseCollection {

	private TimeFrame timeFrame;
	
	private Price totalCost;
	
	private Map<ExpenseType, Price> expensesByType;
	
	private Set<Expense> expenses;
	
	/**
	 * 
	 */
	public ExpenseCollection() {
		
	}
	
	/**
	 * 
	 * @param expenses
	 */
	public ExpenseCollection(Set<Expense> expenses) {
		setExpenses(expenses);
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
	 * @return the totalPrice
	 */
	public Price getTotalCost() {
		return totalCost;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
    public Price getCost(ExpenseType type) {
    	final Price cost = expensesByType.get(type);
    	
    	return cost != null ? cost : new Price();
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
		expensesByType = new HashMap<ExpenseType, Price>();
		
		for (Expense expense : expenses) {
			final Price price = CalculationUtil.calculatePrice(expense);
			totalCost.add(price);
			
			if (false == expensesByType.containsKey(expense.getExpenseType())) {
				expensesByType.put(expense.getExpenseType(), new Price());
			}
			
			expensesByType.get(expense.getExpenseType()).add(price);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<ExpenseType> getIncludedTypes() {
		return expensesByType.keySet();
	}
}
