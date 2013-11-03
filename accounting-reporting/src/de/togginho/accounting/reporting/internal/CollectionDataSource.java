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

import java.util.Collection;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.log4j.Logger;

/**
 * @author thorsten
 *
 */
public class CollectionDataSource implements JRDataSource {

	private static final Logger LOG = Logger.getLogger(CollectionDataSource.class);

	private Collection<ModelWrapper> wrapper;
	
	private Iterator<ModelWrapper> iterator;
	
	private ModelWrapper current;
	
	/**
	 * @param beanCollection
	 */
	public CollectionDataSource(Collection<ModelWrapper> beanCollection) {
		this.wrapper = beanCollection;
		this.iterator = wrapper.iterator();
	}
	
	/**
     * {@inheritDoc}.
     * @see net.sf.jasperreports.engine.data.JRBeanCollectionDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
     */
    @Override
    public Object getFieldValue(JRField field) throws JRException {
    	LOG.debug("getFieldValue: " + field.getName());
	    return current;
    }

	/**
     * {@inheritDoc}.
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
    public boolean next() throws JRException {
    	if (iterator.hasNext()) {
    		current = iterator.next();
    		return true;
    	}
	    return false;
    }
}
