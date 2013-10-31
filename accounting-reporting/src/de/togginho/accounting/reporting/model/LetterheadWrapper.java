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
package de.togginho.accounting.reporting.model;

import java.io.Serializable;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.User;
import de.togginho.accounting.reporting.internal.Messages;

/**
 * @author thorsten
 *
 */
public class LetterheadWrapper implements Serializable, Constants {

	/**
     * 
     */
    private static final long serialVersionUID = -1960507073073157065L;

    private User user;
    
    private Client client;
    
    /**
     * @param user
     */
    public LetterheadWrapper(User user) {
	    this.user = user;
    }
    
	/**
     * @param user
     * @param client
     */
    public LetterheadWrapper(User user, Client client) {
	    this.user = user;
	    this.client = client;
    }

	/**
     * 
     * @return the name of the user
     */
    public String getUserName() {
        return user.getName();
    }

    /**
     * 
     * @return the user's street address
     */
    public String getUserStreet() {
        return user.getAddress().getStreet();
    }

    /**
     * 
     * @return the user's post code and city
     */
    public String getUserPostcodeAndCity() {
    	return getPostcodeAndCity(user.getAddress());
    }

    /**
     * 
     * @return the invoice user's primary phone number
     */
    public String getUserPhone() {
    	final String phone = user.getAddress().getPhoneNumber();
    	if (phone == null) {
    		return null;
    	}
    	
    	return Messages.bind(Messages.Phone, phone);
    }

    /**
     * 
     * @return the invoice user's mobile phone number
     */
    public String getUserMobile() {
    	final String mobile = user.getAddress().getMobileNumber();
    	if (mobile == null) {
    		return null;
    	}
    	
    	return Messages.bind(Messages.Mobile, mobile);
    }
    
    /**
     * 
     * @return the invoice user's email address
     */
    public String getUserEmail() {
    	final String email = user.getAddress().getEmail();
    	if (email == null) {
    		return null;
    	}
    	
    	return Messages.bind(Messages.Email, email);
    }

    /**
     * 
     * @return the invoice user's description text
     */
    public String getUserHeaderInformation() {
        return user.getDescription();
    }

    /**
     * The invoice user's address in a compact form.
     * Syntax: [user name] - [street] - [postcode and city]
     * @return the invoice user's compacted address
     */
    public String getUserCompactAddress() {
        StringBuilder builder = new StringBuilder();
        builder.append(user.getName());
        builder.append(Constants.HYPHEN);
        builder.append(user.getAddress().getStreet());
        builder.append(Constants.HYPHEN);
        builder.append(getUserPostcodeAndCity());

        return builder.toString();
    }

    /**
     * 
     * @return the invoice user's {@link User#getTaxNumber()}
     */
    public String getUserTaxNumberFormatted() {
        return Messages.bind(Messages.TaxId, user.getTaxNumber());
    }

    /**
     * 
     * @return the invoice recipient's name
     */
    public String getClientName() {
        return client != null ? client.getName() : null;
    }

    /**
     * 
     * @return the invoice recipient's street address
     */
    public String getClientStreet() {
        return client != null && client.getAddress() != null ? client.getAddress().getStreet() : null;
    }

    /**
     * 
     * @return the invoice recipient's post code and city
     */
    public String getClientPostcodeAndCity() {
    	return client != null ? getPostcodeAndCity(client.getAddress()) : null;
    }

    /**
     * 
     * @param address
     * @return
     */
    private String getPostcodeAndCity(Address address) {
        // TODO localize this
    	StringBuilder sb = new StringBuilder();
    	if (address != null) {
    		sb.append(address.getPostalCode());
    		sb.append(BLANK_STRING);
    		sb.append(address.getCity());
    	}
    	return sb.toString();
    }
    
    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankName() {
        return user.getBankAccount().getBankName();
    }

    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankAccountNumber() {
        return user.getBankAccount().getAccountNumber();
    }

    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankCode() {
        return user.getBankAccount().getBankCode();
    }

    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankBIC() {
    	return user.getBankAccount().getBic();
    }

    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankIBAN() {
    	return user.getBankAccount().getIban();
    }
}
