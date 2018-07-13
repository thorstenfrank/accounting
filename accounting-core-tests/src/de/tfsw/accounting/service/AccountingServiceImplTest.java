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

import static de.tfsw.accounting.BaseTestFixture.getTestClient;
import static de.tfsw.accounting.BaseTestFixture.getTestContext;
import static de.tfsw.accounting.BaseTestFixture.getTestUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingInitService;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.BaseTestFixture;
import de.tfsw.accounting.EventIds;
import de.tfsw.accounting.model.AbstractBaseEntity;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoiceState;
import de.tfsw.accounting.model.PaymentTerms;
import de.tfsw.accounting.model.PaymentType;
import de.tfsw.accounting.model.User;

/**
 * Tests for {@link AccountingServiceImpl}. This test includes only mocked behavior of the DB4o object container, and
 * as such contains mostly exception handling and business rule testing. The actual persistence part of the service is
 * tested in {@link AccountingServiceImplPersistenceTest}.
 * 
 * @author thorsten
 *
 */
public class AccountingServiceImplTest {
	
	private AccountingServiceImpl service;
	
	private Persistence daoMock;
	private List<Event> receivedEvents;
	private boolean initComplete;
	
	@Before
	public void setUp() {
		initComplete = false;
		service = new AccountingServiceImpl();
		daoMock = mock(Persistence.class);
		
		receivedEvents = null;
		
		service.bindEventAdmin(new EventAdmin() {
			@Override
			public void postEvent(Event event) {
				if (AccountingInitService.EVENT_TOPIC_SERVICE_INIT.equals(event.getTopic())) {
					assertNull("Should not have received events prior to this", receivedEvents);
					receivedEvents = new ArrayList<>();
				} else if (event instanceof ContextInitialisedEventImpl) {
					assertNotNull(receivedEvents);
					assertTrue("Context Init should be the ", receivedEvents.isEmpty());
					initComplete = true;
				} else {
					assertNotNull(receivedEvents);
					receivedEvents.add(event);
				}
			}
			@Override
			public void sendEvent(Event event) {
				throw new UnsupportedOperationException("Synchronous delivery not supported!");	
			}
		});
		service.setPersistence(daoMock);
		
		// this mock behavior is for the model meta info during init()
		when(daoMock.runFindQuery(Expense.class)).thenReturn(Collections.emptySet());
		when(daoMock.runFindQuery(Invoice.class)).thenReturn(Collections.emptySet());
		service.init(BaseTestFixture.getTestContext());
	}
	
	@After
	public void tearDown() {
		assertTrue("Init process never finished completely", initComplete);
		verify(daoMock).init(BaseTestFixture.TEST_DB_FILE);
		verify(daoMock).runFindQuery(Expense.class);
		verify(daoMock).runFindQuery(Invoice.class);
		Mockito.verifyNoMoreInteractions(daoMock);
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#init(de.tfsw.accounting.AccountingContext)}.
	 */
	@Test(expected = AccountingException.class)
	public void testInitNull() {
		service.init(null);
	}
	
	@Test
	public void testContextAlreadyInitialised() {
		// the service was properly initialised in setUp()
		// subsequent calls should have absolutely no result
		service.init(getTestContext());		
		assertTrue(receivedEvents.isEmpty());
		
	}
	
	/**
	 * Test method for {@link AccountingService#saveCurrentUser(User)}.
	 */
	@Test
	public void testSaveUser() {
		doTestEntitySave(getTestUser(), u -> service.saveCurrentUser(u));
	}
	
	@Test
	public void testSaveClient() {
		doTestEntitySave(getTestClient(), c -> service.saveClient(c));
	}
	
	@Test
	public void testDeleteClient() {
		final Client client = getTestClient();
		service.deleteClient(client);
		verify(daoMock).deleteEntity(client);
		verifyModelChange(client);
	}
	
	@Test
	public void testSaveInvoice() {
		// make sure nothing happens if trying to save [null]
		assertNull(service.saveInvoice(null));
		
        Invoice invoice = new Invoice();
        
		// test empty invoice number
		try {
	        service.saveInvoice(invoice);
	        fail("Invoice without number should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		invoice.setNumber("");
		try {
	        service.saveInvoice(invoice);
	        fail("Invoice without number should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		invoice.setNumber("JUnitInvoiceNo");
		
		// test empty user
		try {
	        service.saveInvoice(invoice);
	        fail("Invoice without user should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		// test unknown user
		invoice.setUser(new User());
		try {
	        service.saveInvoice(invoice);
	        fail("Invoice with unknown user should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		invoice.getUser().setName("SomeBogusName");
		try {
	        service.saveInvoice(invoice);
	        fail("Invoice with unknown user should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		// test checks regarding Invoice State
		invoice.setSentDate(LocalDate.now());
		try {
	        service.saveInvoice(invoice);
	        fail("Invoice with state [SENT] should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		invoice.setCancelledDate(LocalDate.now());
		try {
	        service.saveInvoice(invoice);
	        fail("Invoice with state [CANCELLED] should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		// reset values to make the Invoice saveable
		invoice.setCreationDate(LocalDate.now());
		invoice.setSentDate(null);
		invoice.setCancelledDate(null);
		invoice.setUser(getTestUser());
		
		doTestEntitySave(invoice, i -> service.saveInvoice(i));
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#sendInvoice(Invoice)}.
	 */
	@Test
	public void testSendInvoice() {
		final Invoice invoice = new Invoice();
		invoice.setNumber("JUnitTestInvoiceNo");
				
		// nothing should happen
		assertNull(service.sendInvoice(null, null));
		
		final LocalDate today = LocalDate.now();
		
		// try to send a cancelled invoice - should yield an exception
		invoice.setCancelledDate(today);
		
		try {
			service.sendInvoice(invoice, today);
			fail("Cancelled invoice cannot be sent again");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		// try so send an invoie without a due date
		invoice.setCancelledDate(null);
		try {
			service.sendInvoice(invoice, today);
			fail("Sending an invoice without a due date should not be possible");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		// try to send an invoice with a due date in the past
		invoice.setPaymentTerms(new PaymentTerms(PaymentType.TRADE_CREDIT, -50));
		try {
			service.sendInvoice(invoice, today);
			fail("Sending an invoice with a past due date should not be possible");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		// prepare the invoice for a proper save
		invoice.setInvoiceDate(today.minusDays(5));
		invoice.setCreationDate(today.minusDays(4));
		invoice.setPaymentTerms(PaymentTerms.getDefault());
		invoice.setUser(getTestUser());
		
		Invoice saved = doTestEntitySave(invoice, i -> service.sendInvoice(i, today));
		assertNotNull(saved.getCreationDate());
		assertEquals(today, saved.getSentDate());
	}
	
	/**
	 * Tests {@link AccountingServiceImpl#markAsPaid(Invoice, LocalDate)}.
	 */
	@Test
	public void testMarkInvoiceAsPaid() {
		final Invoice invoice = new Invoice();
		invoice.setUser(getTestUser());
		invoice.setClient(new Client());
		invoice.setCreationDate(LocalDate.now());
		invoice.setPaymentTerms(PaymentTerms.getDefault());
				
		assertNull(service.markAsPaid(null, null));
		
		service.markAsPaid(invoice, null);
		assertNull(invoice.getPaymentDate());
		
		final LocalDate paymentDate = LocalDate.now();
		
		try {
	        service.markAsPaid(invoice, paymentDate);
	        fail("Trying to mark an unsent invoice as paid should have resulted in exception");
        } catch (AccountingException e) {
	        // this is what we want
        }
		
		invoice.setSentDate(LocalDate.now());
		
		final Invoice paid = doTestEntitySave(invoice, i -> service.markAsPaid(i, paymentDate));
		assertEquals(invoice, paid);
		assertEquals(paymentDate, paid.getPaymentDate());
	}
	
	/**
	 * Test for {@link AccountingServiceImpl#cancelInvoice(Invoice)}.
	 */
	@Test
	public void testCancelInvoice() {
		assertNull(service.cancelInvoice(null));
		
		final Invoice invoice = new Invoice();
		invoice.setCreationDate(LocalDate.now());
		invoice.setSentDate(LocalDate.now());
		invoice.setPaymentTerms(PaymentTerms.getDefault());
		
		final Invoice cancelled = doTestEntitySave(invoice, i -> service.cancelInvoice(i));
		assertEquals(InvoiceState.CANCELLED, cancelled.getState());
	}
	
	/**
	 * Test method for {@link AccountingService#deleteInvoice(Invoice)}.
	 */
	@Test
	public void testDeleteInvoice() {
		// test data
		final Invoice invoice = new Invoice();
		invoice.setNumber("JUnitInvoiceNo");
				
		// try deleting [null]
		service.deleteInvoice(null);
		
		// try deleting an unsaved invoice
		service.deleteInvoice(invoice);
		
		// try deleting an invoice in an advanced state
		invoice.setCreationDate(LocalDate.now());
		invoice.setSentDate(LocalDate.now());
		invoice.setPaymentTerms(PaymentTerms.getDefault());
		
		try {
	        service.deleteInvoice(invoice);
	        fail("Trying to delete an invoice in state SENT, should have caught exception");
        } catch (AccountingException e) {
	        // this is what we want
        }
		
		// reset the invoice so it can be deleted properly (from a business point of view)
		invoice.setSentDate(null);
		
		// try normal delete
		service.deleteInvoice(invoice);
		verify(daoMock).deleteEntity(invoice);
		verifyModelChange(invoice);
	}
	
	private <E extends AbstractBaseEntity> E doTestEntitySave(E entity, Function<E, E> saveFunction) {
		final E saved = saveFunction.apply(entity);
		assertEquals(entity, saved);
		verify(daoMock).storeEntity(entity);
		verifyModelChange(entity);
		return saved;
	}

	private <E extends AbstractBaseEntity> void verifyModelChange(E entity) {
		assertTrue(receivedEvents.size() == 1);
		assertEquals(EventIds.MODEL_CHANGE_TOPIC_PREFIX + entity.getClass().getSimpleName(), receivedEvents.get(0).getTopic());
	}
}