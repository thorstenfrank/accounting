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

/**
 * @author thorsten
 *
 */
public class IncomeStatementDataSource extends AbstractReportDataSource {

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.AbstractReportDataSource#addFieldsToMap(java.util.Map)
	 */
	@Override
	protected void addFieldsToMap(Map<String, Object> fieldMap) {
		fieldMap.put("incomeStatement", new IncomeStatementWrapper());
		fieldMap.put("incomeStatement.title", "Abschlussbericht");
		fieldMap.put("from.title", "Von:");
		fieldMap.put("until.title", "Bis:");
		fieldMap.put("net.title", "Netto");
		fieldMap.put("tax.title", "USt.");
		fieldMap.put("gross.title", "Brutto");
		fieldMap.put("revenue.title", "Umsatzerlöse");
		fieldMap.put("opex.title", "Betriebsausgaben");
		fieldMap.put("otherExpenses.title", "Sonstige Ausgaben");
		fieldMap.put("ebitda.title", "EBITDA");
		fieldMap.put("ebit.title", "EBIT");
		fieldMap.put("depreciation.title", "Abschreibungen");
	}

}
