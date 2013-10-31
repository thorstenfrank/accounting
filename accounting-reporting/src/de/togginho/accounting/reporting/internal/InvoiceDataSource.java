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

import java.util.Map;

/**
 * Jasperreport data source to produce invoices.
 *  
 * @author thorsten frank
 */
public class InvoiceDataSource extends AbstractReportDataSource {

    //private final static Log LOG = LogFactory.getLog(InvoiceDataSource.class);

    private static final String INVOICE_KEY = "invoice"; //$NON-NLS-1$
        
	private InvoiceWrapper wrapper;
    
    /**
     * Creates a new invoice generator that uses the supplied wrapper to fill the report with dynamic data.
     * 
     * @param wrapper	the {@link InvoiceWrapper} to use for report generation
     */
    public InvoiceDataSource(InvoiceWrapper wrapper) {
    	this.wrapper = wrapper;
    }

	/**
	 * Adds the {@link InvoiceWrapper} that holds dynamic information about the invoice being produced.
	 * 
	 * <p>The map key is <code>invoice</code>.</p>
	 */
	@Override
	protected void addFieldsToMap(Map<String, Object> fieldMap) {
		fieldMap.put(INVOICE_KEY, wrapper);
		
		fieldMap.put("invoice.title", Messages.InvoiceDataSource_invoiceTitle); //$NON-NLS-1$
		fieldMap.put("invoice.number.title", Messages.InvoiceDataSource_invoiceNumberTitle); //$NON-NLS-1$
		fieldMap.put("invoice.date.title", Messages.InvoiceDataSource_invoiceDateTitle); //$NON-NLS-1$
		fieldMap.put("position.header.quantity", Messages.InvoiceDataSource_positionHeaderQuantity); //$NON-NLS-1$
		fieldMap.put("position.header.unit", Messages.InvoiceDataSource_positionHeaderUnit); //$NON-NLS-1$
		fieldMap.put("position.header.description", Messages.InvoiceDataSource_positionHeaderDescription); //$NON-NLS-1$
		fieldMap.put("position.header.pricePerUnit", Messages.InvoiceDataSource_positionHeaderPricePerUnit); //$NON-NLS-1$
		fieldMap.put("position.header.totalPrice", Messages.InvoiceDataSource_positionHeaderPrice); //$NON-NLS-1$
		fieldMap.put("position.header.tax", Messages.InvoiceDataSource_positionHeaderTax); //$NON-NLS-1$
		fieldMap.put("user.bank.title", Messages.InvoiceDataSource_userBankTitle); //$NON-NLS-1$
		fieldMap.put("user.bank.account.title", Messages.InvoiceDataSource_userBankAccountTitle); //$NON-NLS-1$
		fieldMap.put("user.bank.code.title", Messages.InvoiceDataSource_userBankCodeTitle); //$NON-NLS-1$
		
		// only add BIC and IBAN labels if present in bank account
		if (wrapper.getUserBankBIC() != null && !wrapper.getUserBankBIC().isEmpty()) {
			fieldMap.put("user.bank.bic.title", Messages.InvoiceDataSource_userBankBicTitle); //$NON-NLS-1$
		}
		if (wrapper.getUserBankIBAN() != null && !wrapper.getUserBankIBAN().isEmpty()) {
			fieldMap.put("user.bank.iban.title", Messages.InvoiceDataSource_userBankIbanTitle); //$NON-NLS-1$
		}

		fieldMap.put("total.net.title", Messages.InvoiceDataSource_totalNetTitle); //$NON-NLS-1$
		fieldMap.put("total.tax.amount.title", Messages.InvoiceDataSource_totalTaxAmountTitle); //$NON-NLS-1$
		fieldMap.put("total.gross.title", Messages.InvoiceDataSource_totalGrossTitle); //$NON-NLS-1$
		fieldMap.put("user.taxNumber.header", Messages.InvoiceDataSource_userTaxNumberHeader); //$NON-NLS-1$
		
		switch (wrapper.getPaymentTerms().getPaymentType()) {
		case TRADE_CREDIT:
			fieldMap.put(
					"paymentConditionText", 
					Messages.bind(
							Messages.InvoiceDataSource_paymentConditionTradeCredit, 
							wrapper.getPaymentTerms().getFullPaymentTargetInDays()));
			break;
		default:
			// do nothing, since we don't want the payment conditions to be displayed
			break;
		}
	}
}