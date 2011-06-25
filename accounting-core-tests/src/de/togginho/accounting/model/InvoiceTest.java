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
package de.togginho.accounting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
		
		invoice.setUser(new User());
		invoice.setClient(new Client());
		assertFalse(invoice.canBeExported());
		
		invoice.setInvoicePositions(new ArrayList<InvoicePosition>());
		assertFalse(invoice.canBeExported());
		
		InvoicePosition ip = new InvoicePosition();
		invoice.getInvoicePositions().add(ip);
		assertFalse(invoice.canBeExported());
		
		invoice.getClient().setAddress(new Address());
		assertTrue(invoice.canBeExported());
		
		assertEquals(InvoiceState.UNSAVED, invoice.getState());
		assertTrue(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		
		invoice.setCreationDate(new Date());
		assertEquals(InvoiceState.CREATED, invoice.getState());
		assertTrue(invoice.canBeEdited());
		assertTrue(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
		invoice.setSentDate(new Date());
		invoice.setDueDate(cal.getTime());
		
		assertEquals(InvoiceState.SENT, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertTrue(invoice.canBeCancelled());
		assertTrue(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		
		invoice.setCancelledDate(new Date());
		assertEquals(InvoiceState.CANCELLED, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		
		invoice.setCancelledDate(null);
		invoice.setPaymentDate(new Date());
		assertEquals(InvoiceState.PAID, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertTrue(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		
		// this should never happen since a cancelled invoice cannot be marked as paid, but stil...
		invoice.setCancelledDate(new Date());
		assertEquals(InvoiceState.CANCELLED, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		
		invoice.setCancelledDate(null);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
		invoice.setSentDate(cal.getTime());
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 2);
		invoice.setDueDate(cal.getTime());
		invoice.setPaymentDate(null);
		assertEquals(InvoiceState.OVERDUE, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertTrue(invoice.canBeCancelled());
		assertTrue(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
		
		invoice.setCancelledDate(new Date());
		assertEquals(InvoiceState.CANCELLED, invoice.getState());
		assertFalse(invoice.canBeEdited());
		assertFalse(invoice.canBeDeleted());
		assertFalse(invoice.canBeCancelled());
		assertFalse(invoice.canBePaid());
		assertTrue(invoice.canBeExported());
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
}
