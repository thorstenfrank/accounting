/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.expense;

import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.ui.reports.ReportGenerationHandler;
import de.tfsw.accounting.ui.reports.ReportGenerationUtil;

/**
 * @author thorsten
 *
 */
class ExpensesReportGenerationHandler implements ReportGenerationHandler {

	private ExpenseCollection expenseCollection;
	
	private String baseFileName;
	
	/**
     * @param expenseCollection
     * @param baseFileName
     */
    protected ExpensesReportGenerationHandler(ExpenseCollection expenseCollection, String baseFileName) {
	    this.expenseCollection = expenseCollection;
	    this.baseFileName = baseFileName;
    }

	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.reports.ReportGenerationHandler#getTargetFileNameSuggestion()
	 */
	@Override
	public String getTargetFileNameSuggestion() {
		return ReportGenerationUtil.appendTimeFrameToFileNameSuggestion(baseFileName, expenseCollection.getTimeFrame());
	}

	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.reports.ReportGenerationHandler#getModelObject()
	 */
	@Override
	public Object getModelObject() {
		return expenseCollection;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.reports.ReportGenerationHandler#getReportId()
	 */
	@Override
	public String getReportId() {
		return "de.tfsw.accounting.reporting.Expenses";
	}

}
