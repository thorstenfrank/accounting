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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.log4j.Logger;

import de.togginho.commons.resources.MessageResources;

/**
 * Abstract base class for report data sources that make use of resource bundles to add field values to a report. Any
 * pseudo-static content (meaning static for a specific locale) should be kept in a resource bundle that has the same
 * name as the implementing class.
 * 
 * <p>
 * For example, if your implementing class is <code>foo.bar.MyDataSource</code>, you should have a properties file named
 * <code>foo/bar/MyDataSource.properties</code> or <code>foo/bar/MyDataSource_[locale]</code> that contains any
 * key-value pairs used by {@link #getFieldValue(JRField)}.
 * </p>
 * 
 * <p>
 * After having added static field content, {@link #addFieldsToMap(Map)} is called on the implementing class to allow
 * for the addition of further dynamic fields as well.
 * </p>
 * 
 * <p>
 * The field map is not to be confused with the report parameter map used by <code>JRDataSource</code>.
 * </p>
 * 
 * @author thorsten frank
 * 
 * @see JRDataSource
 * 
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
	private MessageResources messageResources;

	/**
     * 
     */
	protected void init() {
		LOG.debug(String.format("Initializing data source [%s]", this.getClass().getName()));
		
		initMessageResources();
		fillFieldMap();
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
			LOG.debug(String.format("Field name: [%s] ::: Value: [%s]", field.getName(), value));
			return value;
		}

		LOG.warn(String.format("No value found for field name [%s]", field.getName()));

		return null;
	}

	/**
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
	public boolean next() throws JRException {
		if (running) {
			LOG.debug(String.format("Data source [%s] has run once, will flag as finished", this.getClass().getName()));
			running = false;
			return true;
		}

		LOG.debug(String.format("Data source [%s] has finished", this.getClass().getName()));
		
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

	/**
	 * Returns the active locale for the generated report. This is used to look up the proper resource files.
	 * 
	 * @return the locale used for report generation
	 */
	protected abstract Locale getLocaleForReport();

	/**
	 * Returns the message resources used for this data source, especially the filling of the field map.
	 * 
	 * <p>
	 * Subclasses should use this method to access localized field values that require special handling in
	 * {@link #addFieldsToMap(Map)}.
	 * </p>
	 * 
	 * @return the {@link MessageResources} used for filling the
	 */
	protected MessageResources getMessageResources() {
		return messageResources;
	}

	/**
	 * 
	 */
	private void initMessageResources() {
		final String baseName = this.getClass().getName();
		LOG.debug(String.format("Getting message resources for data source [%s] and locale [%s]", baseName,
				getLocaleForReport().toString()));
		try {
			messageResources = new MessageResources(baseName, getLocaleForReport());
		} catch (MissingResourceException e) {
			LOG.warn(String.format("No resource bundle found for data source [%s]", baseName), e);
		}
	}

	/**
	 * Fills the field map with static content.
	 */
	private void fillFieldMap() {
		this.fieldMap = new HashMap<String, Object>();

		if (messageResources == null) {
			LOG.debug("No resources used for this report, skipping...");
			return;
		}
		
		LOG.debug("Filling field map with resource values");

		for (String key : messageResources.keySet()) {
			String value = messageResources.getString(key);
			LOG.debug(String.format("Adding to field map - Key: [%s], Value: [%s]", key, value));
			fieldMap.put(key, value);
		}

	}
}
