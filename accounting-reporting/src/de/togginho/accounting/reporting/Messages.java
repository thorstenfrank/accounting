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
package de.togginho.accounting.reporting;

import org.eclipse.osgi.util.NLS;

/**
 * @author thorsten
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "de.togginho.accounting.reporting.messages"; //$NON-NLS-1$
	
	public static String AbstractReportGenerator_errorTemplateNotFound;
	public static String AbstractReportGenerator_errorFillingReport;
	public static String AbstractReportGenerator_errorCreatingReport;

	public static String CashFlowDataSource_cashFlow;
	public static String CashFlowDataSource_expenses;
	public static String CashFlowDataSource_gross;
	public static String CashFlowDataSource_grossProfit;
	public static String CashFlowDataSource_inputTax;
	public static String CashFlowDataSource_net;
	public static String CashFlowDataSource_operatingExpenses;
	public static String CashFlowDataSource_outputTax;
	public static String CashFlowDataSource_revenue;
	public static String CashFlowDataSource_tax;
	public static String CashFlowDataSource_taxSum;
	public static String CashFlowDataSource_totalExpenses;
	public static String CashFlowDataSource_vat;
	public static String CashFlowDataSource_vatSum;
	
	public static String ExpensesDataSource_expensesTitle;
	public static String ExpensesDataSource_expenseCategory;
	
	public static String InvoiceDataSource_invoiceDateTitle;
	public static String InvoiceDataSource_invoiceNumberTitle;
	public static String InvoiceDataSource_invoiceTitle;
	public static String InvoiceDataSource_positionHeaderDescription;
	public static String InvoiceDataSource_positionHeaderPrice;
	public static String InvoiceDataSource_positionHeaderPricePerUnit;
	public static String InvoiceDataSource_positionHeaderQuantity;
	public static String InvoiceDataSource_positionHeaderTax;
	public static String InvoiceDataSource_positionHeaderUnit;
	public static String InvoiceDataSource_totalGrossTitle;
	public static String InvoiceDataSource_totalNetTitle;
	public static String InvoiceDataSource_totalTaxAmountTitle;
	public static String InvoiceDataSource_userBankAccountTitle;
	public static String InvoiceDataSource_userBankBicTitle;
	public static String InvoiceDataSource_userBankCodeTitle;
	public static String InvoiceDataSource_userBankIbanTitle;
	public static String InvoiceDataSource_userBankTitle;
	public static String InvoiceDataSource_userTaxNumberHeader;
	public static String InvoiceDataSource_paymentConditionTradeCredit;
	
	public static String RevenueReportGenerator_clientTitle;

	public static String RevenueReportGenerator_invoiceDate;
	public static String RevenueReportGenerator_invoiceNumberTitle;
	public static String RevenueReportGenerator_paymentDateTitle;
	public static String RevenueReportGenerator_revenueTitle;

	public static String dateTitle;
	public static String descriptionTitle;
	public static String fromTitle;
	public static String grossPriceTitle;
	public static String netPriceTitle;
	public static String sumTitle;
	public static String taxAmountTitle;
	public static String taxRateTitle;
	public static String untilTitle;

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
