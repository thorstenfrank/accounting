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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author thorsten
 *
 */
public class IncomeStatementReportGenerator extends AbstractReportGenerator {

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#addReportParameters(java.util.Map)
	 */
	@Override
	protected void addReportParameters(Map<String, Object> params) {
		IncomeStatementDetailsWrapper w1 = new IncomeStatementDetailsWrapper();
		w1.setCategory("KFZ");
		w1.setNet("€ 5.000");
		w1.setTax("€ 950");
		w1.setGross("€ 5.950");
		
		IncomeStatementDetailsWrapper w2 = new IncomeStatementDetailsWrapper();
		w2.setCategory("Telco");
		w2.setNet("€ 5.000");
		w2.setTax("€ 950");
		w2.setGross("€ 5.950");
		
		List<IncomeStatementDetailsWrapper> opex = new ArrayList<IncomeStatementDetailsWrapper>();
		opex.add(w1);
		opex.add(w2);
		
		params.put("OPEX_DETAILS", new JRBeanCollectionDataSource(opex));
		
		IncomeStatementDetailsWrapper w3 = new IncomeStatementDetailsWrapper();
		w3.setCategory("Krankenversicherung");
		w3.setNet("€ 5.000");
		w3.setTax("€ 950");
		w3.setGross("€ 5.950");
		
		IncomeStatementDetailsWrapper w4 = new IncomeStatementDetailsWrapper();
		w4.setCategory("Lebensversicherung");
		w4.setNet("€ 5.000");
		w4.setTax("€ 950");
		w4.setGross("€ 5.950");
		
		List<IncomeStatementDetailsWrapper> other = new ArrayList<IncomeStatementDetailsWrapper>();
		other.add(w3);
		other.add(w4);
		params.put("OTHER_EXPENSES_DETAILS", new JRBeanCollectionDataSource(other));
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#getReportTemplatePath()
	 */
	@Override
	protected String getReportTemplatePath() {
		return "IncomeStatementTemplate.jasper";
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#getReportDataSource()
	 */
	@Override
	protected AbstractReportDataSource getReportDataSource() {
		return new IncomeStatementDataSource();
	}

}
