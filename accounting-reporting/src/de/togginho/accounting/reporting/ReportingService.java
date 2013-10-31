/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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

import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.model.IncomeStatement;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.Revenue;

/**
 * @author thorsten
 *
 */
public interface ReportingService {
	
	/**
	 * 
	 * @param invoice
	 * @param fileLocation
	 * @param monitor
	 */
	void generateInvoiceToPdf(Invoice invoice, String fileLocation, ReportGenerationMonitor monitor);
	
	/**
	 * 
	 * @param revenue
	 * @param fileLocation
	 * @param monitor
	 */
	void generateRevenueToPdf(Revenue revenue, String fileLocation, ReportGenerationMonitor monitor);
	
	/**
	 * 
	 * @param expenseCollection
	 * @param fileLocation
	 * @param monitor
	 */
	void generateExpensesToPdf(ExpenseCollection expenseCollection, String fileLocation, ReportGenerationMonitor monitor);
		
	/**
	 * 
	 * @param incomeStatement
	 * @param fileLocation
	 * @param monitor
	 */
	void generateIncomeStatementSummaryToPdf(IncomeStatement incomeStatement, String fileLocation, ReportGenerationMonitor monitor);
	
	/**
	 * 
	 * @param incomeStatement
	 * @param fileLocation
	 * @param monitor
	 */
	void generateIncomeStatementToPdf(IncomeStatement incomeStatement, String fileLocation, ReportGenerationMonitor monitor);
}
