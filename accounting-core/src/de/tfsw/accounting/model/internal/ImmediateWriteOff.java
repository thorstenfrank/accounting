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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.tfsw.accounting.model.AnnualDepreciation;
import de.tfsw.accounting.model.Expense;

/**
 * @author thorsten
 *
 */
class ImmediateWriteOff implements Depreciation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8157737274268128239L;

	private Expense expense;
	
	/**
	 * @param expense
	 */
	ImmediateWriteOff(Expense expense) {
		this.expense = expense;
	}

	/**
	 * {@inheritDoc}
	 * @see Depreciation#getDepreciationEnd()
	 */
	@Override
	public LocalDate getDepreciationEnd() {
		return expense.getPaymentDate();
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.model.internal.Depreciation#getDepreciationSchedule()
	 */
	@Override
	public List<AnnualDepreciation> getDepreciationSchedule() {
		List<AnnualDepreciation> schedule = new ArrayList<AnnualDepreciation>();
		
		AnnualDepreciation ad = new AnnualDepreciation();
		ad.setYear(expense.getPaymentDate().getYear());
		ad.setAccumulatedDepreciation(expense.getNetAmount());
		ad.setBeginningOfYearBookValue(expense.getNetAmount());
		ad.setDepreciationAmount(expense.getNetAmount());
		ad.setEndOfYearBookValue(BigDecimal.ZERO);
		schedule.add(ad);
		
		return schedule;
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.model.internal.Depreciation#getMonthlyDepreciationAmount()
	 */
	@Override
	public BigDecimal getMonthlyDepreciationAmount() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.model.internal.Depreciation#getAnnualDepreciationAmount()
	 */
	@Override
	public BigDecimal getAnnualDepreciationAmount() {
		return expense.getNetAmount();
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.model.internal.Depreciation#getTotalDepreciationAmount()
	 */
	@Override
	public BigDecimal getTotalDepreciationAmount() {
		return expense.getNetAmount();
	}

}
