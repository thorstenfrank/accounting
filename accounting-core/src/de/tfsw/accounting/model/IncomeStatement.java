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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.TimeFrame;

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
    
    private boolean totalsUpToDate = false;
    private Price totalExpenses;
    private BigDecimal taxSum;
    private Price grossProfit;
    
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
    	totalsUpToDate = false;
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
    	if (operatingExpenses.getExpenses() != null) {
    		this.operatingExpenseCategories = buildCategoryMap(operatingExpenses);
    	}
    	
    	totalsUpToDate = false;
    }
    
    /**
     * 
     * @return
     */
	public Price getTotalOperatingCosts() {
		if (operatingExpenses != null) {
			return operatingExpenses.getTotalCost();
		}
		return new Price();
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
    	if (otherExpenses.getExpenses() != null) {
    		this.otherExpenseCategories = buildCategoryMap(otherExpenses);
    	}
    	totalsUpToDate = false;
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
    	if (capitalExpenses.getExpenses() != null) {
    		this.capitalExpenseCategories = buildCategoryMap(capitalExpenses);
    	}
    	totalsUpToDate = false;
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
    public Price getTotalExpenses() {
    	if (!totalsUpToDate) {
    		calculateTotals();
    	}
    	
    	return totalExpenses;
    }
    
	/**
     * 
     * @return
     */
    public Price getGrossProfit() {
    	if (!totalsUpToDate) {
    		calculateTotals();
    	}
    	
    	return grossProfit;
    }

    /**
     * TODO implement me
     * @return
     */
    public Price getEBIT() {
    	return null;
    }
    
    /**
     * TODO implement me
     * @return
     */
    public Price getDepreciation() {
    	return null;
    }
    
    /**
     * TODO implement me
     * @return
     */
    public Map<String, Price> getDepreciationCategories() {
    	return null;
    }
    
    /**
     * @return the taxSum
     */
    public BigDecimal getTaxSum() {
    	if (!totalsUpToDate) {
    		calculateTotals();
    	}
    	
    	return taxSum;
    }

	/**
     * 
     * @param expenses
     * @return
     */
    private Map<String, Price> buildCategoryMap(ExpenseCollection expenses) {
    	final Map<String, Price> temp = new HashMap<String, Price>();
    	
    	for (Expense expense : expenses.getExpenses()) {
    		if (false == temp.containsKey(expense.getCategory())) {
    			temp.put(expense.getCategory(), new Price());
    		}
    		
    		temp.get(expense.getCategory()).add(CalculationUtil.calculatePrice(expense));
    	}
    	
    	SortedMap<String, Price> sorted = new TreeMap<String, Price>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
	            return -1 * (temp.get(o1).compareTo(temp.get(o2)));
            }
		});
    	
    	sorted.putAll(temp);
    	
    	return sorted;    	
    }
    
    /**
     * 
     */
    private void calculateTotals() {
    	totalExpenses = new Price();
    	taxSum = BigDecimal.ZERO;
    	grossProfit = new Price();
    	
		if (revenue != null && revenue.getRevenueNet() != null) {
			grossProfit.add(revenue.getTotalRevenue());
			taxSum = revenue.getRevenueTax() != null ? revenue.getRevenueTax() : BigDecimal.ZERO;
		}
		
    	if (operatingExpenses != null) {
    		totalExpenses.add(operatingExpenses.getTotalCost());
    		grossProfit.subtract(operatingExpenses.getTotalCost());
    	}
    	
    	if (capitalExpenses != null) {
    		totalExpenses.add(capitalExpenses.getTotalCost());
    	}
    	
    	if (otherExpenses != null) {
    		totalExpenses.add(otherExpenses.getTotalCost());
    	}
    	
    	if (totalExpenses.getTax() != null) {
    		taxSum = taxSum.subtract(totalExpenses.getTax());
    	}
    	
    	totalsUpToDate = true;
    }
}
