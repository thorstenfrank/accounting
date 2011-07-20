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

import java.io.File;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.User;

/**
 * Tests for {@link AccountingServiceImpl} that use a temporary DB file.
 * 
 * @author thorsten
 *
 */
public class AccountingServiceImplPersistenceTest extends BaseTestFixture {

	private static final Logger LOG = Logger.getLogger(AccountingServiceImplPersistenceTest.class);
	
	private static final Invoice INVOICE1 = new Invoice();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File daFile = new File(TEST_DB_FILE);
		if (daFile.exists()) {
			LOG.debug("DB file already exists, will delete it first");
			daFile.delete();
		}
		
		AccountingServiceImpl service = (AccountingServiceImpl) AccountingCore.getAccountingService();
		service.init(getTestContext());
		
		service.saveCurrentUser(getTestUser());
	
		INVOICE1.setUser(getTestUser());
		INVOICE1.setNumber("JUnitInvoice1");
		service.saveInvoice(INVOICE1);
		
		service.shutDown();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File daFile = new File(TEST_DB_FILE);
		if (daFile.exists()) {
			LOG.debug("Deleting test DB file");
			daFile.delete();
		} else {
			LOG.warn("Something went wrong during setup, there is no DB file!");
		}
	}
	
	/**
	 * Service being tested.
	 */
	private AccountingService serviceUnderTest;
	
	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		serviceUnderTest = AccountingCore.getAccountingService();
		serviceUnderTest.init(getTestContext());
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#getCurrentUser()}.
	 */
	@Test
	public void testGetUser() {
		try {
	        assertNotNull(serviceUnderTest.getCurrentUser());
        } catch (Exception e) {
        	LOG.error("Error getting current user", e);
	        fail("Error while getting current user");
        }
	}

	/**
	 * Test method for {@link AccountingServiceImpl#getInvoice(String)}.
	 */
	@Test
	public void testGetInvoice() {
		Invoice invoice = null;
        try {
	        invoice = serviceUnderTest.getInvoice(INVOICE1.getNumber());
        } catch (Exception e) {
        	LOG.error("Error getting invoice from service", e);
        	fail("Error getting invoice from service");
        }
		assertNotNull(invoice);
		assertEquals(invoice, INVOICE1);
		assertEquals(InvoiceState.CREATED, invoice.getState());
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#findInvoices(User, InvoiceState...)}.
	 */
	@Test
	public void testFindInvoices() {
		Set<Invoice> invoices = null;
        try {
	        invoices = serviceUnderTest.findInvoices();
        } catch (Exception e) {
        	LOG.error("Error getting invoices from service", e);
        	fail("Error getting invoices from service");
        }
		
		assertNotNull(invoices);
		assertEquals(1, invoices.size());
	}
	
	/**
	 * 
	 */
	@Test
	public void testSavingClient() {
		Client client = new Client();
		client.setName("The Client");
		
		serviceUnderTest.saveClient(client);
		
		Client another = new Client();
		another.setName(client.getName());
		
		try {
			serviceUnderTest.saveClient(another);
			fail("Should have caught exception because of unique key violation");
		} catch (AccountingException e) {
			// this is what we want
		}
	}
}
