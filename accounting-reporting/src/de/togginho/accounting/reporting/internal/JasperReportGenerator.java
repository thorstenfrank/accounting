/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.reporting.ReportGenerationMonitor;
import de.togginho.accounting.reporting.xml.generated.Report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author thorsten
 *
 */
public class JasperReportGenerator implements JRDataSource {

	private static final Logger LOG = Logger.getLogger(JasperReportGenerator.class);
	
	private Report report;
	
	private ModelWrapper wrapper;
	
	private boolean running;
	
	private Map<String, Object> fieldMap;
	
	/**
	 * 
	 * @param report
	 */
	JasperReportGenerator(Report report, Object model) {
		this.report = report;
		wrapper = new ModelWrapper(model);
	}
	
	/**
	 * 
	 * @param fileName
	 * @param monitor
	 */
	protected void generateReport(String fileName, ReportGenerationMonitor monitor) {
		LOG.info(String.format("Starting report generation: [%s]", this.getClass().getName())); //$NON-NLS-1$
		
		monitor.startingReportGeneration();
		
		this.running = true;
		this.fieldMap = new HashMap<String, Object>();
		
		monitor.loadingTemplate();
		InputStream in = getTemplateAsStream();
		if (in == null) {
			LOG.error(String.format("Could not load template: [%s]", report.getTemplate())); //$NON-NLS-1$
			throw new AccountingException(
					Messages.bind(Messages.JasperReportGenerator_errorTemplateNotFound, report.getTemplate()));
		}
		
		monitor.addingReportParameters();		

		// add report params
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("wrapper", wrapper);
		
		monitor.fillingReport();
		JasperPrint print = null;
        try {
	        print = JasperFillManager.fillReport(in, params, this);
        } catch (JRException e) {
        	LOG.error("Error filling report", e); //$NON-NLS-1$
        	closeInputStream(in);
        	throw new AccountingException(
        			Messages.bind(Messages.JasperReportGenerator_errorFillingReport, report.getTemplate()), e);
        }
		
		try {
			monitor.exportingReport();
			LOG.debug(String.format("Exporting report to file [%s]", fileName));
//			JRPdfExporter exporter = new JRPdfExporter();
//			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
//			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);
//			exporter.setParameter(JRExporterParameter.PROGRESS_MONITOR, new AccountingScriptlet());
//			exporter.exportReport();
			JasperExportManager.exportReportToPdfFile(print, fileName);
			
		} catch (JRException e) {
			LOG.error("Error occured during report generation", e);
			closeInputStream(in);
			throw new AccountingException(Messages.bind(
					Messages.JasperReportGenerator_errorCreatingReport, report.getTemplate()), e);
		}
		
		monitor.exportFinished();
		LOG.info("Report generation finished successfully");
	}
	
	/**
	 * 
	 * @return
	 */
	private InputStream getTemplateAsStream() {
		ClassLoader loader = this.getClass().getClassLoader();		
		LOG.debug(String.format("Using ClassLoader [%s]", loader.toString()));
		
		LOG.debug(String.format("Now attempting to load template [%s]", report.getTemplate()));
		
		final URL url = loader.getResource(report.getTemplate());
		if (url != null) {
			LOG.debug(String.format("Found template at URL [%s]", url.toString()));
		} else {
			LOG.error("Template URL not found: " + report.getTemplate());
			throw new AccountingException(
					Messages.bind(Messages.JasperReportGenerator_errorTemplateNotFound, report.getTemplate()));
		}
		
		return loader.getResourceAsStream(report.getTemplate());
	}
		
	/**
	 * 
	 * @param in
	 */
	private void closeInputStream(final InputStream in) {
		try {
	        in.close();
        } catch (IOException e) {
        	LOG.warn("Error closing input stream, will ignore", e); //$NON-NLS-1$
        }
	}
	
	/**
	 * {@inheritDoc}.
	 * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
	 */
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		if (fieldMap.containsKey(field.getName())) {
			Object value = fieldMap.get(field.getName());
			LOG.debug(String.format("Field name: [%s] ::: Value: [%s]", field.getName(), value)); //$NON-NLS-1$
			return value;
		}

		LOG.warn(String.format("No value found for field name [%s]", field.getName())); //$NON-NLS-1$

		return null;
	}

	/**
	 * {@inheritDoc}.
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
	@Override
	public boolean next() throws JRException {
		if (running) {
			LOG.debug(String.format("Data source [%s] has run once, will flag as finished", //$NON-NLS-1$
					this.getClass().getName()));
			running = false;
			return true;
		}

		LOG.debug(String.format("Data source [%s] has finished", this.getClass().getName())); //$NON-NLS-1$
		
		return running;
	}

}
