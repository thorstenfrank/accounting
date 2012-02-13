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
import de.togginho.accounting.model.CashFlowStatement;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class CashFlowWrapper {

	private CashFlowStatement cashFlow;

	/**
     * @param cashFlow
     */
    protected CashFlowWrapper(CashFlowStatement cashFlow) {
	    this.cashFlow = cashFlow;
    }
    
    /**
     * 
     * @return
     */
    public String getFromDate() {
    	return FormatUtil.formatDate(cashFlow.getTimeFrame().getFrom());
    }
    
    /**
     * 
     * @return
     */
    public String getUntilDate() {
    	return FormatUtil.formatDate(cashFlow.getTimeFrame().getUntil());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalRevenueNet() {
    	return FormatUtil.formatCurrency(cashFlow.getTotalRevenue().getNet());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalRevenueGross() {
    	return FormatUtil.formatCurrency(cashFlow.getTotalRevenue().getGross());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalRevenueTaxes() {
    	if (cashFlow.getTotalRevenue().getTax() == null) {
    		return Constants.HYPHEN;
    	}
    	return FormatUtil.formatCurrency(cashFlow.getTotalRevenue().getTax());
    }
    
    /**
     * 
     * @return
     */
    public String getOpexNet() {
    	return FormatUtil.formatCurrency(cashFlow.getOperatingCosts().getNet());
    }

    /**
     * 
     * @return
     */
    public String getOpexTax() {
    	return FormatUtil.formatCurrency(cashFlow.getOperatingCosts().getTax());
    }
    
    /**
     * 
     * @return
     */
    public String getOpexGross() {
    	return FormatUtil.formatCurrency(cashFlow.getOperatingCosts().getGross());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalCostsNet() {
    	return FormatUtil.formatCurrency(cashFlow.getTotalCosts().getNet());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalCostsTax() {
    	if (cashFlow.getTotalCosts().getTax() == null) {
    		return Constants.HYPHEN;
    	}
    	return FormatUtil.formatCurrency(cashFlow.getTotalCosts().getTax());
    }
    
    /**
     * 
     * @return
     */
    public String getTotalCostsGross() {
    	return FormatUtil.formatCurrency(cashFlow.getTotalCosts().getGross());
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
    	return FormatUtil.formatCurrency(cashFlow.getTaxSum());
    }
    
    /**
     * 
     * @return
     */
    public String getGrossProfit() {
    	return FormatUtil.formatCurrency(cashFlow.getGrossProfit());
    }
}
