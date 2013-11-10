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
    
    public static final String DEFAULT_SEPARATOR = ";"; //$NON-NLS-1$
    
    private String separator;
    
    private String decimalMark;
    
    private String dateFormatPattern;

	/**
     * 
     */
    public ExpenseImportParams() {
	    this.separator = DEFAULT_SEPARATOR;
	    this.decimalMark = FormatUtil.getDecimalMark();
	    this.dateFormatPattern = FormatUtil.getDateFormattingPattern();
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
    public String getDecimalMark() {
    	return decimalMark;
    }

	/**
     * @param decimalMark the decimalMark to set
     */
    public void setDecimalMark(String decimalMark) {
    	this.decimalMark = decimalMark;
    }

	/**
     * @return the dateFormatPattern
     */
    public String getDateFormatPattern() {
    	return dateFormatPattern;
    }

	/**
     * @param dateFormatPattern the dateFormatPattern to set
     */
    public void setDateFormatPattern(String dateFormatPattern) {
    	this.dateFormatPattern = dateFormatPattern;
    }
}
