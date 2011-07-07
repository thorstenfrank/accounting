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
 * @author thorsten
 *
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
     * 
     */
    public Price() {
    }
    
	/**
     * @param net
     * @param tax
     * @param gross
     */
    public Price(BigDecimal net, BigDecimal tax, BigDecimal gross) {
	    this.net = net;
	    this.tax = tax;
	    this.gross = gross;
    }

	/**
     * @return the net
     */
    public BigDecimal getNet() {
    	return net;
    }

	/**
     * @param net the net to set
     */
    public void setNet(BigDecimal net) {
    	this.net = net;
    }

	/**
     * @return the tax
     */
    public BigDecimal getTax() {
    	return tax;
    }

	/**
     * @param tax the tax to set
     */
    public void setTax(BigDecimal tax) {
    	this.tax = tax;
    }

	/**
     * @return the gross
     */
    public BigDecimal getGross() {
    	return gross;
    }

	/**
     * @param gross the gross to set
     */
    public void setGross(BigDecimal gross) {
    	this.gross = gross;
    }	
}