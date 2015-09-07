/*
 *  Copyright 2012 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import de.tfsw.accounting.BaseTestFixture;
import de.tfsw.accounting.io.xml.XmlAddress;
import de.tfsw.accounting.io.xml.XmlBankAccount;
import de.tfsw.accounting.io.xml.XmlClient;
import de.tfsw.accounting.io.xml.XmlExpense;
import de.tfsw.accounting.io.xml.XmlExpenseTemplate;
import de.tfsw.accounting.io.xml.XmlInvoice;
import de.tfsw.accounting.io.xml.XmlInvoicePosition;
import de.tfsw.accounting.io.xml.XmlPaymentTerms;
import de.tfsw.accounting.io.xml.XmlPaymentType;
import de.tfsw.accounting.io.xml.XmlRecurrenceRule;
import de.tfsw.accounting.io.xml.XmlTaxRate;
import de.tfsw.accounting.io.xml.XmlUser;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.BankAccount;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoicePosition;
import de.tfsw.accounting.model.PaymentTerms;
import de.tfsw.accounting.model.RecurrenceRule;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.User;

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

		LocalDate date = LocalDate.of(2011, 1, 1);
		invoice.setCreationDate(date);
		invoice.setInvoiceDate(date);
		invoice.setSentDate(date);
		
		invoice.setPaymentDate(date.plusDays(invoice.getPaymentTerms().getFullPaymentTargetInDays()));

		InvoicePosition ip = new InvoicePosition();
		ip.setDescription("JUnitInvoicePosition");
		ip.setPricePerUnit(new BigDecimal("55"));
		ip.setQuantity(new BigDecimal("100.5"));
		ip.setTaxRate(DEFAULT_TAXRATE);
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
		expense.setPaymentDate(LocalDate.of(2011, 1, 1));
		if (includeVAT) {
			expense.setTaxRate(DEFAULT_TAXRATE);
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
			assertEquals(1, xmlUser.getTaxRates().getTaxRate().size());
			assertTaxRatesSame(DEFAULT_TAXRATE, xmlUser.getTaxRates().getTaxRate().get(0));
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
	protected void assertDatesSame(LocalDate date, XMLGregorianCalendar xmlDate) {
		if (date == null) {
			assertNull(xmlDate);
			return;
		} else {
			assertNotNull(xmlDate);
			assertEquals(date.getDayOfMonth(), xmlDate.getDay());
			assertEquals(date.getMonthValue(), xmlDate.getMonth());
			assertEquals(date.getYear(), xmlDate.getYear());
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
	
	/**
	 * 
	 * @param template
	 * @param xmlTemplate
	 */
	protected void assertTemplateEquals(ExpenseTemplate template, XmlExpenseTemplate xmlTemplate) {
		assertEquals(template.getCategory(), xmlTemplate.getCategory());
		assertEquals(template.getDescription(), xmlTemplate.getDescription());
		assertEquals(template.getExpenseType().name(), xmlTemplate.getExpenseType().name());
		assertDatesSame(template.getFirstApplication(), xmlTemplate.getFirstApplication());
		assertDatesSame(template.getLastApplication(), xmlTemplate.getLastApplication());
		assertAreEqual(template.getNetAmount(), xmlTemplate.getNetAmount());
		assertEquals(template.getNumberOfApplications(), xmlTemplate.getNumberOfApplications());
		assertTaxRatesSame(template.getTaxRate(), xmlTemplate.getTaxRate());
		RecurrenceRule rule = template.getRule();
		XmlRecurrenceRule xmlRule = xmlTemplate.getRule();
		
		assertEquals(rule.getCount(), xmlRule.getCount());
		assertEquals(rule.getFrequency().name(), xmlRule.getFrequency().name());
		assertEquals(rule.getInterval(), xmlRule.getInterval());
		assertDatesSame(rule.getUntil(), xmlRule.getUntil());
	}
}
