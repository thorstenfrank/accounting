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

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;

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
	public void generateReportToFile(String fileName) throws ReportingException {
		LOG.info(String.format("Starting report generation: [%s]", this.getClass().getName()));
		
		if (fileName == null || fileName.length() < 1) {
			LOG.error("No destination file name was specified");
			throw new ReportingException(ReportingException.REPORT_FILE_NAME_NULL);
		}
		
		Class<? extends AbstractReportGenerator> genClass = getClass();
		
		LOG.debug(String.format("Searching template for class [%s]", genClass.getName()));
		
		ClassLoader loader = genClass.getClassLoader();
		
		LOG.debug(String.format("Using ClassLoader [%s]", loader.toString()));
		
		final String templatePath = getReportTemplatePath();
		
		if (templatePath == null) {
			LOG.error(String.format("Could not find template: [%s]", templatePath));
			throw new ReportingException(ReportingException.TEMPLATE_NOT_FOUND, templatePath);
		}
		
		LOG.debug(String.format("Now attempting to load template [%s]", templatePath));
		
		URL url = loader.getResource(getReportTemplatePath());
		
		if (url != null) {
			LOG.debug(String.format("Found template at URL [%s]", url.toString()));
		}
		
		InputStream in = loader.getResourceAsStream(templatePath); 
		
		if (in == null) {
			LOG.error(String.format("Could not load template: [%s]", templatePath));
			throw new ReportingException(ReportingException.TEMPLATE_NOT_FOUND, templatePath);
		}
		
		// add report params
		Map<Object, Object> params = new HashMap<Object, Object>();
		addReportParameters(params);
		
		try {
			
			JasperPrint print = null;
			
			AbstractReportDataSource dataSource = getReportDataSource();
			if (dataSource != null) {
				LOG.debug(String.format("Found data source and will now init: [%s]", dataSource.getClass().getName()));
				
				dataSource.init();
				
				LOG.debug("Filling report with data");
				
				print = JasperFillManager.fillReport(in, params, dataSource);
				
			} else {
				LOG.debug("Generator is not using a datasource, will only use param map");
				
				print = JasperFillManager.fillReport(in, params);
			}
			
			LOG.debug(String.format("Exporting report to file [%s]", fileName));
			
			JasperExportManager.exportReportToPdfFile(print, fileName);
			
		} catch (JRException e) {
			LOG.error("Error occured during report generation", e);
			throw new ReportingException(ReportingException.REPORT_GENERATION_FAILED, e);
		}
		
		LOG.info("Report generation finished successfully");
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
	protected abstract void addReportParameters(Map<Object, Object> params);
	
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
