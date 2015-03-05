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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tfsw.accounting.AccountingCore;
import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.BaseTestFixture;
import de.tfsw.accounting.Messages;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoicePosition;
import de.tfsw.accounting.model.InvoiceState;
import de.tfsw.accounting.model.PaymentTerms;
import de.tfsw.accounting.model.PaymentType;
import de.tfsw.accounting.model.User;
import de.tfsw.accounting.util.TimeFrame;

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
		
		StringBuilder sb = new StringBuilder("RE");
		Calendar cal = Calendar.getInstance();
		sb.append(cal.get(Calendar.YEAR));
		sb.append("-01");
		
		INVOICE1.setNumber(sb.toString());
		INVOICE1.setClient(getTestClient());
		service.saveInvoice(INVOICE1);
		
		service.saveClient(getTestClient());
		
		// for some reason, this causes strange behavior sometimes... just leave the container open for the
		// duration of the tests...
		//service.shutDown();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// this is only a safetly blanket
		// service should have been properly shut down when stopping the service
		AccountingServiceImpl service = (AccountingServiceImpl) AccountingCore.getAccountingService();
		service.shutDown();
		
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
		// just to make sure - this should never actually cause init, since that's
		// already been done during test class setup
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
	 * Simple positive test for saving and deleting an invoice.
	 */
	@Test
	public void testSaveAndDeleteInvoice() {
		
		int sizeBefore = serviceUnderTest.findInvoices().size();
		
		Invoice invoice = serviceUnderTest.saveInvoice(
				serviceUnderTest.createNewInvoice(serviceUnderTest.getNextInvoiceNumber(), getTestClient()));
		assertEquals(InvoiceState.CREATED, invoice.getState());
		
		Invoice saved = serviceUnderTest.getInvoice(invoice.getNumber());
		assertNotNull(saved);
		
		// delete the invoice again to make sure other tests can continue working 
		serviceUnderTest.deleteInvoice(saved);
		
		assertNull(serviceUnderTest.getInvoice(invoice.getNumber()));
		
		assertEquals(sizeBefore, serviceUnderTest.findInvoices().size());
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
	 * Test for {@link Client} handling.
	 *  
	 * @see AccountingService#saveClient(Client)
	 * @see AccountingService#getClients()
	 * @see AccountingService#deleteClient(Client)
	 */
	@Test
	public void testClient() {
		// make sure the setup was ok - the default test client should be in the DB
		Set<Client> clients = serviceUnderTest.getClients();
		assertNotNull(clients);
		assertEquals(1, clients.size());
		assertEquals(getTestClient(), clients.iterator().next());
		
		// save another client
		Client client = new Client();
		client.setName("The Client");
		client = serviceUnderTest.saveClient(client);
		clients = serviceUnderTest.getClients();
		assertEquals(2, clients.size());
		for (Client saved : clients) {
			if (!getTestClient().equals(saved)) {
				assertEquals(client, saved);
			}
		}
		
		// check Unique name constraint
		Client another = new Client();
		another.setName(client.getName());
		try {
			serviceUnderTest.saveClient(another);
			fail("Should have caught exception because of unique key violation");
			
			// make sure the client wasn't saved
			clients = serviceUnderTest.getClients();
			assertEquals(2, clients.size());
		} catch (AccountingException e) {
			// this is what we want
		}
		
		// check delete...
		serviceUnderTest.deleteClient(client);
		clients = serviceUnderTest.getClients();
		assertNotNull(clients);
		assertEquals(1, clients.size());
		assertEquals(getTestClient(), clients.iterator().next());
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
		assertEquals(getTestClient().getDefaultPaymentTerms(), invoice.getPaymentTerms());
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
		assertEquals(getTestClient().getDefaultPaymentTerms(), copy.getPaymentTerms());
		
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
	 * Test method for {@link AccountingService#getNextInvoiceNumber()}.
	 */
	@Test
	public void testInvoiceSequencer() {
		String  invoiceNumber = serviceUnderTest.getNextInvoiceNumber();
		int sequence = new Integer(invoiceNumber.substring(7));
		
		Invoice new1 = serviceUnderTest.createNewInvoice(invoiceNumber, getTestClient());
		serviceUnderTest.saveInvoice(new1);
		serviceUnderTest.deleteInvoice(new1);
		
		
		invoiceNumber = serviceUnderTest.getNextInvoiceNumber();
		sequence = sequence + 1; 
		assertEquals(new Integer(sequence), new Integer(invoiceNumber.substring(7)));
		
		sequence = sequence + 5;
		StringBuilder newInvoiceNumber = new StringBuilder();
		newInvoiceNumber.append(invoiceNumber.substring(0, 7));
		newInvoiceNumber.append(sequence);
		
		Invoice new2 = serviceUnderTest.createNewInvoice(newInvoiceNumber.toString(), getTestClient());
		serviceUnderTest.saveInvoice(new2);
		
		invoiceNumber = serviceUnderTest.getNextInvoiceNumber();
		sequence++;
		
		serviceUnderTest.deleteInvoice(new2);
		
		assertEquals(new Integer(sequence), new Integer(invoiceNumber.substring(7)));
	}
	
	/**
	 * Tests for expenses persistence.
	 * @see AccountingService#findExpenses(de.tfsw.accounting.util.TimeFrame)
	 * @see AccountingService#saveExpense(de.tfsw.accounting.model.Expense)
	 * @see AccountingService#deleteExpense(de.tfsw.accounting.model.Expense) 
	 */
	@Test
	public void testExpenses() {
		ExpenseCollection ec = serviceUnderTest.findExpenses(null);
		
		assertNull(ec.getTimeFrame());
		assertNotNull(ec.getExpenses());
		assertTrue("Expenses should be empty, but contains: " + ec.getExpenses().size(), ec.getExpenses().isEmpty());
		
		Expense e1 = new Expense();
		e1.setDescription("Expense 1");
		e1.setExpenseType(ExpenseType.OPEX);
		e1.setNetAmount(new BigDecimal("125"));
		e1.setPaymentDate(LocalDate.of(2012, 1, 1));
		
		Expense e2 = new Expense();
		e2.setDescription("Expense 2");
		e2.setExpenseType(ExpenseType.OPEX);
		e2.setNetAmount(new BigDecimal("200"));
		e2.setPaymentDate(LocalDate.of(2012, 1, 31));
		
		List<Expense> expenses = new ArrayList<Expense>();
		expenses.add(e1);
		expenses.add(e2);
		
		serviceUnderTest.saveExpenses(expenses);
		
		ec = serviceUnderTest.findExpenses(null);
		assertEquals(2, ec.getExpenses().size());
		
		Expense e3 = new Expense();
		e3.setDescription("Expense 3");
		e3.setExpenseType(ExpenseType.CAPEX);
		e3.setNetAmount(new BigDecimal("300"));
		e3.setPaymentDate(LocalDate.of(2012, 2, 28));
		serviceUnderTest.saveExpense(e3);

		ec = serviceUnderTest.findExpenses(null);
		assertEquals(3, ec.getExpenses().size());
		
		ec = serviceUnderTest.findExpenses(new TimeFrame(LocalDate.of(2012, 1, 1), LocalDate.of(2012, 1, 30)));
		assertEquals(1, ec.getExpenses().size());
		
		Expense fromService = ec.getExpenses().iterator().next();
		assertEquals(e1.getDescription(), fromService.getDescription());
		assertEquals(e1.getExpenseType(), fromService.getExpenseType());
		assertEquals(e1.getPaymentDate(), fromService.getPaymentDate());
		assertAreEqual(e1.getNetAmount(), fromService.getNetAmount());
		
		serviceUnderTest.deleteExpense(fromService);
		
		ec = serviceUnderTest.findExpenses(null);
		assertEquals(2, ec.getExpenses().size());
		
		serviceUnderTest.deleteExpense(e2);
		serviceUnderTest.deleteExpense(e3);
		
		ec = serviceUnderTest.findExpenses(null);
		assertEquals(0, ec.getExpenses().size());
	}
	
	/**
	 * Test for {@link AccountingService#findExpenseCategories()}.
	 */
	@Test
	public void testFindExpenseCategories() {
		final String cat1 = "Category1";
		final String cat2 = "Category2";
		
		Expense expense = new Expense();
		expense.setDescription("Desc1");
		expense.setPaymentDate(LocalDate.now());
		expense.setCategory(cat2);
		serviceUnderTest.saveExpense(expense);
		
		expense = new Expense();
		expense.setDescription("Desc2");
		expense.setCategory(cat2);
		expense.setPaymentDate(LocalDate.now());
		serviceUnderTest.saveExpense(expense);
		
		expense = new Expense();
		expense.setDescription("Desc3");
		expense.setCategory(cat1);
		expense.setPaymentDate(LocalDate.now());
		serviceUnderTest.saveExpense(expense);

		expense = new Expense();
		expense.setDescription("Desc4");
		expense.setCategory(null);
		expense.setPaymentDate(LocalDate.now());
		serviceUnderTest.saveExpense(expense);
		
		List<String> categories = 
				new ArrayList<String>(serviceUnderTest.getModelMetaInformation().getExpenseCategories());
		assertNotNull(categories);
		assertEquals(2, categories.size());
		// check if sorting was done properly
		assertEquals(cat1, categories.get(0));
		assertEquals(cat2, categories.get(1));
		
		for (Expense saved : serviceUnderTest.findExpenses(null).getExpenses()) {
			serviceUnderTest.deleteExpense(saved);
		}
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
