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
package de.togginho.accounting.reporting.internal;

import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.model.IncomeStatement;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.Revenue;
import de.togginho.accounting.model.User;
import de.togginho.accounting.reporting.ReportGenerationMonitor;
import de.togginho.accounting.reporting.ReportingService;

/**
 * @author thorsten
 *
 */
public class ReportingServiceImpl implements ReportingService {

	/**
	 * {@inheritDoc}.
	 * @see ReportingService#generateInvoiceToPdf(Invoice, java.lang.String, ReportGenerationMonitor)
	 */
	@Override
	public void generateInvoiceToPdf(Invoice invoice, String fileLocation, ReportGenerationMonitor monitor) {
		doGenerateReport(new InvoiceGenerator(invoice), fileLocation, monitor);
	}

	/**
     * {@inheritDoc}.
     * @see ReportingService#generateRevenueToPdf(Revenue, java.lang.String, ReportGenerationMonitor)
     */
    @Override
    public void generateRevenueToPdf(Revenue revenue, String fileLocation, ReportGenerationMonitor monitor) {
    	doGenerateReport(new RevenueReportGenerator(revenue), fileLocation, monitor);
    }

    /**
     * 
     * {@inheritDoc}
     * @see ReportingService#generateExpensesToPdf(ExpenseCollection, String, ReportGenerationMonitor)
     */
    @Override
    public void generateExpensesToPdf(ExpenseCollection expenseCollection, String fileLocation, ReportGenerationMonitor monitor) {
    	doGenerateReport(new ExpensesReportGenerator(expenseCollection), fileLocation, monitor);
    }
    
    /**
     * {@inheritDoc}.
     * @see ReportingService#generateIncomeStatementSummaryToPdf(IncomeStatement, String, ReportGenerationMonitor)
     */
    @Override
    public void generateIncomeStatementSummaryToPdf(IncomeStatement incomeStatement, String fileLocation, ReportGenerationMonitor monitor) {
    	doGenerateReport(new IncomeStatementSummaryReportGenerator(incomeStatement), fileLocation, monitor);
    }

	/**
     * 
     * {@inheritDoc}.
     * @see ReportingService#generateIncomeStatementToPdf(IncomeStatement, String, ReportGenerationMonitor)
     */
    @Override
    public void generateIncomeStatementToPdf(IncomeStatement incomeStatement, String fileLocation, ReportGenerationMonitor monitor) {
    	doGenerateReport(new IncomeStatementReportGenerator(incomeStatement), fileLocation, monitor);
    }

	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.reporting.ReportingService#generateLetterhead(de.togginho.accounting.model.User, java.lang.String, de.togginho.accounting.reporting.ReportGenerationMonitor)
     */
    @Override
    public void generateLetterhead(User user, String fileLocation, ReportGenerationMonitor monitor) {
    	doGenerateReport(new LetterheadReportGenerator(user), fileLocation, monitor);
    }
    
    /**
     * 
     * @param generator
     * @param fileLocation
     * @param monitor
     */
    private void doGenerateReport(AbstractReportGenerator generator, String fileLocation, ReportGenerationMonitor monitor) {
    	generator.generateReportToFile(fileLocation, monitor);
    }
}
