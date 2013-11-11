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
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.Constants;
import de.togginho.accounting.Messages;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseImportParams;
import de.togginho.accounting.model.ExpenseImportResult;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.TaxRate;

/**
 * @author thorsten
 *
 */
public class ExpenseImporter {

	private static final String NON_DIGITS = "[^\\d]";

	private static final Logger LOG = Logger.getLogger(ExpenseImporter.class);
	
	//private static final String DEFAULT_ENCODING = "ISO-8859-1"; //$NON-NLS-1$
	
	private File inputFile;
	private Set<TaxRate> knownTaxRates;
	private ExpenseImportParams params;
	private DateFormat dateFormatter;
	
	private ExpenseImportResult result;
	
	/**
     * @param inputFile
     * @param knownTaxRates
     */
    public ExpenseImporter(File inputFile, Set<TaxRate> knownTaxRates, ExpenseImportParams params) {
    	this.inputFile = inputFile;
    	this.knownTaxRates = knownTaxRates;
    	this.params = params;
    	this.dateFormatter = new SimpleDateFormat(params.getDateFormatPattern());
    }
    
	/**
     * 
     * @return
     */
    public ExpenseImportResult parse() {
    	result = new ExpenseImportResult();
    	result.setExpenses(new ArrayList<Expense>());
    	result.setWarnings(new HashMap<Expense, String>());
    	for (String line : readInputFileByLine()) {
    		LOG.debug("Importing line: " + line); //$NON-NLS-1$
    		
	    	String[] parts = line.split(params.getSeparator());
			if (parts.length < 1) {
				LOG.warn("Empty or illegal line in import file!"); //$NON-NLS-1$
				continue;
			}
			
			Expense current = new Expense();
			StringBuilder warnings = new StringBuilder();
			int index = 0;
			for (String part : parts) {
				part = part.trim();
				
				if (part.length() > 0) {
					switch (index) {
					case 0:
						parseDate(current, part, warnings);
						break;
					case 1:
						current.setDescription(part);
						break;
					case 2:
						parseExpenseType(current, part, warnings);
						break;
					case 3:
						current.setCategory(part);
						break;
					case 4:
						parseNetAmount(current, part, warnings);
						break;
					case 5:
						parseTaxRate(current, part, warnings);
						break;
					default:
						break;
					}
				}
				
				index++;
			}
			
			result.getExpenses().add(current);
			if (warnings.length() > 0) {
				result.getWarnings().put(current, warnings.toString());
			}
    	}
    	
		return result;
    }
    
    /**
     * 
     * @return
     */
    private List<String> readInputFileByLine() {
    	LOG.debug("Reading input file"); //$NON-NLS-1$
    	final List<String> lines = new ArrayList<>();
    	BufferedReader in = null;
		try {
	        in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));//, encoding));

	        String line = in.readLine();
	        
	        // remove any control characters from the first line (such as Byte Order Marks)
	        if (Character.getType(line.charAt(0)) == Character.FORMAT) {
	        	int startAt = 1;
	        	LOG.debug("First char is a format char, will remove until clean..."); //$NON-NLS-1$
	        	for (int i = startAt; i < line.length(); i++) {
	        		if (Character.getType(line.charAt(i)) == Character.FORMAT) {
	        			startAt++;
	        		} else {
	        			break;
	        		}
	        	}
	        	line = line.substring(startAt);
	        }
	        
	        while (line != null) {
	        	lines.add(line);
	        	line = in.readLine();
	        }
	        
	        in.close();
        } catch (Exception e) {
        	LOG.error("Error reading file", e); //$NON-NLS-1$
        	result.setError(Messages.ExpenseImporter_ErrorReadingFile);
        } finally {
        	if (in != null) {
        		try {
	                in.close();
                } catch (IOException e) {
                	LOG.warn("Trouble closing input stream", e); //$NON-NLS-1$
                }
        	}
        }
		
		return lines;
    }
    
    /**
     * 
     * @param expense
     * @param rawDate
     */
    private void parseDate(Expense target, String source, StringBuilder warnings) {
    	try {
    		target.setPaymentDate(dateFormatter.parse(source));
        } catch (Exception e) {
        	LOG.warn("Unparseable date: " + source, e); //$NON-NLS-1$
        	addToWarnings(warnings, Messages.bind(Messages.ExpenseImporter_WarningUnparseableDate, source));
        }
    }
    
	/**
     * @param target
     * @param source
     */
    private void parseExpenseType(Expense target, String source, StringBuilder warnings) {
	    try {
			target.setExpenseType(ExpenseType.valueOf(source));
		} catch (IllegalArgumentException e) {
			LOG.warn("Not a valid expense type: " + source, e);
			addToWarnings(warnings, Messages.bind(Messages.ExpenseImporter_WarningInvalidType, source));
		}
    }
    
	/**
     * @param target
     * @param source
     */
    private void parseNetAmount(Expense target, String source, StringBuilder warnings) {
    	target.setNetAmount(parseDecimal(source));
    	if (target.getNetAmount() == null) {
    		addToWarnings(warnings, Messages.bind(Messages.ExpenseImporter_WarningUnparseableAmount, source));
    	}
    }
    
    /**
     * 
     * @param target
     * @param source
     */
    private void parseTaxRate(Expense target, String source, StringBuilder warnings) {
    	if (source == null || source.isEmpty()) {
    		LOG.info("No tax rate specified"); //$NON-NLS-1$
    		return;
    	}
    	
    	BigDecimal rate = parseDecimal(source);
    	if (rate == null) {
    		LOG.warn(String.format("Tax rate [%s] could not be parsed into a number, skipping!", source)); //$NON-NLS-1$
    		addToWarnings(warnings, Messages.bind(Messages.ExpenseImporter_WarningUnparseableTaxRate, source));
    		return;
    	}
    	
    	for (TaxRate taxRate : knownTaxRates) {
    		if (rate.compareTo(taxRate.getRate()) == 0) {
    			target.setTaxRate(taxRate);
    			return;
    		}
    	}
    	
    	LOG.warn("No known tax rate found for: " + source); //$NON-NLS-1$
    	addToWarnings(warnings, Messages.bind(Messages.ExpenseImporter_WarningUnknownTaxRate, source));
    }
    
    /**
     * 
     * @param source
     * @return
     */
    private BigDecimal parseDecimal(String source) {
    	try {
    		int mark = source.lastIndexOf(params.getDecimalMark());
    		StringBuilder sb = new StringBuilder();
    		if (mark < 0) { // no fraction digits
    			sb.append(source.replaceAll(NON_DIGITS, Constants.EMPTY_STRING));
    		} else {
    			sb.append(source.substring(0, mark).replaceAll(NON_DIGITS, Constants.EMPTY_STRING));
    			sb.append(Constants.DOT);
    			sb.append(source.substring(mark + 1).replaceAll(NON_DIGITS, Constants.EMPTY_STRING));
    		}
    		
    		if (sb.length() < 1) {
    			LOG.warn(String.format("Not a valid number value: [%s] (sanitized: [%s]", source, sb.toString())); //$NON-NLS-1$
    			return null;
    		}
    		
    		return new BigDecimal(sb.toString());
        } catch (AccountingException e) {
        	LOG.warn("Error parsing decimal value: " + source, e); //$NON-NLS-1$
        	return null;
        }
    }
    
    /**
     * 
     * @param warnings
     * @param message
     */
    private void addToWarnings(StringBuilder warnings, String message) {
    	if (warnings.length() > 0) {
    		warnings.append("\n");
    	}
    	warnings.append(message);
    }
}
