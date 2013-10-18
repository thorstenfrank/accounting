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
package de.togginho.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The depreciation expense of a single year, usually as part of a depreciation / amortization schedule.
 * 
 * @author thorsten
 *
 */
public class AnnualDepreciation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3456661660912928780L;

	private int year;
	
	private BigDecimal beginningOfYearBookValue;
	
	private BigDecimal endOfYearBookValue;
	
	private BigDecimal depreciationAmount;
	
	private BigDecimal accumulatedDepreciation;
	
	/**
	 * 
	 */
	public AnnualDepreciation() {
		super();
	}
	
	/**
	 * @param year
	 * @param beginningOfYearBookValue
	 * @param endOfYearBookValue
	 * @param depreciationAmount
	 * @param accumulatedDepreciation
	 */
	public AnnualDepreciation(
			int year, 
			BigDecimal beginningOfYearBookValue, 
			BigDecimal endOfYearBookValue, 
			BigDecimal depreciationAmount, 
			BigDecimal accumulatedDepreciation) {
		
		this.year = year;
		this.beginningOfYearBookValue = beginningOfYearBookValue;
		this.endOfYearBookValue = endOfYearBookValue;
		this.depreciationAmount = depreciationAmount;
		this.accumulatedDepreciation = accumulatedDepreciation;
	}



	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
     * @return the beginningOfYearBookValue
     */
    public BigDecimal getBeginningOfYearBookValue() {
    	return beginningOfYearBookValue;
    }

	/**
     * @param beginningOfYearBookValue the beginningOfYearBookValue to set
     */
    public void setBeginningOfYearBookValue(BigDecimal beginningOfYearBookValue) {
    	this.beginningOfYearBookValue = beginningOfYearBookValue;
    }

	/**
	 * @return the endOfYearBookValue
	 */
	public BigDecimal getEndOfYearBookValue() {
		return endOfYearBookValue;
	}

	/**
	 * @param endOfYearBookValue the endOfYearBookValue to set
	 */
	public void setEndOfYearBookValue(BigDecimal endOfYearBookValue) {
		this.endOfYearBookValue = endOfYearBookValue;
	}

	/**
	 * @return the depreciationAmount
	 */
	public BigDecimal getDepreciationAmount() {
		return depreciationAmount;
	}

	/**
	 * @param depreciationAmount the depreciationAmount to set
	 */
	public void setDepreciationAmount(BigDecimal depreciationAmount) {
		this.depreciationAmount = depreciationAmount;
	}

	/**
	 * @return the accumulatedDepreciation
	 */
	public BigDecimal getAccumulatedDepreciation() {
		return accumulatedDepreciation;
	}

	/**
	 * @param accumulatedDepreciation the accumulatedDepreciation to set
	 */
	public void setAccumulatedDepreciation(BigDecimal accumulatedDepreciation) {
		this.accumulatedDepreciation = accumulatedDepreciation;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append(" | Depreciation: ");
		if (depreciationAmount == null) {
			sb.append(" - ");
		} else {
			sb.append(depreciationAmount.toString());
		}
		sb.append(" | Book Value: ");
		if (endOfYearBookValue == null) {
			sb.append(" - ");
		} else {
			sb.append(endOfYearBookValue.toString());
		}
		
		
		return sb.toString();
	}
}
