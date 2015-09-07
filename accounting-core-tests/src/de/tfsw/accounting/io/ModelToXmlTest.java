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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.io.xml.XmlExpense;
import de.tfsw.accounting.io.xml.XmlUser;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.DepreciationMethod;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.Frequency;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.RecurrenceRule;
import de.tfsw.accounting.model.User;

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
	 * {@link AccountingXmlImportExport#modelToXml(User, Set, Set, String)}.
	 */
	@Test
	public void testConvertToXml() {
		final User user = getTestUser();
		final Client client = getTestClient();
		
		final Set<Client> clients = new HashSet<Client>();
		clients.add(client);
		
		final Set<Invoice> invoices = new HashSet<Invoice>();
		invoices.add(createInvoice());
		
		final Set<Expense> expenses = new HashSet<Expense>();
		Expense opex = createExpense(ExpenseType.OPEX, true);
		expenses.add(opex);
		Expense other = createExpense(ExpenseType.OTHER, false);
		expenses.add(other);
		Expense capex = createExpense(ExpenseType.CAPEX, true);
		capex.setDepreciationMethod(DepreciationMethod.STRAIGHTLINE);
		capex.setDepreciationPeriodInYears(3);
		capex.setSalvageValue(BigDecimal.ONE);
		expenses.add(capex);
		
		final Set<ExpenseTemplate> templates = new HashSet<ExpenseTemplate>();
		RecurrenceRule rule = new RecurrenceRule(Frequency.MONTHLY, 1);
		ExpenseTemplate template = new ExpenseTemplate(true, LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), rule);
		template.setCategory("Template_Cat");
		template.setDescription("Template_Desc");
		template.setExpenseType(ExpenseType.OPEX);
		template.setNetAmount(new BigDecimal("44.99"));
		templates.add(template);
		
		XmlModelDTO model = new XmlModelDTO();
		model.setUser(user);
		model.setClients(clients);
		model.setInvoices(invoices);
		model.setExpenses(expenses);
		model.setExpenseTemplates(templates);
		
		XmlUser xmlUser = modelToXml.convertToXml(model);
		assertUserSame(user, xmlUser);
		
		assertNotNull(xmlUser.getClients());
		assertEquals(1, xmlUser.getClients().getClient().size());
		assertClientsSame(client, xmlUser.getClients().getClient().get(0));
		
		assertNotNull(xmlUser.getInvoices());
		assertEquals(1, xmlUser.getInvoices().getInvoice().size());
		
		assertNotNull(xmlUser.getExpenses());
		assertEquals(3, xmlUser.getExpenses().getExpense().size());
		for (XmlExpense xmlExpense : xmlUser.getExpenses().getExpense()) {
			switch (xmlExpense.getExpenseType()) {
			case OPEX:
				assertExpensesSame(opex, xmlExpense);
				break;
			case OTHER:
				assertExpensesSame(other, xmlExpense);
				break;
			case CAPEX:
				assertExpensesSame(capex, xmlExpense);
				break;
			}
		}
		
		assertNotNull(xmlUser.getExpenseTemplates());
		assertEquals(1, xmlUser.getExpenseTemplates().getTemplate().size());
		assertTemplateEquals(template, xmlUser.getExpenseTemplates().getTemplate().get(0));
		
		// test writing to XML
		AccountingXmlImportExport.exportModelToXml(model, XML_TEST_FILE);
		File file = new File(XML_TEST_FILE);
		assertTrue(file.exists());
	}
}
