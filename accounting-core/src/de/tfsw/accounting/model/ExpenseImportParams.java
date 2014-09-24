/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.model;

import java.io.Serializable;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.Messages;
import de.tfsw.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class ExpenseImportParams implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 6259396423161494789L;
    
    public static final String DEFAULT_SEPARATOR = Constants.SEMICOLON; //$NON-NLS-1$
    
    /** 
     *
     */
    public enum DecimalMark {
    	DOT(Constants.DOT), COMMA(Constants.COMMA);
    	
    	private String value;
    	
    	private DecimalMark(String value) {
    		this.value = value;
    	}
    	
    	public String getValue() {
    		return value;
    	}
    }
    
    /**
     *
     */
    public enum DateFormatPattern {
    	
    	DMY(Messages.DateFormatPattern_DMY, 0, 1, 2), 
    	MDY(Messages.DateFormatPattern_MDY, 1, 0, 2), 
    	YMD(Messages.DateFormatPattern_YMD, 2, 1, 0), 
    	MYD(Messages.DateFormatPattern_MYD, 2, 0, 1), 
    	DYM(Messages.DateFormatPattern_DYM, 0, 2, 1), 
    	YDM(Messages.DateFormatPattern_YDM, 1, 2, 0);
    	
    	private String translation;
    	private int dayOrder;
    	private int monthOrder;
    	private int yearOrder;
    	
    	private DateFormatPattern(String translation, int day, int month, int year) {
    		this.translation = translation;
    		this.dayOrder = day;
    		this.monthOrder = month;
    		this.yearOrder = year;
    	}
    	
		public String getTranslation() {
			return translation;
		}
		
		public int getDayOrder() {
			return dayOrder;
		}
		
		public int getMonthOrder() {
			return monthOrder;
		}
		
		public int getYearOrder() {
			return yearOrder;
		}
		
    }
    
    private String separator;
    
    private DecimalMark decimalMark;
    
    private DateFormatPattern dateFormatPattern;

	/**
     * 
     */
    public ExpenseImportParams() {
	    this.separator = DEFAULT_SEPARATOR;
	    if (Constants.COMMA.equals(FormatUtil.getDecimalMark())) {
	    	this.decimalMark = DecimalMark.COMMA;
	    } else {
	    	this.decimalMark = DecimalMark.DOT;
	    }
	    this.dateFormatPattern = DateFormatPattern.DMY;
    }

	/**
     * @return the separator
     */
    public String getSeparator() {
    	return separator;
    }

	/**
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
    	this.separator = separator;
    }

	/**
	 * @return the decimalMark
	 */
	public DecimalMark getDecimalMark() {
		return decimalMark;
	}

	/**
	 * @param decimalMark the decimalMark to set
	 */
	public void setDecimalMark(DecimalMark decimalMark) {
		this.decimalMark = decimalMark;
	}

	/**
	 * @return the dateFormatPattern
	 */
	public DateFormatPattern getDateFormatPattern() {
		return dateFormatPattern;
	}

	/**
	 * @param dateFormatPattern the dateFormatPattern to set
	 */
	public void setDateFormatPattern(DateFormatPattern dateFormatPattern) {
		this.dateFormatPattern = dateFormatPattern;
	}
}
