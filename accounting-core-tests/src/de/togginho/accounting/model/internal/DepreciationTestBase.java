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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import de.togginho.accounting.BaseTestFixture;
import de.togginho.accounting.model.AnnualDepreciation;
import de.togginho.accounting.model.DepreciationMethod;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.util.CalculationUtil;

/**
 * @author thorsten
 *
 */
public abstract class DepreciationTestBase extends BaseTestFixture {

	//private static final Logger LOG = Logger.getLogger(DepreciationTestBase.class);
	
	/**
	 * 
	 */
	@Test
	public void testDepreciation() {
		for (int index = 0; index < getNumberOfRuns(); index++) {
			List<AnnualDepreciation> plan = CalculationUtil.calculateDepreciationSchedule(getExpense(index));
			AnnualDepreciation[] expectedPlan = getExpectedPlan(index);
			
			assertEquals("Number of entries in plan do not match", expectedPlan.length, plan.size());
			
			for (int planIndex = 0; planIndex < plan.size(); planIndex++) {
//				LOG.debug(String.format("Run [%s], comparing plan no. [%s]", index, planIndex));
				assertEqual(expectedPlan[planIndex], plan.get(planIndex));
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract int getNumberOfRuns();
	
	/**
	 * 
	 * @param testRunIndex
	 * @return
	 */
	protected abstract Expense getExpense(int testRunIndex);
	
	/**
	 * 
	 * @param testRunIndex
	 * @return
	 */
	protected abstract AnnualDepreciation[] getExpectedPlan(int testRunIndex);
	
	/**
	 * 
	 * @param testRunIndex
	 * @return
	 */
	protected abstract Calendar getExpectedEnd(int testRunIndex);
	
	/**
	 * 
	 * @param testRunIndex
	 */
	protected abstract BigDecimal getExpectedAnnualAmount(int testRunIndex);
	
	/**
	 * 
	 * @param testRunIndex
	 */
	protected abstract BigDecimal getExpectedMonthlyAmount(int testRunIndex);
	
	/**
	 * 
	 * @param testRunIndex
	 */
	protected abstract BigDecimal getExpectedTotalAmount(int testRunIndex);
	
	/**
	 * 
	 * @param method
	 * @param amount
	 * @param day
	 * @param month
	 * @param year
	 * @param period
	 * @param salvage
	 * @return
	 */
	protected static Expense createExpense(DepreciationMethod method, BigDecimal amount, int day, int month, int year, int period, BigDecimal salvage) {
		Expense ex = new Expense();
		ex.setDescription("JUnit Test Expense");
		ex.setCategory("JUnit Test Category");
		ex.setDepreciationMethod(method);
		ex.setDepreciationPeriodInYears(period);
		ex.setExpenseType(ExpenseType.CAPEX);
		ex.setNetAmount(amount);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		ex.setPaymentDate(cal.getTime());
		ex.setSalvageValue(salvage);
		
		return ex;
	}
	
	/**
	 * 
	 * @param expected
	 * @param actual
	 */
	protected void assertEqual(AnnualDepreciation expected, AnnualDepreciation actual) {
//		LOG.debug("Expected: " + expected.toString());
//		LOG.debug("Actual: " + actual.toString());
		
		assertEquals(expected.getYear(), actual.getYear());
		assertAreEqual(expected.getAccumulatedDepreciation(), actual.getAccumulatedDepreciation());
		assertAreEqual(expected.getBeginningOfYearBookValue(), actual.getBeginningOfYearBookValue());
		assertAreEqual(expected.getDepreciationAmount(), actual.getDepreciationAmount());
		assertAreEqual(expected.getEndOfYearBookValue(), actual.getEndOfYearBookValue());
	}
}
