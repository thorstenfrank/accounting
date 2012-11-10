/*
 *  Copyright 2010 thorsten frank (thorsten.frank@gmx.de).
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.ReportGenerationMonitor;

/**
 * Abstract base class of report generators, taking care of the actual Jasper calls. This class is currently only
 * capable of exporting PDF reports.
 * 
 * TODO add more file formats
 * 
 * <p>
 * The basic sequence of {@link #generateReportToFile(String)} is as follows:
 * 
 * <ol>
 * <li>Look up the template file supplied by {@link #getReportTemplatePath()}</li>
 * <li>Add report parameters through {@link #addReportParameters(Map)}</li>
 * <li>If {@link #getReportDataSource()} is not <code>null</code>, call {@link AbstractReportDataSource#init()}</li>
 * <li>Fill and export the actual report to the supplied file using Jasperreports.</li>
 * </ol>
 * </p>
 * 
 * @author thorsten frank
 * 
 * @see AbstractReportDataSource
 *
 */
public abstract class AbstractReportGenerator {

	/**
	 * 
	 */
	private final static Logger LOG = Logger.getLogger(AbstractReportGenerator.class);
	
	/**
	 * Generates a report to a file denoted by the supplied name.
	 * 
	 * @param fileName the name of the file 
	 * 
	 * @throws ReportingException if an error occurs during report generation
	 */
	public void generateReportToFile(String fileName, ReportGenerationMonitor monitor) {
		LOG.info(String.format("Starting report generation: [%s]", this.getClass().getName())); //$NON-NLS-1$
		
		monitor.startingReportGeneration();
		
		monitor.loadingTemplate();
		InputStream in = getTemplateAsStream();
		if (in == null) {
			LOG.error(String.format("Could not load template: [%s]", getReportTemplatePath())); //$NON-NLS-1$
			throw new AccountingException(
					Messages.bind(Messages.AbstractReportGenerator_errorTemplateNotFound, getReportTemplatePath()));
		}
		
		monitor.addingReportParameters();		
		// add report params
		Map<String, Object> params = new HashMap<String, Object>();
		addReportParameters(params);
		
		monitor.fillingReport();
		JasperPrint print = null;
        try {
	        print = fillReport(in, params);
        } catch (JRException e) {
        	LOG.error("Error filling report", e); //$NON-NLS-1$
        	closeInputStream(in);
        	throw new AccountingException(
        			Messages.bind(Messages.AbstractReportGenerator_errorFillingReport, getReportTemplatePath()), e);
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
					Messages.AbstractReportGenerator_errorCreatingReport, getReportTemplatePath()), e);
		}
		
		LOG.info("Report generation finished successfully");
	}

	/**
	 * 
	 * @return
	 */
	private InputStream getTemplateAsStream() {
		final Class<? extends AbstractReportGenerator> genClass = getClass();
		LOG.debug(String.format("Searching template for class [%s]", genClass.getName()));

		ClassLoader loader = genClass.getClassLoader();		
		LOG.debug(String.format("Using ClassLoader [%s]", loader.toString()));
		
		final String templatePath = getReportTemplatePath();
		LOG.debug(String.format("Now attempting to load template [%s]", templatePath));
		
		final URL url = loader.getResource(templatePath);
		if (url != null) {
			LOG.debug(String.format("Found template at URL [%s]", url.toString()));
		} else {
			LOG.error("Template URL not found: " + templatePath);
			throw new AccountingException(
					Messages.bind(Messages.AbstractReportGenerator_errorTemplateNotFound, templatePath));
		}
		
		return loader.getResourceAsStream(templatePath); 
	}
	
	/**
	 * 
	 * @param in
	 * @param params
	 * @return
	 * @throws JRException
	 */
	private JasperPrint fillReport(InputStream in, Map<String, Object> params) throws JRException {
		AbstractReportDataSource dataSource = getReportDataSource();
		if (dataSource != null) {
			LOG.debug(String.format("Found data source and will now init: [%s]", dataSource.getClass().getName()));
			
			dataSource.init();
			
			LOG.debug("Filling report with data");
			
			return JasperFillManager.fillReport(in, params, dataSource);
			
		} else {
			LOG.debug("Generator is not using a datasource, will only use param map");
			
			return JasperFillManager.fillReport(in, params);
		}
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
	 * Implementations should use this optional method to add any needed parameters to the supplied map, which will be 
	 * passed to the reporting engine.
	 * 
	 * <p>
	 * This method is optional in that subclasses aren't forced to add any parameters, if they aren't needed during the
	 * generation phase.
	 * </p>
	 *  
	 * <p>
	 * See {@link JasperFillManager#fillReport(InputStream, Map, net.sf.jasperreports.engine.JRDataSource)} for details
	 * on using report params.
	 * </p>
	 * 
	 * @param params the map to add parameters to
	 */
	protected abstract void addReportParameters(Map<String, Object> params);
	
	/**
	 * The fully accessible path to a precompiled Jasper template, used by {@link #generateReportToFile(String)} to
	 * fill and then generate the actual report.
	 * 
	 * <p>
	 * The string returned by this method is used through {@link ClassLoader#getResourceAsStream(String)}, so
	 * implementations must make sure that the jasper template can be found through their class loader.
	 * </p>
	 * 
	 * @return	the path to the precompiled report template file
	 */
	protected abstract String getReportTemplatePath();
	
	/**
	 * Optional method for subclasses to provide an {@link AbstractReportDataSource}, which will provide dynamic
	 * content to the report if needed. If this method returns <code>null</code>, only the report parameters will
	 * be used.
	 * 
	 * @return	the data source responsible for supplying dynamic content to the report generation, or 
	 * 			<code>null</code> if no data source is needed
	 */
	protected abstract AbstractReportDataSource getReportDataSource();
}
