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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.User;
import de.togginho.accounting.xml.generated.XmlUser;

/**
 * @author thorsten
 *
 */
public class ModelToXmlTest extends XmlTestBase {

	private static final String XML_TEST_FILE = "ModelToXmlTest.xml";
	
	private ModelToXml modelToXml;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		modelToXml = new ModelToXml();
		File file = new File(XML_TEST_FILE);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		File file = new File(XML_TEST_FILE);
		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * Test method for {@link ModelToXml#convertToXml(User, java.util.Set, java.util.Set)} and
	 * {@link ModelMapper#modelToXml(User, Set, Set, String)}.
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
		assertUserSame(user, xmlUser);
		
		assertNotNull(xmlUser.getClients());
		assertEquals(1, xmlUser.getClients().getClient().size());
		assertClientsSame(client, xmlUser.getClients().getClient().get(0));
		
		assertNotNull(xmlUser.getInvoices());
		assertEquals(1, xmlUser.getInvoices().getInvoice().size());
		
		// test writing to XML
		ModelMapper.modelToXml(user, clients, invoices, XML_TEST_FILE);
		File file = new File(XML_TEST_FILE);
		assertTrue(file.exists());
	}
}
