/*
 *  Copyright 2012, 2014 Thorsten Frank (accounting@tfsw.de).
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

import java.math.BigDecimal;

/**
 * The depreciation expense of a single year, usually as part of a depreciation / amortization schedule.
 * 
 * <p>
 * Instances of this class represent a single year's depreciation, and are independent of the actual method that was
 * used to calculate its values.
 * </p>
 * 
 * @author Thorsten Frank
 * @since  1.1
 */
public class AnnualDepreciation extends AbstractBaseEntity {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 2L;

	private int year;
	
	private BigDecimal beginningOfYearBookValue;
	
	private BigDecimal endOfYearBookValue;
	
	private BigDecimal depreciationAmount;
	
	private BigDecimal accumulatedDepreciation;
	
	/**
	 * Creates a new, empty annual depreciation instance.
	 */
	public AnnualDepreciation() {
		super();
	}
	
	/**
	 * Creates a new annual depreciation instance.
	 * 
	 * @param year						the 4-digit year (e.g. 2011, 1988) for which this instance holds values
	 * @param beginningOfYearBookValue	beginning-of-year value
	 * @param endOfYearBookValue		end-of-year value, usually <code>beginning - depreciation amount</code>
	 * @param depreciationAmount		the amount depreciated during this year
	 * @param accumulatedDepreciation	the total depreciated amount at the beginning of the year
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
	 * @return the 4-digit year, e.g. 2011, 1988
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year 4-digit year, e.g. 2011, 1988
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
	
	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
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
