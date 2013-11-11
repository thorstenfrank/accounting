/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.model;

import java.io.Serializable;

import de.togginho.accounting.Constants;
import de.togginho.accounting.Messages;
import de.togginho.accounting.util.FormatUtil;

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
    	
    	DMY(Messages.DateFormatPattern_DMY), 
    	MDY(Messages.DateFormatPattern_MDY), 
    	YMD(Messages.DateFormatPattern_YMD), 
    	MYD(Messages.DateFormatPattern_MYD), 
    	DYM(Messages.DateFormatPattern_DYM), 
    	YDM(Messages.DateFormatPattern_YDM);
    	
    	private String translation;
    	
    	private DateFormatPattern(String translation) {
    		this.translation = translation;
    	}

		/**
		 * @return the translation
		 */
		public String getTranslation() {
			return translation;
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
