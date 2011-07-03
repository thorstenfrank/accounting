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

import java.util.Map;

/**
 * Jasperreport data source to produce revenue reports.
 * 
 * @author thorsten frank
 *
 */
public class RevenueReportDataSource extends AbstractReportDataSource {

	private static final String REVENUE_KEY = "revenue";
	/**
	 * 
	 */
	private RevenueWrapper wrapper;
	
	/**
	 * 
	 * @param wrapper
	 */
	public RevenueReportDataSource(RevenueWrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected void addFieldsToMap(Map<String, Object> fieldMap) {
		fieldMap.put(REVENUE_KEY, wrapper);
		
		fieldMap.put("revenue.title", Messages.RevenueReportGenerator_revenueTitle); //$NON-NLS-1$
		fieldMap.put("from.title", Messages.RevenueReportGenerator_fromTitle); //$NON-NLS-1$
		fieldMap.put("until.title", Messages.RevenueReportGenerator_untilTitle); //$NON-NLS-1$
		fieldMap.put("invoice.date.title", Messages.RevenueReportGenerator_invoiceDate); //$NON-NLS-1$
		fieldMap.put("invoice.no.title", Messages.RevenueReportGenerator_invoiceNumberTitle); //$NON-NLS-1$
		fieldMap.put("client.name.title", Messages.RevenueReportGenerator_clientTitle); //$NON-NLS-1$
		fieldMap.put("payment.date.title", Messages.RevenueReportGenerator_paymentDateTitle); //$NON-NLS-1$
		fieldMap.put("net.price.title", Messages.RevenueReportGenerator_netPriceTitle); //$NON-NLS-1$
		fieldMap.put("taxRate.title", Messages.RevenueReportGenerator_taxRateTitle); //$NON-NLS-1$
		fieldMap.put("tax.amount.title", Messages.RevenueReportGenerator_taxAmountTitle); //$NON-NLS-1$
		fieldMap.put("gross.price.title", Messages.RevenueReportGenerator_grossPriceTitle); //$NON-NLS-1$
		fieldMap.put("sum.title", Messages.RevenueReportGenerator_sumTitle); //$NON-NLS-1$
	}
}
