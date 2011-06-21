/*
 *  Copyright 2010 thorsten frank (thorsten.frank@gmx.de).
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
import java.util.HashSet;
import java.util.Set;

/**
 * A provider or seller of products or services.
 * 
 * <p>This is the main entity of the accounting application.</p>
 * 
 * @author thorsten frank
 */
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5026076582008704143L;
	
	public static final String FIELD_NAME = "name";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_ADDRESS = "address";
	public static final String FIELD_TAX_NUMBER = "taxNumber";
	public static final String FIELD_BANK_ACCOUNT = "bankAccount";
	public static final String FIELD_CLIENTS = "clients";
	public static final String FIELD_TAX_RATES = "taxRates";
	
	// must be unique
	private String name;
	private String description;
	private Address address;
	private String taxNumber;
	private BankAccount bankAccount;
	private Set<Client> clients;
	private Set<TaxRate> taxRates;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the taxNumber
	 */
	public String getTaxNumber() {
		return taxNumber;
	}

	/**
	 * @param taxNumber the taxNumber to set
	 */
	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	/**
	 * @return the bankAccount
	 */
	public BankAccount getBankAccount() {
		return bankAccount;
	}

	/**
	 * @param bankAccount the bankAccount to set
	 */
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	/**
	 * @return the clients
	 */
	public Set<Client> getClients() {
		return clients;
	}

	/**
	 * Finds the client with the supplied name.
	 * @param name the {@link Client#getName()} to find
	 * @return the client, or <code>null</code> if there is no client by that name or this user has no clients
	 */
	public Client getClientByName(String name) {
		if (clients != null && name != null) {
			for (Client client : clients) {
				if (name.equals(client.getName())) {
					return client;
				}
			}
		}
		return null;
	}
	
	/**
	 * @param clients the clients to set
	 */
	public void setClients(Set<Client> clients) {
		this.clients = clients;
	}

	/**
	 * 
	 * @param client the client to add
	 */
	public void addClient(Client client) {
		if (clients == null){
			clients = new HashSet<Client>();
		}
		
		clients.add(client);
	}
	
	/**
	 * 
	 * @param client the client to remove
	 */
	public void removeClient(Client client) {
		if (clients != null) {
			clients.remove(client);
		}
	}

	
	/**
	 * @return the taxRates
	 */
	public Set<TaxRate> getTaxRates() {
		return taxRates;
	}

	/**
	 * @param taxRates the taxRates to set
	 */
	public void setTaxRates(Set<TaxRate> taxRates) {
		this.taxRates = taxRates;
	}

	/**
	 * 
	 * @param taxRate the {@link TaxRate} to add
	 */
	public void addTaxRate(TaxRate taxRate) {
		if (taxRates == null) {
			taxRates = new HashSet<TaxRate>();
		}
		taxRates.add(taxRate);
	}
	
	/**
	 * 
	 * @param taxRate the {@link TaxRate} to remove
	 */
	public void removeTaxRate(TaxRate taxRate) {
		if (taxRates != null) {
			taxRates.remove(taxRate);
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Returns {@link #getName()}.
	 */
	@Override
	public String toString() {
		return getName();
	}	
}