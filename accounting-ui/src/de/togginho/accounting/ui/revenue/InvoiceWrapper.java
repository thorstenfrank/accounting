/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.revenue;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;
import de.togginho.accounting.model.Price;

/**
 * 
 * @author thorsten
 *
 */
class InvoiceWrapper {

	private Invoice invoice;
		
	private String invoiceDate;
	
	private String paymentDate;
		
	private Price price;
	
	private String netAmount;
	
	private String grossAmount;
	
	private String taxAmount;
	
	public InvoiceWrapper(Invoice invoice) {
		this.invoice = invoice;
		invoiceDate = FormatUtil.formatDate(invoice.getInvoiceDate());
		paymentDate = FormatUtil.formatDate(invoice.getPaymentDate());
		
		price = CalculationUtil.calculateTotalPrice(invoice);
		netAmount = FormatUtil.formatCurrency(price.getNet());
		grossAmount = FormatUtil.formatCurrency(price.getGross());
		if (price.getTax() != null) {
			taxAmount = FormatUtil.formatCurrency(price.getTax());
		} else {
			taxAmount = Constants.HYPHEN;
		}
		
    }

	/**
     * @return the invoice
     */
    public Invoice getInvoice() {
    	return invoice;
    }

	/**
     * @return the price
     */
    public Price getPrice() {
    	return price;
    }

	/**
     * @return the number
     */
    public String getNumber() {
    	return invoice.getNumber();
    }

	/**
     * @return the invoiceDate
     */
    public String getInvoiceDate() {
    	return invoiceDate;
    }

	/**
     * @return the paymentDate
     */
    public String getPaymentDate() {
    	return paymentDate;
    }

	/**
     * @return the client
     */
    public String getClient() {
    	return invoice.getClient().getName();
    }

	/**
     * @return the netAmount
     */
    public String getNetAmount() {
    	return netAmount;
    }

	/**
     * @return the grossAmount
     */
    public String getGrossAmount() {
    	return grossAmount;
    }

	/**
     * @return the taxAmount
     */
    public String getTaxAmount() {
    	return taxAmount;
    }
	
	
}
