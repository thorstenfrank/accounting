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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.model.IncomeStatement;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.Revenue;
import de.togginho.accounting.reporting.ReportGenerationMonitor;
import de.togginho.accounting.reporting.ReportingService;
import de.togginho.accounting.reporting.xml.generated.AccountingReports;
import de.togginho.accounting.reporting.xml.generated.Report;

/**
 * @author thorsten
 *
 */
public class ReportingServiceImpl implements ReportingService {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(ReportingServiceImpl.class);
	
	/**
	 * 
	 */
	private AccountingReports availableReports;

	/**
     * @param availableReports the availableReports to set
     */
    public void setAvailableReports(AccountingReports availableReports) {
    	this.availableReports = availableReports;
    }
	
	/**
     * @return the availableReports
     */
    public Map<String, String> getAvailableReports() {
    	Map<String, String> map = new HashMap<String, String>();
    	
    	for (Report report : availableReports.getReports().getReport()) {
			String name = report.getId();

			try {
	            name = (String) Messages.class.getField(report.getId()).get(new String());
            } catch (IllegalArgumentException e) {
            	LOG.error("Error translating report name for " + name, e);
            } catch (IllegalAccessException e) {
            	LOG.error("Error translating report name for " + name, e);
            } catch (NoSuchFieldException e) {
            	LOG.warn("No proper translation provided for report ID: " + name, e);
            } catch (SecurityException e) {
            	LOG.error("Error translating report name for " + name, e);
            }
			
			map.put(report.getId(), name);
    	}
    	
    	return map;
    }
    
	/**
     * {@inheritDoc}.
     * @see ReportingService#generateReport(String, Object, String, ReportGenerationMonitor)
     */
    @Override
    public void generateReport(String reportId, Object model, String fileLocation, ReportGenerationMonitor monitor) {
	    JasperReportGenerator generator = new JasperReportGenerator(getReportById(reportId), model);
	    generator.generateReport(fileLocation, monitor);
    }

    /**
     * 
     * @param id
     * @return
     */
    private Report getReportById(String id) {
    	for (Report report : availableReports.getReports().getReport()) {
    		if (report.getId().equals(id)) {
    			return report;
    		}
    	}
    	
    	return null;
    }
    
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
     * 
     * @param generator
     * @param fileLocation
     * @param monitor
     */
    private void doGenerateReport(AbstractReportGenerator generator, String fileLocation, ReportGenerationMonitor monitor) {
    	generator.generateReportToFile(fileLocation, monitor);
    }
}
