/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import de.togginho.accounting.BaseTestFixture;
import de.togginho.accounting.io.xml.XmlAddress;
import de.togginho.accounting.io.xml.XmlBankAccount;
import de.togginho.accounting.io.xml.XmlClient;
import de.togginho.accounting.io.xml.XmlExpense;
import de.togginho.accounting.io.xml.XmlInvoice;
import de.togginho.accounting.io.xml.XmlInvoicePosition;
import de.togginho.accounting.io.xml.XmlPaymentTerms;
import de.togginho.accounting.io.xml.XmlPaymentType;
import de.togginho.accounting.io.xml.XmlTaxRate;
import de.togginho.accounting.io.xml.XmlUser;
import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.BankAccount;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.model.User;

/**
 * @author thorsten
 *
 */
class XmlTestBase extends BaseTestFixture {
	
	/**
	 * 
	 * @return
	 */
	protected Invoice createInvoice() {
		Invoice invoice = new Invoice();
		invoice.setClient(getTestClient());
		invoice.setNumber("JUnitInvoiceNo");
		invoice.setPaymentTerms(getTestClient().getDefaultPaymentTerms());
		invoice.setUser(getTestUser());

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, 2011);
		invoice.setCreationDate(cal.getTime());
		invoice.setInvoiceDate(cal.getTime());
		invoice.setSentDate(cal.getTime());
		
		cal.add(Calendar.DAY_OF_MONTH, invoice.getPaymentTerms().getFullPaymentTargetInDays());
		invoice.setPaymentDate(cal.getTime());

		InvoicePosition ip = new InvoicePosition();
		ip.setDescription("JUnitInvoicePosition");
		ip.setPricePerUnit(new BigDecimal("55"));
		ip.setQuantity(new BigDecimal("100.5"));
		ip.setRevenueRelevant(true);
		ip.setTaxRate(invoice.getUser().getTaxRates().iterator().next());
		ip.setUnit("JUnitUnit");
		
		final List<InvoicePosition> invoicePositions = new ArrayList<InvoicePosition>();
		invoicePositions.add(ip);
		invoice.setInvoicePositions(invoicePositions);
		
		return invoice;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	protected Expense createExpense(ExpenseType type, boolean includeVAT) {
		Expense expense = new Expense();
		expense.setCategory("ExpenseCategory");
		expense.setDescription("ExpenseDescription");
		expense.setExpenseType(type);
		expense.setNetAmount(new BigDecimal("20.23"));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, 2011);
		expense.setPaymentDate(cal.getTime());
		if (includeVAT) {
			expense.setTaxRate(getTestUser().getTaxRates().iterator().next());
		}
		return expense;
	}
	
	/**
	 * 
	 * @param user
	 * @param xmlUser
	 */
	protected void assertUserSame(User user, XmlUser xmlUser) {
		if (user == null) {
			assertNull(xmlUser);
		} else {
			assertNotNull(xmlUser);
			assertEquals(user.getName(), xmlUser.getName());
			assertEquals(user.getDescription(), xmlUser.getDescription());
			assertEquals(user.getTaxNumber(), xmlUser.getTaxId());
			assertAddressesSame(user.getAddress(), xmlUser.getAddress());
			assertBankAccountSame(user.getBankAccount(), xmlUser.getBankAccount());
			
			assertNotNull(user.getTaxRates());
			assertNotNull(xmlUser.getTaxRates());
			TaxRate rate = user.getTaxRates().iterator().next();
			XmlTaxRate xmlRate = xmlUser.getTaxRates().getTaxRate().get(0);
			assertTaxRatesSame(rate, xmlRate);
		}
	}
	
	/**
	 * 
	 * @param address
	 * @param xmlAddress
	 */
	protected void assertAddressesSame(Address address, XmlAddress xmlAddress) {
		if (address == null) {
			assertNull(xmlAddress);
		} else {
			assertNotNull(xmlAddress);
			assertEquals(address.getCity(), xmlAddress.getCity());
			assertEquals(address.getEmail(), xmlAddress.getEmail());
			assertEquals(address.getFaxNumber(), xmlAddress.getFax());
			assertEquals(address.getMobileNumber(), xmlAddress.getMobile());
			assertEquals(address.getPhoneNumber(), xmlAddress.getPhone());
			assertEquals(address.getPostalCode(), xmlAddress.getPostalCode());
			assertEquals(address.getRecipientDetail(), xmlAddress.getRecipientDetail());
			assertEquals(address.getStreet(), xmlAddress.getStreet());
		}
	}
	
	/**
	 * 
	 * @param account
	 * @param xmlAccount
	 */
	protected void assertBankAccountSame(BankAccount account, XmlBankAccount xmlAccount) {
		if (account == null) {
			assertNull(xmlAccount);
		} else {
			assertNotNull(xmlAccount);
			assertEquals(account.getAccountNumber(), xmlAccount.getAccountNumber());
			assertEquals(account.getBankCode(), xmlAccount.getBankCode());
			assertEquals(account.getBankName(), xmlAccount.getBankName());
			assertEquals(account.getBic(), xmlAccount.getBic());
			assertEquals(account.getIban(), xmlAccount.getIban());
		}
	}
	
	/**
	 * 
	 * @param client
	 * @param xmlClient
	 */
	protected void assertClientsSame(Client client, XmlClient xmlClient) {
		if (client == null) {
			assertNull(xmlClient);
		} else {
			assertNotNull(xmlClient);
			assertEquals(client.getClientNumber(), xmlClient.getClientNumber());
			assertEquals(client.getName(), xmlClient.getName());
			assertAddressesSame(client.getAddress(), xmlClient.getAddress());
			assertPaymentTermsSame(client.getDefaultPaymentTerms(), xmlClient.getDefaultPaymentTerms());
		}
	}
	
	/**
	 * 
	 * @param terms
	 * @param xmlTerms
	 */
	protected void assertPaymentTermsSame(PaymentTerms terms, XmlPaymentTerms xmlTerms) {
		if (terms == null) {
			assertNull(xmlTerms);
		} else {
			assertNotNull(xmlTerms);
		
			switch (terms.getPaymentType()) {
			case TRADE_CREDIT:
				assertEquals(XmlPaymentType.NET, xmlTerms.getType());
				break;
			default:
				break;
			}
			
			assertEquals(terms.getFullPaymentTargetInDays(), xmlTerms.getFullPaymentTargetInDays());
		}
	}
	
	/**
	 * 
	 * @param invoice
	 * @param xmlInvoice
	 */
	protected void assertInvoicesSame(Invoice invoice, XmlInvoice xmlInvoice) {
		if (invoice == null) {
			assertNull(xmlInvoice);
		} else {
			assertNotNull(xmlInvoice);		
			assertEquals(invoice.getNumber(), xmlInvoice.getNumber());
			assertEquals(invoice.getClient().getName(), xmlInvoice.getClient());
			assertDatesSame(invoice.getCancelledDate(), xmlInvoice.getCancelledDate());
			assertDatesSame(invoice.getCreationDate(), xmlInvoice.getCreationDate());
			assertDatesSame(invoice.getInvoiceDate(), xmlInvoice.getInvoiceDate());
			assertDatesSame(invoice.getPaymentDate(), xmlInvoice.getPaymentDate());
			assertDatesSame(invoice.getSentDate(), xmlInvoice.getSentDate());
			
			if (invoice.getInvoicePositions() == null) {
				assertNull(xmlInvoice.getInvoicePositions());
			} else {
				assertNotNull(xmlInvoice.getInvoicePositions());
				
				final List<InvoicePosition> ips = invoice.getInvoicePositions();
				final List<XmlInvoicePosition> xmlIps = xmlInvoice.getInvoicePositions().getInvoicePosition();
				
				for (int i = 0; i < ips.size(); i++) {
					InvoicePosition ip = ips.get(i);
					XmlInvoicePosition xmlIp = xmlIps.get(i);
					
					assertEquals(ip.getDescription(), xmlIp.getDescription());
					assertEquals(ip.getUnit(), xmlIp.getUnit());
					assertEquals(ip.getPricePerUnit(), xmlIp.getPricePerUnit());
					assertEquals(ip.getQuantity(), xmlIp.getQuantity());
					assertTaxRatesSame(ip.getTaxRate(), xmlIp.getTaxRate());
				}
			}
		}
	}
	
	/**
	 * 
	 * @param date
	 * @param xmlDate
	 */
	protected void assertDatesSame(Date date, XMLGregorianCalendar xmlDate) {
		if (date == null) {
			assertNull(xmlDate);
			return;
		} else {
			assertNotNull(xmlDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
			assertEquals(cal.get(Calendar.DAY_OF_MONTH), xmlDate.getDay());
			assertEquals(cal.get(Calendar.MONTH) + 1, xmlDate.getMonth());
			assertEquals(cal.get(Calendar.YEAR), xmlDate.getYear());
		}
	}
	
	/**
	 * 
	 * @param expense
	 * @param xmlExpense
	 */
	protected void assertExpensesSame(Expense expense, XmlExpense xmlExpense) {
		assertEquals(expense.getCategory(), xmlExpense.getCategory());
		assertEquals(expense.getDescription(), xmlExpense.getDescription());
		assertEquals(expense.getExpenseType().name(), xmlExpense.getExpenseType().name());
		assertEquals(expense.getNetAmount(), xmlExpense.getNetAmount());
		assertDatesSame(expense.getPaymentDate(), xmlExpense.getPaymentDate());
		assertTaxRatesSame(expense.getTaxRate(), xmlExpense.getTaxRate());
		if (expense.getDepreciationMethod() == null) {
			assertNull(xmlExpense.getDepreciationMethod());
		} else {
			assertEquals(expense.getDepreciationMethod().name(), xmlExpense.getDepreciationMethod().name());
		}
		
		if (expense.getDepreciationPeriodInYears() == null) {
			assertNull(xmlExpense.getDepreciationPeriodInYears());
		} else {
			assertEquals(expense.getDepreciationPeriodInYears(), xmlExpense.getDepreciationPeriodInYears());
		}
		
		assertAreEqual(expense.getSalvageValue(), xmlExpense.getSalvageValue());
	}
	
	/**
	 * @param rate
	 * @param xmlRate
	 */
	protected void assertTaxRatesSame(TaxRate rate, XmlTaxRate xmlRate) {
		if (rate == null) {
			assertNull(xmlRate);
			return;
		} else {
			assertNotNull(xmlRate);
			assertEquals(rate.getLongName(), xmlRate.getName());
			assertEquals(rate.getShortName(), xmlRate.getAbbreviation());
			assertEquals(rate.getRate(), xmlRate.getRate());
		}
	}
}
