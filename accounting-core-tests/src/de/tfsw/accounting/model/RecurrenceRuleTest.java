package de.tfsw.accounting.model;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class RecurrenceRuleTest {
	
	@Test
	public void testDefaultValues() {
		RecurrenceRule rule = new RecurrenceRule();
		assertEquals(Frequency.MONTHLY, rule.getFrequency());
		assertEquals(1, rule.getInterval());
		assertNull(rule.getCount());
		assertNull(rule.getUntil());
	}
	
	/**
	 * Checks that only setting either {@link RecurrenceRule#setCount(int)} or 
	 * {@link RecurrenceRule#setUntil(java.time.LocalDate)} is possible. 
	 */
	@Test
	public void testEitherUntilOrCount() {
		RecurrenceRule rule = new RecurrenceRule();
		assertNull("Until should be null", rule.getUntil());
		assertNull("Count should be null", rule.getCount());
		
		LocalDate until = LocalDate.now().plusYears(5); // just any date in the future...
		rule.setUntil(until);
		assertEquals("End date is wrong", until, rule.getUntil());
		assertNull("Count should be null", rule.getCount());
		
		final Integer count = 10;
		rule.setCount(count);
		assertNull("Until should be null", rule.getUntil());
		assertEquals("Count is wrong", count, rule.getCount());
		
		// and switch back...
		rule.setUntil(until);
		assertEquals("End date is wrong", until, rule.getUntil());
		assertNull("Count should be null", rule.getCount());
	}
	
	@Test
	public void testMethodParamAssertions() {
		RecurrenceRule rule = new RecurrenceRule();
		
		try {
			rule.setCount(-1);
			fail("Setting count to a negative value should have raised exception");
		} catch (IllegalArgumentException e) {
			// this is what we want
			assertNull(rule.getCount());
		}
				
		try {
			rule.setInterval(-1);
			fail("Setting interval to a negative value should have raised exception");
		} catch (IllegalArgumentException e) {
			assertEquals("Default value for interval should be one!", 1, rule.getInterval());
		}
		
		try {
			rule.setFrequency(Frequency.YEARLY);
			rule.setFrequency(null);
			fail("Setting rule to null should have raised an exception");
		} catch (IllegalArgumentException e) {
			assertEquals(Frequency.YEARLY, rule.getFrequency());
		}
	}
	
	@Test
	public void testEquals() {
		RecurrenceRule r1 = new RecurrenceRule();
		RecurrenceRule r2 = new RecurrenceRule();
		assertEquals(r1, r2);
		
		r1.setFrequency(Frequency.DAILY);
		assertNotEquals(r1, r2);
		
		r2.setFrequency(Frequency.DAILY);
		assertEquals(r1, r2);
		
		r1.setCount(5);
		r2.setCount(5);
		
		r1.setInterval(10);
		r2.setInterval(10);
		assertEquals(r1, r2);
		
		r2.setCount(6);
		assertNotEquals(r1, r2);
		
		r2.setCount(null);
		assertNotEquals(r1, r2);
		
		r2.setCount(r1.getCount());
		r1.setInterval(9);
		assertNotEquals(r1, r2);
		
		r2 = null;
		assertNotEquals(r1, r2);
	}
}
