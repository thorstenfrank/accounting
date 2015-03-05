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
package de.tfsw.accounting.model.internal;

import java.time.LocalDate;
import java.util.Set;

import de.tfsw.accounting.model.ModelMetaInformation;

/**
 * @author thorsten
 *
 */
public class ModelMetaInformationImpl implements ModelMetaInformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2787279017341211597L;

	private int numberOfInvoices;
	
	private LocalDate oldestInvoice;
	
	private int numberOfExpenses;
	
	private LocalDate oldestExpense;

	private Set<String> expenseCategories;
	
	
	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.model.ModelMetaInformation#getOldestKnownInvoiceDate()
	 */
	@Override
	public LocalDate getOldestKnownInvoiceDate() {
		return oldestInvoice;
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.model.ModelMetaInformation#getOldestKnownExpenseDate()
	 */
	@Override
	public LocalDate getOldestKnownExpenseDate() {
		return oldestExpense;
	}

	/**
	 * @return the numberOfInvoices
	 */
	public int getNumberOfInvoices() {
		return numberOfInvoices;
	}

	/**
	 * @param numberOfInvoices the numberOfInvoices to set
	 */
	public void setNumberOfInvoices(int numberOfInvoices) {
		this.numberOfInvoices = numberOfInvoices;
	}

	/**
	 * @param oldestInvoice the oldestInvoice to set
	 */
	public void setOldestInvoice(LocalDate oldestInvoice) {
		this.oldestInvoice = oldestInvoice;
	}

	/**
	 * @return the numberOfExpenses
	 */
	public int getNumberOfExpenses() {
		return numberOfExpenses;
	}

	/**
	 * @param numberOfExpenses the numberOfExpenses to set
	 */
	public void setNumberOfExpenses(int numberOfExpenses) {
		this.numberOfExpenses = numberOfExpenses;
	}

	/**
	 * @param oldestExpense the oldestExpense to set
	 */
	public void setOldestExpense(LocalDate oldestExpense) {
		this.oldestExpense = oldestExpense;
	}

	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.model.ModelMetaInformation#getExpenseCategories()
	 */
    @Override
    public Set<String> getExpenseCategories() {
    	return expenseCategories;
    }

	/**
     * @param expenseCategories the expenseCategories to set
     */
    public void setExpenseCategories(Set<String> expenseCategories) {
    	this.expenseCategories = expenseCategories;
    }
}
