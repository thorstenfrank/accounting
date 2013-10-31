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
package de.togginho.accounting.reporting.model;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.IncomeStatement;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class IncomeStatementSummaryWrapper {
	
	private IncomeStatement incomeStatement;
    
    /**
     * 
     * @return
     */
    public String getFromDate() {
    	return FormatUtil.formatDate(incomeStatement.getTimeFrame().getFrom());
    }
    
    /**
     * 
     * @return
     */
    public String getUntilDate() {
    	return FormatUtil.formatDate(incomeStatement.getTimeFrame().getUntil());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalRevenueNet() {
    	return FormatUtil.formatCurrency(incomeStatement.getTotalRevenue().getNet());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalRevenueGross() {
    	return FormatUtil.formatCurrency(incomeStatement.getTotalRevenue().getGross());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalRevenueTaxes() {
    	if (incomeStatement.getTotalRevenue().getTax() == null) {
    		return Constants.HYPHEN;
    	}
    	return FormatUtil.formatCurrency(incomeStatement.getTotalRevenue().getTax());
    }
    
    /**
     * 
     * @return
     */
    public String getOpexNet() {
    	return FormatUtil.formatCurrency(incomeStatement.getOperatingExpenses().getTotalCost().getNet());
    }

    /**
     * 
     * @return
     */
    public String getOpexTax() {
    	return FormatUtil.formatCurrency(incomeStatement.getOperatingExpenses().getTotalCost().getTax());
    }
    
    /**
     * 
     * @return
     */
    public String getOpexGross() {
    	return FormatUtil.formatCurrency(incomeStatement.getOperatingExpenses().getTotalCost().getGross());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalCostsNet() {
    	return FormatUtil.formatCurrency(incomeStatement.getTotalExpenses().getNet());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalCostsTax() {
    	if (incomeStatement.getTotalExpenses().getTax() == null) {
    		return Constants.HYPHEN;
    	}
    	return FormatUtil.formatCurrency(incomeStatement.getTotalExpenses().getTax());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalCostsGross() {
    	return FormatUtil.formatCurrency(incomeStatement.getTotalExpenses().getGross());
    }
    
    /**
     * 
     * @return
     */
    public String getInputTax() {
    	return getTotalCostsTax();
    }
    
    /**
     * 
     * @return
     */
    public String getOutputTax() {
    	return getTotalRevenueTaxes();
    }
    
    /**
     * 
     * @return
     */
    public String getTaxSum() {
    	return FormatUtil.formatCurrency(incomeStatement.getTaxSum());
    }
    
    /**
     * 
     * @return
     */
    public String getGrossProfit() {
    	return FormatUtil.formatCurrency(incomeStatement.getGrossProfit().getNet());
    }
}
