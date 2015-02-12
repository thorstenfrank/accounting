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
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import de.tfsw.accounting.AccountingException;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public abstract class AbstractElsterAdapter<T> implements ElsterAdapter {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(AbstractElsterAdapter.class);
	
	/**
	 * 
	 */
	private static final String ENCODING = "ISO-8859-15";
	
	/**
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private static final Map<Class, JAXBContext> CONTEXT_MAP = new HashMap<Class, JAXBContext>();
	
	/**
	 * 
	 */
	private ElsterDTO data;
	
	
	
	/**
	 * @see de.tfsw.accounting.elster.adapter.ElsterAdapter#init(de.tfsw.accounting.elster.adapter.ElsterDTO)
	 */
	@Override
	public void init(ElsterDTO data) {
		this.data = data;
	}

	/**
	 * @see de.tfsw.accounting.elster.adapter.ElsterAdapter#writeDataToXML(java.lang.String)
	 */
	@Override
	public void writeDataToXML(String targetFilePath) {
		try {
			T interfaceModel = mapToInterfaceModel();
			
			getMarshaller(interfaceModel).marshal(interfaceModel, new File(targetFilePath));
		} catch (Exception e) {
			LOG.error("Error creating/writing UStVA export file", e);
			throw new AccountingException("Error creating/writing UStVA export file", e);
		}
	}
	
	/**
	 * 
	 * @param timeFrame
	 * @return
	 */
	public ElsterDTO getData() {
		return data;
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
	
	/**
	 * 
	 * @return
	 */
	protected abstract T mapToInterfaceModel();
}
