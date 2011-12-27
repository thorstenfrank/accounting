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

/**
 * One of the clients / customers of a {@link User}.
 * 
 * @author thorsten frank
 */
public class Client implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6108633451701651981L;
	
	public static final String FIELD_NAME = "name";
	public static final String FIELD_ADDRESS = "address";
	public static final String FIELD_DEFAULT_PAYMENT_TERMS = "defaultPaymentTerms";
	
	private String name;
	private Address address;
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
     * @return the defaultPaymentTerms
     */
    public PaymentTerms getDefaultPaymentTerms() {
    	return defaultPaymentTerms;
    }

	/**
     * @param defaultPaymentTerms the defaultPaymentTerms to set
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
	 */
	@Override
	public String toString() {
		return getName();
	}

}
