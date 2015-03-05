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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

import de.tfsw.accounting.model.AnnualDepreciation;
import de.tfsw.accounting.model.DepreciationMethod;
import de.tfsw.accounting.model.Expense;

/**
 * @author thorsten
 *
 */
public class StraightlineDepreciationTest extends DepreciationTestBase {
	
	private static final int NUMBER_OF_RUNS = 2;
	
	private static Expense[] EXPENSES;
	private static List<AnnualDepreciation[]> EXPECTED_PLANS;
	private static LocalDate[] EXPECTED_END;
	private static BigDecimal[] EXPECTED_ANNUAL_AMOUNTS;
	private static BigDecimal[] EXPECTED_MONTHLY_AMOUNTS;
	private static BigDecimal[] EXPECTED_TOTAL_AMOUNTS;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		EXPENSES = new Expense[NUMBER_OF_RUNS];
		EXPECTED_PLANS = new ArrayList<AnnualDepreciation[]>(NUMBER_OF_RUNS);
		EXPECTED_END = new LocalDate[NUMBER_OF_RUNS];
		EXPECTED_ANNUAL_AMOUNTS = new BigDecimal[NUMBER_OF_RUNS];
		EXPECTED_MONTHLY_AMOUNTS = new BigDecimal[NUMBER_OF_RUNS];
		EXPECTED_TOTAL_AMOUNTS = new BigDecimal[NUMBER_OF_RUNS];
		
		buildExpectedOne(0);
		buildExpectedTwo(1);
	}

	/**
	 * 
	 * @param index
	 */
	private static void buildExpectedOne(int index) {
		EXPENSES[index] = createExpense(DepreciationMethod.STRAIGHTLINE, new BigDecimal("1738.58"), 2, 5, 2007, 3, BigDecimal.ONE);
		EXPECTED_END[index] = LocalDate.of(2010, 4, 2);
		EXPECTED_ANNUAL_AMOUNTS[index] = new BigDecimal("579.19");
		EXPECTED_MONTHLY_AMOUNTS[index] = new BigDecimal("48.27");
		EXPECTED_TOTAL_AMOUNTS[index] = new BigDecimal("1737.58");

		AnnualDepreciation[] plan = new AnnualDepreciation[4];
		plan[0] = new AnnualDepreciation(2007, BigDecimal.ZERO, new BigDecimal("1352.42"), new BigDecimal("386.16"), BigDecimal.ZERO);
		plan[1] = new AnnualDepreciation(2008, plan[0].getEndOfYearBookValue(), new BigDecimal("773.23"), EXPECTED_ANNUAL_AMOUNTS[index], plan[0].getDepreciationAmount());
		plan[2] = new AnnualDepreciation(2009, plan[1].getEndOfYearBookValue(), new BigDecimal("194.04"), EXPECTED_ANNUAL_AMOUNTS[index], new BigDecimal("965.35"));
		plan[3] = new AnnualDepreciation(2010, plan[2].getEndOfYearBookValue(), EXPENSES[index].getSalvageValue(), new BigDecimal("193.04"), new BigDecimal("1544.54"));
		EXPECTED_PLANS.add(index, plan);
	}
	
	/**
	 * 
	 * @param index
	 */
	private static void buildExpectedTwo(int index) {
		EXPENSES[index] = createExpense(DepreciationMethod.STRAIGHTLINE, new BigDecimal("3846.45"), 30, 10, 2013, 6, new BigDecimal("355"));
		EXPECTED_END[index] = LocalDate.of(2019, 9, 30);
		EXPECTED_ANNUAL_AMOUNTS[index] = new BigDecimal("581.91");
		EXPECTED_MONTHLY_AMOUNTS[index] = new BigDecimal("48.49");
		EXPECTED_TOTAL_AMOUNTS[index] = new BigDecimal("3491.45");
		
		AnnualDepreciation[] plan = new AnnualDepreciation[7];
		plan[0] = new AnnualDepreciation(2013, BigDecimal.ZERO, new BigDecimal("3700.98"), new BigDecimal("145.47"), BigDecimal.ZERO);
		plan[1] = new AnnualDepreciation(2014, plan[0].getEndOfYearBookValue(), new BigDecimal("3119.07"), EXPECTED_ANNUAL_AMOUNTS[index], plan[0].getDepreciationAmount());
		plan[2] = new AnnualDepreciation(2015, plan[1].getEndOfYearBookValue(), new BigDecimal("2537.16"), EXPECTED_ANNUAL_AMOUNTS[index], new BigDecimal("727.38"));
		plan[3] = new AnnualDepreciation(2016, plan[2].getEndOfYearBookValue(), new BigDecimal("1955.25"), EXPECTED_ANNUAL_AMOUNTS[index], new BigDecimal("1309.29"));
		plan[4] = new AnnualDepreciation(2017, plan[3].getEndOfYearBookValue(), new BigDecimal("1373.34"), EXPECTED_ANNUAL_AMOUNTS[index], new BigDecimal("1891.2"));
		plan[5] = new AnnualDepreciation(2018, plan[4].getEndOfYearBookValue(), new BigDecimal("791.43"), EXPECTED_ANNUAL_AMOUNTS[index], new BigDecimal("2473.11"));
		plan[6] = new AnnualDepreciation(2019, plan[5].getEndOfYearBookValue(), EXPENSES[index].getSalvageValue(), new BigDecimal("436.43"), new BigDecimal("3055.02"));
		EXPECTED_PLANS.add(index, plan);
	}
		
	/**
	 * 
	 * @return
	 */
	protected int getNumberOfRuns() {
		return NUMBER_OF_RUNS;
	}
	
	/**
	 * 
	 * @param testRunIndex
	 * @return
	 */
	protected Expense getExpense(int testRunIndex) {
		return EXPENSES[testRunIndex];
	}
	
	/**
	 * 
	 * @param testRunIndex
	 * @return
	 */
	protected AnnualDepreciation[] getExpectedPlan(int testRunIndex) {
		return EXPECTED_PLANS.get(testRunIndex);
	}
	
	/**
	 * 
	 * @param testRunIndex
	 * @return
	 */
	protected LocalDate getExpectedEnd(int testRunIndex) {
		return EXPECTED_END[testRunIndex];
	}
	
	/**
	 * 
	 * @param testRunIndex
	 */
	protected BigDecimal getExpectedAnnualAmount(int testRunIndex) {
		return EXPECTED_ANNUAL_AMOUNTS[testRunIndex];
	}

	/**
	 * 
	 * @param testRunIndex
	 */
	protected BigDecimal getExpectedMonthlyAmount(int testRunIndex) {
		return EXPECTED_MONTHLY_AMOUNTS[testRunIndex];
	}
	
	/**
	 * 
	 * @param testRunIndex
	 */
	protected BigDecimal getExpectedTotalAmount(int testRunIndex) {
		return EXPECTED_TOTAL_AMOUNTS[testRunIndex];
	}
}
