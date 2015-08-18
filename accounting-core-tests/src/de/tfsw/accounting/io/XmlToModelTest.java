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

import static org.junit.Assert.*;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import de.tfsw.accounting.io.AccountingXmlImportExport;
import de.tfsw.accounting.io.XmlModelDTO;
import de.tfsw.accounting.io.XmlToModel;
import de.tfsw.accounting.io.xml.XmlExpense;
import de.tfsw.accounting.io.xml.XmlExpenseTemplate;
import de.tfsw.accounting.io.xml.XmlExpenseTemplates;
import de.tfsw.accounting.io.xml.XmlExpenseType;
import de.tfsw.accounting.io.xml.XmlExpenses;
import de.tfsw.accounting.io.xml.XmlRecurrenceRule;
import de.tfsw.accounting.io.xml.XmlUser;
import de.tfsw.accounting.model.DepreciationMethod;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.Frequency;
import de.tfsw.accounting.model.RecurrenceRule;

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
		assertNotNull(result.getExpenseTemplates());
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
			assertExpenses(xmlUser.getExpenses());
			// EXPENSE TEMPLATES
			assertExpenseTemplates(xmlUser.getExpenseTemplates());
		} catch (JAXBException e) {
			fail(e.toString());
		}
	}
	
	/**
	 * 
	 * @param xmlExpenses
	 */
	private void assertExpenses(XmlExpenses xmlExpenses) {
		assertNotNull(xmlExpenses);
		assertEquals(3, xmlExpenses.getExpense().size());
		for (XmlExpense xmlExpense : xmlExpenses.getExpense()) {
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
			default:
				fail("Unknown / Illegal expense type: " + xmlExpense.getExpenseType());
			}
		}		
	}
	
	/**
	 * 
	 * @param xmlTemplates
	 */
	private void assertExpenseTemplates(XmlExpenseTemplates xmlTemplates) {
		assertNotNull(xmlTemplates);
		assertEquals(3, xmlTemplates.getTemplate().size());
		for (XmlExpenseTemplate xmlTemplate : xmlTemplates.getTemplate()) {
			switch (xmlTemplate.getExpenseType()) {
			case OPEX:
				assertEquals("ExpenseCategory", xmlTemplate.getCategory());
				assertEquals("JUnitTemplateDesc_1", xmlTemplate.getDescription());
				assertEquals(XmlExpenseType.OPEX, xmlTemplate.getExpenseType());
				assertDatesSame(LocalDate.of(2015, 1, 1), xmlTemplate.getFirstApplication());
				assertDatesSame(LocalDate.of(2015, 8, 1), xmlTemplate.getLastApplication());
				assertAreEqual(new BigDecimal("10.55"), xmlTemplate.getNetAmount());
				assertEquals(8, xmlTemplate.getNumberOfApplications());
				assertTaxRatesSame(DEFAULT_TAXRATE, xmlTemplate.getTaxRate());
				assertTrue(xmlTemplate.isActive());
				assertAreEqual(new RecurrenceRule(Frequency.MONTHLY, 1), xmlTemplate.getRule());
				break;
			case CAPEX:
				assertEquals("CapexCategory", xmlTemplate.getCategory());
				assertEquals("JUnitTemplateDesc_2", xmlTemplate.getDescription());
				assertEquals(XmlExpenseType.CAPEX, xmlTemplate.getExpenseType());
				assertDatesSame(LocalDate.of(2010, 6, 15), xmlTemplate.getFirstApplication());
				assertDatesSame(LocalDate.of(2014, 6, 15), xmlTemplate.getLastApplication());
				assertAreEqual(new BigDecimal("150"), xmlTemplate.getNetAmount());
				assertEquals(2, xmlTemplate.getNumberOfApplications());
				assertNull(xmlTemplate.getTaxRate());
				assertFalse(xmlTemplate.isActive());
				assertAreEqual(new RecurrenceRule(Frequency.YEARLY, 2, 2), xmlTemplate.getRule());
				break;
			case OTHER:
				assertNull(xmlTemplate.getCategory());
				assertEquals("JUnitTemplateDesc_3", xmlTemplate.getDescription());
				assertEquals(XmlExpenseType.OTHER, xmlTemplate.getExpenseType());
				assertDatesSame(LocalDate.of(2015, 1, 1), xmlTemplate.getFirstApplication());
				assertNull(xmlTemplate.getLastApplication());
				assertAreEqual(new BigDecimal("0.69"), xmlTemplate.getNetAmount());
				assertEquals(0, xmlTemplate.getNumberOfApplications());
				assertNull(xmlTemplate.getTaxRate());
				assertFalse(xmlTemplate.isActive());
				assertAreEqual(new RecurrenceRule(Frequency.WEEKLY, 5, LocalDate.of(2020, 6, 1)), xmlTemplate.getRule());
				break;
			default:
				fail("Unknown / Illegal expense type: " + xmlTemplate.getExpenseType());
			}
		}
	}
	
	private void assertAreEqual(RecurrenceRule rule, XmlRecurrenceRule xmlRule) {
		assertEquals(rule.getCount(), xmlRule.getCount());
		assertEquals(rule.getFrequency().name(), xmlRule.getFrequency().name());
		assertEquals(rule.getInterval(), xmlRule.getInterval());
		assertDatesSame(rule.getUntil(), xmlRule.getUntil());
	}
}
