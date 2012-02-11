/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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

/**
 * A container for a price including tax, which applies to both an {@link InvoicePosition} and an entire {@link Invoice}.
 * 
 * <p> Each {@link Price} instance can hold net, gross and tax amounts that make up a single price. 
 * {@link #getTax()} may be <code>null</code> if the invoice position or invoice is not tax applicable, in which
 * case net and gross price will be equal.</p> 
 * 
 * <p>This class is not meant to be used directly by clients, but should rather be obtained using the calculation
 * utility {@link de.togginho.accounting.util.CalculationUtil}.</p>
 * 
 * @author thorsten
 *
 * @see de.togginho.accounting.util.CalculationUtil#calculatePrice(InvoicePosition)
 * @see de.togginho.accounting.util.CalculationUtil#calculateTotalPrice(Invoice)
 */
public class Price implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = -58766435614756711L;

	private BigDecimal net;
	
	private BigDecimal tax;
	
	private BigDecimal gross;

	/**
     * Creates a new price with no values.
     */
    public Price() {
    	this (BigDecimal.ZERO, null, BigDecimal.ZERO);
    }
    
	/**
	 * Creates a new price with the specified values.
	 * 
     * @param net net price
     * @param tax tax amount
     * @param gross gross price
     */
    public Price(BigDecimal net, BigDecimal tax, BigDecimal gross) {
	    this.net = net;
	    this.tax = tax;
	    this.gross = gross;
    }

	/**
     * @return the net price
     */
    public BigDecimal getNet() {
    	return net;
    }

	/**
     * @param net the net price
     */
    public void setNet(BigDecimal net) {
    	this.net = net;
    }

	/**
     * @return the tax amount
     */
    public BigDecimal getTax() {
    	return tax;
    }

	/**
     * @param tax the tax amount
     */
    public void setTax(BigDecimal tax) {
    	this.tax = tax;
    }

	/**
     * @return the gross price
     */
    public BigDecimal getGross() {
    	return gross;
    }

	/**
     * @param gross the gross price
     */
    public void setGross(BigDecimal gross) {
    	this.gross = gross;
    }
    
    /**
     * 
     * @param net
     */
    public void addToNet(BigDecimal net) {
    	if (net == null) {
    		return;
    	}
    	
    	if (this.net == null) {
    		this.net = BigDecimal.ZERO;
    	}
    	
    	this.net = this.net.add(net);
    }
    
    /**
     * 
     * @param tax
     */
    public void addToTax(BigDecimal tax) {
    	if (tax == null) {
    		return;
    	}
    	
    	if (this.tax == null) {
    		this.tax = BigDecimal.ZERO;
    	}
    	
    	this.tax = this.tax.add(tax);
    }
    
    /**
     * 
     * @param gross
     */
    public void addToGross(BigDecimal gross) {
    	if (gross == null) {
    		return;
    	}
    	
    	if (this.gross == null) {
    		this.gross = BigDecimal.ZERO;
    	}
    	this.gross = this.gross.add(gross);
    }
    
    /**
     * 
     * @param price
     */
    public void add(Price price) {
    	if (price == null) {
    		return;
    	}
    	addToNet(price.getNet());
    	addToTax(price.getTax());
    	addToGross(price.getGross());
    }
}