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
package de.togginho.accounting.reporting.internal;

import java.util.Map;

import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.IncomeStatement;

/**
 * @author thorsten
 *
 */
public class IncomeStatementSummaryDataSource extends AbstractReportDataSource {
	
	private IncomeStatementWrapper wrapper;
	
	/**
     * @param incomeStatement
     */
    public IncomeStatementSummaryDataSource(IncomeStatement incomeStatement) {
	    this.wrapper = new IncomeStatementWrapper(incomeStatement);
    }
    
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.internal.AbstractReportDataSource#addFieldsToMap(java.util.Map)
	 */
	@Override
	protected void addFieldsToMap(Map<String, Object> fieldMap) {
		fieldMap.put("incomeStatement", wrapper); //$NON-NLS-1$
		fieldMap.put("incomeStatementSummary.title", Messages.IncomeStatementSummary_title); //$NON-NLS-1$
		fieldMap.put("from.title", Messages.fromTitle); //$NON-NLS-1$
		fieldMap.put("until.title", Messages.untilTitle); //$NON-NLS-1$
		fieldMap.put("revenue.title", Messages.revenueTitle); //$NON-NLS-1$
		fieldMap.put("expenses.title", Messages.IncomeStatement_expenses); //$NON-NLS-1$
		fieldMap.put("operating.expenses.title", ExpenseType.OPEX.getTranslatedString()); //$NON-NLS-1$
		fieldMap.put("total.expenses.title", Messages.IncomeStatement_totalExpenses); //$NON-NLS-1$
		fieldMap.put("vat.title", Messages.taxTitle); //$NON-NLS-1$
		fieldMap.put("input.tax.title", Messages.IncomeStatement_inputTax); //$NON-NLS-1$
		fieldMap.put("output.tax.title", Messages.IncomeStatement_outputTax); //$NON-NLS-1$
		fieldMap.put("tax.sum.title", Messages.IncomeStatement_taxSum); //$NON-NLS-1$
		fieldMap.put("result.title", Messages.IncomeStatement_vatSum); //$NON-NLS-1$
		fieldMap.put("operating.profit.title", Messages.operatingProfitTitle); //$NON-NLS-1$
		fieldMap.put("net.title", Messages.IncomeStatement_net); //$NON-NLS-1$
		fieldMap.put("gross.title", Messages.IncomeStatement_gross); //$NON-NLS-1$
		fieldMap.put("tax.title", Messages.IncomeStatement_tax); //$NON-NLS-1$
	}

}