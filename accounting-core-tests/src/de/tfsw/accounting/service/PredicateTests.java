/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

import de.tfsw.accounting.BaseTestFixture;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoiceState;
import de.tfsw.accounting.model.PaymentTerms;
import de.tfsw.accounting.model.User;
import de.tfsw.accounting.util.TimeFrame;

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
		
		candidate.setCreationDate(LocalDate.now());
		candidate.setSentDate(LocalDate.now());
		candidate.setPaymentTerms(PaymentTerms.getDefault());
		assertTrue(predicate.match(candidate));
		
		predicate = new FindInvoicesPredicate(getTestContext(), InvoiceState.CREATED, InvoiceState.SENT);
		
		assertTrue(predicate.match(candidate));
		
		candidate.setCancelledDate(LocalDate.now());
		assertFalse(predicate.match(candidate));
	}
	
	/**
	 * Test for {@link FindInvoicesForRevenuePredicate}.
	 */
	@Test
	public void testFindInvoicesForRevenuePredicate() {
		TimeFrame timeFrame = new TimeFrame(LocalDate.of(2012, 1, 2), LocalDate.of(2012, 1, 30));
		
		FindInvoicesForRevenuePredicate predicate = new FindInvoicesForRevenuePredicate(timeFrame);
				
		Invoice invoice = new Invoice();
		invoice.setCreationDate(LocalDate.of(2012, 1, 2)); // actual date doesn't matter, just need this to get state CREATED		
		assertFalse(predicate.match(invoice));
		
		// even though the payment date is in range, it was cancelled, so it should be ignored by the predicate
		invoice.setPaymentDate(LocalDate.of(2012, 1, 15));
		invoice.setCancelledDate(invoice.getPaymentDate()); // Invoice was paid, but also cancelled - should be ignored
		assertFalse(predicate.match(invoice));
		
		invoice.setCancelledDate(null);
		assertTrue(predicate.match(invoice));
		
		// outside of range (one day before)
		invoice.setPaymentDate(LocalDate.of(2012, 1, 1));
		assertFalse(predicate.match(invoice));
		
		// outside of range (one day late)
		invoice.setPaymentDate(LocalDate.of(2012, 1, 31));
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
		
		LocalDate date = LocalDate.now();
		Expense expense = new Expense();
		expense.setExpenseType(ExpenseType.OPEX);
		expense.setPaymentDate(date);
		
		assertTrue(predicate.match(expense));
		
		predicate = new FindExpensesPredicate(TimeFrame.currentMonth());
		assertTrue(predicate.match(expense));
		
		expense.setPaymentDate(date.minusMonths(1));
		assertFalse(predicate.match(expense));
		
		expense.setPaymentDate(date.plusMonths(2));
		assertFalse(predicate.match(expense));
		
		// and back to the original date
		expense.setPaymentDate(LocalDate.now());
		
		predicate = new FindExpensesPredicate(TimeFrame.currentMonth(), ExpenseType.CAPEX, ExpenseType.OTHER);
		assertFalse(predicate.match(expense));
		
		predicate = new FindExpensesPredicate(TimeFrame.currentMonth(), ExpenseType.OPEX);
		assertTrue(predicate.match(expense));
	}
}
