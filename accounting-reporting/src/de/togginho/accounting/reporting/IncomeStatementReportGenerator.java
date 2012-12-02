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

import de.togginho.accounting.model.IncomeStatement;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.util.FormatUtil;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author thorsten
 *
 */
public class IncomeStatementReportGenerator extends AbstractReportGenerator {

	private IncomeStatementWrapper wrapper;
	
	/**
	 * 
	 * @param incomeStatement
	 */
	public IncomeStatementReportGenerator(IncomeStatement incomeStatement) {
		this.wrapper = new IncomeStatementWrapper(incomeStatement);
	}
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportGenerator#addReportParameters(java.util.Map)
	 */
	@Override
	protected void addReportParameters(Map<String, Object> params) {
		IncomeStatement statement = wrapper.getIncomeStatement();
		
		if (statement.getOperatingExpenseCategories() != null) {
			params.put("OPEX_DETAILS", 
					new JRBeanCollectionDataSource(buildWrapperList(statement.getOperatingExpenseCategories())));
		}
		
		if (statement.getCapitalExpenseCategories() != null) {
			params.put("CAPEX_DETAILS", 
					new JRBeanCollectionDataSource(buildWrapperList(statement.getCapitalExpenseCategories())));
		}
		
		if (statement.getOtherExpenseCategories() != null) {
			params.put("OTHER_EXPENSES_DETAILS", 
					new JRBeanCollectionDataSource(buildWrapperList(statement.getOtherExpenseCategories())));
		}		
		
//		IncomeStatementDetailsWrapper w5 = new IncomeStatementDetailsWrapper();
//		w5.setCategory("Alter Rechner");
//		w5.setNet("€ 1.000");
//		w5.setGross("€ 1.000");
//		
//		IncomeStatementDetailsWrapper w6 = new IncomeStatementDetailsWrapper();
//		w6.setCategory("Älterer Rechner");
//		w6.setNet("€ 937,48");
//		w6.setGross("€ 937,48");
//		
//		List<IncomeStatementDetailsWrapper> depreciation = new ArrayList<IncomeStatementDetailsWrapper>();
//		depreciation.add(w5);
//		depreciation.add(w6);
//		params.put("DEPRECIATION_DETAILS", new JRBeanCollectionDataSource(depreciation));
	}

	/**
	 * 
	 * @param expensesCategory
	 * @return
	 */
	private List<IncomeStatementDetailsWrapper> buildWrapperList(Map<String, Price> expensesCategory) {
		if (expensesCategory == null) {
			return null;
		}
		
		List<IncomeStatementDetailsWrapper> wrapperList = new ArrayList<IncomeStatementDetailsWrapper>();
		for (String category : expensesCategory.keySet()) {
			IncomeStatementDetailsWrapper wrapper = new IncomeStatementDetailsWrapper();
			wrapper.setCategory(category);
			Price price = expensesCategory.get(category);
			wrapper.setNet(FormatUtil.formatCurrency(price.getNet()));
			wrapper.setTax(FormatUtil.formatCurrency(price.getTax()));
			wrapper.setGross(FormatUtil.formatCurrency(price.getGross()));
			wrapperList.add(wrapper);
		}
		return wrapperList;
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
		return new IncomeStatementDataSource(wrapper);
	}

}
