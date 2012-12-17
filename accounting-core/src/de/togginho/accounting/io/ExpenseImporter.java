/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class ExpenseImporter {

	private static final Logger LOG = Logger.getLogger(ExpenseImporter.class);
	
	private static final String DEFAULT_ENCODING = "ISO-8859-1"; //$NON-NLS-1$
	private static final String DEFAULT_SEPARATOR = ";"; //$NON-NLS-1$
	
	/**
	 * 
	 * @param sourceFile
	 * @param knownTaxRates
	 * @return
	 * @throws AccountingException
	 */
	public static Collection<Expense> importExpenses(String sourceFile, Set<TaxRate> knownTaxRates) {		
        return new ExpenseImporter(new File(sourceFile), knownTaxRates).parse();
	}
	
	private File inputFile;
	private Set<TaxRate> knownTaxRates;
	private String encoding;
	private String separator;
	
	/**
     * @param inputFile
     * @param knownTaxRates
     */
    private ExpenseImporter(File inputFile, Set<TaxRate> knownTaxRates) {
    	this(inputFile, knownTaxRates, DEFAULT_ENCODING, DEFAULT_SEPARATOR);
    }

    /**
     * 
     * @param inputFile
     * @param knownTaxRates
     * @param encoding
     * @param separator
     */
    private ExpenseImporter(File inputFile, Set<TaxRate> knownTaxRates, String encoding, String separator) {
	    this.inputFile = inputFile;
	    this.knownTaxRates = knownTaxRates;
	    this.encoding = encoding;
	    this.separator = separator;
    }
    
    /**
     * 
     * @return
     */
    private Collection<Expense> parse() {
    	Collection<Expense> imported = new ArrayList<Expense>();
    	
    	String[] parts = readInputFileContents().split(separator);
		if (parts.length < 1) {
			throw new AccountingException("No expenses found in file " + inputFile.getName());
		}
		
		Expense current = new Expense();
		
		int index = 0;
		for (String part : parts) {
			part = part.trim();
			
			if (part.length() > 0) {
				switch (index) {
				case 0:
					parseDate(current, part);
					break;
				case 1:
					current.setDescription(part);
					break;
				case 2:
					parseExpenseType(current, part);
					break;
				case 3:
					current.setCategory(part);
					break;
				case 4:
					parseNetAmount(current, part);
					break;
				case 5:
					parseTaxRate(current, part);
					break;
				default:
					break;
				}
			}
			
			if (index == 5) {
				imported.add(current);
				current = new Expense();
				index = 0;
			} else {
				index++;
			}
			
		}
		
		return imported;
    }
    
    /**
     * 
     * @return
     */
    private String readInputFileContents() {
		try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding));
	        StringBuilder sb = new StringBuilder();
	        int read;
	        while ((read = in.read()) != -1) {
	        	sb.append((char) read);
	        }
	        
	        in.close();
	        
	        return sb.toString();
        } catch (UnsupportedEncodingException e) {
        	LOG.error("Unknown encoding", e); //$NON-NLS-1$
        	throw new AccountingException("Unknown encoding", e);
        } catch (FileNotFoundException e) {
        	LOG.error("File not found", e); //$NON-NLS-1$
        	throw new AccountingException("File not found", e);
        } catch (IOException e) {
        	LOG.error("I/O Error", e); //$NON-NLS-1$
        	throw new AccountingException("I/O Error", e);
        }
    }
    
    /**
     * 
     * @param expense
     * @param rawDate
     */
    private void parseDate(Expense target, String source) {
    	try {
	        target.setPaymentDate(FormatUtil.parseDate(source));
        } catch (AccountingException e) {
        	LOG.warn("Date could not be parsed: " + source); //$NON-NLS-1$
        }
    }
    
	/**
     * @param target
     * @param source
     */
    private void parseExpenseType(Expense target, String source) {
	    target.setExpenseType(ExpenseType.valueOf(source));
    }
    
	/**
     * @param target
     * @param source
     */
    private void parseNetAmount(Expense target, String source) {
    	try {
	        target.setNetAmount(FormatUtil.parseDecimalValue(source));
        } catch (AccountingException e) {
        	LOG.warn("Error parsing decimal value: " + source, e); //$NON-NLS-1$
        }
    }
    
    /**
     * 
     * @param target
     * @param source
     */
    private void parseTaxRate(Expense target, String source) {
    	if (source == null || source.isEmpty()) {
    		LOG.info("No tax rate specified");
    		return;
    	}
    	
    	BigDecimal rate = FormatUtil.parseDecimalValue(source);
    	LOG.debug(rate.toString());
    	for (TaxRate taxRate : knownTaxRates) {
    		if (rate.compareTo(taxRate.getRate()) == 0) {
    			target.setTaxRate(taxRate);
    			return;
    		}
    	}
    	
    	LOG.warn("No known tax rate found for: " + source); //$NON-NLS-1$
    }
}
