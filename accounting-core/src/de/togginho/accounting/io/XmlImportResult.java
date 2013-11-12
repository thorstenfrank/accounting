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
package de.togginho.accounting.io;

import java.util.Set;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.User;

/**
 * @author tfrank1
 *
 */
public class XmlImportResult {

	private User importedUser;
	
	private Set<Client> importedClients;
	
	private Set<Invoice> importedInvoices;
	
	private Set<Expense> importedExpenses;
	
	/**
     * @return the importedUser
     */
    public User getImportedUser() {
    	return importedUser;
    }

	/**
     * @param importedUser the importedUser to set
     */
    void setImportedUser(User importedUser) {
    	this.importedUser = importedUser;
    }

	/**
     * @return the importedClients
     */
    public Set<Client> getImportedClients() {
    	return importedClients;
    }

	/**
     * @param importedClients the importedClients to set
     */
    void setImportedClients(Set<Client> importedClients) {
    	this.importedClients = importedClients;
    }

	/**
     * @return the importedInvoices
     */
    public Set<Invoice> getImportedInvoices() {
    	return importedInvoices;
    }

	/**
     * @param importedInvoices the importedInvoices to set
     */
    void setImportedInvoices(Set<Invoice> importedInvoices) {
    	this.importedInvoices = importedInvoices;
    }

	/**
	 * @return the importedExpenses
	 */
	public Set<Expense> getImportedExpenses() {
		return importedExpenses;
	}

	/**
	 * @param importedExpenses the importedExpenses to set
	 */
	void setImportedExpenses(Set<Expense> importedExpenses) {
		this.importedExpenses = importedExpenses;
	}	
}
