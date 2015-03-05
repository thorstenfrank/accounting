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
package de.tfsw.accounting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Test;

/**
 * Test for {@link Invoice}.
 * 
 * @author thorsten
 *
 */
public class InvoiceTest {

	/**
	 * Test method for {@link Invoice#getState()}.
	 */
	@Test
	public void testInvoiceLifecycle() {
		Invoice invoice = new Invoice();
		
		assertFalse(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		invoice.setUser(new User());
		invoice.setClient(new Client());
		assertFalse(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		invoice.setInvoicePositions(new ArrayList<InvoicePosition>());
		assertFalse(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		InvoicePosition ip = new InvoicePosition();
		invoice.getInvoicePositions().add(ip);
		assertFalse(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		invoice.getClient().setAddress(new Address());
		assertTrue(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		// UNSAVED
		assertEquals(InvoiceState.UNSAVED, invoice.getState());
		assertTrue(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		// CREATED
		invoice.setCreationDate(LocalDate.now());
		assertEquals(InvoiceState.CREATED, invoice.getState());
		assertTrue(invoice.canBeEdited());
		assertTrue(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		assertTrue(invoice.canBeSent());
		
		invoice.setSentDate(LocalDate.now());
		invoice.setPaymentTerms(PaymentTerms.getDefault());
		
		// SENT
		assertEquals(InvoiceState.SENT, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertTrue(invoice.canBeCancelled());
		assertTrue(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		// CANCELLED from SENT
		invoice.setCancelledDate(LocalDate.now());
		assertEquals(InvoiceState.CANCELLED, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		// PAID
		invoice.setCancelledDate(null);
		invoice.setPaymentDate(LocalDate.now());
		assertEquals(InvoiceState.PAID, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertTrue(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		// CANCELLED from PAID
		// this should never happen since a cancelled invoice cannot be marked as paid, but stil...
		invoice.setCancelledDate(LocalDate.now());
		assertEquals(InvoiceState.CANCELLED, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		// "uncancel" the invoice for the rest of the tests
		invoice.setCancelledDate(null);
		
		// OVERDUE
		LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
		invoice.setSentDate(threeMonthsAgo);
		invoice.setInvoiceDate(threeMonthsAgo);
		invoice.setPaymentDate(null);
		assertEquals(InvoiceState.OVERDUE, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertTrue(invoice.canBeCancelled());
		assertTrue(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
		
		// CANCELLED from OVERDUE
		invoice.setCancelledDate(LocalDate.now());
		assertEquals(InvoiceState.CANCELLED, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		assertFalse(invoice.canBeSent());
	}
	
	/**
	 * Test method for {@link Invoice#equals(Object)} and {@link Invoice#hashCode()}.
	 */
	@Test
	public void testEqualsAndHashCode() {
		Invoice one = new Invoice();
		one.setNumber("One");
		
		Invoice two = new Invoice();
		two.setNumber("Two");
		
		assertFalse(one.equals(two));
		assertFalse(one.hashCode() == two.hashCode());
		
		two.setNumber(one.getNumber());
		assertTrue(one.equals(two));
		assertTrue(one.hashCode() == two.hashCode());
	}
	
	/**
	 * Test method for {@link Invoice#getDueDate()}.
	 */
	@Test
	public void testDueDateUpdating() {
		Invoice invoice = new Invoice();
		invoice.setInvoiceDate(LocalDate.now());
		assertNull(invoice.getDueDate());
		invoice.setPaymentTerms(PaymentTerms.getDefault());
		assertEquals(LocalDate.now().plusDays(PaymentTerms.getDefault().getFullPaymentTargetInDays()), invoice.getDueDate());
	}
}
