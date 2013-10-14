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
package de.togginho.accounting.model.internal;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.togginho.accounting.BaseTestFixture;
import de.togginho.accounting.model.AnnualDepreciation;
import de.togginho.accounting.model.Expense;

/**
 * @author thorsten
 *
 */
public class StraightlineDepreciationTest extends BaseTestFixture {

	private StraightlineDepreciation depreciation;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.MAY);
		cal.set(Calendar.YEAR, 2012);
		
		Expense expense = new Expense();
		expense.setNetAmount(new BigDecimal("2000"));
		expense.setPaymentDate(cal.getTime());
		expense.setDepreciationPeriodInYears(3);
		expense.setSalvageValue(new BigDecimal("1"));
		depreciation = new StraightlineDepreciation(expense);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link StraightlineDepreciation#getTotalDepreciationAmount()}.
	 */
	@Test
	public void testGetTotalDepreciationAmount() {
		assertAreEqual(new BigDecimal("1999"), depreciation.getTotalDepreciationAmount());
	}

	/**
	 * Test method for {@link StraightlineDepreciation#getAnnualDepreciationAmount()()}.
	 */
	@Test
	public void testGetAnnualDepreciationAmount() {
		assertAreEqual(new BigDecimal("666.67"), depreciation.getAnnualDepreciationAmount());
	}
	
	/**
	 * Test method for {@link StraightlineDepreciation#getMonthlyDepreciationAmount()}.
	 */
	@Test
	public void testGetMonthlyDepreciationAmount() {
		assertAreEqual(new BigDecimal("55.56"), depreciation.getMonthlyDepreciationAmount());
	}
	
	/**
	 * Test method for {@link StraightlineDepreciation#getDepreciationEnd()}.
	 */
	@Test
	public void testGetDepreciationEnd() {
		Date end = depreciation.getDepreciationEnd();
		assertNotNull(end);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(end);
		
		assertEquals(2015, cal.get(Calendar.YEAR));
		assertEquals(Calendar.APRIL, cal.get(Calendar.MONTH));
		
		Expense e2 = new Expense();
		cal.set(Calendar.YEAR, 2012);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		e2.setPaymentDate(cal.getTime());
		e2.setDepreciationPeriodInYears(5);
		StraightlineDepreciation dep2 = new StraightlineDepreciation(e2);
		
		assertNotNull(dep2.getDepreciationEnd());
		cal.setTime(dep2.getDepreciationEnd());
		assertEquals(2016, cal.get(Calendar.YEAR));
		assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
	}
	
	/**
	 * Test method for {@link StraightlineDepreciation#getDepreciationSchedule()}.
	 */
	@Test
	public void testGetDepreciationPlan() {
		List<AnnualDepreciation> plan = depreciation.getDepreciationSchedule();
		assertEquals(4, plan.size());
		
		AnnualDepreciation ad = plan.get(0);
		
		assertEquals(2012, ad.getYear());
		assertAreEqual(new BigDecimal("444.44"), ad.getDepreciationAmount());
		assertAreEqual(ad.getDepreciationAmount(), ad.getAccumulatedDepreciation());
		assertAreEqual(new BigDecimal("1555.56"), ad.getEndOfYearBookValue());
		
		ad = plan.get(1);
		assertEquals(2013, ad.getYear());
		assertAreEqual(new BigDecimal("666.67"), ad.getDepreciationAmount());
		assertAreEqual(new BigDecimal("1111.11"), ad.getAccumulatedDepreciation());
		assertAreEqual(new BigDecimal("888.89"), ad.getEndOfYearBookValue());

		ad = plan.get(2);
		assertEquals(2014, ad.getYear());
		assertAreEqual(new BigDecimal("666.67"), ad.getDepreciationAmount());
		assertAreEqual(new BigDecimal("1777.78"), ad.getAccumulatedDepreciation());
		assertAreEqual(new BigDecimal("222.22"), ad.getEndOfYearBookValue());
		
		ad = plan.get(3);
		assertEquals(2015, ad.getYear());
		assertAreEqual(new BigDecimal("221.22"), ad.getDepreciationAmount());
		assertAreEqual(new BigDecimal("1999"), ad.getAccumulatedDepreciation());
		assertAreEqual(new BigDecimal("1"), ad.getEndOfYearBookValue());
	}
	
	/**
	 * Test method for {@link StraightlineDepreciation#getDepreciationSchedule()}.
	 */
	@Test
	public void testGetDepreciationPlan2() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, 2010);
		
		Expense expense = new Expense();
		expense.setNetAmount(new BigDecimal("5000"));
		expense.setPaymentDate(cal.getTime());
		expense.setDepreciationPeriodInYears(5);
		depreciation = new StraightlineDepreciation(expense);
		
		List<AnnualDepreciation> plan = depreciation.getDepreciationSchedule();
		assertEquals(5, plan.size());
		
		BigDecimal depreciationAmount = new BigDecimal("1000");
		
		AnnualDepreciation ad = plan.get(0);
		
		assertEquals(2010, ad.getYear());
		assertAreEqual(depreciationAmount, ad.getDepreciationAmount());
		assertAreEqual(depreciationAmount, ad.getAccumulatedDepreciation());
		assertAreEqual(new BigDecimal("4000"), ad.getEndOfYearBookValue());
		
		ad = plan.get(1);
		assertEquals(2011, ad.getYear());
		assertAreEqual(depreciationAmount, ad.getDepreciationAmount());
		assertAreEqual(new BigDecimal("2000"), ad.getAccumulatedDepreciation());
		assertAreEqual(new BigDecimal("3000"), ad.getEndOfYearBookValue());
		
		ad = plan.get(2);
		assertEquals(2012, ad.getYear());
		assertAreEqual(depreciationAmount, ad.getDepreciationAmount());
		assertAreEqual(new BigDecimal("3000"), ad.getAccumulatedDepreciation());
		assertAreEqual(new BigDecimal("2000"), ad.getEndOfYearBookValue());
		
		ad = plan.get(3);
		assertEquals(2013, ad.getYear());
		assertAreEqual(depreciationAmount, ad.getDepreciationAmount());
		assertAreEqual(new BigDecimal("4000"), ad.getAccumulatedDepreciation());
		assertAreEqual(new BigDecimal("1000"), ad.getEndOfYearBookValue());
		
		ad = plan.get(4);
		assertEquals(2014, ad.getYear());
		assertAreEqual(depreciationAmount, ad.getDepreciationAmount());
		assertAreEqual(new BigDecimal("5000"), ad.getAccumulatedDepreciation());
		assertAreEqual(BigDecimal.ZERO, ad.getEndOfYearBookValue());
	}
}
