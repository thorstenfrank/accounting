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
	 */
	public RevenueReportGenerator(Revenue revenue) {
		this.wrapper = new RevenueWrapper(revenue);
	}

	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#addReportParameters(java.util.Map)
	 */
	@Override
	protected void addReportParameters(Map<String, Object> params) {
		params.put(REVENUE_DETAILS_DATASOURCE, new JRBeanCollectionDataSource(wrapper.getRevenueDetails()));
	}

	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#getReportTemplatePath()
	 */
	@Override
	protected String getReportTemplatePath() {
		return JASPER_PATH;
	}

	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#getReportDataSource()
	 */
	@Override
	protected AbstractReportDataSource getReportDataSource() {
		return new RevenueReportDataSource(wrapper);
	}
}
