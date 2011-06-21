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
package de.togginho.accounting.reporting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.User;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * Locale-sensitive wrapper around an {@link Invoice}, providing properly formatted and localized values as convenience
 * strings.
 * 
 * @author thorsten frank
 */
public class InvoiceWrapper {

    private Invoice invoice;
    private Locale locale;
    private BigDecimal totalNet = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private BigDecimal totalGross = BigDecimal.ZERO;
    private List<InvoicePositionWrapper> positions;

    /**
     * Creates a new wrapper around an {@link Invoice}.
     * @param invoice the invoice to wrap
     * @param locale the {@link Locale} to use for formatting
     */
    public InvoiceWrapper(Invoice invoice, Locale locale) {
        this.invoice = invoice;
        this.locale = locale;

        this.positions = new ArrayList<InvoicePositionWrapper>();

        for (InvoicePosition pos : invoice.getInvoicePositions()) {
            positions.add(new InvoicePositionWrapper(pos, locale));
        }

        this.totalNet = CalculationUtil.calculateTotalNetPrice(invoice);
        this.totalGross = CalculationUtil.calculateTotalGrossPrice(invoice);
        this.totalTax = CalculationUtil.calculateTotalTaxAmount(invoice);
    }

    /**
     * 
     * @return the formatted {@link Invoice#getInvoiceDate()}
     */
    public String getInvoiceDate() {
    	return FormatUtil.formatDate(locale, invoice.getInvoiceDate());
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
        // TODO localize this
        return "Phone: " + phone;
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
    	// TODO localize this
    	return "Mobile: " + mobile;
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
        // TODO localize this
        return "Mail: " + email;
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
    	// TODO localize this
        return "Steuernr: " + invoice.getUser().getTaxNumber();
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
     * @return {@link CalculationUtil#calculateTotalNetPrice(Invoice)}
     */
    public String getTotalNet() {
        return FormatUtil.formatCurrency(locale, totalNet);
    }

    /**
     * 
     * @return {@link CalculationUtil#calculateTotalTaxAmount(Invoice)}
     */
    public String getTotalTaxAmount() {
        return FormatUtil.formatCurrency(locale, totalTax);
    }

    /**
     * 
     * @return {@link CalculationUtil#calculateTotalGrossPrice(Invoice)}
     */
    public String getTotalGross() {
        return FormatUtil.formatCurrency(locale, totalGross);
    }

    /**
     * 
     * @return the {@link Locale} used by this wrapper for formatting
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * 
     * @return a list of wrappers around the invoice positions
     */
    public List<InvoicePositionWrapper> getPositions() {
        return positions;
    }
}
