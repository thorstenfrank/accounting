/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.model.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.togginho.accounting.model.AnnualDepreciation;
import de.togginho.accounting.model.Expense;

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
	public Date getDepreciationEnd() {
		return expense.getPaymentDate();
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.model.internal.Depreciation#getDepreciationSchedule()
	 */
	@Override
	public List<AnnualDepreciation> getDepreciationSchedule() {
		List<AnnualDepreciation> schedule = new ArrayList<AnnualDepreciation>();
		
		AnnualDepreciation ad = new AnnualDepreciation();
		Calendar cal = Calendar.getInstance();
		cal.setTime(expense.getPaymentDate());
		ad.setYear(cal.get(Calendar.YEAR));
		ad.setAccumulatedDepreciation(expense.getNetAmount());
		ad.setBeginningOfYearBookValue(expense.getNetAmount());
		ad.setDepreciationAmount(expense.getNetAmount());
		ad.setEndOfYearBookValue(BigDecimal.ZERO);
		schedule.add(ad);
		
		return schedule;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.model.internal.Depreciation#getMonthlyDepreciationAmount()
	 */
	@Override
	public BigDecimal getMonthlyDepreciationAmount() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.model.internal.Depreciation#getAnnualDepreciationAmount()
	 */
	@Override
	public BigDecimal getAnnualDepreciationAmount() {
		return expense.getNetAmount();
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.model.internal.Depreciation#getTotalDepreciationAmount()
	 */
	@Override
	public BigDecimal getTotalDepreciationAmount() {
		return expense.getNetAmount();
	}

}
