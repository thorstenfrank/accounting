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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.config.ObjectField;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.internal.encoding.UTF8StringEncoding;
import com.db4o.osgi.Db4oService;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.User;

/**
 * @author thorsten
 *
 */
public class AccountingServiceImplTest extends BaseTestFixture {

	private ObjectContainer ocMock;
	
	private Object[] initMocks;
	
	private AccountingServiceImpl serviceUnderTest;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File daFile = new File(TEST_DB_FILE);
		if (daFile.exists()) {
			System.out.println("deleting existing test db file");
			daFile.delete();
		} else {
			System.out.println("Using db file: " + daFile.getAbsolutePath());
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File daFile = new File(TEST_DB_FILE);
		if (!daFile.exists()) {
			System.out.println("Fuckit");
		} else {
			daFile.delete();
			System.out.println("Deleting");
		}
	}

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
		
		ObjectClass userClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(User.class)).andReturn(userClassMock);
		userClassMock.cascadeOnUpdate(true);
		userClassMock.cascadeOnDelete(true);
		ObjectField userNameFieldMock = createMock(ObjectField.class);
		expect(userClassMock.objectField(User.FIELD_NAME)).andReturn(userNameFieldMock);
		userNameFieldMock.indexed(true);
		configurationMock.add(anyObject(UniqueFieldValueConstraint.class)); // TODO check arguments too
		
		ObjectClass clientClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(Client.class)).andReturn(clientClassMock);
		clientClassMock.cascadeOnUpdate(true);
		clientClassMock.cascadeOnDelete(true);
		ObjectField clientNameMock = createMock(ObjectField.class);
		expect(clientClassMock.objectField(Client.FIELD_NAME)).andReturn(clientNameMock);
		clientNameMock.indexed(true);
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
				
		initMocks = new Object[]{db4oServiceMock, configurationMock, userClassMock, userNameFieldMock, clientClassMock, 
				clientNameMock, invoiceClassMock, invoicePositionsMock, invoiceNumberMock};
		
		expect(db4oServiceMock.openFile(configurationMock, TEST_DB_FILE)).andReturn(ocMock);
		
		replay(initMocks);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		verify(initMocks);
		verify(ocMock);
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#init(de.togginho.accounting.AccountingContext)}.
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
		serviceUnderTest.init(getTestContext());
		
		setupMockForEntitySave(getTestUser());
		
		replay(ocMock);
		
		// null save - nothing should happen
		assertNull(serviceUnderTest.saveCurrentUser(null));
		
		User user = getTestUser();
		assertEquals(user, serviceUnderTest.saveCurrentUser(user));
		
		// call save twice, to test both exceptions
		saveUserWithException();
		saveUserWithException();
	}
		
	/**
	 * Test method for {@link AccountingService#saveInvoice(Invoice)}.
	 */
	@Test
	public void testSaveInvoice() {
		Invoice invoice = new Invoice();
		
		serviceUnderTest.init(getTestContext());
		
		setupMockForEntitySave(invoice);
		
		replay(ocMock);
		
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
		invoice.setSentDate(new Date());
		try {
	        serviceUnderTest.saveInvoice(invoice);
	        fail("Invoice with state [SENT] should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }
		
		invoice.setCancelledDate(new Date());
		try {
	        serviceUnderTest.saveInvoice(invoice);
	        fail("Invoice with state [CANCELLED] should not have been saved properly");
        } catch (AccountingException e) {
	        // this is what we want	
        }

		// reset values to make the Invoice saveable
		invoice.setSentDate(null);
		invoice.setCancelledDate(null);
		invoice.setUser(getTestUser());
		
		// try a normal save
		Invoice saved = serviceUnderTest.saveInvoice(invoice);
		assertEquals(InvoiceState.CREATED, saved.getState());
		
		// try saves with exceptions
		saveInvoiceWithException(invoice);
		saveInvoiceWithException(invoice);
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
		serviceUnderTest.init(getTestContext());
		replay(ocMock);
		
		// try deleting [null]
		serviceUnderTest.deleteInvoice(null);
		
		// try deleting an unsaved invoice
		serviceUnderTest.deleteInvoice(invoice);
		
		// try deleting an invoice in an advanced state
		invoice.setCreationDate(new Date());
		invoice.setSentDate(new Date());
		invoice.setDueDate(new Date());
		
		try {
	        serviceUnderTest.deleteInvoice(invoice);
	        fail("Trying to delete an invoice in state SENT, should have caught exception");
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
	 * 
	 * @param entity
	 */
	private void setupMockForEntitySave(Object entity) {
		ocMock.store(entity);
		ocMock.commit();

		// throw DB closed exception
		ocMock.store(entity);
		expectLastCall().andThrow(new DatabaseClosedException());
		ocMock.rollback();
		
		ocMock.store(entity);
		expectLastCall().andThrow(new DatabaseReadOnlyException());
		ocMock.rollback();
	}
}
