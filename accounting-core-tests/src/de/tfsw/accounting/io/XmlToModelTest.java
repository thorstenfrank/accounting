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
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import de.tfsw.accounting.io.AccountingXmlImportExport;
import de.tfsw.accounting.io.XmlModelDTO;
import de.tfsw.accounting.io.XmlToModel;
import de.tfsw.accounting.io.xml.XmlExpense;
import de.tfsw.accounting.io.xml.XmlUser;
import de.tfsw.accounting.model.DepreciationMethod;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseType;

/**
 * @author thorsten
 *
 */
public class XmlToModelTest extends XmlTestBase {

	/** */
	private static final String TEST_FILE_NAME = "XmlMappingTestFile.xml";
	
	/**
	 * Test method for {@link AccountingXmlImportExport#importModelFromXml(String)}.
	 */
	@Test
	public void testModelMapper() {
		XmlModelDTO result = AccountingXmlImportExport.importModelFromXml(TEST_FILE_NAME);
		assertNotNull(result);
		assertNotNull(result.getUser());
		assertNotNull(result.getClients());
		assertNotNull(result.getInvoices());
		assertNotNull(result.getExpenses());
	}
	
	/**
	 * Test method for {@link XmlToModel#convert(XmlUser)}.
	 */
	@Test
	public void testConvert() {
        try {
			Unmarshaller unmarshaller = JAXBContext.newInstance(AccountingXmlImportExport.JAXB_CONTEXT).createUnmarshaller();
			final XmlUser xmlUser = (XmlUser) unmarshaller.unmarshal(new File(TEST_FILE_NAME));
			// USER
			assertUserSame(getTestUser(), xmlUser);
			// CLIENTS
			assertNotNull(xmlUser.getClients());
			assertEquals(1, xmlUser.getClients().getClient().size());
			assertClientsSame(getTestClient(), xmlUser.getClients().getClient().get(0));
			// INVOICES
			assertNotNull(xmlUser.getInvoices());
			assertEquals(1, xmlUser.getInvoices().getInvoice().size());
			assertInvoicesSame(createInvoice(), xmlUser.getInvoices().getInvoice().get(0));
			// EXPENSES
			assertNotNull(xmlUser.getExpenses());
			assertEquals(3, xmlUser.getExpenses().getExpense().size());
			for (XmlExpense xmlExpense : xmlUser.getExpenses().getExpense()) {
				switch (xmlExpense.getExpenseType()) {
				case OPEX:
					assertExpensesSame(createExpense(ExpenseType.OPEX, true), xmlExpense);
					break;
				case CAPEX:
					Expense capex = createExpense(ExpenseType.CAPEX, true);
					capex.setCategory("CapexCategory");
					capex.setDescription("CapexDescription");
					capex.setNetAmount(new BigDecimal("100"));
					capex.setDepreciationMethod(DepreciationMethod.STRAIGHTLINE);
					capex.setDepreciationPeriodInYears(3);
					capex.setSalvageValue(BigDecimal.ONE);
					assertExpensesSame(capex, xmlExpense);
					break;
				case OTHER:
					assertExpensesSame(createExpense(ExpenseType.OTHER, false), xmlExpense);
					break;
				}
			}
		} catch (JAXBException e) {
			fail(e.toString());
		}
	}
}
