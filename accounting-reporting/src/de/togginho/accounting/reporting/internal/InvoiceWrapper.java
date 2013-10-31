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
package de.togginho.accounting.reporting.internal;

import java.util.ArrayList;
import java.util.List;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.model.User;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * Locale-sensitive wrapper around an {@link Invoice}, providing properly formatted and localized values as convenience
 * strings.
 * 
 * @author thorsten frank
 */
public class InvoiceWrapper implements Constants {

    private Invoice invoice;
    private Price totalPrice;
    private List<InvoicePositionWrapper> positions;

    /**
     * Creates a new wrapper around an {@link Invoice}.
     * @param invoice the invoice to wrap
     */
    public InvoiceWrapper(Invoice invoice) {
        this.invoice = invoice;

        this.positions = new ArrayList<InvoicePositionWrapper>();

        for (InvoicePosition pos : invoice.getInvoicePositions()) {
            positions.add(new InvoicePositionWrapper(pos));
        }

        this.totalPrice = CalculationUtil.calculateTotalPrice(invoice);
    }

    /**
     * 
     * @return the formatted {@link Invoice#getInvoiceDate()}
     */
    public String getInvoiceDate() {
    	return FormatUtil.formatDate(invoice.getInvoiceDate());
    }
    
    /**
     * @return
     * @see de.togginho.accounting.model.Invoice#getPaymentTerms()
     */
    public PaymentTerms getPaymentTerms() {
	    return invoice.getPaymentTerms();
    }
    
	/**
     * 
     * @return {@link Invoice#getNumber()}
     */
    public String getInvoiceNumber() {
        return invoice.getNumber();
    }

    /**
     * 
     * @return the name of the invoice's user ({@link Invoice#getUser()})
     */
    public String getUserName() {
        return invoice.getUser().getName();
    }

    /**
     * 
     * @return the invoice user's street address
     */
    public String getUserStreet() {
        return invoice.getUser().getAddress().getStreet();
    }

    /**
     * 
     * @return the invoice user's post code and city
     */
    public String getUserPostcodeAndCity() {
        // TODO Localize this
        Address address = invoice.getUser().getAddress();
        return address.getPostalCode() + " " + address.getCity();
    }

    /**
     * 
     * @return the invoice user's primary phone number
     */
    public String getUserPhone() {
    	final String phone = invoice.getUser().getAddress().getPhoneNumber();
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
    	final String mobile = invoice.getUser().getAddress().getMobileNumber();
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
    	final String email = invoice.getUser().getAddress().getEmail();
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
        return invoice.getUser().getDescription();
    }

    /**
     * The invoice user's address in a compact form.
     * Syntax: [user name] - [street] - [postcode and city]
     * @return the invoice user's compacted address
     */
    public String getUserCompactAddress() {
        StringBuilder builder = new StringBuilder();
        builder.append(invoice.getUser().getName());
        builder.append(Constants.HYPHEN);
        builder.append(invoice.getUser().getAddress().getStreet());
        builder.append(Constants.HYPHEN);
        builder.append(getUserPostcodeAndCity());

        return builder.toString();
    }

    /**
     * 
     * @return the invoice user's {@link User#getTaxNumber()}
     */
    public String getUserTaxNumberFormatted() {
        return Messages.bind(Messages.TaxId, invoice.getUser().getTaxNumber());
    }

    /**
     * 
     * @return the invoice recipient's name
     */
    public String getClientName() {
        return invoice.getClient().getName();
    }

    /**
     * 
     * @return the invoice recipient's street address
     */
    public String getClientStreet() {
        return invoice.getClient().getAddress().getStreet();
    }

    /**
     * 
     * @return the invoice recipient's post code and city
     */
    public String getClientPostcodeAndCity() {
        // TODO localize this
        Address address = invoice.getClient().getAddress();
        return address.getPostalCode() + " " + address.getCity();
    }

    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankName() {
        return invoice.getUser().getBankAccount().getBankName();
    }

    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankAccountNumber() {
        return invoice.getUser().getBankAccount().getAccountNumber();
    }

    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankCode() {
        return invoice.getUser().getBankAccount().getBankCode();
    }

    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankBIC() {
    	return invoice.getUser().getBankAccount().getBic();
    }

    /**
     * 
     * @return the invoice user's bank account
     */
    public String getUserBankIBAN() {
    	return invoice.getUser().getBankAccount().getIban();
    }
    
    /**
     * 
     * @return total net price of the invoice
     */
    public String getTotalNet() {
        return FormatUtil.formatCurrency(totalPrice.getNet());
    }

    /**
     * 
     * @return total tax amount contained in the invoice
     */
    public String getTotalTaxAmount() {
        return FormatUtil.formatCurrency(totalPrice.getTax());
    }

    /**
     * 
     * @return total gross price of the invoice
     */
    public String getTotalGross() {
        return FormatUtil.formatCurrency(totalPrice.getGross());
    }

    /**
     * 
     * @return a list of wrappers around the invoice positions
     */
    public List<InvoicePositionWrapper> getPositions() {
        return positions;
    }
}
