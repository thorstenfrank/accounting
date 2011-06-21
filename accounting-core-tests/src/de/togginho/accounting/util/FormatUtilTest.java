/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import de.togginho.accounting.AccountingException;

/**
 * @author thorsten
 *
 */
public class FormatUtilTest {
    private static final char EURO_SIGN = '\u20ac';
	
    private static final BigDecimal ONE_POINT_ONE_NINE_FOUR = new BigDecimal("1.194");
    private static final BigDecimal ONE_POINT_ONE_EIGHT_SIX = new BigDecimal("1.186");
    private static final BigDecimal ONE_MILLION = new BigDecimal("1000000");
    private static final BigDecimal POINT_ONE_NINE = new BigDecimal("0.19");
    private static final BigDecimal POINT_ZERO_ONE_NINE = new BigDecimal("0.019");
    private static final BigDecimal POINT_ZERO_ZERO_ONE_NINE = new BigDecimal("0.0019");
    
    /**
     * Test method for {@link FormatUtil#formatCurrency(java.util.Locale, java.lang.Double)}.
     */
	@Test
	public void testFormatCurrency() {
		assertEquals("1,19 "+EURO_SIGN, FormatUtil.formatCurrency(Locale.GERMANY, ONE_POINT_ONE_NINE_FOUR));
		assertEquals("1,19 "+EURO_SIGN, FormatUtil.formatCurrency(Locale.GERMANY, ONE_POINT_ONE_EIGHT_SIX));
		assertEquals("1.000.000,00 "+EURO_SIGN, FormatUtil.formatCurrency(Locale.GERMANY, ONE_MILLION));
		assertEquals("$1.19", FormatUtil.formatCurrency(Locale.US, ONE_POINT_ONE_NINE_FOUR));
		assertEquals("$1.19", FormatUtil.formatCurrency(Locale.US, ONE_POINT_ONE_EIGHT_SIX));
		assertEquals("$1,000,000.00", FormatUtil.formatCurrency(Locale.US, ONE_MILLION));
	}

    /**
     * Test method for {@link FormatUtil#formatDecimalValue(java.util.Locale, java.lang.Double)}.
     */
	@Test
	public void testFormatDecimalValue() {
		assertEquals("1,19", FormatUtil.formatDecimalValue(Locale.GERMANY, ONE_POINT_ONE_NINE_FOUR));
		assertEquals("1,19", FormatUtil.formatDecimalValue(Locale.GERMANY, ONE_POINT_ONE_EIGHT_SIX));
		assertEquals("1.000.000", FormatUtil.formatDecimalValue(Locale.GERMANY, ONE_MILLION));
		assertEquals("1.19", FormatUtil.formatDecimalValue(Locale.US, ONE_POINT_ONE_NINE_FOUR));
		assertEquals("1.19", FormatUtil.formatDecimalValue(Locale.US, ONE_POINT_ONE_EIGHT_SIX));
		assertEquals("1,000,000", FormatUtil.formatDecimalValue(Locale.US, ONE_MILLION));
	}

    /**
     * Test method for {@link FormatUtil#formatPercentValue(java.util.Locale, java.lang.Double)}.
     */
	@Test
	public void testFormatPercentValue() {
		assertEquals("19%", FormatUtil.formatPercentValue(Locale.GERMANY, POINT_ONE_NINE));
		assertEquals("19%", FormatUtil.formatPercentValue(Locale.US, POINT_ONE_NINE));
		assertEquals("1,9%", FormatUtil.formatPercentValue(Locale.GERMANY, POINT_ZERO_ONE_NINE));
		assertEquals("1.9%", FormatUtil.formatPercentValue(Locale.US, POINT_ZERO_ONE_NINE));
	}

    /**
     * Test method for {@link FormatUtil#parsePercentValue(java.util.Locale, java.lang.String)}.
     */
	@Test
	public void testParsePercentValue() {
		
		assertEquals(POINT_ONE_NINE, FormatUtil.parsePercentValue(Locale.GERMANY, "19%"));
		assertEquals(POINT_ONE_NINE, FormatUtil.parsePercentValue(Locale.US, "19%"));
		
		assertEquals(POINT_ONE_NINE, FormatUtil.parsePercentValue(Locale.GERMANY, ".19%"));
		assertEquals(POINT_ZERO_ZERO_ONE_NINE, FormatUtil.parsePercentValue(Locale.US, ".19%"));
		
		assertEquals(POINT_ZERO_ZERO_ONE_NINE, FormatUtil.parsePercentValue(Locale.GERMANY, "0,19%"));
		assertEquals(POINT_ONE_NINE, FormatUtil.parsePercentValue(Locale.US, "0,19%"));		
		
		assertEquals(BigDecimal.ONE, FormatUtil.parsePercentValue(Locale.GERMANY, "100%"));
		assertEquals(BigDecimal.ONE, FormatUtil.parsePercentValue(Locale.US, "100%"));
		
		// check parsing error
		
		try {
			FormatUtil.parsePercentValue(Locale.GERMANY, "SomeBogusValue/)");
			fail("Unparseable value should have yielded an exception!");
		} catch (AccountingException e) {
			// TODO use unique error codes?
		}
	}

	@Test
	public void testFormatDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 13);
		cal.set(Calendar.MONTH, 8);
		cal.set(Calendar.YEAR, 2010);
		
		Date date = cal.getTime();
		
		assertEquals("Sep 13, 2010", FormatUtil.formatDate(Locale.US, date));
		assertEquals("13.09.2010", FormatUtil.formatDate(Locale.GERMANY, date));
	}
	
	@Test
	public void testParseDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 13);
		cal.set(Calendar.MONTH, 8);
		cal.set(Calendar.YEAR, 2010);
		
		Calendar compareCal = Calendar.getInstance();
		
		Date date = FormatUtil.parseDate(Locale.US, "Sep 13, 2010");	
		
		assertNotNull(date);
		compareCal.setTime(date);
		assertDatesAreEqual(cal, compareCal);
		
		try {
			date = FormatUtil.parseDate(Locale.US, "9/13/10");
			fail("Unparseable date should have yielded an exception");
		} catch (AccountingException e) {
			// TODO use unique error codes?
		}
		
		date = FormatUtil.parseDate(Locale.GERMANY, "13.09.2010");			
		assertNotNull(date);
		compareCal.setTime(date);
		assertDatesAreEqual(cal, compareCal);
	}
	
	/**
	 * 
	 * @param expected
	 * @param actual
	 */
	private void assertDatesAreEqual(Calendar expected, Calendar actual) {
		assertEquals(expected.get(Calendar.DAY_OF_MONTH), actual.get(Calendar.DAY_OF_MONTH));
		assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
		assertEquals(expected.get(Calendar.YEAR), actual.get(Calendar.YEAR));		
	}
}
