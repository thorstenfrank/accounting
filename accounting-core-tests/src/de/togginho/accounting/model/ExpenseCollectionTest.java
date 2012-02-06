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
import java.util.HashSet;
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
	 * Test method for {@link de.togginho.accounting.model.ExpenseCollection#setExpenses(java.util.Set)}.
	 */
	@Test
	public void testPriceCalculation() {
		final TaxRate vat = getTestUser().getTaxRates().iterator().next();
		Expense capex = new Expense();
		capex.setExpenseType(ExpenseType.CAPEX);
		capex.setNetAmount(new BigDecimal("1000"));
		capex.setTaxRate(vat);
		
		Expense opex1 = new Expense();
		opex1.setExpenseType(ExpenseType.OPEX);
		opex1.setNetAmount(new BigDecimal("100"));
		opex1.setTaxRate(vat);
		
		Expense opex2 = new Expense();
		opex2.setExpenseType(ExpenseType.OPEX);
		opex2.setNetAmount(new BigDecimal("200"));
		
		Set<Expense> expenses = new HashSet<Expense>();
		expenses.add(capex);
		expenses.add(opex1);
		expenses.add(opex2);
		
		ExpenseCollection ec = new ExpenseCollection();
		ec.setExpenses(expenses);
		
		Price total = ec.getTotalCost();
		assertEquals(0, new BigDecimal("1300").compareTo(total.getNet()));
		assertEquals(0, new BigDecimal("1465").compareTo(total.getGross()));
		assertEquals(0, new BigDecimal("165").compareTo(total.getTax()));
		
		Price operating = ec.getOperatingCosts();
		assertEquals(0, new BigDecimal("300").compareTo(operating.getNet()));
		assertEquals(0, new BigDecimal("315").compareTo(operating.getGross()));
		assertEquals(0, new BigDecimal("15").compareTo(operating.getTax()));
	}

}
