/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.elster.internal;

import java.time.YearMonth;

import de.tfsw.accounting.elster.ElsterDTO;

/**
 * Implementation of {@link ElsterDTO} with a specific filing period and using a {@link TaxNumberConverter}.
 * 
 * @author Thorsten Frank
 *
 */
class ElsterDTOImpl extends ElsterDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * What is says on the tin.
	 */
	private TaxNumberConverter taxNumberConverter;
	
	/**
	 * 
	 */
	private YearMonth filingPeriod;
	
	/**
	 * Uses {@link DefaultTaxNumberConverter}.
	 * 
	 * @param filingPeriod
	 */
	ElsterDTOImpl(YearMonth filingPeriod) {
		this(filingPeriod, new DefaultTaxNumberConverter());
	}
	
	/**
	 * Creates a new DTO.
	 * 
	 * @param filingPeriod the filing period
	 * 
	 * @param taxNumberConverter the converter to use for converting the tax number to a machine-readable format
	 * 
	 * @see TaxNumberConverter
	 * @see #getFilingPeriod()
	 * @see #generateTaxNumber()
	 */
	ElsterDTOImpl(YearMonth filingPeriod, TaxNumberConverter taxNumberConverter) {
		super();
		this.filingPeriod = filingPeriod;
		this.taxNumberConverter = taxNumberConverter;
	}
	
	/**
	 * @return the filingPeriod
	 */
	public YearMonth getFilingPeriod() {
		return filingPeriod;
	}
	
	/**
	 * Don't call this method unless you know what you're doing. 
	 * @param period
	 */
	protected void setFilingPeriod(YearMonth period) {
		this.filingPeriod = period;
	}
	
	/**
	 * @see de.tfsw.accounting.elster.ElsterDTO#generateTaxNumber()
	 */
	@Override
	protected String generateTaxNumber() { 
		return getCompanyTaxNumberOrig() != null
				? taxNumberConverter.convertToInterfaceFormat(getFinanzAmtBL(), getCompanyTaxNumberOrig())
				: null;
	}
}
