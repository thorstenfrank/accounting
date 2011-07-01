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
import java.util.Map;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import de.togginho.accounting.model.Revenue;

/**
 * 
 * @author thorsten frank
 *
 */
public class RevenueReportGenerator extends AbstractReportGenerator {

	/**
	 * 
	 */
	private static final String REVENUE_DETAILS_DATASOURCE = "REVENUE_DETAILS_DATASOURCE"; //$NON-NLS-1$

	/**
	 * 
	 */
	private static final String JASPER_PATH = "PeriodicalRevenue.jasper"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private RevenueWrapper wrapper;
	
	/**
	 * 
	 * @param revenue
	 * @param locale
	 */
	public RevenueReportGenerator(Revenue revenue, Locale locale) {
		this.wrapper = new RevenueWrapper(locale, revenue);
	}

	@Override
	protected void addReportParameters(Map<Object, Object> params) {
		params.put(REVENUE_DETAILS_DATASOURCE, new JRBeanCollectionDataSource(wrapper.getRevenueDetails()));
		
		params.put("revenue.title", Messages.RevenueReportGenerator_revenueTitle); //$NON-NLS-1$
		params.put("from.title", Messages.RevenueReportGenerator_fromTitle); //$NON-NLS-1$
		params.put("until.title", Messages.RevenueReportGenerator_untilTitle); //$NON-NLS-1$
		params.put("invoice.date.title", Messages.RevenueReportGenerator_invoiceDate); //$NON-NLS-1$
		params.put("invoice.no.title", Messages.RevenueReportGenerator_invoiceNumberTitle); //$NON-NLS-1$
		params.put("client.name.title", Messages.RevenueReportGenerator_clientTitle); //$NON-NLS-1$
		params.put("payment.date.title", Messages.RevenueReportGenerator_paymentDateTitle); //$NON-NLS-1$
		params.put("net.price.title", Messages.RevenueReportGenerator_netPriceTitle); //$NON-NLS-1$
		params.put("taxRate.title", Messages.RevenueReportGenerator_taxRateTitle); //$NON-NLS-1$
		params.put("tax.amount.title", Messages.RevenueReportGenerator_taxAmountTitle); //$NON-NLS-1$
		params.put("gross.price.title", Messages.RevenueReportGenerator_grossPriceTitle); //$NON-NLS-1$
		params.put("sum.title", Messages.RevenueReportGenerator_sumTitle); //$NON-NLS-1$
	}

	@Override
	protected String getReportTemplatePath() {
		return JASPER_PATH;
	}

	@Override
	protected AbstractReportDataSource getReportDataSource() {
		return new RevenueReportDataSource(wrapper);
	}
}
