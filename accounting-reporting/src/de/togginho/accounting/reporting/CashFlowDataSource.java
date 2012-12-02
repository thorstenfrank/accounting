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
import de.togginho.accounting.model.ExpenseType;

/**
 * @author thorsten
 *
 */
public class CashFlowDataSource extends AbstractReportDataSource {

	/**
	 * 
	 */
	private CashFlowWrapper wrapper;
	
	/**
	 * 
	 * @param cashFlow
	 */
	public CashFlowDataSource(CashFlowStatement cashFlow) {
		this.wrapper = new CashFlowWrapper(cashFlow);
	}
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportDataSource#addFieldsToMap(java.util.Map)
	 */
	@Override
	protected void addFieldsToMap(Map<String, Object> fieldMap) {
		fieldMap.put("cashflow", wrapper); //$NON-NLS-1$
		fieldMap.put("cashflow.title", Messages.CashFlowDataSource_cashFlow); //$NON-NLS-1$
		fieldMap.put("from.title", Messages.fromTitle); //$NON-NLS-1$
		fieldMap.put("until.title", Messages.untilTitle); //$NON-NLS-1$
		fieldMap.put("revenue.title", Messages.revenueTitle); //$NON-NLS-1$
		fieldMap.put("expenses.title", Messages.CashFlowDataSource_expenses); //$NON-NLS-1$
		fieldMap.put("operating.expenses.title", ExpenseType.OPEX.getTranslatedString()); //$NON-NLS-1$
		fieldMap.put("total.expenses.title", Messages.CashFlowDataSource_totalExpenses); //$NON-NLS-1$
		fieldMap.put("vat.title", Messages.CashFlowDataSource_vat); //$NON-NLS-1$
		fieldMap.put("input.tax.title", Messages.CashFlowDataSource_inputTax); //$NON-NLS-1$
		fieldMap.put("output.tax.title", Messages.CashFlowDataSource_outputTax); //$NON-NLS-1$
		fieldMap.put("tax.sum.title", Messages.CashFlowDataSource_taxSum); //$NON-NLS-1$
		fieldMap.put("result.title", Messages.CashFlowDataSource_vatSum); //$NON-NLS-1$
		fieldMap.put("gross.profit.title", Messages.grossProfitTitle); //$NON-NLS-1$
		fieldMap.put("net.title", Messages.CashFlowDataSource_net); //$NON-NLS-1$
		fieldMap.put("gross.title", Messages.CashFlowDataSource_gross); //$NON-NLS-1$
		fieldMap.put("tax.title", Messages.CashFlowDataSource_tax); //$NON-NLS-1$
	}

}
