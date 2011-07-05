/*
 *  Copyright 2010 thorsten frank (thorsten.frank@gmx.de).
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
import java.util.Date;
import java.util.List;

import de.togginho.accounting.util.CalculationUtil;

/**
 * A container for paid invoices within a specific time period.
 * 
 * @author thorsten frank
 */
public class Revenue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9220047923401626299L;

	private Date from;

	private Date until;

	private List<Invoice> invoices;

	private BigDecimal revenueNet = BigDecimal.ZERO;
	
	private BigDecimal revenueTax = BigDecimal.ZERO;
	
	private BigDecimal revenueGross = BigDecimal.ZERO;
	
	/**
	 * @return the from
	 */
	public Date getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(Date from) {
		this.from = from;
	}

	/**
	 * @return the until
	 */
	public Date getUntil() {
		return until;
	}

	/**
	 * @param until the until to set
	 */
	public void setUntil(Date until) {
		this.until = until;
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
		
		revenueGross = BigDecimal.ZERO;
		revenueNet = BigDecimal.ZERO;
		revenueTax = BigDecimal.ZERO;
		
		for (Invoice invoice : invoices) {
			revenueGross = revenueGross.add(CalculationUtil.calculateTotalGrossPrice(invoice));
			revenueNet = revenueNet.add(CalculationUtil.calculateTotalNetPrice(invoice));
			revenueTax = revenueTax.add(CalculationUtil.calculateTotalTaxAmount(invoice));
		}
	}

	/**
     * @return the revenueNet
     */
    public BigDecimal getRevenueNet() {
    	return revenueNet;
    }

	/**
     * @return the revenueTax
     */
    public BigDecimal getRevenueTax() {
    	return revenueTax;
    }

	/**
     * @return the revenueGross
     */
    public BigDecimal getRevenueGross() {
    	return revenueGross;
    }
}
