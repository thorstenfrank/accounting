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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Information about a bank account to which payments are made.
 * 
 * @author Thorsten Frank
 * @since  1.0
 */
@Entity
@Table(name = "BankAccount")
public class BankAccount extends AbstractBaseEntity {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 2L;

	public static final String FIELD_BANK_NAME = "bankName";
	public static final String FIELD_BANK_CODE = "bankCode";
	public static final String FIELD_ACCOUNT_NUMBER = "accountNumber";
	public static final String FIELD_IBAN = "iban";
	public static final String FIELD_BIC = "bic";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String bankName;
	private String bankCode;
	private String accountNumber;
	private String iban;
	private String bic;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Describes a code that uniquely defines the credit institution, such as a Bankleitzahl in Germany, routing transit
	 * numbers in the US, a code banque in France, and so on.
	 * 
	 * @return the unique identifier for the credit institute
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * Describes a code that uniquely defines the credit institution, such as a Bankleitzahl in Germany, routing transit
	 * numbers in the US, a code banque in France, and so on.
	 * 
	 * @param bankCode the unique identifier for the credit institute
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	/**
	 * 
	 * @return name of the credit institute
	 */
	public String getBankName() {
		return bankName;
	}
	
	/**
	 * 
	 * @param bankName name of the credit institute
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	/**
	 * 
	 * @return the account number
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * 
	 * @param accountNumber the account number
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the iban
	 */
	public String getIban() {
		return iban;
	}

	/**
	 * @param iban the iban to set
	 */
	public void setIban(String iban) {
		this.iban = iban;
	}

	/**
	 * @return the bic
	 */
	public String getBic() {
		return bic;
	}

	/**
	 * @param bic the bic to set
	 */
	public void setBic(String bic) {
		this.bic = bic;
	}
}
