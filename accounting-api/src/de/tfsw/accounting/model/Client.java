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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * A client is the orderer and recipient of services and/or products and 
 * ultimately billed for them through an {@link Invoice}.
 * 
 * @author Thorsten Frank
 * @since  1.0
 */
@Entity
public class Client extends AbstractBaseEntity {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 2L;
	
	public static final String FIELD_NAME = "name";
	public static final String FIELD_CLIENT_NUMBER = "clientNumber";
	public static final String FIELD_ADDRESS = "primaryAddress";
//	public static final String FIELD_DEFAULT_PAYMENT_TERMS = "defaultPaymentTerms";
	
	/**
	 * Name of the client - must be unique.
	 */
	@Id
	private String name;
	
	@Column(unique = true)
	private String clientNumber;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "primaryAddress")
	private Address primaryAddress;
	
	/**
	 * 
	 */
	public Client() {
		// 
	}
	
	/**
	 * @param name
	 */
	public Client(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public Address getPrimaryAddress() {
		return primaryAddress;
	}

	public void setPrimaryAddress(Address primaryAddress) {
		this.primaryAddress = primaryAddress;
	}

	@Override
	public String toString() {
		return "Client: " + name;
	}
}
