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
package de.togginho.accounting.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import de.togginho.accounting.BaseTestFixture;
import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.BankAccount;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.xml.generated.XmlAddress;
import de.togginho.accounting.xml.generated.XmlBankAccount;
import de.togginho.accounting.xml.generated.XmlClient;
import de.togginho.accounting.xml.generated.XmlInvoice;
import de.togginho.accounting.xml.generated.XmlInvoicePosition;
import de.togginho.accounting.xml.generated.XmlPaymentTerms;
import de.togginho.accounting.xml.generated.XmlPaymentType;
import de.togginho.accounting.xml.generated.XmlTaxRate;

/**
 * @author thorsten
 *
 */
class XmlTestBase extends BaseTestFixture {
	
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
		}
		assertEquals(address.getCity(), xmlAddress.getCity());
		assertEquals(address.getEmail(), xmlAddress.getEmail());
		assertEquals(address.getFaxNumber(), xmlAddress.getFax());
		assertEquals(address.getMobileNumber(), xmlAddress.getMobile());
		assertEquals(address.getPhoneNumber(), xmlAddress.getPhone());
		assertEquals(address.getPostalCode(), xmlAddress.getPostalCode());
		assertEquals(address.getStreet(), xmlAddress.getStreet());
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
		}
		assertEquals(account.getAccountNumber(), xmlAccount.getAccountNumber());
		assertEquals(account.getBankCode(), xmlAccount.getBankCode());
		assertEquals(account.getBankName(), xmlAccount.getBankName());
		assertEquals(account.getBic(), xmlAccount.getBic());
		assertEquals(account.getIban(), xmlAccount.getIban());
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
		}
		
		assertEquals(client.getClientNumber(), xmlClient.getClientNumber());
		assertEquals(client.getName(), xmlClient.getName());
		assertAddressesSame(client.getAddress(), xmlClient.getAddress());
		assertPaymentTermsSame(client.getDefaultPaymentTerms(), xmlClient.getDefaultPaymentTerms());
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
		}
		
		switch (terms.getPaymentType()) {
		case TRADE_CREDIT:
			assertEquals(XmlPaymentType.NET, xmlTerms.getType());
			break;
		default:
			break;
		}
		
		assertEquals(terms.getFullPaymentTargetInDays(), xmlTerms.getFullPaymentTargetInDays());
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
		}
		
		assertEquals(invoice.getNumber(), xmlInvoice.getNumber());
		assertEquals(invoice.getClient().getName(), xmlInvoice.getClient());
		assertDatesSame(invoice.getCancelledDate(), xmlInvoice.getCancelledDate());
		assertDatesSame(invoice.getCreationDate(), xmlInvoice.getCreationDate());
		assertDatesSame(invoice.getDueDate(), xmlInvoice.getDueDate());
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
	
	/**
	 * 
	 * @param date
	 * @param xmlDate
	 */
	protected void assertDatesSame(Date date, XMLGregorianCalendar xmlDate) {
		if (date == null) {
			assertNull(xmlDate);
		} else {
			assertNotNull(xmlDate);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		assertEquals(cal.get(Calendar.DAY_OF_MONTH), xmlDate.getDay());
		assertEquals(cal.get(Calendar.MONTH) + 1, xmlDate.getMonth());
		assertEquals(cal.get(Calendar.YEAR), xmlDate.getYear());
	}
	
	/**
	 * @param rate
	 * @param xmlRate
	 */
	protected void assertTaxRatesSame(TaxRate rate, XmlTaxRate xmlRate) {
		if (rate == null) {
			assertNull(xmlRate);
		} else {
			assertNotNull(xmlRate);
		}
		assertEquals(rate.getLongName(), xmlRate.getName());
		assertEquals(rate.getShortName(), xmlRate.getAbbreviation());
		assertEquals(rate.getRate(), xmlRate.getRate());
	}
}
