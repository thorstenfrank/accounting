package de.tfsw.accounting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

import de.tfsw.accounting.BaseTestFixture;
import de.tfsw.accounting.model.RecurrenceRule.Frequency;

public class RecurringExpenseTest extends BaseTestFixture {

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
		RecurringExpense re = buildTestInstance();
		
		assertEquals(LocalDate.now(), re.getFirstApplication());
		assertNotNull(re.getRule());
		assertEquals(new RecurrenceRule(), re.getRule());
	}
	
	/**
	 * 
	 */
	@Test
	public void testMethodParamChecking() {
		RecurringExpense re = buildTestInstance();
		RecurrenceRule rule = re.getRule();
		
		try {
			re.setRule(null);
			fail("Setting rule to null should have raised exception!");
		} catch (IllegalArgumentException e) {
			assertEquals(rule, re.getRule());
		}
	}
	
	@Test
	public void testApply() {
		RecurringExpense re = buildTestInstance();
		re.setActive(false);
		assertNull(re.apply());
		
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
	 * 
	 */
	@Test
	public void testAutomaticInactive() {
		RecurringExpense re = buildTestInstance();
		
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
		RecurringExpense re = buildTestInstance();
		LocalDate expected = LocalDate.now().minusMonths(1);
		re.setFirstApplication(expected);
		
		expected = expected.plusDays(1);
		re.getRule().setFrequency(Frequency.DAILY);
		assertEquals(expected, re.getNextApplication());
	}
	
	/**
	 * 
	 * @return
	 */
	private RecurringExpense buildTestInstance() {
		RecurringExpense re = new RecurringExpense();
		re.setActive(true);
		re.setCategory(TEST_CATEGORY);
		re.setDescription(TEST_DESCRIPTION);
		re.setExpenseType(TEST_TYPE);
		re.setNetAmount(TEST_AMOUNT);
		re.setTaxRate(TEST_TAX_RATE);
		return re;
	}
}
