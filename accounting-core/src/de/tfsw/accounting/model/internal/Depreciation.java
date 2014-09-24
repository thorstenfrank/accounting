/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.model.internal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import de.tfsw.accounting.model.AnnualDepreciation;

/**
 * @author thorsten
 *
 */
public interface Depreciation extends Serializable {

	/**
	 * 
	 * @return
	 */
	Date getDepreciationEnd();

	/**
	 * 
	 * @return
	 */
	List<AnnualDepreciation> getDepreciationSchedule();

	/**
	 * 
	 * @return
	 */
	BigDecimal getMonthlyDepreciationAmount();

	/**
	 * 
	 * @return
	 */
	BigDecimal getAnnualDepreciationAmount();

	/**
	 * 
	 * @return
	 */
	BigDecimal getTotalDepreciationAmount();

}
