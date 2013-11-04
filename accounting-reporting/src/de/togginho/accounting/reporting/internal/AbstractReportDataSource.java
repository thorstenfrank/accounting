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
package de.togginho.accounting.reporting.internal;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.log4j.Logger;

/**
 * Abstract base class for report data sources.
 * 
 * @author thorsten frank
 * 
 * @see JRDataSource
 * 
 * @deprecated
 */
abstract class AbstractReportDataSource implements JRDataSource {

	/**
	 * 
	 */
	private final static Logger LOG = Logger.getLogger(AbstractReportDataSource.class);

	/**
     * 
     */
	private Map<String, Object> fieldMap;

	/**
     * 
     */
	private boolean running;

	/**
     * 
     */
	protected void init() {
		LOG.debug(String.format("Initializing data source [%s]", this.getClass().getName())); //$NON-NLS-1$
		
		fieldMap = new HashMap<String, Object>();
		addFieldsToMap(fieldMap);

		this.running = true;
		LOG.debug("data source is ready for report generation");
	}

	/**
	 * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
	 */
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
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
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

	/**
	 * Called during {@link #init()} after static content has been added. Implementations should add any dynamic fields
	 * to the supplied map within this method.
	 * 
	 * @param fieldMap
	 *            the map that is used by {@link #getFieldValue(JRField)} during report generation
	 */
	protected abstract void addFieldsToMap(Map<String, Object> fieldMap);
}
