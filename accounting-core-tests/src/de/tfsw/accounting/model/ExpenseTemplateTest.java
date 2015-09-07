package de.tfsw.accounting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.Test;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.BaseTestFixture;

public class ExpenseTemplateTest extends BaseTestFixture {

	private static final TaxRate TEST_TAX_RATE = new TaxRate("ShortName", "LongName", new BigDecimal("0.15"), true);
	private static final BigDecimal TEST_AMOUNT = new BigDecimal("14.99");
	private static final ExpenseType TEST_TYPE = ExpenseType.OTHER;
	private static final String TEST_DESCRIPTION = "JUnit_Test_Description";
	private static final String TEST_CATEGORY = "JUnit_Test_Category";

	/**
	 * 
	 */
	@Test
	public void testDefaultValues() {
		ExpenseTemplate re = buildTestInstance();
		
		assertEquals(LocalDate.now(), re.getFirstApplication());
		assertNotNull(re.getRule());
		assertEquals(new RecurrenceRule(), re.getRule());
	}
	
	/**
	 * 
	 */
	@Test
	public void testMethodParamChecking() {
		ExpenseTemplate re = buildTestInstance();
		RecurrenceRule rule = re.getRule();
		
		try {
			re.setRule(null);
			fail("Setting rule to null should have raised exception!");
		} catch (IllegalArgumentException e) {
			assertEquals(rule, re.getRule());
		}
		
		LocalDate firstAppl = re.getFirstApplication();
		try {
			re.setFirstApplication(null);
			fail("Setting first application to null should have raised exception!");
		} catch (IllegalArgumentException e) {
			assertEquals(firstAppl, re.getFirstApplication());
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testApply() {
		ExpenseTemplate re = buildTestInstance();
		
		// turn off the master switch
		// --> no expenses should be created regardless of the RecurrenceRule
		re.setActive(false);
		assertNull(re.getNextApplication());
		assertNull(re.apply());
		
		// turn it on again
		re.setActive(true);
		
		Expense expense = re.apply();
		assertNotNull(expense);
		
		assertAreEqual(TEST_AMOUNT, expense.getNetAmount());
		assertEquals(TEST_CATEGORY, expense.getCategory());
		assertEquals(TEST_DESCRIPTION, expense.getDescription());
		assertEquals(TEST_TAX_RATE, expense.getTaxRate());
		assertEquals(TEST_TYPE, expense.getExpenseType());
	}
	
	/**
	 * Tests creating multiple expenses until the current date is reached.
	 */
	@Test
	public void testApplyInThePast() {
		ExpenseTemplate re = buildTestInstance();
		re.getRule().setFrequency(Frequency.DAILY);
		re.setFirstApplication(LocalDate.now().minusDays(5));
		
		Set<LocalDate> outstanding = re.getOutstandingApplications();
		assertEquals(6, outstanding.size());
		// just checking the ordering...
		assertEquals(re.getFirstApplication(), outstanding.iterator().next());
		
		for (int i = 5; i >= 0; i--) {
			assertEquals(i + 1, re.getNumberOfOutstandingApplications());
			Expense exp = re.apply();
			assertNotNull(exp);
			assertEquals(LocalDate.now().minusDays(i), exp.getPaymentDate());
			assertEquals(exp.getPaymentDate(), re.getLastApplication());
		}
		assertEquals(0, re.getNumberOfOutstandingApplications());
		assertTrue(re.isActive());
		assertNull(re.apply());
		assertEquals(LocalDate.now().plusDays(1), re.getNextApplication());
	}
	
	/**
	 * 
	 */
	@Test
	public void testAutomaticInactive() {
		ExpenseTemplate re = buildTestInstance();
		
		// test count-based limits
		re.setActive(true);
		re.getRule().setCount(2);
		assertTrue(re.isActive());
		assertNotNull(re.apply());
		assertTrue(re.isActive());
		assertNotNull(re.apply());
		assertFalse(re.isActive());
		assertNull(re.apply());
		
		// test date-based limits
		re.getRule().setUntil(LocalDate.now().plusMonths(1));
		re.setActive(true);
		assertNotNull(re.apply());
		assertTrue(re.isActive());
		re.getRule().setUntil(LocalDate.now().minusMonths(1));
		assertNull(re.apply());
		assertFalse(re.isActive());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetNextApplication() {
		ExpenseTemplate re = buildTestInstance();
		LocalDate expected = LocalDate.now().minusMonths(1);
		re.setFirstApplication(expected);
		
		re.getRule().setFrequency(Frequency.DAILY);
		assertEquals(expected, re.getNextApplication());
		
		LocalDate future = LocalDate.now().plusMonths(6).plusDays(17);
		re.setFirstApplication(future);
		assertEquals(future, re.getNextApplication());
	}
	
	/**
	 * 
	 */
	@Test
	public void testIsApplicable() {
		ExpenseTemplate re = buildTestInstance();
		assertTrue(re.isApplicable());
		
		re.setActive(false);
		assertFalse(re.isApplicable());
		
		re.setActive(true);
		re.setFirstApplication(LocalDate.now().plusDays(5));
		assertFalse(re.isApplicable());
		
		re.setFirstApplication(LocalDate.now().minusDays(10));
		assertTrue(re.isApplicable());
		
		re.setLastApplication(LocalDate.now().minusDays(5));
		assertFalse(re.isApplicable());
		re.getRule().setFrequency(Frequency.DAILY);
		assertTrue(re.isApplicable());
	}
	
	/**
	 * 
	 */
	@Test
	public void testDescriptionPlaceholders() {
		ExpenseTemplate et = buildTestInstance();
		et.setDescription("Some {month} test");
		et.setFirstApplication(LocalDate.of(2015, 1, 24));
		
		Expense e = et.apply();
		assertEquals("Some 01 test", e.getDescription());
		
		et.setDescription("{year} changed description");
		e = et.apply();
		assertEquals("15 changed description", e.getDescription());
		
		et.setDescription("Some Expense {month}/{year}");
		e = et.apply();
		assertEquals("Some Expense 03/15", e.getDescription());
		
		et.setDescription("heppes_{year_long}{month}");
		e = et.apply();
		assertEquals("heppes_201504", e.getDescription());
	}
	
	/**
	 * 
	 */
	@Test
	public void testApplyWithParam() {
		ExpenseTemplate et = buildTestInstance();
		
		try {
			et.apply(LocalDate.now().plusDays(1));
			fail("Tried to apply template in the future, should have caught exception!");
		} catch (AccountingException e) {
			// good...
		}
		
		LocalDate now = LocalDate.now(); 
		
		et.setFirstApplication(now.minusMonths(2));
		
		Expense e = et.apply(now.minusMonths(1));
		assertEquals(e.getPaymentDate(), et.getLastApplication());
		
		assertNull(et.apply(null));
	}
	
	/**
	 * 
	 * @return
	 */
	private ExpenseTemplate buildTestInstance() {
		ExpenseTemplate re = new ExpenseTemplate();
		re.setActive(true);
		re.setCategory(TEST_CATEGORY);
		re.setDescription(TEST_DESCRIPTION);
		re.setExpenseType(TEST_TYPE);
		re.setNetAmount(TEST_AMOUNT);
		re.setTaxRate(TEST_TAX_RATE);
		return re;
	}	
}
