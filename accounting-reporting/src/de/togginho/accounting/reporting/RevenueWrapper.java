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

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.Revenue;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * A locale-sensitive wrapper around {@link Revenue}.
 * 
 * <p><b>Note:</b>This class should not be used directly, as it is instantiated by an {@link InvoiceGenerator} and 
 * public only to be visible by the reporting framework.</p>
 * 
 */
public class RevenueWrapper {

	/**
	 * the locale used by this wrapper for formatting
	 */
	private Locale locale;

	/**
	 * the wrapped revenue object
	 */
	private Revenue revenue;

	/**
	 * a list of wrappers around the specific invoices that make up the revenue
	 */
	private List<RevenueDetailWrapper> revenueDetails;

	/** Total net revenue. */
	private BigDecimal totalNet = BigDecimal.ZERO;
	
	/** Total gross revenue. */
	private BigDecimal totalGross = BigDecimal.ZERO;
	
	/** Total sales tax. */
	private BigDecimal totalTax = BigDecimal.ZERO;
	
	/**
	 * Creates a new revenue wrapper.
	 * 
	 * @param locale the locale to use for formatting
	 * @param revenue the {@link Revenue} to wrap
	 */
	public RevenueWrapper(Locale locale, Revenue revenue) {
		super();
		this.locale = locale;
		this.revenue = revenue;

		revenueDetails = new ArrayList<RevenueDetailWrapper>(revenue.getInvoices().size());

		for (Invoice invoice : revenue.getInvoices()) {
			RevenueDetailWrapper detail = new RevenueDetailWrapper(invoice, locale);
			
			totalNet = totalNet.add(CalculationUtil.calculateTotalNetPrice(invoice));
			totalGross = totalGross.add(CalculationUtil.calculateTotalGrossPrice(invoice));
			totalTax = totalTax.add(CalculationUtil.calculateTotalTaxAmount(invoice));
			
			revenueDetails.add(detail);
		}
	}

	/**
	 * @return
	 */
	public String getFromDate() {
		return FormatUtil.formatDate(locale, revenue.getFrom());
	}

	/**
	 * @return
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @return
	 */
	public List<RevenueDetailWrapper> getRevenueDetails() {
		return revenueDetails;
	}

	/**
	 * @return
	 */
	public String getTotalGross() {
		return FormatUtil.formatCurrency(locale, totalGross);
	}

	/**
	 * @return
	 */
	public String getTotalNet() {
		return FormatUtil.formatCurrency(locale, totalNet);
	}

	/**
	 * @return
	 */
	public String getTotalTaxAmount() {
		return FormatUtil.formatCurrency(locale, totalTax);
	}

	/**
	 * @return
	 */
	public String getUntilDate() {
		return FormatUtil.formatDate(locale, revenue.getUntil());
	}

	/**
	 * @param locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
