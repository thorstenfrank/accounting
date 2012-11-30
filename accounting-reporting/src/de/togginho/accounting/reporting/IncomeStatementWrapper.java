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

/**
 * @author thorsten
 *
 */
public class IncomeStatementWrapper implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -7527256735651022230L;

	public String getFrom() {
		return "01.01.2012";
	}
	
	public String getUntil() {
		return "31.12.2012";
	}
	
	public String getRevenueNet() {
		return "€ 100.000";
	}
	
	public String getRevenueTax() {
		return "€ 19.000";
	}
	
	public String getRevenueGross() {
		return "€ 119.000";
	}
	
	public String getOpexNet() {
		return "€ 10.000";
	}
	
	public String getOpexTax() {
		return "€ 1.900";
	}
	
	public String getOpexGross() {
		return "€ 11.900";
	}
	
	public String getOtherExpensesNet() {
		return "€ 10.000";
	}
	
	public String getOtherExpensesTax() {
		return "€ 1.900";
	}
	
	public String getOtherExpensesGross() {
		return "€ 11.900";
	}
	
	public String getEBITDAnet() {
		return "€ 80.000";
	}
	
	public String getEBITDAtax() {
		return "€ 15.200";
	}
	
	public String getEBITDAgross() {
		return "€ 95.200";
	}
	
	public String getDepreciationTotal() {
		return "€ 1.937,48";
	}
	
	public String getEBITnet() {
		return "€ 78.062,52";
	}
	
	public String getEBITtax() {
		return getEBITDAtax();
	}
	
	public String getEBITgross() {
		return "€ 93.262,52";
	}
}
