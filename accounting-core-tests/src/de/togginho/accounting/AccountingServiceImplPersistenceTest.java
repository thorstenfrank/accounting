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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.PaymentType;
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
	
	/**
	 * Test method for {@link AccountingService#createNewInvoice(String, Client)}.
	 */
	@Test
	public void testCreateNewInvoice() {
		serviceUnderTest.init(getTestContext());
		
		Invoice invoice = null;
		
		// null invoice no
		try {
	        invoice = serviceUnderTest.createNewInvoice(null, null);
	        fail("Trying to create an invoice without an invoice number");
        } catch (AccountingException e) {
        	assertEquals(Messages.AccountingService_errorMissingInvoiceNumber, e.getMessage());
        }
		
		// empty invoice no
		try {
	        invoice = serviceUnderTest.createNewInvoice("", null);
	        fail("Trying to create an invoice without an invoice number");
        } catch (AccountingException e) {
        	assertEquals(Messages.AccountingService_errorMissingInvoiceNumber, e.getMessage());
        }
		
		// no client
		try {
	        invoice = serviceUnderTest.createNewInvoice("TEST_INVOICE_NO", null);
	        fail("Trying to create a new invoice without a client.");
        } catch (AccountingException e) {
        	assertEquals(Messages.AccountingService_errorMissingClient, e.getMessage());
        }
		
		// Existing invoice number
		try {
	        invoice = serviceUnderTest.createNewInvoice(INVOICE1.getNumber(), getTestClient());
	        fail("Trying to create a new invoice with an existing invoice number!");
        } catch (AccountingException e) {
        	assertEquals(Messages.AccountingService_errorInvoiceNumberExists, e.getMessage());
        }
		
		// SUCCESSFUL RUN:
		invoice = serviceUnderTest.createNewInvoice("TEST_INVOICE_NO", getTestClient());
		
		assertNotNull(invoice);
		assertEquals(getTestUser(), invoice.getUser());
		assertNotNull(invoice.getInvoiceDate());
		assertNull(invoice.getCreationDate());
		assertEquals(PaymentTerms.getDefault(), invoice.getPaymentTerms());
		

	}
	
	/**
	 * Tests {@link AccountingServiceImpl#copyInvoice(Invoice, String)}.
	 */
	@Test
	public void testCopyInvoice() {		
		final String newInvoiceNo = "TheNewInvoiceNumber";
		
		Invoice original = new Invoice();
		original.setClient(getTestClient());
		
		// Test copying without actual content
		Invoice copy = serviceUnderTest.copyInvoice(original, newInvoiceNo);
		
		assertCopiedInvoice(copy, newInvoiceNo);
		assertNull(copy.getInvoicePositions());
		assertNotNull(copy.getInvoiceDate());
		assertEquals(getTestUser(), copy.getUser());
		assertEquals(getTestClient(), copy.getClient());
		assertEquals(PaymentTerms.getDefault(), copy.getPaymentTerms());
		
		// test simple copy
		original.setUser(getTestUser());
		final PaymentTerms terms = new PaymentTerms(PaymentType.TRADE_CREDIT, 35);
		original.setPaymentTerms(terms);
		
		copy = serviceUnderTest.copyInvoice(original, newInvoiceNo);
		assertCopiedInvoice(copy, newInvoiceNo);
		assertEquals(getTestUser(), copy.getUser());
		assertEquals(getTestClient(), copy.getClient());
		assertNull(copy.getInvoicePositions());
		assertEquals(terms, copy.getPaymentTerms());
		
		// full copy including invoice positions
		InvoicePosition ip = new InvoicePosition();
		ip.setDescription("IP_Description");
		ip.setPricePerUnit(new BigDecimal("25"));
		ip.setQuantity(new BigDecimal("10"));
		ip.setUnit("IP_Unit");
		
		List<InvoicePosition> positions = new ArrayList<InvoicePosition>();
		positions.add(ip);
		original.setInvoicePositions(positions);
		
		copy = serviceUnderTest.copyInvoice(original, newInvoiceNo);
		assertCopiedInvoice(copy, newInvoiceNo);
		assertEquals(getTestUser(), copy.getUser());
		assertEquals(getTestClient(), copy.getClient());
		assertNotNull(copy.getInvoicePositions());
		assertEquals(1, copy.getInvoicePositions().size());
		
		InvoicePosition copiedIP = copy.getInvoicePositions().get(0);
		assertEquals(ip.getDescription(), copiedIP.getDescription());
		assertEquals(ip.getPricePerUnit(), copiedIP.getPricePerUnit());
		assertEquals(ip.getQuantity(), copiedIP.getQuantity());
		assertEquals(ip.getTaxRate(), copiedIP.getTaxRate());
		assertEquals(ip.getUnit(), copiedIP.getUnit());
	}
	
	/**
	 * 
	 * @param copy
	 */
	private void assertCopiedInvoice(Invoice copy, String invoiceNumber) {
		assertNotNull("Copied invoice should not be null", copy);
		assertEquals("Invoice number doesn't match", invoiceNumber, copy.getNumber());
		assertNull("Cancelled date should be null", copy.getCancelledDate());
		assertNull("Creation date should be null", copy.getCreationDate());		
		assertNull("Payment date should be null", copy.getPaymentDate());
		assertNull("Sent date should be null", copy.getSentDate());
		assertNotNull("Invoice date should not be null", copy.getInvoiceDate());
		assertNotNull("Due date should not be null", copy.getDueDate());
		assertEquals("Copied invoice state should be UNSAVED", InvoiceState.UNSAVED, copy.getState());
	}
}
