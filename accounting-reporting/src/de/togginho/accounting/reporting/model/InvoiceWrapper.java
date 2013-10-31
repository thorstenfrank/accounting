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
package de.togginho.accounting.reporting.model;

import java.util.ArrayList;
import java.util.List;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * Locale-sensitive wrapper around an {@link Invoice}, providing properly formatted and localized values as convenience
 * strings.
 * 
 * @author thorsten frank
 */
public class InvoiceWrapper extends LetterheadWrapper {

    /**
     * 
     */
    private static final long serialVersionUID = -5542277351422036357L;
    
	private Invoice invoice;
    private Price totalPrice;
    private List<InvoicePositionWrapper> positions;

    /**
     * Creates a new wrapper around an {@link Invoice}.
     * @param invoice the invoice to wrap
     */
    public InvoiceWrapper(Invoice invoice) {
    	super(invoice.getUser(), invoice.getClient());
    	
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
