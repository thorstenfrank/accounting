package de.tfsw.accounting.model;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class RecurringExpenseTest {

	@Test
	public void testDefaultValues() {
		RecurringExpense re = new RecurringExpense();
		
		assertEquals(LocalDate.now(), re.getFirstApplication());
		assertNotNull(re.getRule());
		assertEquals(new RecurrenceRule(), re.getRule());
	}
	
	@Test
	public void testMethodParamChecking() {
		RecurringExpense re = new RecurringExpense();
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
		RecurringExpense re = new RecurringExpense();
		assertNull(re.apply());
	}
}
