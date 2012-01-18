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

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.togginho.accounting.xml.generated.XmlUser;

/**
 * @author thorsten
 *
 */
public class XmlToModelTest extends XmlTestBase {

	/** */
	private static final String TEST_FILE_NAME = "XmlMappingTestFile.xml";
	
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ModelMapper#xmlToModel(String)}.
	 */
	@Test
	public void testModelMapper() {
		ImportResult result = ModelMapper.xmlToModel(TEST_FILE_NAME);
		assertNotNull(result);
		assertNotNull(result.getImportedUser());
		assertNotNull(result.getImportedClients());
		assertNotNull(result.getImportedInvoices());		
	}
	
	/**
	 * Test method for {@link XmlToModel#convert(XmlUser)}.
	 */
	@Test
	public void testConvert() {
        try {
			Unmarshaller unmarshaller = JAXBContext.newInstance(ModelMapper.JAXB_CONTEXT).createUnmarshaller();
			final XmlUser xmlUser = (XmlUser) unmarshaller.unmarshal(new File(TEST_FILE_NAME));
			
			assertUserSame(getTestUser(), xmlUser);
			
			assertNotNull(xmlUser.getClients());
			assertEquals(1, xmlUser.getClients().getClient().size());
			assertClientsSame(getTestClient(), xmlUser.getClients().getClient().get(0));
			assertNotNull(xmlUser.getInvoices());
			assertEquals(1, xmlUser.getInvoices().getInvoice().size());
			assertInvoicesSame(createInvoice(), xmlUser.getInvoices().getInvoice().get(0));
		} catch (JAXBException e) {
			fail(e.toString());
		}
	}
}
