/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import de.togginho.accounting.BaseTestFixture;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.User;
import de.togginho.accounting.service.FindCurrentUserPredicate;
import de.togginho.accounting.service.FindInvoicesForRevenuePredicate;
import de.togginho.accounting.service.FindInvoicesPredicate;
import de.togginho.accounting.util.TimeFrame;

/**
 * Tests for DB4o predicates. Since these usually only contain a single method, all tests are compressed into this 
 * class.
 * 
 * <p>See method documentation for details.</p>
 * 
 * @author thorsten
 *
 */
public class PredicateTests extends BaseTestFixture {

	/**
	 * Test for {@link FindCurrentUserPredicate}.
	 */
	@Test
	public void testFindCurrentUserPredicate() {
		FindCurrentUserPredicate predicate = new FindCurrentUserPredicate(getTestContext());
		
		assertFalse(predicate.match(null));
		
		User candidate = new User();
		
		candidate.setName("SomeBogusName");
		assertFalse(predicate.match(candidate));
		
		candidate.setName(TEST_USER_NAME);
		assertTrue(predicate.match(candidate));
	}
	
	/**
	 * Test for {@link FindInvoicesPredicate}.
	 */
	@Test
	public void testFindInvoicesPredicate() {
		FindInvoicesPredicate predicate = new FindInvoicesPredicate(getTestContext());
		
		Invoice candidate = new Invoice();
		
		assertFalse(predicate.match(candidate));
		
		candidate.setUser(getTestUser());
		assertTrue(predicate.match(candidate));
		
		candidate.setCreationDate(new Date());
		candidate.setSentDate(new Date());
		candidate.setPaymentTerms(PaymentTerms.getDefault());
		assertTrue(predicate.match(candidate));
		
		predicate = new FindInvoicesPredicate(getTestContext(), InvoiceState.CREATED, InvoiceState.SENT);
		
		assertTrue(predicate.match(candidate));
		
		candidate.setCancelledDate(new Date());
		assertFalse(predicate.match(candidate));
	}
	
	/**
	 * Test for {@link FindInvoicesForRevenuePredicate}.
	 */
	@Test
	public void testFindInvoicesForRevenuePredicate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 2);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, 2012);
		
		final Date from = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, 30);
		final Date until = cal.getTime();
		TimeFrame timeFrame = new TimeFrame(from, until);
		
		FindInvoicesForRevenuePredicate predicate = new FindInvoicesForRevenuePredicate(timeFrame);
		
		Invoice invoice = new Invoice();
		invoice.setCreationDate(cal.getTime()); // actual date doesn't matter, just need this to get state CREATED		
		assertFalse(predicate.match(invoice));
		
		cal.set(Calendar.DAY_OF_MONTH, 15);
		invoice.setPaymentDate(cal.getTime());
		invoice.setCancelledDate(cal.getTime()); // Invoice was paid, but also cancelled - should be ignored
		assertFalse(predicate.match(invoice));
		
		invoice.setCancelledDate(null);
		assertTrue(predicate.match(invoice));
		
		cal.set(Calendar.DAY_OF_MONTH, 1); // outside of range
		invoice.setPaymentDate(cal.getTime());
		assertFalse(predicate.match(invoice));
		
		cal.set(Calendar.DAY_OF_MONTH, 31); // outside of range
		invoice.setPaymentDate(cal.getTime());
		assertFalse(predicate.match(invoice));
	}
	
	/**
	 * Test for {@link FindInvoicesForClientPredicate}.
	 */
	@Test
	public void testFindInvoicesForClientPredicate() {
		Client client = new Client();
		client.setName("JUnitClient");
		
		FindInvoicesForClientPredicate predicate = new FindInvoicesForClientPredicate(client);
		
		Invoice invoice = new Invoice();
		assertFalse(predicate.match(invoice));
		
		invoice.setClient(client);
		assertTrue(predicate.match(invoice));
	}
	
	/**
	 * Test for {@link FindExpensesPredicate}.
	 */
	@Test
	public void testFindExpensesPredicate() {
		FindExpensesPredicate predicate = new FindExpensesPredicate();
		
		Calendar cal = Calendar.getInstance();
		Expense expense = new Expense();
		expense.setExpenseType(ExpenseType.OPEX);
		expense.setPaymentDate(cal.getTime());
		
		assertTrue(predicate.match(expense));
		
		predicate = new FindExpensesPredicate(TimeFrame.currentMonth());
		assertTrue(predicate.match(expense));
		
		cal.add(Calendar.MONTH, -1);
		expense.setPaymentDate(cal.getTime());
		assertFalse(predicate.match(expense));
		
		cal.add(Calendar.MONTH, 2);
		expense.setPaymentDate(cal.getTime());
		assertFalse(predicate.match(expense));
		
		// and back to the original date
		cal = Calendar.getInstance();
		expense.setPaymentDate(cal.getTime());
		
		predicate = new FindExpensesPredicate(TimeFrame.currentMonth(), ExpenseType.CAPEX, ExpenseType.OTHER);
		assertFalse(predicate.match(expense));
		
		predicate = new FindExpensesPredicate(TimeFrame.currentMonth(), ExpenseType.OPEX);
		assertTrue(predicate.match(expense));
	}
}
