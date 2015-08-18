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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db4o.ObjectContainer;
import com.db4o.config.BigMathSupport;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.config.ObjectField;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.constraints.UniqueFieldValueConstraintViolationException;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oIOException;
import com.db4o.internal.encoding.UTF8StringEncoding;
import com.db4o.osgi.Db4oService;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.BaseTestFixture;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.CurriculumVitae;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseTemplate;
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
public class AccountingServiceImplTest extends BaseTestFixture {

	/**
	 * 
	 */
	private static final DummyObjectSet<Expense> DUMMY_EXPENSES = new DummyObjectSet<Expense>();
	
	/**
	 * 
	 */
	private static final DummyObjectSet<Invoice> DUMMY_INVOICES = new DummyObjectSet<Invoice>();
	
	/** DB4o mock. */
	private ObjectContainer ocMock;
	
	/** Mocks for config objects used during init. */
	private Object[] initMocks;
	
	/** The service instance being tested. */
	private AccountingServiceImpl serviceUnderTest;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Db4oService db4oServiceMock = createMock(Db4oService.class);
		ocMock = createMock(ObjectContainer.class);
		serviceUnderTest = new AccountingServiceImpl(db4oServiceMock);
		
		// init mock behavior
		Configuration configurationMock = createMock(Configuration.class);
		expect(db4oServiceMock.newConfiguration()).andReturn(configurationMock);
		configurationMock.stringEncoding(anyObject(UTF8StringEncoding.class));
		configurationMock.add(anyObject(BigMathSupport.class));
		configurationMock.allowVersionUpdates(true);
		
		ObjectClass userClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(User.class)).andReturn(userClassMock);
		userClassMock.cascadeOnUpdate(true);
		userClassMock.cascadeOnDelete(true);
		ObjectField userNameFieldMock = createMock(ObjectField.class);
		expect(userClassMock.objectField(User.FIELD_NAME)).andReturn(userNameFieldMock);
		userNameFieldMock.indexed(true);
		configurationMock.add(anyObject(UniqueFieldValueConstraint.class)); // TODO check arguments too
		configurationMock.add(anyObject(UniqueFieldValueConstraint.class)); // TODO check arguments too
		
		ObjectClass clientClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(Client.class)).andReturn(clientClassMock);
		clientClassMock.cascadeOnUpdate(true);
		clientClassMock.cascadeOnDelete(true);
		ObjectField clientNameMock = createMock(ObjectField.class);
		expect(clientClassMock.objectField(Client.FIELD_NAME)).andReturn(clientNameMock);
		clientNameMock.indexed(true);
		ObjectField clientNumberMock = createMock(ObjectField.class);
		expect(clientClassMock.objectField(Client.FIELD_CLIENT_NUMBER)).andReturn(clientNumberMock);
		clientNumberMock.indexed(true);		
		configurationMock.add(anyObject(UniqueFieldValueConstraint.class)); // TODO check arguments too
		
		ObjectClass invoiceClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(Invoice.class)).andReturn(invoiceClassMock);
		ObjectField invoicePositionsMock = createMock(ObjectField.class);
		expect(invoiceClassMock.objectField(Invoice.FIELD_INVOICE_POSITIONS)).andReturn(invoicePositionsMock);
		invoicePositionsMock.cascadeOnUpdate(true);
		invoicePositionsMock.cascadeOnDelete(true);
		ObjectField invoiceNumberMock = createMock(ObjectField.class);
		expect(invoiceClassMock.objectField(Invoice.FIELD_NUMBER)).andReturn(invoiceNumberMock);
		invoiceNumberMock.indexed(true);
		configurationMock.add(anyObject(UniqueFieldValueConstraint.class)); // TODO check arguments too
		
		ObjectClass cvClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(CurriculumVitae.class)).andReturn(cvClassMock);
		cvClassMock.cascadeOnUpdate(true);
		cvClassMock.cascadeOnDelete(true);
		
		ObjectClass expenseTemplateClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(ExpenseTemplate.class)).andReturn(expenseTemplateClassMock);
		expenseTemplateClassMock.cascadeOnUpdate(true);
		expenseTemplateClassMock.cascadeOnDelete(true);
		
		initMocks = new Object[]{db4oServiceMock, configurationMock, userClassMock, userNameFieldMock, clientClassMock, 
				clientNameMock, invoiceClassMock, invoicePositionsMock, invoiceNumberMock, cvClassMock, expenseTemplateClassMock};
		
		expect(db4oServiceMock.openFile(configurationMock, TEST_DB_FILE)).andReturn(ocMock);
		
		expect(ocMock.query(Expense.class)).andReturn(DUMMY_EXPENSES);
		expect(ocMock.query(Invoice.class)).andReturn(DUMMY_INVOICES);
		
		replay(initMocks);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		verify(initMocks);
		verify(ocMock);
		
		assertNotNull(serviceUnderTest.getModelMetaInformation());
		assertNotNull(serviceUnderTest.getModelMetaInformation().getOldestKnownExpenseDate());
		assertNotNull(serviceUnderTest.getModelMetaInformation().getOldestKnownInvoiceDate());
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#init(de.tfsw.accounting.AccountingContext)}.
	 */
	@Test
	public void testInit() {
				
		replay(ocMock);
		
		try {
			serviceUnderTest.init(null);
			fail("Initialising the service without a context should have yielded an exception");
		} catch (AccountingException e) {
			// this is what we want
		} catch (Exception e) {
			fail("Wrong type of exception during init");
		}
		
		try {
			serviceUnderTest.init(getTestContext());
		} catch (Exception e) {
			fail("Service should have initialised properly");
		}
		
		// subsequent calls should have absolutely no result
		try {
			serviceUnderTest.init(getTestContext());
		} catch (Exception e) {
			fail("Service should not have done anything!");
		}
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#shutDown()}.
	 */
	@Test
	public void testShutDown() {
		expect(ocMock.close()).andReturn(true);
		replay(ocMock);
		
		try {
			serviceUnderTest.init(getTestContext());
			serviceUnderTest.shutDown();
		} catch (AccountingException e) {
			fail("Unexpected exception during shutdown");
		}
		
		// subsequent calls to shutDown() shouldn't do anything
		serviceUnderTest.shutDown();
	}
	
	/**
	 * Test method for {@link AccountingService#saveCurrentUser(User)}.
	 */
	@Test
	public void testSaveUser() {
		ocMock.store(getTestUser());
		ocMock.commit();
		
		setupMockForEntitySaveWithExceptions(getTestUser());
		
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		// null save - nothing should happen
		assertNull(serviceUnderTest.saveCurrentUser(null));
		
		User user = getTestUser();
		assertEquals(user, serviceUnderTest.saveCurrentUser(user));
		
		// call save twice, to test both exceptions
		saveUserWithException();
		saveUserWithException();
	}
	
	/**
	 * 
	 */
	@Test
	public void testClientExceptionHandling() {
		ocMock.store(getTestClient());
		ocMock.commit();
		expectLastCall().andThrow(new UniqueFieldValueConstraintViolationException(Client.class.getName(), "name"));
		ocMock.rollback();
		
		setupMockForEntitySaveWithExceptions(getTestClient());
		
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		saveClientWithException();
		saveClientWithException();
		saveClientWithException();
	}
	
	/**
	 * Test method for {@link AccountingService#saveInvoice(Invoice)}.
	 */
	@Test
	public void testSaveInvoice() {
		Invoice invoice = new Invoice();
		
		setupMockForEntitySaveWithExceptions(invoice);
		
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		// make sure nothing happens if trying to save [null]
		assertNull(serviceUnderTest.saveInvoice(null));
		
		// test empty invoice number
		try {
	        serviceUnderTest.saveInvoice(invoice);
	        fail("Invoice without number should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		invoice.setNumber("");
		try {
	        serviceUnderTest.saveInvoice(invoice);
	        fail("Invoice without number should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		invoice.setNumber("JUnitInvoiceNo");
		
		// test empty user
		try {
	        serviceUnderTest.saveInvoice(invoice);
	        fail("Invoice without user should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		// test unknown user
		invoice.setUser(new User());
		try {
	        serviceUnderTest.saveInvoice(invoice);
	        fail("Invoice with unknown user should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		invoice.getUser().setName("SomeBogusName");
		try {
	        serviceUnderTest.saveInvoice(invoice);
	        fail("Invoice with unknown user should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		// test checks regarding Invoice State
		invoice.setSentDate(LocalDate.now());
		try {
	        serviceUnderTest.saveInvoice(invoice);
	        fail("Invoice with state [SENT] should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		invoice.setCancelledDate(LocalDate.now());
		try {
	        serviceUnderTest.saveInvoice(invoice);
	        fail("Invoice with state [CANCELLED] should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }

		// reset values to make the Invoice saveable
		invoice.setCreationDate(LocalDate.now());
		invoice.setSentDate(null);
		invoice.setCancelledDate(null);
		invoice.setUser(getTestUser());
				
		// try saves with exceptions
		saveInvoiceWithException(invoice);
		saveInvoiceWithException(invoice);
	} 

	/**
	 * Test method for {@link AccountingServiceImpl#sendInvoice(Invoice)}.
	 */
	@Test
	public void testSendInvoice() {
		Invoice invoice = new Invoice();
		invoice.setNumber("JUnitTestInvoiceNo");
		
		ocMock.store(invoice);
		ocMock.commit();
		
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		// nothing should happen
		assertNull(serviceUnderTest.sendInvoice(null, null));
		
		final LocalDate today = LocalDate.now();
		
		// try to send a cancelled invoice - should yield an exception
		invoice.setCancelledDate(today);
		
		try {
			serviceUnderTest.sendInvoice(invoice, today);
			fail("Cancelled invoice cannot be sent again");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		// try so send an invoie without a due date
		invoice.setCancelledDate(null);
		try {
			serviceUnderTest.sendInvoice(invoice, today);
			fail("Sending an invoice without a due date should not be possible");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		// try to send an invoice with a due date in the past
		invoice.setPaymentTerms(new PaymentTerms(PaymentType.TRADE_CREDIT, -50));
		try {
			serviceUnderTest.sendInvoice(invoice, today);
			fail("Sending an invoice with a past due date should not be possible");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		// prepare the invoice for a proper save
		invoice.setInvoiceDate(LocalDate.now());
		invoice.setCreationDate(LocalDate.now());
		invoice.setPaymentTerms(PaymentTerms.getDefault());
		invoice.setUser(getTestUser());
		
		Invoice saved = serviceUnderTest.sendInvoice(invoice, today);
		assertNotNull(saved.getCreationDate());
		assertNotNull(saved.getSentDate());
	}
	
	/**
	 * Tests {@link AccountingServiceImpl#markAsPaid(Invoice, Date)}.
	 */
	@Test
	public void testMarkInvoiceAsPaid() {
		Invoice invoice = new Invoice();
		invoice.setUser(getTestUser());
		invoice.setClient(new Client());
		invoice.setCreationDate(LocalDate.now());
		invoice.setPaymentTerms(PaymentTerms.getDefault());
		
		// mock behavior
		ocMock.store(invoice);
		expectLastCall().once();
		ocMock.commit();
		expectLastCall().once();
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		assertNull(serviceUnderTest.markAsPaid(null, null));
		
		serviceUnderTest.markAsPaid(invoice, null);
		assertNull(invoice.getPaymentDate());
		
		final LocalDate paymentDate = LocalDate.now();
		
		try {
	        serviceUnderTest.markAsPaid(invoice, paymentDate);
	        fail("Trying to mark an unsent invoice as paid should have resulted in exception");
        } catch (AccountingException e) {
	        // this is what we want
        }
		
		invoice.setSentDate(LocalDate.now());
		
		Invoice paid = serviceUnderTest.markAsPaid(invoice, paymentDate);
		assertEquals(invoice, paid);
		assertEquals(paymentDate, paid.getPaymentDate());
	}
	
	/**
	 * Test for {@link AccountingServiceImpl#cancelInvoice(Invoice)}.
	 */
	@Test
	public void testCancelInvoice() {
		assertNull(serviceUnderTest.cancelInvoice(null));
		
		Invoice invoice = new Invoice();
		invoice.setCreationDate(LocalDate.now());
		invoice.setSentDate(LocalDate.now());
		invoice.setPaymentTerms(PaymentTerms.getDefault());
		
		ocMock.store(invoice);
		expectLastCall().once();
		ocMock.commit();
		expectLastCall().once();
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		try {
	        Invoice cancelled = serviceUnderTest.cancelInvoice(invoice);
	        assertEquals(InvoiceState.CANCELLED, cancelled.getState());
        } catch (AccountingException e) {
        	fail(e.toString());
        }
	}
	
	/**
	 * Tests {@link AccountingServiceImpl#getInvoice(String)}.
	 */
    @Test
	public void testGetInvoiceExceptionHandling() {
		expect(ocMock.query(anyObject(GetInvoicePredicate.class))).andThrow(new Db4oIOException());
		expect(ocMock.query(anyObject(GetInvoicePredicate.class))).andThrow(new DatabaseClosedException());
		
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		final String testInvoiceNo = "JUnitTestInvoiceNo";
		
		try {
			serviceUnderTest.getInvoice(testInvoiceNo);
			fail("Mocked ObjectContainer threw exception, didn't expect to get through clean");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		try {
			serviceUnderTest.getInvoice(testInvoiceNo);
			fail("Mocked ObjectContainer threw exception, didn't expect to get through clean");
		} catch (AccountingException e) {
			// this is what we want
		}
	}
	
	/**
	 * Tests {@link AccountingServiceImpl#findInvoices(User, InvoiceState...)}.
	 */
	@Test
	public void testFindInvoicesExceptionHandling() {
		expect(ocMock.query(anyObject(FindInvoicesPredicate.class))).andThrow(new Db4oIOException());
		expect(ocMock.query(anyObject(FindInvoicesPredicate.class))).andThrow(new DatabaseClosedException());
		
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		try {
			serviceUnderTest.findInvoices(InvoiceState.CREATED, InvoiceState.SENT);
			fail("Mocked ObjectContainer threw exception, didn't expect to get through clean");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		try {
			serviceUnderTest.findInvoices(InvoiceState.CREATED, InvoiceState.SENT);
			fail("Mocked ObjectContainer threw exception, didn't expect to get through clean");
		} catch (AccountingException e) {
			// this is what we want
		}
	}
	
	/**
	 * Test method for {@link AccountingService#deleteInvoice(Invoice)}.
	 */
	@Test
	public void testDeleteInvoice() {
		// test data
		Invoice invoice = new Invoice();
		invoice.setNumber("JUnitInvoiceNo");
		
		// mocks
		ocMock.delete(invoice);
		expectLastCall().once();
		ocMock.commit();
		expectLastCall().once();
		
		// exception tests
		ocMock.delete(invoice);
		expectLastCall().andThrow(new Db4oIOException());
		expectLastCall().once();

		ocMock.delete(invoice);
		expectLastCall().andThrow(new DatabaseClosedException());
		expectLastCall().once();
		
		ocMock.delete(invoice);
		expectLastCall().andThrow(new DatabaseReadOnlyException());
		expectLastCall().once();
		
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		// try deleting [null]
		serviceUnderTest.deleteInvoice(null);
		
		// try deleting an unsaved invoice
		serviceUnderTest.deleteInvoice(invoice);
		
		// try deleting an invoice in an advanced state
		invoice.setCreationDate(LocalDate.now());
		invoice.setSentDate(LocalDate.now());
		invoice.setPaymentTerms(PaymentTerms.getDefault());
		
		try {
	        serviceUnderTest.deleteInvoice(invoice);
	        fail("Trying to delete an invoice in state SENT, should have caught exception");
        } catch (AccountingException e) {
	        // this is what we want
        }
		
		// reset the invoice so it can be deleted properly (from a business point of view)
		invoice.setSentDate(null);
		
		// try normal delete, check proper service call
		try {
			serviceUnderTest.deleteInvoice(invoice);
		} catch (AccountingException e) {
			fail("Caught exception but expected proper delete of invoice");
		}
		
		// now test exception handling
		// Step1 - DB4oIOException -> see mock behavior recording 
		try {
			serviceUnderTest.deleteInvoice(invoice);
			fail("Mock service threw exception, didn't expect to get through cleanly");
		} catch (AccountingException e) {
			// this is what we want
		}

		// Step2 - DatabaseClosedException -> see mock behavior recording 
		try {
			serviceUnderTest.deleteInvoice(invoice);
			fail("Mock service threw exception, didn't expect to get through cleanly");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		// Step3 - DatabaseReadOnlyException -> see mock behavior recording 
		try {
			serviceUnderTest.deleteInvoice(invoice);
			fail("Mock service threw exception, didn't expect to get through cleanly");
		} catch (AccountingException e) {
			// this is what we want
		}
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#getCurrentUser()} exception handling.
	 */
	@Test
	public void testGetCurrentUserExceptionHandling() {
		expect(ocMock.query(anyObject(FindCurrentUserPredicate.class))).andThrow(new Db4oIOException());
		expect(ocMock.query(anyObject(FindCurrentUserPredicate.class))).andThrow(new DatabaseClosedException());
		
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		try {
			serviceUnderTest.getCurrentUser();
			fail("Mocked ObjectContainer threw exception, didn't expect to get through clean");
		} catch (AccountingException e) {
			// this is what we want
		}
		
		try {
			serviceUnderTest.getCurrentUser();
			fail("Mocked ObjectContainer threw exception, didn't expect to get through clean");
		} catch (AccountingException e) {
			// this is what we want
		}
	}
	
	/**
	 * 
	 */
	private void saveUserWithException() {
		try {
			serviceUnderTest.saveCurrentUser(getTestUser());
			fail("Should have caught AccountingException");
		} catch (AccountingException e) {
			// this is what we want
		} catch (Exception e) {
			fail("Unexpected exception: " + e.toString());
		}
	}
	
	/**
	 * 
	 */
	private void saveClientWithException() {
		try {
			serviceUnderTest.saveClient(getTestClient());
		} catch (AccountingException e) {
			// this is what we want
		} catch (Exception e) {
			fail("Unexpected exception: " + e.toString());
		}
	}
	
	/**
	 * 
	 */
	private void saveInvoiceWithException(Invoice invoice) {
		try {
			serviceUnderTest.saveInvoice(invoice);
			fail("Should have caught AccountingException");
		} catch (AccountingException e) {
			// this is what we want
		} catch (Exception e) {
			fail("Unexpected exception: " + e.toString());
		}
	}
	
	/**
	 * Sets up mock calls to {@link ObjectContainer#store(Object)} twice, once throwing {@link DatabaseClosedException} 
	 * and once {@link DatabaseReadOnlyException}. Both of these exceptions should not cause a rollback, as nothing was
	 * written to the DB anyway.
	 * 
	 * @param entity the entity that {@link ObjectContainer#store(Object)} is expected to be called with
	 */
	private void setupMockForEntitySaveWithExceptions(Object entity) {
		// throw DB closed exception
		ocMock.store(entity);
		expectLastCall().andThrow(new DatabaseClosedException());
		
		// throw DB read-only exception
		ocMock.store(entity);
		expectLastCall().andThrow(new DatabaseReadOnlyException());
	}
}
