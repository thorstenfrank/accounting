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

import java.util.Locale;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * 
 * @author thorsten frank
 *
 */
public class RevenueDetailWrapper implements Constants {

	private Invoice invoice;

	private Locale locale;

	public RevenueDetailWrapper(Invoice invoice, Locale locale) {
		super();
		this.invoice = invoice;
		this.locale = locale;
	}

	public String getInvoiceDate() {
		return FormatUtil.formatDate(locale, invoice.getInvoiceDate());
	}

	public String getInvoiceNumber() {
		return invoice.getNumber();
	}

	public String getPaymentDate() {
		if (invoice.getPaymentDate() != null) {
			return FormatUtil.formatDate(locale, invoice.getPaymentDate());
		}
		
		return Constants.HYPHEN;
	}

	public String getNetPrice() {
		return FormatUtil.formatCurrency(locale, CalculationUtil.calculateTotalNetPrice(invoice));
	}

	public String getTaxRate() {
		// FIXME get this from the invoice somehow
		return "19%";
	}

	public String getTaxAmount() {
		return FormatUtil.formatCurrency(locale, CalculationUtil.calculateTotalTaxAmount(invoice));
	}

	public String getGrossPrice() {
		return FormatUtil.formatCurrency(locale, CalculationUtil.calculateTotalGrossPrice(invoice));
	}

	public String getClientName() {
		return invoice.getClient().getName();
	}
}
