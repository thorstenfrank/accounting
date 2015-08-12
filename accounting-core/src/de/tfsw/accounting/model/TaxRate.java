/*
 *  Copyright 2011, 2014 Thorsten Frank (accounting@tfsw.de).
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

import de.tfsw.accounting.util.FormatUtil;

/**
 * Tax rates are applicable to any monetary amount.
 * 
 * @author Thorsten Frank
 * @since  1.0
 *
 */
public class TaxRate extends AbstractBaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public static final String FIELD_SHORT_NAME = "shortName";
	public static final String FIELD_LONG_NAME = "longName";
	public static final String FIELD_RATE = "rate";
	public static final String FIELD_IS_VAT = "isVAT";
	
	/** Short name for this tax rate. */
	private String shortName;
	
	/** Long name for this tax rate. */
	private String longName;
	
	/** The actual tax rate. */
	private BigDecimal rate;
	
	/** Whether this tax rate is a VAT/GST or similar type of tax.
	 *  Defaults to <code>true</code>.
	 */
	private boolean isVAT = true;
	
	/**
	 * 
	 */
	public TaxRate() {
		super();
	}
	
	/**
	 * @param shortName
	 * @param longName
	 * @param rate
	 * @param isVAT
	 */
	public TaxRate(String shortName, String longName, BigDecimal rate, boolean isVAT) {
		this.shortName = shortName;
		this.longName = longName;
		this.rate = rate;
		this.isVAT = isVAT;
	}
	
	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return the longName
	 */
	public String getLongName() {
		return longName;
	}

	/**
	 * @param longName the longName to set
	 */
	public void setLongName(String longName) {
		this.longName = longName;
	}

	/**
	 * @return the rate
	 */
	public BigDecimal getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(BigDecimal rate) {
		this.rate = rate.stripTrailingZeros();
	}

	/**
	 * @return the isVAT
	 */
	public boolean getIsVAT() {
		return isVAT;
	}

	/**
	 * @param isVAT the isVAT to set
	 */
	public void setIsVAT(boolean isVAT) {
		this.isVAT = isVAT;
	}

	/**
	 * Returns [shortName] ([formatted rate]).
	 * @return
	 */
	public String toShortString() {
		StringBuilder sb = new StringBuilder();
		sb.append(shortName).append(" (");
		sb.append(FormatUtil.formatPercentValue(rate)).append(")");
		return sb.toString();
	}
	
	/**
	 * Returns [longName] ([formatted rate]).
	 * @return
	 */
	public String toLongString() {
		StringBuilder sb = new StringBuilder();
		sb.append(longName).append(" (");
		sb.append(FormatUtil.formatPercentValue(rate)).append(")");
		return sb.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toShortString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isVAT ? 1231 : 1237);
		result = prime * result + ((rate == null) ? 0 : rate.hashCode());
		result = prime * result
				+ ((shortName == null) ? 0 : shortName.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TaxRate other = (TaxRate) obj;
		if (isVAT != other.isVAT) {
			return false;
		}
		if (rate == null) {
			if (other.rate != null) {
				return false;
			}
		} else if (rate.compareTo(other.rate) != 0) {
			return false;
		}
		if (shortName == null) {
			if (other.shortName != null) {
				return false;
			}
		} else if (!shortName.equals(other.shortName)) {
			return false;
		}
		return true;
	}
}
