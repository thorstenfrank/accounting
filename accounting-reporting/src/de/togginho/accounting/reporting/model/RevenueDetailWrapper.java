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

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * 
 * @author thorsten frank
 *
 */
public class RevenueDetailWrapper implements Constants {

	private Invoice invoice;

	private Price totalPrice;
	
	/**
	 * 
	 * @param invoice
	 */
	public RevenueDetailWrapper(Invoice invoice) {
		super();
		this.invoice = invoice;
		
		totalPrice = CalculationUtil.calculateTotalPrice(invoice);
	}

	/**
	 * 
	 * @return
	 */
	public String getInvoiceDate() {
		return FormatUtil.formatDate(invoice.getInvoiceDate());
	}

	/**
	 * 
	 * @return
	 */
	public String getInvoiceNumber() {
		return invoice.getNumber();
	}

	/**
	 * 
	 * @return
	 */
	public String getPaymentDate() {
		if (invoice.getPaymentDate() != null) {
			return FormatUtil.formatDate(invoice.getPaymentDate());
		}
		
		return Constants.HYPHEN;
	}

	/**
	 * 
	 * @return
	 */
	public String getNetPrice() {
		return FormatUtil.formatCurrency(totalPrice.getNet());
	}

	/**
	 * 
	 * @return
	 */
	public String getTaxRate() {
		// FIXME get this from the invoice somehow
		return "19%";
	}

	/**
	 * 
	 * @return
	 */
	public String getTaxAmount() {
		return FormatUtil.formatCurrency(totalPrice.getTax());
	}

	/**
	 * 
	 * @return
	 */
	public String getGrossPrice() {
		return FormatUtil.formatCurrency(totalPrice.getGross());
	}

	/**
	 * 
	 * @return
	 */
	public String getClientName() {
		return invoice.getClient().getName();
	}
}
