/*
 *  Copyright 2012 , 2014 Thorsten Frank (accounting@tfsw.de).
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
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tfsw.accounting.model.AnnualDepreciation;
import de.tfsw.accounting.model.Expense;

/**
 * @author thorsten
 *
 */
class StraightlineDepreciation implements Depreciation {
	
	/**
     * 
     */
    private static final long serialVersionUID = 2147139507234466389L;
    
    /**
     * 
     */
    private static final Logger LOG = Logger.getLogger(StraightlineDepreciation.class);
    
    /**
     * 
     */
	private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);
	
	/**
	 * Default scale for monetary amounts: 2
	 */
	private static final int SCALE = 2;
	
	/**
	 * The expense being depreciated
	 */
	private Expense expense;
	
	/**
     * @param expense
     */
    protected StraightlineDepreciation(Expense expense) {
	    this.expense = expense;
    }

	/**
	 * 
	 * @return
	 */
	@Override
    public BigDecimal getTotalDepreciationAmount() {
		BigDecimal amount;
		if (expense.getSalvageValue() == null) {
			amount = expense.getNetAmount();
		} else {
			amount = expense.getNetAmount().subtract(expense.getSalvageValue());
		}
		return amount;
	}

	/**
	 * 
	 * @return
	 */
	private BigDecimal getAnnualDepreciationAmountUnrounded() {
		return getTotalDepreciationAmount().divide(new BigDecimal(expense.getDepreciationPeriodInYears()), MATH_CONTEXT);
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
    public BigDecimal getAnnualDepreciationAmount() {
		return getAnnualDepreciationAmountUnrounded().setScale(SCALE, MATH_CONTEXT.getRoundingMode());
	}
	
	/**
	 * 
	 * @return
	 */
	private BigDecimal getMonthlyDepreciationAmountUnrounded() {
		return getAnnualDepreciationAmountUnrounded().divide(new BigDecimal(12), MATH_CONTEXT);
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
    public BigDecimal getMonthlyDepreciationAmount() {
		return getMonthlyDepreciationAmountUnrounded().setScale(SCALE, MATH_CONTEXT.getRoundingMode());
	}

	/**
	 * 
	 * @return
	 */
	@Override
    public List<AnnualDepreciation> getDepreciationSchedule() {
		LOG.debug("Building depreciation plan for Expense: " + expense.toString()); //$NON-NLS-1$
		
		List<AnnualDepreciation> schedule = new ArrayList<AnnualDepreciation>();
		
		BigDecimal annualDepreciationAmount = getAnnualDepreciationAmount();

		LOG.debug("Annual amount: " + annualDepreciationAmount.toString()); //$NON-NLS-1$
		
		LocalDate paymentDate = expense.getPaymentDate();
		
		// calculate the number of months in the purchase year
		LOG.debug("Payment date was: " + paymentDate.toString()); //$NON-NLS-1$
		
		final int startingYear = paymentDate.getYear();
		
		LOG.debug("Starting year: " + startingYear); //$NON-NLS-1$
		
		// calculate the number of months that the first year of depreciation includes,
		// e.g. 12 months if the expense was bought in january, 6 months if it was bought in July, 1 month if december
		final int monthsFirstYear = 13 - paymentDate.getMonthValue();
		
		LOG.debug("Months considered in first year: " + monthsFirstYear); //$NON-NLS-1$
		
		int finalYear = startingYear + expense.getDepreciationPeriodInYears();
		
		// cut off the final (partial) year if the expense was made in january
		if (monthsFirstYear == 12) {
			finalYear--;
		}
		
		LOG.debug("Depreciation will end in " + finalYear); //$NON-NLS-1$
		
		// Calculate first year
		AnnualDepreciation adFirst = new AnnualDepreciation();
		adFirst.setBeginningOfYearBookValue(BigDecimal.ZERO);
		adFirst.setYear(startingYear);
		adFirst.setDepreciationAmount(
				getMonthlyDepreciationAmount().multiply(new BigDecimal(monthsFirstYear), MATH_CONTEXT)
				.setScale(SCALE, MATH_CONTEXT.getRoundingMode()));
		adFirst.setAccumulatedDepreciation(BigDecimal.ZERO);
		adFirst.setEndOfYearBookValue(expense.getNetAmount().subtract(adFirst.getDepreciationAmount()));
		schedule.add(adFirst);
		
		LOG.debug(adFirst.toString());
		
		BigDecimal accumulated = adFirst.getDepreciationAmount();
		BigDecimal currentBookValue = adFirst.getEndOfYearBookValue();
		
		int currentYear = startingYear + 1;
		while (currentYear < finalYear) {
			AnnualDepreciation ad = new AnnualDepreciation();
			ad.setYear(currentYear);
			ad.setDepreciationAmount(annualDepreciationAmount);
			ad.setBeginningOfYearBookValue(currentBookValue);
			ad.setAccumulatedDepreciation(accumulated);
			
			accumulated = accumulated.add(ad.getDepreciationAmount());
			currentBookValue = currentBookValue.subtract(annualDepreciationAmount);
			
			ad.setEndOfYearBookValue(currentBookValue);
			
			LOG.debug(ad.toString());
			
			schedule.add(ad);
			currentYear++;
		}
		
		// calculate final year
		AnnualDepreciation adLast = new AnnualDepreciation();
		adLast.setYear(finalYear);
		adLast.setBeginningOfYearBookValue(currentBookValue);
		adLast.setAccumulatedDepreciation(accumulated);
		
		if (expense.getSalvageValue() != null) {
			adLast.setDepreciationAmount(currentBookValue.subtract(expense.getSalvageValue()));
			adLast.setEndOfYearBookValue(expense.getSalvageValue());
		} else {
			adLast.setDepreciationAmount(currentBookValue);
			adLast.setEndOfYearBookValue(BigDecimal.ZERO);
		}
		
		schedule.add(adLast);
		
		LOG.debug(adLast.toString());
		
		return schedule;
	}
	
	/**
	 * @return the depreciationEnd
	 */
	@Override
    public LocalDate getDepreciationEnd() {
		if (expense != null && expense.getPaymentDate() != null) {
			return expense.getPaymentDate().plusYears(expense.getDepreciationPeriodInYears()).minusMonths(1);
		}
		return null;
	}
}