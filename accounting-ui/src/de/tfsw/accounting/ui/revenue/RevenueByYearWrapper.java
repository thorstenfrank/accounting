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
package de.tfsw.accounting.ui.revenue;

import de.tfsw.accounting.model.Revenue;
import de.tfsw.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
class RevenueByYearWrapper {

	
	private String year;
	private String numberOfInvoices;
	private String netTotal;
	private String grossTotal;
	private String taxTotal;
	
	protected RevenueByYearWrapper(int year, Revenue revenue) {
		this.year = Integer.toString(year);
		this.numberOfInvoices = Integer.toString(revenue.getInvoices().size());
		netTotal = FormatUtil.formatCurrency(revenue.getRevenueNet());
		taxTotal = FormatUtil.formatCurrency(revenue.getRevenueTax());
		grossTotal = FormatUtil.formatCurrency(revenue.getRevenueGross());
	}

	/**
     * @return the year
     */
    protected String getYear() {
    	return year;
    }
	
    /**
     * @return the numberOfInvoices
     */
    public String getNumberOfInvoices() {
    	return numberOfInvoices;
    }
    
	/**
     * @return the netTotal
     */
    protected String getNetTotal() {
    	return netTotal;
    }

	/**
     * @return the grossTotal
     */
    protected String getGrossTotal() {
    	return grossTotal;
    }

	/**
     * @return the taxTotal
     */
    protected String getTaxTotal() {
    	return taxTotal;
    }
}
