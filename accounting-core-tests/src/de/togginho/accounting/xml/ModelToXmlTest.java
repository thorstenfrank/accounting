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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.model.User;
import de.togginho.accounting.xml.generated.XmlTaxRate;
import de.togginho.accounting.xml.generated.XmlTaxRates;
import de.togginho.accounting.xml.generated.XmlUser;

/**
 * @author thorsten
 *
 */
public class ModelToXmlTest extends XmlTestBase {

	private ModelToXml modelToXml;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		modelToXml = new ModelToXml();
	}

	/**
	 * Test method for {@link ModelToXml#convertToXml(User, java.util.Set, java.util.Set)}.
	 */
	@Test
	public void testConvertToXml() {
		final User user = getTestUser();
		final Client client = getTestClient();
		
		final Set<Client> clients = new HashSet<Client>();
		clients.add(client);
		
		final Set<Invoice> invoices = new HashSet<Invoice>();
		invoices.add(createInvoice());
		
		XmlUser xmlUser = modelToXml.convertToXml(user, clients, invoices);
		assertNotNull(xmlUser);
		assertEquals(user.getName(), xmlUser.getName());
		assertEquals(user.getDescription(), xmlUser.getDescription());
		assertEquals(user.getTaxNumber(), xmlUser.getTaxId());
		
		assertAddressesSame(user.getAddress(), xmlUser.getAddress());
		assertBankAccountSame(user.getBankAccount(), xmlUser.getBankAccount());
		assertTaxRates(xmlUser.getTaxRates());
	
		assertNotNull(xmlUser.getClients());
		assertEquals(1, xmlUser.getClients().getClient().size());
		assertClientsSame(client, xmlUser.getClients().getClient().get(0));
		
		assertNotNull(xmlUser.getInvoices());
		assertEquals(1, xmlUser.getInvoices().getInvoice().size());
		
		ModelMapper.modelToXml(user, clients, invoices, "JUnitTestFile.xml");
	}
	
	/**
	 * 
	 * @return
	 */
	private Invoice createInvoice() {
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
		ip.setUnit("JUnitUni");
		
		final List<InvoicePosition> invoicePositions = new ArrayList<InvoicePosition>();
		invoicePositions.add(ip);
		invoice.setInvoicePositions(invoicePositions);
		
		return invoice;
	}
	
	/**
	 * 
	 * @param xmlRates
	 */
	private void assertTaxRates(XmlTaxRates xmlRates) {
		TaxRate rate = getTestUser().getTaxRates().iterator().next();
		
		assertNotNull(xmlRates);
		assertEquals(1, xmlRates.getTaxRate().size());
		XmlTaxRate xmlRate = xmlRates.getTaxRate().get(0);
		
		assertTaxRatesSame(rate, xmlRate);
	}
}
