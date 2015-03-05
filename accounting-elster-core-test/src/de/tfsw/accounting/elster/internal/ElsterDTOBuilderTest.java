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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.elster.Bundesland;
import de.tfsw.accounting.elster.ElsterDTO;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.model.IncomeStatement;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoicePosition;
import de.tfsw.accounting.model.Revenue;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.User;
import de.tfsw.accounting.util.TimeFrame;

/**
 * @author Thorsten Frank
 *
 */
public class ElsterDTOBuilderTest {

	private static final TaxRate UST_19;
	private static final TaxRate UST_7;
	private static final TaxRate BOGUS_TAXRATE;
	static {
		UST_19 = new TaxRate();
		UST_19.setIsVAT(true);
		UST_19.setRate(new BigDecimal("0.19"));
		
		UST_7 = new TaxRate();
		UST_7.setIsVAT(true);
		UST_7.setRate(new BigDecimal("0.07"));
		
		BOGUS_TAXRATE = new TaxRate();
		BOGUS_TAXRATE.setIsVAT(true);
		BOGUS_TAXRATE.setRate(new BigDecimal("0.125"));
	}
	
	private AccountingService asMock;
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		if (asMock != null) {
			verify(asMock);
		}
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
		YearMonth period = YearMonth.now().minusMonths(1);
		initMock(period, getTestUser(), getTestIncomeStatement(period));		
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(period);
		
		assertDefaultValues(period, dto);
		assertAreEqual(BigDecimal.ZERO, dto.getRevenue19());
		assertAreEqual(BigDecimal.ZERO, dto.getRevenue19tax());
		assertNull(dto.getRevenue7());
		assertNull(dto.getRevenue7tax());
		assertNull(dto.getInputTax());
		assertAreEqual(BigDecimal.ZERO, dto.getTaxSum());
	}
		
	/**
	 * Tests {@link ElsterDTOBuilder#createDTO(java.time.YearMonth)}.
	 * 
	 * Standard test, address and revenue data need to be properly adapted.
	 */
	@Test
	public void testCreateDTO() {
		YearMonth period = YearMonth.now().minusMonths(1);
		initMock(period, getTestUser(), getTestIncomeStatementDefault(period));		
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(period);
		
		assertDefaultValues(period, dto);
		assertDefaultIncomeStatementAmounts(dto);
	}


	
	/**
	 * Tests {@link ElsterDTOBuilder#createDTO(java.time.YearMonth)}.
	 * 
	 * No revenue provided for the period, Kz81 needs to be set to 0, Kz83 must be negative. 
	 */
	@Test
	public void testCreateDTONoRevenue() {
		YearMonth period = YearMonth.now().minusMonths(1);
		User user = getTestUser();
		IncomeStatement is = getTestIncomeStatement(period);
		
		Set<Expense> opex = new HashSet<Expense>();
		addExpense(opex, 167.23, UST_19);
		addExpense(opex, 12.60, UST_19);
		addExpense(opex, 50, null);
		is.getOperatingExpenses().setExpenses(opex);
		
		initMock(period, user, is);		
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(period);
		
		assertDefaultValues(period, dto);
		assertAreEqual(BigDecimal.ZERO, dto.getRevenue19());
		assertAreEqual(BigDecimal.ZERO, dto.getRevenue19tax());
		assertNull(dto.getRevenue7());
		assertNull(dto.getRevenue7tax());
		assertAreEqual(new BigDecimal("34.17"), dto.getInputTax());
		assertAreEqual(new BigDecimal("-34.17"), dto.getTaxSum());		
	}
	
	/**
	 * Tests {@link ElsterDTOBuilder#createDTO(java.time.YearMonth)}.
	 * 
	 * Revenue is provided with both known (19%, 7%) and unknown VAT rates. Ensure that filed output tax contains only
	 * known tax rates.
	 */
	@Test
	public void testCreateDTOUnknownTaxRates() {
		YearMonth period = YearMonth.now().minusMonths(1);
		User user = getTestUser();
		IncomeStatement is = getTestIncomeStatement(period);
		
		Invoice invoice = new Invoice();
		invoice.setInvoicePositions(new ArrayList<InvoicePosition>());
		addInvoicePosition(invoice.getInvoicePositions(), 75, 123, UST_19);
		addInvoicePosition(invoice.getInvoicePositions(), 121.5, 66.87, UST_7);
		addInvoicePosition(invoice.getInvoicePositions(), 113.75, 1, BOGUS_TAXRATE);
		is.getRevenue().setInvoices(Collections.singletonList(invoice));
		
		Set<Expense> opex = new HashSet<Expense>();
		addExpense(opex, 167.23, UST_19);
		addExpense(opex, 456.48, UST_19);
		addExpense(opex, 77.42, UST_19);
		addExpense(opex, 235.98, UST_19);
		addExpense(opex, 12.60, UST_7);
		addExpense(opex, 50, null);
		is.getOperatingExpenses().setExpenses(opex);
		
		initMock(period, user, is);		
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(period);
		
		assertDefaultValues(period, dto);
		assertAreEqual(new BigDecimal("9225"), dto.getRevenue19());
		assertAreEqual(new BigDecimal("1752.75"), dto.getRevenue19tax());
		assertAreEqual(new BigDecimal("8124"), dto.getRevenue7());
		assertAreEqual(new BigDecimal("568.68"), dto.getRevenue7tax());
		assertAreEqual(new BigDecimal("178.93"), dto.getInputTax());
		assertAreEqual(new BigDecimal("2142.5"), dto.getTaxSum());
	}
	
	/**
	 * Tests {@link ElsterDTOBuilder#adaptToPeriod(de.tfsw.accounting.elster.ElsterDTO, java.time.YearMonth)}.
	 * 
	 * Address data must remain untouched, while amounts must be adapted.
	 */
	@Test
	public void testAdaptToPeriod() {

		// create an "original" DTO - the values really don't matter, they just need to be completely different from
		// the default test data
		ElsterDTOImpl dto = new ElsterDTOImpl(YearMonth.now().minusMonths(2));
		dto.setCompanyCity("Up The Whazoo");
		dto.setCompanyCountry("Milkandhoney");
		dto.setCompanyEmail("foo@bar.com");
		dto.setCompanyName("Hit & Sunk Ltd.");
		dto.setCompanyPhone("+99 23 34 45 949");
		dto.setCompanyPostCode("Zippedizip");
		dto.setCompanyStreetAddendum("More to Add");
		dto.setCompanyStreetName("Up your Alley");
		dto.setCompanyStreetNumber("123");
		dto.setCompanyTaxNumberOrig("98765/12345");
		dto.setFinanzAmtBL(Bundesland.THUERINGEN);
		dto.setUserFirstName("Holy");
		dto.setUserLastName("Shizzle");
		BigDecimal bd = new BigDecimal("123.45");
		dto.setInputTax(bd);
		dto.setRevenue19(bd);
		dto.setRevenue19tax(bd);
		dto.setRevenue7(bd);
		dto.setRevenue7tax(bd);
		dto.setTaxSum(bd);
		dto.setTimeFrameMonth("Never");
		dto.setTimeFrameYear("Ever");
		
		final Set<String> validProperties  = new HashSet<String>();
		validProperties.add("inputTax");
		validProperties.add("revenue19");
		validProperties.add("revenue19tax");
		validProperties.add("revenue7");
		validProperties.add("revenue7tax");
		validProperties.add("taxSum");
		validProperties.add("timeFrameMonth");
		validProperties.add("timeFrameYear");
		validProperties.add("filingPeriod");
		validProperties.add("creationDate");
		
		dto.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!validProperties.contains(evt.getPropertyName())) {
					fail("DTO Builder should not have accessed " + evt.getPropertyName());
				}
			}
		});
		
		// We have to init the mock ourselves, because the user data will not be queried by the dto builder
		YearMonth lastMonth = YearMonth.now().minusMonths(1);
		asMock = createMock(AccountingService.class);
		IncomeStatement is = getTestIncomeStatementDefault(YearMonth.now().minusMonths(1));
		expect(asMock.getIncomeStatement(TimeFrame.of(lastMonth))).andReturn(is);
		
		replay(asMock);
		
		ElsterDTO adapted = new ElsterDTOBuilder(asMock).adaptToPeriod(dto, lastMonth);
		
		assertCreationDate(adapted);
		assertTimeFrames(lastMonth, adapted);
		assertDefaultIncomeStatementAmounts(adapted);
	}
	
	/**
	 * Tests {@link ElsterDTOBuilder#adaptToPeriod(de.tfsw.accounting.elster.ElsterDTO, java.time.YearMonth)}.
	 * 
	 * Tries to adapt an unknown {@link ElsterDTO} implementation - ensure that the returned DTO is not equal to the
	 * supplied one, and that it is of class {@link ElsterDTOImpl}.
	 */
	@Test
	public void testAdaptToPeriodUnknownClass() {
		UnknownElsterDTOImpl impl = new UnknownElsterDTOImpl();
		impl.setCompanyName("Some Funky Name");
		impl.setUserFirstName("FirstNameDude");
		impl.setUserLastName("UserLastNameDude");
		
		YearMonth period = YearMonth.now().minusMonths(1);
		
		initMock(period, getTestUser(), getTestIncomeStatementDefault(period));
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).adaptToPeriod(impl, period);
		
		assertTrue(dto instanceof ElsterDTOImpl);
		assertNotEquals(impl, dto);
		assertDefaultValues(period, dto);
		assertDefaultIncomeStatementAmounts(dto);
	}
	
	/**
	 * Tests conversion of names from {@link User} to {@link ElsterDTO}.
	 */
	@Test
	public void testNameConversionSingleName() {
		YearMonth period = YearMonth.now().minusMonths(1);
		User user = getTestUser();
		user.setName("Singlename");
		
		initMock(period, user, getTestIncomeStatement(period));		
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(period);
		
		assertEquals(user.getName(), dto.getCompanyName());
		assertNull(dto.getUserFirstName());
		assertNull(dto.getUserLastName());
	}
	
	/**
	 * Tests conversion of names from {@link User} to {@link ElsterDTO}.
	 */
	@Test
	public void testNameConversionMultipleNames() {
		YearMonth period = YearMonth.now().minusMonths(1);
		User user = getTestUser();
		user.setName("More Than Three Names");
		
		initMock(period, user, getTestIncomeStatement(period));		
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(period);
		
		assertNull(dto.getCompanyName());
		assertNull(dto.getUserFirstName());
		assertNull(dto.getUserLastName());
	}
	
	/**
	 * Tests conversion of street address data from {@link Address} to {@link ElsterDTO}.
	 */
	@Test
	public void testStreetAddressConversion() {
		YearMonth period = YearMonth.now().minusMonths(1);
		User user = getTestUser();
		user.getAddress().setStreet("Street With House Number 1");
		
		initMock(period, user, getTestIncomeStatement(period));		
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(period);
		
		assertEquals("Street With House Number", dto.getCompanyStreetName());
		assertEquals("1", dto.getCompanyStreetNumber());
		assertNull(dto.getCompanyStreetAddendum());
	}

	/**
	 * Tests conversion of street address data from {@link Address} to {@link ElsterDTO}.
	 */
	@Test
	public void testStreetAddressWithAddendumConversion() {
		YearMonth period = YearMonth.now().minusMonths(1);
		User user = getTestUser();
		user.getAddress().setStreet("WithAddendum 123a");
		
		initMock(period, user, getTestIncomeStatement(period));		
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(period);
		
		assertEquals("WithAddendum", dto.getCompanyStreetName());
		assertEquals("123", dto.getCompanyStreetNumber());
		assertEquals("a", dto.getCompanyStreetAddendum());
	}

	/**
	 * Tests conversion of street address data from {@link Address} to {@link ElsterDTO}.
	 */
	@Test
	public void testStreetAddressWithAddendumConversion2() {
		YearMonth period = YearMonth.now().minusMonths(1);
		User user = getTestUser();
		user.getAddress().setStreet("WithAddendum 64/2");
		
		initMock(period, user, getTestIncomeStatement(period));		
		
		ElsterDTO dto = new ElsterDTOBuilder(asMock).createDTO(period);
		
		assertEquals("WithAddendum", dto.getCompanyStreetName());
		assertEquals("64", dto.getCompanyStreetNumber());
		assertEquals("2", dto.getCompanyStreetAddendum());
	}
	
	/**
	 * 
	 * @param period
	 * @param user
	 * @param is
	 */
	private void initMock(YearMonth period, User user, IncomeStatement is) {
		asMock = createMock(AccountingService.class);
		expect(asMock.getCurrentUser()).andReturn(user);
		
		expect(asMock.getIncomeStatement(is.getTimeFrame())).andReturn(is);
		
		replay(asMock);
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
	 * @param period
	 * @param dto
	 */
	private void assertTimeFrames(YearMonth period, ElsterDTO dto) {
		assertEquals(period, dto.getFilingPeriod());
		assertEquals(Integer.toString(period.getYear()), dto.getTimeFrameYear());
		assertEquals(String.format("%02d", period.getMonthValue()), dto.getTimeFrameMonth());
	}
	
	/**
	 * 
	 * @param user
	 * @param dto
	 */
	private void assertTaxNumbers(User user, ElsterDTO dto) {
		assertEquals(user.getTaxNumber(), dto.getCompanyTaxNumberOrig());
		if (dto.getFinanzAmtBL() == null) {
			assertNull(dto.getCompanyTaxNumberGenerated());
		} else {
			// no need to check the actual value, that's done in the tax number generator tests
			assertNotNull(dto.getCompanyTaxNumberGenerated());
		}
	}
	
	/**
	 * 
	 * @param address
	 * @param dto
	 */
	private void assertDefaultAddressData(Address address, ElsterDTO dto) {
		assertEquals(address.getCity(), dto.getCompanyCity());
		assertEquals(address.getEmail(), dto.getCompanyEmail());
		assertEquals(address.getPhoneNumber(), dto.getCompanyPhone());
		assertEquals(address.getPostalCode(), dto.getCompanyPostCode());
		assertEquals("JUnitStreet", dto.getCompanyStreetName());
		assertEquals("27", dto.getCompanyStreetNumber());
		assertEquals("Deutschland", dto.getCompanyCountry());
		assertNull(dto.getCompanyStreetAddendum());
		
	}
	
	/**
	 * This works only if the source data was {@link #getTestIncomeStatementDefault(YearMonth)}.
	 * 
	 * @param dto
	 */
	private void assertDefaultIncomeStatementAmounts(ElsterDTO dto) {
		assertAreEqual(new BigDecimal("9338"), dto.getRevenue19());
		assertAreEqual(new BigDecimal("1774.22"), dto.getRevenue19tax());
		assertNull(dto.getRevenue7());
		assertNull(dto.getRevenue7tax());
		assertAreEqual(new BigDecimal("34.17"), dto.getInputTax());
		assertAreEqual(new BigDecimal("1740.05"), dto.getTaxSum());
	}
	
	/**
	 * 
	 * @param period
	 * @param dto
	 */
	private void assertDefaultValues(YearMonth period, ElsterDTO dto) {
		assertCreationDate(dto);
		assertTimeFrames(period, dto);
		User user = getTestUser();
		assertTaxNumbers(user, dto);
		assertDefaultAddressData(user.getAddress(), dto);
	}
	
	/**
	 * 
	 * @param expected
	 * @param actual
	 */
	private void assertAreEqual(BigDecimal expected, BigDecimal actual) {
		if (expected == null) {
			assertNull(actual);
		} else {
			assertNotNull(actual);
			assertEquals("Expected: " + expected.toString() + ", actual: " + actual.toString(), 0, actual.compareTo(expected));
		}
		
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
	
	/**
	 * 
	 * @param period
	 * @return
	 */
	private IncomeStatement getTestIncomeStatement(YearMonth period) {
		IncomeStatement is = new IncomeStatement();
		is.setTimeFrame(TimeFrame.of(period));
		Revenue revenue = new Revenue();
		revenue.setInvoices(new ArrayList<Invoice>());		
		is.setRevenue(revenue);
		is.setOperatingExpenses(new ExpenseCollection());
		
		return is;
	}
	
	/**
	 * 
	 * @param period
	 * @return
	 */
	private IncomeStatement getTestIncomeStatementDefault(YearMonth period) {
		IncomeStatement is = getTestIncomeStatement(period);
		Invoice invoice = new Invoice();
		invoice.setInvoicePositions(new ArrayList<InvoicePosition>());
		addInvoicePosition(invoice.getInvoicePositions(), 75, 123, UST_19);
		addInvoicePosition(invoice.getInvoicePositions(), 113.75, 1, UST_19);
		is.getRevenue().setInvoices(Collections.singletonList(invoice));
		
		Set<Expense> opex = new HashSet<Expense>();
		addExpense(opex, 167.23, UST_19);
		addExpense(opex, 12.60, UST_19);
		addExpense(opex, 50, null);
		is.getOperatingExpenses().setExpenses(opex);
		return is;
	}
	
	/**
	 * 
	 * @param list
	 * @param ppu
	 * @param amount
	 * @param rate
	 */
	private void addInvoicePosition(List<InvoicePosition> list, double pricePerUnit, double quantity, TaxRate rate) {
		InvoicePosition ip = new InvoicePosition();
		ip.setPricePerUnit(new BigDecimal(pricePerUnit));
		ip.setQuantity(new BigDecimal(quantity));
		ip.setTaxRate(rate);
		list.add(ip);
	}
	
	/**
	 * 
	 * @param expenses
	 * @param netValue
	 * @param rate
	 */
	private void addExpense(Set<Expense> expenses, double netValue, TaxRate rate) {
		Expense e = new Expense();
		e.setNetAmount(new BigDecimal(netValue));
		e.setTaxRate(rate);
		expenses.add(e);
	}
	
	/**
	 * Some dummy impl, used by {@link ElsterDTOBuilderTest#testAdaptToPeriodUnknownClass()}.
	 * Doesn't do anything.
	 *
	 */
	@SuppressWarnings("serial")
	private class UnknownElsterDTOImpl extends ElsterDTO {
		/**
		 * @see de.tfsw.accounting.elster.ElsterDTO#getFilingPeriod()
		 */
		@Override
		public YearMonth getFilingPeriod() {
			return null;
		}

		/**
		 * @see de.tfsw.accounting.elster.ElsterDTO#generateTaxNumber()
		 */
		@Override
		protected String generateTaxNumber() {
			return null;
		}
	}
}
