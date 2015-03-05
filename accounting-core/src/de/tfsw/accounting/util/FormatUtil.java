/*
 *  Copyright 2010 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.Constants;
import de.tfsw.accounting.Messages;

/**
 * A utility class for formatting and parsing numbers, currencies and dates in a locale-sensitive way. 
 * 
 * @author thorsten frank
 */
public final class FormatUtil {

	/**
	 * 
	 */
    private static final Logger LOG = Logger.getLogger(FormatUtil.class);

    /**
     * 
     */
    private static final Map<Locale, NumberFormat> CURRENCY_FORMATTER_MAP = new HashMap<Locale, NumberFormat>();
    
    /**
     * 
     */
    private static final Map<Locale, NumberFormat> NUMBER_FORMATTER_MAP = new HashMap<Locale, NumberFormat>();
    
    /**
     * 
     */
    private static final Map<Locale, NumberFormat> PERCENT_FORMATTER_MAP = new HashMap<Locale, NumberFormat>();
        
    /**
     * 
     */
    private static final Map<Locale, DateTimeFormatter> DATE_FORMATTER_MAP = new HashMap<Locale, DateTimeFormatter>();
    
    /**
     * 
     */
    private FormatUtil() {
    	
    }
    
    /**
     * 
     * @param locale
     * @return
     */
    private static NumberFormat getCurrencyFormatter(Locale locale) {
    	
    	if (!CURRENCY_FORMATTER_MAP.containsKey(locale)) {
    		DecimalFormat currencyFormatter = (DecimalFormat)DecimalFormat.getCurrencyInstance(locale);
    		currencyFormatter.setGroupingUsed(true);
    		currencyFormatter.setRoundingMode(RoundingMode.HALF_UP);
    		currencyFormatter.setMaximumFractionDigits(2);
    		currencyFormatter.setMinimumFractionDigits(2);
    		currencyFormatter.setParseBigDecimal(true);
    		CURRENCY_FORMATTER_MAP.put(locale, currencyFormatter);
    	}
    	
    	return CURRENCY_FORMATTER_MAP.get(locale);
    }
    
    /**
     * 
     * @param locale
     * @return
     */
    private static NumberFormat getNumberFormatter(Locale locale) {
    	if (!NUMBER_FORMATTER_MAP.containsKey(locale)) {
    		DecimalFormat format = (DecimalFormat)DecimalFormat.getNumberInstance(locale);
            format.setGroupingUsed(true);
            format.setMaximumFractionDigits(2);
            format.setRoundingMode(RoundingMode.HALF_UP);
            format.setParseBigDecimal(true);
            NUMBER_FORMATTER_MAP.put(locale, format);
    	}
        
        return NUMBER_FORMATTER_MAP.get(locale);
    }
    
    /**
     * 
     * @param locale
     * @return
     */
    private static NumberFormat getPercentFormatter(Locale locale) {
    	if (!PERCENT_FORMATTER_MAP.containsKey(locale)) {
    		DecimalFormat format = (DecimalFormat)DecimalFormat.getPercentInstance(locale);
            format.setMaximumFractionDigits(3);
            format.setMinimumFractionDigits(0);
            format.setRoundingMode(RoundingMode.HALF_UP);
            format.setParseBigDecimal(true);
            PERCENT_FORMATTER_MAP.put(locale, format);
    	}
        
    	return PERCENT_FORMATTER_MAP.get(locale);
    }
    
    /**
     * 
     * @param locale
     * @return
     */
    private static DateTimeFormatter getDateFormatter(Locale locale) {
    	if (!DATE_FORMATTER_MAP.containsKey(locale)) {
    		DATE_FORMATTER_MAP.put(locale, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale));
    	}
    	return DATE_FORMATTER_MAP.get(locale);
    }
    
    /**
     * Formats a decimal value into a string including a currency symbol using the supplied {@link Locale}.
     * 
     * <p>
     * The precision of the formatted value is limited to 2 decimal digits using {@link RoundingMode#HALF_UP}. The
     * formatted string uses digit grouping. 
     * </p>
     * 
     * @param locale the {@link Locale} to use for formatting
     * @param value the decimal value to format into a currency
     * @return the formatted currency value
     * @see DecimalFormat#getCurrencyInstance(Locale)
     * @see DecimalFormat#format(Object)
     * @see BigDecimal#setScale(int, RoundingMode)
     */
    public static String formatCurrency(Locale locale, BigDecimal value) {
    	if (value == null) {
    		return Constants.HYPHEN;
    	}
        return getCurrencyFormatter(locale).format(value.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Formats a decimal value into a locale-sensitive string including a currency symbol using the default {@link Locale}.
     * 
     * <p>This is the equivalent of {@link #formatCurrency(Locale.getDefault(), BigDecimal)}.</p>
     * 
     * @param value the decimal value to format
     * @return the formatted currency value
     */
    public static String formatCurrency(BigDecimal value) {
    	return formatCurrency(Locale.getDefault(), value);
    }
    
    /**
     * Formats a decimal value into a locale-sensitive string.
     *
     * <p>
     * The precision of the formatted value is limited to 2 decimal digits using {@link RoundingMode#HALF_UP}. The
     * formatted string uses digit grouping. 
     * </p>
     * 
     * @param locale the {@link Locale} to use for formatting
     * @param value the decimal value to format
     * @return the formatted decimal value
     * @see DecimalFormat#getNumberInstance(Locale)
     * @see DecimalFormat#format(Object)
     * @see BigDecimal#setScale(int, RoundingMode)
     */
    public static String formatDecimalValue(Locale locale, BigDecimal value) {
        return getNumberFormatter(locale).format(value.setScale(2, RoundingMode.HALF_UP));
    }
    
    /**
     * Formats a decimal value into a locale-sensitive string using the default locale.
     * 
     * <p>This is the equivalent of {@link #formatDecimalValue(Locale.getDefault(), BigDecimal)}.</p>
     * 
     * @param value the decimal value to format
     * @return the formatted decimal value
     */
    public static String formatDecimalValue(BigDecimal value) {
    	return formatDecimalValue(Locale.getDefault(), value);
    }

    /**
     * 
     * @param locale
     * @param value
     * @return
     * @throws AccountingException if the value cannot be parsed into a decimal value
     */
    public static BigDecimal parseDecimalValue(Locale locale, String value) {
    	try {
			return (BigDecimal)getNumberFormatter(locale).parse(value);
		} catch (ParseException e) {
			LOG.error(String.format("Unparseable decimal value [%s] using locale [%s]", value, locale.toString()), e); //$NON-NLS-1$
			throw new AccountingException(Messages.bind(Messages.FormatUtil_errorParsingDecimal, value), e);
		}
    }
    
    /**
     * 
     * @param value
     * @return
     * @throws AccountingException if the value cannot be parsed into a decimal value
     */
    public static BigDecimal parseDecimalValue(String value) {
    	return parseDecimalValue(Locale.getDefault(), value);
    }
    
    /**
     *
     * @param locale
     * @param value
     * @return
     */
    public static String formatPercentValue(Locale locale, BigDecimal value) {
        return getPercentFormatter(locale).format(value);
    }

    /**
     * 
     * @param value
     * @return
     */
    public static String formatPercentValue(BigDecimal value) {
    	return formatPercentValue(Locale.getDefault(), value);
    }
    
    /**
     *
     * @param locale
     * @param value
     * @return
     * @throws FormattingException if the supplied value cannot be parsed
     */
    public static BigDecimal parsePercentValue(Locale locale, String value) {
        try {
        	Number numberValue = getPercentFormatter(locale).parse(value);
            return new BigDecimal(numberValue.toString());
        } catch (ParseException e) {
            LOG.error("Unparseable percentage value: " + value, e); //$NON-NLS-1$
            throw new AccountingException(Messages.bind(Messages.FormatUtil_errorParsingPercentage, value), e);
        }
    }
    
    /**
     * 
     * @param value
     * @return
     */
    public static BigDecimal parsePercentValue(String value) {
    	return parsePercentValue(Locale.getDefault(), value);
    }
    
    /**
     * This is equivalent to <code>formatDate(Locale.getDefault(), date)</code>.
     * 
     * @see #formatDate(Locale, LocalDate)
     */
    public static String formatDate(LocalDate date) {
    	return formatDate(Locale.getDefault(), date);
    }
    
    /**
     * 
     * @param locale
     * @param date
     * @return
     */
    public static String formatDate(Locale locale, LocalDate date) {
    	return getDateFormatter(locale).format(date);
    }
    
    /**
     * 
     * @return
     */
    public static String getDecimalMark() {
    	return getDecimalMark(Locale.getDefault());
    }
    
    /**
     * 
     * @param locale
     * @return
     */
    public static String getDecimalMark(Locale locale) {
    	return String.valueOf(((DecimalFormat)getNumberFormatter(locale)).getDecimalFormatSymbols().getDecimalSeparator());
    }
}
