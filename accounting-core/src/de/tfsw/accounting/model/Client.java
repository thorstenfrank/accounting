/*
 *  Copyright 2010, 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.model;

import java.io.Serializable;

/**
 * A client is the orderer and recipient of services and/or products and 
 * ultimately billed for them through an {@link Invoice}.
 * 
 * @author Thorsten Frank
 * @since  1.0
 */
public class Client implements Serializable {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 2L;
	
	public static final String FIELD_NAME = "name";
	public static final String FIELD_CLIENT_NUMBER = "clientNumber";
	public static final String FIELD_ADDRESS = "address";
	public static final String FIELD_DEFAULT_PAYMENT_TERMS = "defaultPaymentTerms";
	
	/**
	 * Name of the client - must be unique.
	 */
	private String name;
	
	/**
	 * Unique number.
	 */
	private String clientNumber;
	
	/**
	 * Address where invoices are billed to.
	 */
	private Address address;
	
	/**
	 * Used for new invoices to this client.
	 */
	private PaymentTerms defaultPaymentTerms;
	
	/**
	 * 
	 * @return the name of the client
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name the name of the customer
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the clientNumber
	 */
	public String getClientNumber() {
		return clientNumber;
	}

	/**
	 * @param clientNumber the clientNumber to set
	 */
	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	/**
	 * 
	 * @return the address of the client
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * 
	 * @param address the address of the client
	 */
	public void setAddress(Address address) {
		this.address = address;
	}
	
	/**
	 * The default payment terms are used as templates when creating new invoices for a client.
	 * 
     * @return the default payment terms for this client
     */
    public PaymentTerms getDefaultPaymentTerms() {
    	return defaultPaymentTerms;
    }

	/**
	 * Sets the default payment terms for this client. 
	 * Defaults are used as templates when creating new invoices for a client.
	 * 
     * @param defaultPaymentTerms the default payment terms for this client
     */
    public void setDefaultPaymentTerms(PaymentTerms defaultPaymentTerms) {
    	this.defaultPaymentTerms = defaultPaymentTerms;
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
		Client other = (Client) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Returns {@link #getName()}.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

}
