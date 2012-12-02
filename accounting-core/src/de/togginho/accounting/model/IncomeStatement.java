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
import java.util.HashMap;
import java.util.Map;

import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class IncomeStatement implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 2213895663642333659L;

    private TimeFrame timeFrame;
    
    private Revenue revenue;
    
    private ExpenseCollection operatingExpenses;
    private Map<String, Price> operatingExpenseCategories;

    private ExpenseCollection otherExpenses;
    private Map<String, Price> otherExpenseCategories;
    
    private ExpenseCollection capitalExpenses;
    private Map<String, Price> capitalExpenseCategories;
        
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
     * @return the revenue
     */
    public Revenue getRevenue() {
    	return revenue;
    }

	/**
     * @param revenue the revenue to set
     */
    public void setRevenue(Revenue revenue) {
    	this.revenue = revenue;
    }
    
    /**
     * @return the operatingExpenses
     */
    public ExpenseCollection getOperatingExpenses() {
    	return operatingExpenses;
    }

	/**
     * @param operatingExpenses the operatingExpenses to set
     */
    public void setOperatingExpenses(ExpenseCollection operatingExpenses) {
    	this.operatingExpenses = operatingExpenses;
    	this.operatingExpenseCategories = buildCategoryMap(operatingExpenses);
    }
    
	/**
     * @return the operatingExpenseCategories
     */
    public Map<String, Price> getOperatingExpenseCategories() {
    	return operatingExpenseCategories;
    }

	/**
     * @return the otherExpenses
     */
    public ExpenseCollection getOtherExpenses() {
    	return otherExpenses;
    }

	/**
     * @param otherExpenses the otherExpenses to set
     */
    public void setOtherExpenses(ExpenseCollection otherExpenses) {
    	this.otherExpenses = otherExpenses;
    	this.otherExpenseCategories = buildCategoryMap(otherExpenses);
    }

	/**
     * @return the otherExpenseCategories
     */
    public Map<String, Price> getOtherExpenseCategories() {
    	return otherExpenseCategories;
    }

	/**
     * @return the capitalExpenses
     */
    public ExpenseCollection getCapitalExpenses() {
    	return capitalExpenses;
    }

	/**
     * @param capitalExpenses the capitalExpenses to set
     */
    public void setCapitalExpenses(ExpenseCollection capitalExpenses) {
    	this.capitalExpenses = capitalExpenses;
    	this.capitalExpenseCategories = buildCategoryMap(capitalExpenses);
    }

	/**
     * @return the capitalExpenseCategories
     */
    public Map<String, Price> getCapitalExpenseCategories() {
    	return capitalExpenseCategories;
    }

	/**
     * 
     * @return
     */
    public Price getGrossProfit() {
    	Price grossProfit = revenue != null ? revenue.getTotalRevenue() : new Price();
    	
    	if (operatingExpenses != null) {
    		grossProfit.subtract(operatingExpenses.getTotalCost());
    	}
    	
    	return grossProfit;
    }
    
    /**
     * 
     * @param expenses
     * @return
     */
    private Map<String, Price> buildCategoryMap(ExpenseCollection expenses) {
    	Map<String, Price> map = new HashMap<String, Price>();
    	
    	for (Expense expense : expenses.getExpenses()) {
    		if (false == map.containsKey(expense.getCategory())) {
    			map.put(expense.getCategory(), new Price());
    		}
    		
    		map.get(expense.getCategory()).add(CalculationUtil.calculatePrice(expense));
    	}
    	
    	return map;    	
    }
}
