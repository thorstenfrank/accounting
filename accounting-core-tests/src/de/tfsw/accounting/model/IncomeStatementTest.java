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
package de.tfsw.accounting.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tfsw.accounting.BaseTestFixture;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.model.IncomeStatement;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class IncomeStatementTest extends BaseTestFixture {

	private static final IncomeStatement IS = new IncomeStatement();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		IS.setTimeFrame(new TimeFrame(buildDate(1, 0, 2010), buildDate(31, 11, 2010)));
		
		Set<Expense> opexExpenses = new HashSet<>();
		Set<Expense> capexExpenses = new HashSet<>();
		Set<Expense> otherExpenses = new HashSet<>();
		
		for (Expense expense : createTestExpenses()) {
			if (expense.getExpenseType() == null) {
				continue;
			}
			switch (expense.getExpenseType()) {
			case OPEX:
				opexExpenses.add(expense);
				break;
			case CAPEX:
				capexExpenses.add(expense);
				break;
			case OTHER:
				otherExpenses.add(expense);
				break;
			default:
				break;
			}
		}
		
		ExpenseCollection opex = new ExpenseCollection();
		opex.setTimeFrame(IS.getTimeFrame());
		opex.setExpenses(opexExpenses);
		IS.setOperatingExpenses(opex);
		
		ExpenseCollection capex = new ExpenseCollection();
		capex.setTimeFrame(IS.getTimeFrame());
		capex.setExpenses(capexExpenses);
		IS.setCapitalExpenses(capex);
		
		ExpenseCollection other = new ExpenseCollection();
		other.setTimeFrame(IS.getTimeFrame());
		other.setExpenses(otherExpenses);
		IS.setOtherExpenses(other);
		
		IS.setRevenue(createTestRevenue());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getRevenue()}.
	 */
	@Test
	public void testGetRevenue() {
		assertIsTestRevenue(IS.getRevenue());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getTotalRevenue()}.
	 */
	@Test
	public void testGetTotalRevenue() {
		assertIsTestRevenue(IS.getTotalRevenue());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getOperatingExpenses()}.
	 */
	@Test
	public void testGetOperatingExpenses() {
		ExpenseCollection opex = IS.getOperatingExpenses();
		assertEquals(2, opex.getExpenses().size());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getTotalOperatingCosts()}.
	 */
	@Test
	public void testGetTotalOperatingCosts() {
		Price total = IS.getTotalOperatingCosts();
		assertAreEqual(new BigDecimal("300"), total.getNet());
		assertAreEqual(new BigDecimal("15"), total.getTax());
		assertAreEqual(new BigDecimal("315"), total.getGross());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getOperatingExpenseCategories()}.
	 */
	@Test
	public void testGetOperatingExpenseCategories() {
		assertEquals(2, IS.getOperatingExpenseCategories().size());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getOtherExpenses()}.
	 */
	@Test
	public void testGetOtherExpenses() {
		assertTrue(IS.getOtherExpenses().getExpenses().isEmpty());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getOtherExpenseCategories()}.
	 */
	@Test
	public void testGetOtherExpenseCategories() {
		assertTrue(IS.getOtherExpenseCategories().isEmpty());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getCapitalExpenses()}.
	 */
	@Test
	public void testGetCapitalExpenses() {
		assertEquals(1, IS.getCapitalExpenses().getExpenses().size());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getCapitalExpenseCategories()}.
	 */
	@Test
	public void testGetCapitalExpenseCategories() {
		assertEquals(1, IS.getCapitalExpenseCategories().size());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getTotalExpenses()}.
	 */
	@Test
	public void testGetTotalExpenses() {
		assertIsTestExpenseTotal(IS.getTotalExpenses());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getGrossProfit()}.
	 */
	@Test
	public void testGetGrossProfit() {
		Price grossProfit = IS.getGrossProfit();
		assertAreEqual(new BigDecimal("8298.78"), grossProfit.getNet());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.model.IncomeStatement#getTaxSum()}.
	 */
	@Test
	public void testGetTaxSum() {
		assertAreEqual(new BigDecimal("1035"), IS.getTaxSum());
	}

}
