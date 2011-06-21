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
	private static final String REVENUE_DETAILS_DATASOURCE = "REVENUE_DETAILS_DATASOURCE";

	/**
	 * 
	 */
	private static final String JASPER_PATH = "PeriodicalRevenue.jasper";
	
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
		super();
		this.wrapper = new RevenueWrapper(locale, revenue);
	}

	@Override
	protected void addReportParameters(Map<Object, Object> params) {
		params.put(REVENUE_DETAILS_DATASOURCE, new JRBeanCollectionDataSource(wrapper.getRevenueDetails()));
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
