/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.elster.adapter;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import de.tfsw.accounting.AccountingException;

/**
 * Base class for concrete adapters of the VAT part of the German electronic tax system (ELSTER).
 * Most implementations will only have to implement {@link #mapToInterfaceModel()} and map the input data from
 * the suplied {@link ElsterDTO} to the respective XML interface model, which changes at least yearly.
 * 
 * <p>
 * This base implementation takes care primarily of marshalling mapped data to an actual XML.
 * </p>
 * 
 * @param T the class of the root XML element that a concrete implementation will return
 * 
 * @author Thorsten Frank
 *
 */
public abstract class AbstractElsterAdapter<T> implements ElsterAdapter {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = Logger.getLogger(AbstractElsterAdapter.class);
	
	/**
	 * Default encoding used for the resulting XML as defined by the ELSTER interface spec.
	 */
	private static final String ENCODING = "ISO-8859-15"; //$NON-NLS-1$
	
	/**
	 * Cache of JAXB contexts.
	 */
	@SuppressWarnings("rawtypes")
	private static final Map<Class, JAXBContext> CONTEXT_MAP = new HashMap<Class, JAXBContext>();
	
	/**
	 * Data container.
	 */
	private ElsterDTO data;
	
	/**
	 * 
	 * @return the data to be mapped to XML by this adapter
	 */
	public ElsterDTO getData() {
		return data;
	}
	
	/**
	 * Subclasses need to map data as contained in {@link #getData()} to their concrete JAXB interface model and
	 * return the root element of that model.
	 * 
	 * @return the root element of the concrete JAXB interface model of this adapter
	 */
	protected abstract T mapToInterfaceModel();
	
	/**
	 * Subclasses may override this method, but must make sure to call <code>super()</code>.
	 * 
	 * @see de.tfsw.accounting.elster.adapter.ElsterAdapter#init(de.tfsw.accounting.elster.adapter.ElsterDTO)
	 */
	protected void init(ElsterDTO data) {
		this.data = data;
	}

	/**
	 * Returns the tax number generator applicable for this specific adapter implementation.
	 *  
	 * @return the tax number converter for this specific adapter instance 
	 */
	protected TaxNumberConverter getTaxNumberConverter() {
		return new DefaultTaxNumberConverter();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see de.tfsw.accounting.elster.adapter.ElsterAdapter#writeDataToXML(java.lang.String)
	 */
	@Override
	public void writeDataToXML(String targetFilePath) {
		try {
			T interfaceModel = mapToInterfaceModel();
			
			getMarshaller(interfaceModel).marshal(interfaceModel, new File(targetFilePath));
		} catch (Exception e) {
			LOG.error("Error creating/writing UStVA export file", e); //$NON-NLS-1$
			throw new AccountingException(Messages.AbstractElsterAdapter_errorCreatingXML, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see de.tfsw.accounting.elster.adapter.ElsterAdapter#writeDataToXML()
	 */
	@Override
	public String writeDataToXML() {
		StringWriter sw = new StringWriter();
		
		try {
			T interfaceModel = mapToInterfaceModel();
			getMarshaller(interfaceModel).marshal(interfaceModel, sw);
		} catch (JAXBException e) {
			LOG.error("Error marshalling ELSTER XML", e); //$NON-NLS-1$
			sw.write(e.toString());
		}
		
		return sw.toString();
	}
	
	/**
	 * 
	 * @param interfaceModel
	 * @return
	 * @throws JAXBException
	 */
	private Marshaller getMarshaller(T interfaceModel) throws JAXBException {
				
		JAXBContext jaxbContext = CONTEXT_MAP.get(interfaceModel.getClass());
		
		if (jaxbContext == null) {
			jaxbContext = JAXBContext.newInstance(interfaceModel.getClass());
			CONTEXT_MAP.put(interfaceModel.getClass(), jaxbContext);
		}
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
		return marshaller;
	}
}
