/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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

import de.togginho.accounting.model.CashFlowStatement;

/**
 * @author thorsten
 *
 */
public class CashFlowReportGenerator extends AbstractReportGenerator {

	private CashFlowStatement cashFlow;
	
	
	/**
     * 
     */
    public CashFlowReportGenerator(CashFlowStatement cashFlow) {
    	this.cashFlow = cashFlow;
    }

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#addReportParameters(java.util.Map)
	 */
	@Override
	protected void addReportParameters(Map<Object, Object> params) {

	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#getReportTemplatePath()
	 */
	@Override
	protected String getReportTemplatePath() {
		return "CashFlowTemplate.jasper";
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#getReportDataSource()
	 */
	@Override
	protected AbstractReportDataSource getReportDataSource() {
		return new CashFlowDataSource(cashFlow);
	}

}