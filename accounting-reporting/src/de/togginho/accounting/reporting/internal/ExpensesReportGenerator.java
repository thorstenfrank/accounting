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

import de.togginho.accounting.model.ExpenseCollection;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author thorsten
 *
 */
public class ExpensesReportGenerator extends AbstractReportGenerator {

	/**
	 * 
	 */
	private static final String EXPENSE_DETAILS_DATASOURCE = "EXPENSE_DETAILS_DATASOURCE"; //$NON-NLS-1$

	/**
	 * 
	 */
	private static final String JASPER_PATH = "ExpensesTemplate.jasper"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private ExpensesWrapper wrapper;
		
	/**
	 * @param wrapper
	 */
	public ExpensesReportGenerator(ExpenseCollection expenseCollection) {
		this.wrapper = new ExpensesWrapper(expenseCollection);
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.reporting.internal.AbstractReportGenerator#addReportParameters(java.util.Map)
	 */
	@Override
	protected void addReportParameters(Map<String, Object> params) {
		params.put(EXPENSE_DETAILS_DATASOURCE, new JRBeanCollectionDataSource(wrapper.getExpenseDetails()));
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.reporting.internal.AbstractReportGenerator#getReportTemplatePath()
	 */
	@Override
	protected String getReportTemplatePath() {
		return JASPER_PATH;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.reporting.internal.AbstractReportGenerator#getReportDataSource()
	 */
	@Override
	protected AbstractReportDataSource getReportDataSource() {
		return new ExpensesDataSource(wrapper);
	}

}
