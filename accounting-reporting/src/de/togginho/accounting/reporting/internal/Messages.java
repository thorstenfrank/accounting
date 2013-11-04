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
package de.togginho.accounting.reporting.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author thorsten
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "de.togginho.accounting.reporting.internal.messages"; //$NON-NLS-1$
	
	public static String Letterhead;
	
	public static String CashFlowStatement;
	public static String CashFlowStatement_title;
	
	public static String AbstractReportGenerator_errorTemplateNotFound;
	public static String AbstractReportGenerator_errorFillingReport;
	public static String AbstractReportGenerator_errorCreatingReport;
	
	public static String Expenses;
	public static String expensesTitle;
	public static String expenseCategoryTitle;
	
	public static String IncomeStatement_title;
	public static String IncomeStatementSummary_title;
	public static String IncomeStatement_expenses;
	public static String IncomeStatement_gross;
	public static String IncomeStatement_inputTax;
	public static String IncomeStatement_net;
	public static String IncomeStatement_outputTax;
	public static String IncomeStatement_tax;
	public static String IncomeStatement_taxSum;
	public static String IncomeStatement_totalExpenses;
	public static String IncomeStatement_vat;
	public static String IncomeStatement_vatSum;
	
	public static String invoiceDateTitle;
	public static String invoiceNumberTitle;
	public static String invoiceTitle;
	public static String invoicePositionDescription;
	public static String invoicePositionTotalPrice;
	public static String invoicePositionPricePerUnit;
	public static String invoicePositionQuantity;
	public static String invoicePositionTax;
	public static String invoicePositionUnit;
	public static String invoiceTotalGross;
	public static String invoiceTotalNet;
	public static String invoiceTotalTax;
	public static String paymentTermsTRADE_CREDIT;
	
	public static String RevenueReportGenerator_clientTitle;

	public static String RevenueReportGenerator_invoiceDate;
	public static String RevenueReportGenerator_invoiceNumberTitle;
	public static String RevenueReportGenerator_paymentDateTitle;
	public static String RevenueReportGenerator_revenueTitle;

	public static String bankAccount;
	public static String bankBIC;
	public static String bankIBAN;
	public static String bankCode;
	public static String bank;
	
	public static String dateTitle;
	public static String descriptionTitle;
	public static String fromTitle;
	public static String grossPriceTitle;
	public static String operatingProfitTitle;
	public static String grossTitle;
	public static String netPriceTitle;
	public static String netTitle;
	public static String revenueTitle;
	public static String sumTitle;
	public static String taxAmountTitle;
	public static String taxRateTitle;
	public static String taxes;
	public static String taxTitle;
	public static String untilTitle;

	public static String from;
	public static String until;
	public static String operatingExpenses;
	public static String result;
	public static String TaxId;
	public static String Phone;
	public static String Mobile;
	public static String Email;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
