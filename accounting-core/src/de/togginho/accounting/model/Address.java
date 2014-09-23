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
 * Basic representation of a contact's address.
 * 
 * Currently only supports a single phone number.
 * 
 * @author thorsten frank
 * @since 1.0
 */
public class Address implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2089684022714034270L;

	public static final String FIELD_RECIPIENT_DETAIL = "recipientDetail";
	public static final String FIELD_STREET = "street";
	public static final String FIELD_POSTAL_CODE = "postalCode";
	public static final String FIELD_CITY = "city";
	public static final String FIELD_PHONE_NUMBER = "phoneNumber";
	public static final String FIELD_MOBILE_NUMBER = "mobileNumber";
	public static final String FIELD_FAX_NUMBER = "faxNumber";
	public static final String FIELD_EMAIL = "email";
	
	private String recipientDetail;
	private String street;
	private String postalCode;
	private String city;
	private String phoneNumber;
	private String mobileNumber;
	private String faxNumber;
	private String email;
	    
	/**
     * @return the recipientDetail
     */
    public String getRecipientDetail() {
    	return recipientDetail;
    }

	/**
     * @param recipientDetail the recipientDetail to set
     */
    public void setRecipientDetail(String recipientDetail) {
    	this.recipientDetail = recipientDetail;
    }

	/**
	 * 
	 * @return street address including house or apt. number
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * 
	 * @param street street address including house or apt. number
	 */
	public void setStreet(String street) {
		this.street = street;
	}
	
	/**
	 * City name.
	 * 
	 * @return the city name of this address
	 */
	public String getCity() {
		return city;
	}

	/**
	 * 
	 * @param city the city name of this address
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Email address.
	 * 
	 * @return the email address
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * 
	 * @param email the email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * 
	 * @return the fax number in arbitrary format
	 */
	public String getFaxNumber() {
		return faxNumber;
	}

	/**
	 * 
	 * @param faxNumber the fax number in arbitrary format
	 */
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	
	/**
	 * 
	 * @return the phone number in arbitrary format
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * 
	 * @param phoneNumber the phone number in arbitrary format
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * Postal Code, postcode, ZIP code, etc. Depends on the addressee's locale.
	 * 
	 * @return postal code for this address
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * Postal Code, postcode, ZIP code, etc. Depends on the addressee's locale.
	 * 
	 * @param postalCode postal code for this address
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
}
