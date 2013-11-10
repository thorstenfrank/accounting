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
import java.util.List;
import java.util.Map;

/**
 * @author thorsten
 *
 */
public class ExpenseImportResult implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 6839892651766072338L;

    private List<Expense> expenses;
    
    private String error;
    
    private Map<Expense, String> warnings;

	/**
     * @return the expenses
     */
    public List<Expense> getExpenses() {
    	return expenses;
    }

	/**
     * @param expenses the expenses to set
     */
    public void setExpenses(List<Expense> expenses) {
    	this.expenses = expenses;
    }

	/**
     * @return the error
     */
    public String getError() {
    	return error;
    }

	/**
     * @param error the error to set
     */
    public void setError(String error) {
    	this.error = error;
    }

    /**
     * 
     * @return
     */
    public boolean hasError() {
    	return error != null;
    }
    
	/**
     * @return the warnings
     */
    public Map<Expense, String> getWarnings() {
    	return warnings;
    }

	/**
     * @param warnings the warnings to set
     */
    public void setWarnings(Map<Expense, String> warnings) {
    	this.warnings = warnings;
    }
    
    /**
     * 
     * @return
     */
    public boolean hasWarnings() {
    	return warnings != null && warnings.size() > 0;
    }
}
