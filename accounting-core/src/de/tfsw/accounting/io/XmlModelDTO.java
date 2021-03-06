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
package de.tfsw.accounting.io;

import java.util.Set;

import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.User;

/**
 * @author thorsten
 *
 */
public class XmlModelDTO {

	private User user;
	
	private Set<Client> clients;
	
	private Set<Invoice> invoices;
	
	private Set<Expense> expenses;
	
	private Set<ExpenseTemplate> expenseTemplates;
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the clients
	 */
	public Set<Client> getClients() {
		return clients;
	}

	/**
	 * @param clients the clients to set
	 */
	public void setClients(Set<Client> clients) {
		this.clients = clients;
	}

	/**
	 * @return the invoices
	 */
	public Set<Invoice> getInvoices() {
		return invoices;
	}

	/**
	 * @param invoices the invoices to set
	 */
	public void setInvoices(Set<Invoice> invoices) {
		this.invoices = invoices;
	}

	/**
	 * @return the expenses
	 */
	public Set<Expense> getExpenses() {
		return expenses;
	}

	/**
	 * @param expenses the expenses to set
	 */
	public void setExpenses(Set<Expense> expenses) {
		this.expenses = expenses;
	}

	/**
	 * @return the expenseTemplates
	 */
	public Set<ExpenseTemplate> getExpenseTemplates() {
		return expenseTemplates;
	}

	/**
	 * @param expenseTemplates the expenseTemplates to set
	 */
	public void setExpenseTemplates(Set<ExpenseTemplate> expenseTemplates) {
		this.expenseTemplates = expenseTemplates;
	}
}
