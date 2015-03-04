/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.elster.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.elster.ElsterDTO;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.model.IncomeStatement;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.Revenue;
import de.tfsw.accounting.model.User;
import de.tfsw.accounting.util.TimeFrame;

/**
 * @author Thorsten Frank
 *
 */
public class ElsterDTOBuilderTest {

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
	 * Tests {@link ElsterDTOBuilder#ElsterDTOBuilder(de.tfsw.accounting.AccountingService)} 
	 * with a <code>null</code> argument.
	 */
	@Test
	public void testConstructorNullArgument() {
		try {
			new ElsterDTOBuilder(null);
			fail("AccountingException should have been thrown");
		} catch (Exception e) {
			assertTrue("Exception thrown should have been AccountingException", e instanceof AccountingException);
		}
	}

	/**
	 * Tests {@link ElsterDTOBuilder#createDTO(java.time.YearMonth)}.
	 * 
	 * No revenue, no expenses - in this case, address data still needs to be properly adjusted, and amounts need to ne
	 * Kz81 = 0, Kz66 = null and Kz83 = 0.
	 */
	@Test
	public void testCreateDTOEmptyData() {
		AccountingService asMock = createMock(AccountingService.class);
		expect(asMock.getCurrentUser()).andReturn(getTestUser());
		
		TimeFrame lastMonth = TimeFrame.lastMonth();
		
		IncomeStatement is = new IncomeStatement();
		is.setTimeFrame(lastMonth);
		Revenue revenue = new Revenue();
		revenue.setInvoices(new ArrayList<Invoice>());		
		is.setRevenue(revenue);
		is.setOperatingExpenses(new ExpenseCollection(new HashSet<Expense>()));
		
		expect(asMock.getIncomeStatement(TimeFrame.lastMonth())).andReturn(is);
		
		replay(asMock);
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(YearMonth.now().minusMonths(1));
		
		assertCreationDate(dto);
		
		verify(asMock);
	}
	
	/**
	 * Tests {@link ElsterDTOBuilder#createDTO(java.time.YearMonth)}.
	 * 
	 * Standard test, address and revenue data need to be properly adapted.
	 */
	@Test
	public void testCreateDTO() {
		
		
		fail("Not yet implemented");
	}
	
	/**
	 * Tests {@link ElsterDTOBuilder#createDTO(java.time.YearMonth)}.
	 * 
	 * No revenue provided for the period, Kz81 needs to be set to 0, Kz83 must be negative. 
	 */
	@Test
	public void testCreateDTONoRevenue() {
		fail("Not yet implemented");
	}
	
	/**
	 * Tests {@link ElsterDTOBuilder#createDTO(java.time.YearMonth)}.
	 * 
	 * Revenue is provided with both known (19%, 7%) and unknown VAT rates. Ensure that filed output tax contains only
	 * known tax rates.
	 */
	@Test
	public void testCreateDTOUnknownTaxRates() {
		fail("Not yet implemented");
	}
	
	/**
	 * Tests {@link ElsterDTOBuilder#adaptToPeriod(de.tfsw.accounting.elster.ElsterDTO, java.time.YearMonth)}.
	 * 
	 * Address data must remain untouched, while amounts must be adapted.
	 */
	@Test
	public void testAdaptToPeriod() {
		fail("Not yet implemented");
	}
	
	/**
	 * Tests {@link ElsterDTOBuilder#adaptToPeriod(de.tfsw.accounting.elster.ElsterDTO, java.time.YearMonth)}.
	 * 
	 * Tries to adapt an unknown {@link ElsterDTO} implementation - ensure that the returned DTO is not equal to the
	 * supplied one, and that it is of class {@link ElsterDTOImpl}.
	 */
	@Test
	public void testAdaptToPeriodUnknownClass() {
		fail("Not yet implemented");
	}
	
	/**
	 * 
	 * @param dto
	 */
	private void assertCreationDate(ElsterDTO dto) {
		LocalDate now = LocalDate.now();
		StringBuilder sb = new StringBuilder();
		sb.append(now.getYear());
		sb.append(String.format("%02d", now.getMonthValue()));
		sb.append(String.format("%02d", now.getDayOfMonth()));
		
		assertEquals(sb.toString(), dto.getCreationDate());
	}
	
	/**
	 * 
	 * @return test user instance
	 */
	private User getTestUser() {
		User testUser = new User();
		testUser.setName("ElsterDTOBuilderTest User");
		testUser.setTaxNumber("12345/67890");

		Address add = new Address();
		add.setCity("JUnitCity");
		add.setEmail("JUnit@email.com");
		add.setPhoneNumber("987654321");
		add.setPostalCode("12345");
		add.setStreet("JUnitStreet 27");
		testUser.setAddress(add);
		
		return testUser;
	}
}
