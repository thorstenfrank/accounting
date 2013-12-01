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
import java.util.Calendar;
import java.util.Set;

/**
 * @author thorsten
 *
 */
public interface ModelMetaInformation extends Serializable {

	/**
	 * Returns the earliest {@link Invoice#getInvoiceDate()} of all invoices in the Database.
	 * 
	 * @return the oldest known invoice date
	 * 
	 * @see Invoice#getInvoiceDate()
	 */
	Calendar getOldestKnownInvoiceDate();

	/**
	 * Returns the earliest {@link Expense#getPaymentDate()} of all expenses in the Database.
	 * 
	 *  @return the oldest known payment date of all expenses
	 *  
	 *  @see Expense#getPaymentDate()
	 */
	Calendar getOldestKnownExpenseDate();

	/**
	 * Returns a distinct and alphabetically sorted list of all expense categories stored in the database.
	 * 
     * @return a distinct list of all expense categories
     * 
     * @see Expense#getCategory()
     */
	Set<String> getExpenseCategories();
}
