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

import java.io.Serializable;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.IncomeStatement;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class IncomeStatementWrapper implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -7527256735651022230L;

    private IncomeStatement incomeStatement;
        
	/**
     * @param incomeStatement
     */
    IncomeStatementWrapper(IncomeStatement incomeStatement) {
	    this.incomeStatement = incomeStatement;
    }
    
	/**
     * @return the incomeStatement
     */
    public IncomeStatement getIncomeStatement() {
    	return incomeStatement;
    }
    
    /**
     * 
     * @return
     */
	public String getFrom() {
		StringBuilder sb = new StringBuilder(Messages.fromTitle);
		sb.append(Constants.BLANK_STRING);
		sb.append(FormatUtil.formatDate(incomeStatement.getTimeFrame().getFrom()));
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUntil() {
		StringBuilder sb = new StringBuilder(Messages.untilTitle);
		sb.append(Constants.BLANK_STRING);
		sb.append(FormatUtil.formatDate(incomeStatement.getTimeFrame().getUntil()));
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRevenueNet() {
		return FormatUtil.formatCurrency(incomeStatement.getRevenue().getRevenueNet());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRevenueTax() {
		return FormatUtil.formatCurrency(incomeStatement.getRevenue().getRevenueTax());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRevenueGross() {
		return FormatUtil.formatCurrency(incomeStatement.getRevenue().getRevenueGross());
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
	public String getOtherExpensesNet() {
		return FormatUtil.formatCurrency(incomeStatement.getOtherExpenses().getTotalCost().getNet());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getOtherExpensesTax() {
		return FormatUtil.formatCurrency(incomeStatement.getOtherExpenses().getTotalCost().getTax());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getOtherExpensesGross() {
		return FormatUtil.formatCurrency(incomeStatement.getOtherExpenses().getTotalCost().getGross());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCapexNet() {
		return FormatUtil.formatCurrency(incomeStatement.getCapitalExpenses().getTotalCost().getNet());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCapexTax() {
		return FormatUtil.formatCurrency(incomeStatement.getCapitalExpenses().getTotalCost().getTax());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCapexGross() {
		return FormatUtil.formatCurrency(incomeStatement.getCapitalExpenses().getTotalCost().getGross());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEBITDAnet() {
		return FormatUtil.formatCurrency(incomeStatement.getGrossProfit().getNet());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEBITDAtax() {
		return FormatUtil.formatCurrency(incomeStatement.getGrossProfit().getTax());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEBITDAgross() {
		return FormatUtil.formatCurrency(incomeStatement.getGrossProfit().getGross());
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDepreciationTotal() {
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEBITnet() {
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEBITtax() {
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEBITgross() {
		return null;
	}
}