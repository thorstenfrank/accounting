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
		fieldMap.put("cashflow.title", "Cash Flow"); //$NON-NLS-1$
		fieldMap.put("from.title", Messages.fromTitle); //$NON-NLS-1$
		fieldMap.put("until.title", Messages.untilTitle); //$NON-NLS-1$
		fieldMap.put("revenue.title", "Umsätze");
		fieldMap.put("expenses.title", "Ausgaben");
		fieldMap.put("operating.expenses.title", "Betriebskosten");
		fieldMap.put("total.expenses.title", "Gesamtkosten");
		fieldMap.put("vat.title", "Umsatzsteuer");
		fieldMap.put("input.tax.title", "Umsatzsteuer:");
		fieldMap.put("output.tax.title", "Vorsteuer:");
		fieldMap.put("taxes.balance", "Saldo:");
		
		fieldMap.put("net.title", "Netto:");
		fieldMap.put("gross.title", "Brutto:");
		fieldMap.put("tax.title", "Steuern:");
	}

}
