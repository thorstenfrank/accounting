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

import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.IncomeStatement;

/**
 * @author thorsten
 *
 */
public class IncomeStatementDataSource extends AbstractReportDataSource {

	private IncomeStatementWrapper wrapper;
		
	/**
     * @param wrapper
     */
    IncomeStatementDataSource(IncomeStatementWrapper wrapper) {
	    this.wrapper = wrapper;
    }



	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportDataSource#addFieldsToMap(java.util.Map)
	 */
	@Override
	protected void addFieldsToMap(Map<String, Object> fieldMap) {
		fieldMap.put("incomeStatement", wrapper);
		fieldMap.put("incomeStatement.title", Messages.IncomeStatement_title);
		fieldMap.put("net.title", Messages.netTitle);
		fieldMap.put("tax.title", Messages.taxTitle);
		fieldMap.put("gross.title", Messages.grossTitle);
		
		IncomeStatement statement = wrapper.getIncomeStatement();
		
		if (statement.getRevenue() != null) {
			fieldMap.put("revenue.title", Messages.revenueTitle);
		}
		if (notEmpty(statement.getOperatingExpenses())) {
			fieldMap.put("opex.title", ExpenseType.OPEX.getTranslatedString());
		}
		if (notEmpty(statement.getCapitalExpenses())) {
			fieldMap.put("capex.title", ExpenseType.CAPEX.getTranslatedString());
		}
		if (notEmpty(statement.getOtherExpenses())) {
			fieldMap.put("otherExpenses.title", ExpenseType.OTHER.getTranslatedString());
		}
		
		fieldMap.put("ebitda.title", Messages.grossProfitTitle);
		
//		fieldMap.put("ebit.title", "EBIT");
//		fieldMap.put("depreciation.title", "Abschreibungen");
	}

	private boolean notEmpty(ExpenseCollection ec) {
		return ec != null && ec.getExpenses() != null && ec.getExpenses().isEmpty() == false;
	}
}
