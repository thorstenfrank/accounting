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
package de.togginho.accounting.model;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.togginho.accounting.BaseTestFixture;

/**
 * @author thorsten
 *
 */
public class ExpenseCollectionTest extends BaseTestFixture {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	 * OPEX: 300, 15, 315
	 * CAPEX: 1000, 150, 1150
	 * TOTAL: 1300, 165, 1465
	 * 
	 * Test method for {@link ExpenseCollection#setExpenses(java.util.Set)}.
	 */
	@Test
	public void testPriceCalculation() {
		ExpenseCollection ec = new ExpenseCollection();
		ec.setExpenses(createTestExpenses());
		
		assertIsTestExpenses(ec);
		
		// this has all been outsourced so other tests can re-use the expense collection
	}

	/**
	 * Test method for {@link ExpenseCollection#setExpenses(java.util.Set)}.
	 */
	@Test
	public void testGetIncludedTypes() {
		Set<Expense> expenses = createTestExpenses();
		
		ExpenseCollection ec = new ExpenseCollection(expenses);
		
		Set<ExpenseType> types = ec.getIncludedTypes();
		assertEquals(3, types.size());
		assertTrue(types.contains(ExpenseType.OPEX));
		assertTrue(types.contains(ExpenseType.CAPEX));
		assertTrue(types.contains(null));
		
		// add two new expenses - one known, one previously unknown type
		Expense ex = new Expense();
		ex.setExpenseType(ExpenseType.CAPEX);
		ex.setNetAmount(new BigDecimal("5"));
		expenses.add(ex);
		
		Expense otherEx = new Expense();
		otherEx.setExpenseType(ExpenseType.OTHER);
		otherEx.setNetAmount(new BigDecimal("100"));
		expenses.add(otherEx);
		
		ec = new ExpenseCollection(expenses);
		types = ec.getIncludedTypes();
		
		assertEquals(4, types.size());
		assertTrue(types.contains(ExpenseType.OPEX));
		assertTrue(types.contains(ExpenseType.CAPEX));
		assertTrue(types.contains(ExpenseType.OTHER));
		assertTrue(types.contains(null));
	}

}
