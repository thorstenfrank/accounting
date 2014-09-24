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
package de.tfsw.accounting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.BankAccount;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoicePosition;
import de.tfsw.accounting.model.PaymentTerms;
import de.tfsw.accounting.model.PaymentType;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.model.Revenue;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.User;

/**
 * @author thorsten
 *
 */
public abstract class BaseTestFixture {

	protected static final String TEST_USER_NAME = "JUnitTestUser";
	
	protected static final String TEST_DB_FILE = "JUnitTestDbFile";
	
	private static User testUser;
	
	private static Client testClient;
	
	private static AccountingContext testContext;

	/**
	 * @return the testContext
	 */
	@SuppressWarnings("serial")
	protected static AccountingContext getTestContext() {
		if (testContext == null) {
			testContext = new AccountingContext() {
				@Override
				public String getUserName() { return TEST_USER_NAME;}
				
				@Override
				public String getDbFileName() { return TEST_DB_FILE;}
			};			
		}
		return testContext;
	}
	
	/**
	 * 
	 * @return test user instance
	 */
	protected static User getTestUser() {
		if (testUser == null) {
			testUser = new User();
			testUser.setName(TEST_USER_NAME);
			testUser.setDescription("JUnitTestUserDescription");
			testUser.setTaxNumber("JUnitTaxNumber");
			
			Address add = new Address();
			add.setCity("JUnitCity");
			add.setEmail("JUnit@email.com");
			add.setFaxNumber("JUnitFaxNumber");
			add.setMobileNumber("123456789");
			add.setPhoneNumber("987654321");
			add.setPostalCode("12345");
			add.setStreet("JUnitStreet");
			testUser.setAddress(add);
			
			BankAccount ba = new BankAccount();
			ba.setAccountNumber("JUnitAccountNumber");
			ba.setBankCode("JUnitBankCode");
			ba.setBankName("JUnitBankName");
			ba.setBic("JUnitBIC");
			ba.setIban("JUnitIBAN");
			testUser.setBankAccount(ba);
			
			TaxRate rate = new TaxRate();
			rate.setLongName("JUnitTax");
			rate.setShortName("JUT");
			rate.setRate(new BigDecimal("0.15"));
			Set<TaxRate> rates = new HashSet<TaxRate>();
			rates.add(rate);
			
			testUser.setTaxRates(rates);
		}
		return testUser;
	}
	
	/**
	 * 
	 * @return the test client instance
	 */
	protected static Client getTestClient() {
		if (testClient == null) {
			testClient = new Client();
			testClient.setName("JUnitTestClientName");
			
			Address add = new Address();
			add.setCity("JUnitClientCity");
			add.setEmail("JUnit@client-email.com");
			add.setPostalCode("12345");
			add.setStreet("JUnitClientStreet");
			testClient.setAddress(add);
			
			testClient.setClientNumber("001");
			testClient.setDefaultPaymentTerms(new PaymentTerms(PaymentType.TRADE_CREDIT, 60));
		}
		return testClient;
	}
	
	/**
	 * 
	 * @param expected
	 * @param actual
	 */
	protected static void assertAreEqual(BigDecimal expected, BigDecimal actual) {
		if (expected == null) {
			assertNull(actual);
		} else {
			assertEquals("Expected: " + expected.toString() + ", actual: " + actual.toString(), 0, actual.compareTo(expected));
		}
		
	}
	
	/**
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 */
	protected static Date buildDate(int day, int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
	
	/**
	 * Net total: 8598.78
	 * Gross total: 9798.78
	 * Tax total: 1200
	 * 
     * @return test revenue
     */
    protected static Revenue createTestRevenue() {
	    TaxRate taxRate = getTestUser().getTaxRates().iterator().next();
		
		// INVOICE 1
		InvoicePosition ip1 = new InvoicePosition();
		ip1.setPricePerUnit(new BigDecimal("50"));
		ip1.setQuantity(new BigDecimal("160"));
		ip1.setTaxRate(taxRate);
		
		List<InvoicePosition> ipList1 = new ArrayList<InvoicePosition>();
		ipList1.add(ip1);
		
		Invoice invoice1 = new Invoice();
		invoice1.setInvoicePositions(ipList1);
		
		// INVOICE 2
		InvoicePosition ip2 = new InvoicePosition();
		ip2.setPricePerUnit(new BigDecimal("598.78"));
		ip2.setQuantity(BigDecimal.ONE);

		List<InvoicePosition> ipList2 = new ArrayList<InvoicePosition>();
		ipList2.add(ip2);
		
		Invoice invoice2 = new Invoice();
		invoice2.setInvoicePositions(ipList2);		
		
		List<Invoice> invoices = new ArrayList<Invoice>();
		invoices.add(invoice1);
		invoices.add(invoice2);
		
		Revenue revenue = new Revenue();
		revenue.setInvoices(invoices);
	    return revenue;
    }
	
    /**
     * 
     * @param revenue
     */
    protected static void assertIsTestRevenue(Revenue revenue) {
		assertEquals(0, new BigDecimal("8598.78").compareTo(revenue.getRevenueNet()));
		assertEquals(0, new BigDecimal("9798.78").compareTo(revenue.getRevenueGross()));
		assertEquals(0, new BigDecimal("1200").compareTo(revenue.getRevenueTax()));
    }
    
    /**
     * 
     * @param revenue
     */
    protected static void assertIsTestRevenue(Price revenue) {
		assertEquals(0, new BigDecimal("8598.78").compareTo(revenue.getNet()));
		assertEquals(0, new BigDecimal("9798.78").compareTo(revenue.getGross()));
		assertEquals(0, new BigDecimal("1200").compareTo(revenue.getTax()));
    }
    
    /**
     * 
     * @param category
     * @param description
     * @param type
     * @param netAmount
     * @param useTax
     * @return
     */
    protected static Expense createExpense(String category, String description, ExpenseType type, String netAmount, boolean useTax) {
		Expense expense = new Expense();
		expense.setCategory(category);
		expense.setDescription(description);
		expense.setExpenseType(type);
		expense.setNetAmount(new BigDecimal(netAmount));
		if (useTax){
			expense.setTaxRate(getTestUser().getTaxRates().iterator().next());
		}
		return expense;
    }
    
    /**
     * 
     * @param category
     * @param description
     * @param type
     * @param netAmount
     * @param useTax
     * @param day
     * @param month
     * @param year
     * @return
     */
	protected static Expense createExpense(String category, String description, ExpenseType type, String netAmount, boolean useTax, int day, int month, int year) {
		Expense expense = createExpense(category, description, type, netAmount, useTax);		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		expense.setPaymentDate(cal.getTime());
		
		return expense;
	}
	
	/**
     * OPEX: 300, 15, 315
     * CAPEX: 1000, 150, 1150
     * TOTAL: 1300, 165, 1465 
     * 
     * @return
     */
    protected static Set<Expense> createTestExpenses() {
    	Set<Expense> expenses = new HashSet<Expense>();
    	expenses.add(createExpense("Capex Category", "Capex Description", ExpenseType.CAPEX, "1000", true, 23, Calendar.FEBRUARY, 2010)); //$NON-NLS-1$
    	expenses.add(createExpense("Opex Cat One", "Opex Desc One", ExpenseType.OPEX, "100", true, 1, Calendar.JANUARY, 2010)); //$NON-NLS-1$
    	expenses.add(createExpense("Opex Cat Two", "Opex Desc Two", ExpenseType.OPEX, "200", false, 15, Calendar.JANUARY, 2010)); //$NON-NLS-1$
    	expenses.add(createExpense(null, null, null, "0", false));
    	return expenses;
    }
    
    /**
     * 
     * @param ec
     */
    protected static void assertIsTestExpenses(ExpenseCollection ec) {
		assertIsTestExpenseTotal(ec.getTotalCost());
		
		Price operating = ec.getCost(ExpenseType.OPEX);
		assertAreEqual(new BigDecimal("300"), operating.getNet());
		assertAreEqual(new BigDecimal("315"), operating.getGross());
		assertAreEqual(new BigDecimal("15"), operating.getTax());
		
		Price capital = ec.getCost(ExpenseType.CAPEX);
		assertAreEqual(new BigDecimal("1000"), capital.getNet());
		assertAreEqual(new BigDecimal("150"), capital.getTax());
		assertAreEqual(new BigDecimal("1150"), capital.getGross());
		
		int found = 0;
		
		for (Expense expense : ec.getExpenses()) {
			if ("Capex Description".equals(expense.getDescription())) {
				assertEquals("Capex Category", expense.getCategory());
				assertNotNull(expense.getTaxRate());
				assertDatesMatch(23, Calendar.FEBRUARY, 2010, expense.getPaymentDate());
				found++;
			} else if ("Opex Desc One".equals(expense.getDescription())) {
				assertEquals("Opex Cat One", expense.getCategory());
				assertNotNull(expense.getTaxRate());
				assertDatesMatch(1, Calendar.JANUARY, 2010, expense.getPaymentDate());
				found++;
			} else if ("Opex Desc Two".equals(expense.getDescription())) {
				assertEquals("Opex Cat Two", expense.getCategory());
				assertNull(expense.getTaxRate());
				assertDatesMatch(15, Calendar.JANUARY, 2010, expense.getPaymentDate());
				found++;
			}
		}
		
		assertEquals(3, found);
    }
    
    /**
     * 
     * @param dayExpected
     * @param monthExpected
     * @param yearExpected
     * @param actual
     */
    protected static void assertDatesMatch(int dayExpected, int monthExpected, int yearExpected, Date actual) {
    	assertNotNull(actual);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(actual);
    	assertEquals(dayExpected, cal.get(Calendar.DAY_OF_MONTH));
    	assertEquals(monthExpected, cal.get(Calendar.MONTH));
    	assertEquals(yearExpected, cal.get(Calendar.YEAR));
    }
    
    /**
     * 
     * @param total
     */
    protected static void assertIsTestExpenseTotal(Price total) {
		assertAreEqual(new BigDecimal("1300"), total.getNet());
		assertAreEqual(new BigDecimal("1465"), total.getGross());
		assertAreEqual(new BigDecimal("165"), total.getTax());    	
    }
}
