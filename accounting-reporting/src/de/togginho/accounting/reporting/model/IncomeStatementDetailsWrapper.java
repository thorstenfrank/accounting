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

import java.io.Serializable;

/**
 * @author thorsten
 *
 */
public class IncomeStatementDetailsWrapper implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -2540060070283183553L;
    
	private String category;
	private String net;
	private String tax;
	private String gross;
	/**
     * @return the category
     */
    public String getCategory() {
    	return category;
    }
	/**
     * @param category the category to set
     */
    public void setCategory(String category) {
    	this.category = category;
    }
	/**
     * @return the net
     */
    public String getNet() {
    	return net;
    }
	/**
     * @param net the net to set
     */
    public void setNet(String net) {
    	this.net = net;
    }
	/**
     * @return the tax
     */
    public String getTax() {
    	return tax;
    }
	/**
     * @param tax the tax to set
     */
    public void setTax(String tax) {
    	this.tax = tax;
    }
	/**
     * @return the gross
     */
    public String getGross() {
    	return gross;
    }
	/**
     * @param gross the gross to set
     */
    public void setGross(String gross) {
    	this.gross = gross;
    }
	
}
