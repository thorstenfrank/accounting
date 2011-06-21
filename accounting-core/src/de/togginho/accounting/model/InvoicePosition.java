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

/**
 * An individual service or product being sold and billed via an {@link Invoice} .
 * 
 * @author thorsten frank
 */
public class InvoicePosition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4248955076367607485L;
	
	public static final String FIELD_QUANTITY = "quantity";
	public static final String FIELD_UNIT = "unit";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_PRICE_PER_UNIT = "pricePerUnit";
	public static final String FIELD_TAX_RATE = "taxRate";
	
	private BigDecimal quantity;
	private String unit;
	private String description;
	private BigDecimal pricePerUnit;
	private TaxRate taxRate;
	
	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the pricePerUnit
	 */
	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	/**
	 * @param pricePerUnit the pricePerUnit to set
	 */
	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTaxApplicable() {
		return taxRate != null;
	}

	/**
	 * @return the taxRate
	 */
	public TaxRate getTaxRate() {
		return taxRate;
	}

	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(TaxRate taxRate) {
		this.taxRate = taxRate;
	}
}
