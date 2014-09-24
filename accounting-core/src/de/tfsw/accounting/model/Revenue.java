/*
 *  Copyright 2010 , 2014 Thorsten Frank (accounting@tfsw.de).
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.TimeFrame;

/**
 * A container for paid invoices within a specific time period.
 * 
 * <p>This is a non-persistent entity.</p>
 * 
 * @author thorsten frank
 */
public class Revenue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9220047923401626299L;

	private TimeFrame timeFrame;

	private List<Invoice> invoices;
	
	private Map<Invoice, Price> invoiceRevenues;
	
	private Price regularRevenue;
	private Price otherRevenue;
	private Price totalRevenue;
	
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
	 * @return the invoices
	 */
	public List<Invoice> getInvoices() {
		return invoices;
	}

	/**
	 * @param invoices the invoices to set
	 */
	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
		this.invoiceRevenues = new HashMap<Invoice, Price>();
		updateTotals();
	}

	/**
	 * 
	 */
	private void updateTotals() {
		BigDecimal revenueGross = BigDecimal.ZERO;
		BigDecimal revenueNet = BigDecimal.ZERO;
		BigDecimal revenueTax = BigDecimal.ZERO;
		
		for (Invoice invoice : invoices) {
			final Price price = CalculationUtil.calculateRevenue(invoice);
			invoiceRevenues.put(invoice, price);
			revenueNet = revenueNet.add(price.getNet());
			revenueGross = revenueGross.add(price.getGross());
			if (price.getTax() != null) {
				revenueTax = revenueTax.add(price.getTax());
			}
		}
		
		this.regularRevenue = new Price(revenueNet, revenueTax, revenueGross);
		this.totalRevenue = new Price(revenueNet, revenueTax, revenueGross);		
	}
	
	/**
	 * @return total revenue for the period
	 */
	public Price getTotalRevenue() {
		return totalRevenue;
	}

	/**
	 * 
     * @return total net revenue
     */
    public BigDecimal getRevenueNet() {
    	return totalRevenue.getNet();
    }

	/**
     * @return the revenueTax
     */
    public BigDecimal getRevenueTax() {
    	return totalRevenue.getTax();
    }

	/**
     * @return the revenueGross
     */
    public BigDecimal getRevenueGross() {
    	return totalRevenue.getGross();
    }

    /**
     * 
     * @return
     */
    public Price getRegularRevenue() {
    	return regularRevenue;
    }
    
    /**
     * 
     * @return
     */
    public Price getOtherRevenue() {
    	return otherRevenue;
    }
    
	/**
	 * @return the invoiceRevenues
	 */
	public Map<Invoice, Price> getInvoiceRevenues() {
		return invoiceRevenues;
	}
}
