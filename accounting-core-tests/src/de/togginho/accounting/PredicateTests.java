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
package de.togginho.accounting;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.User;

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
		Calendar dueDate = Calendar.getInstance();
		dueDate.add(Calendar.MONTH, 1);
		candidate.setDueDate(dueDate.getTime());
		assertTrue(predicate.match(candidate));
		
		predicate = new FindInvoicesPredicate(getTestContext(), InvoiceState.CREATED, InvoiceState.SENT);
		
		assertTrue(predicate.match(candidate));
		
		candidate.setCancelledDate(new Date());
		assertFalse(predicate.match(candidate));
	}
}
