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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.togginho.accounting.BaseTestFixture;
import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.BankAccount;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.model.User;
import de.togginho.accounting.xml.generated.XmlAddress;
import de.togginho.accounting.xml.generated.XmlBankAccount;
import de.togginho.accounting.xml.generated.XmlClient;
import de.togginho.accounting.xml.generated.XmlPaymentTerms;
import de.togginho.accounting.xml.generated.XmlPaymentType;
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
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		modelToXml = new ModelToXml();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
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
		
		XmlUser xmlUser = modelToXml.convertToXml(user, clients, null);
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
		
		assertNull(xmlUser.getInvoices());

		ModelMapper.modelToXml(user, null, null, "JUnitTestFile.xml");
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
		assertEquals(rate.getLongName(), xmlRate.getName());
		assertEquals(rate.getShortName(), xmlRate.getAbbreviation());
		assertEquals(rate.getRate(), xmlRate.getRate());
	}
}
