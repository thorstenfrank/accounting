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
package de.tfsw.accounting.elster.internal;

import java.io.File;
import java.io.StringWriter;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.elster.ElsterDTO;
import de.tfsw.accounting.elster.ElsterInterfaceAdapter;
import de.tfsw.accounting.elster.ElsterService;

/**
 * @author Thorsten Frank
 *
 */
@SuppressWarnings("rawtypes")
public class ElsterServiceImpl implements ElsterService {

	/**
	 * 
	 */
	private static final String ADAPTER_EXTENSION_POINT_ID = "de.tfsw.accounting.elster.elsterInterfaceApdapter"; //$NON-NLS-1$
	
	/**
	 * Logger.
	 */
	private static final Logger LOG = Logger.getLogger(ElsterServiceImpl.class);

	/**
	 * Default encoding used for the resulting XML as defined by the ELSTER interface spec.
	 */
	private static final String ENCODING = "ISO-8859-15"; //$NON-NLS-1$
	
	/**
	 * A map of adapter instances using {@link ElsterInterfaceAdapter#validFrom()} as the key.
	 */
	private Map<YearMonth, ElsterInterfaceAdapter> adapterMap;

	/**
	 * Cache of JAXB contexts.
	 */
	private Map<Class, JAXBContext> jaxbContextMap;
	
	/**
	 * 
	 */
	private AccountingService accountingService;
	
	/**
	 * Creates a new service instance.
	 */
	public ElsterServiceImpl() {
		LOG.debug("Init ELSTER Service"); //$NON-NLS-1$
		adapterMap = new TreeMap<YearMonth, ElsterInterfaceAdapter>();
		jaxbContextMap = new HashMap<Class, JAXBContext>();
		lookupAdapterImplementations();
	}
	
	/**
	 * 
	 */
	private void lookupAdapterImplementations() {
		LOG.debug("Looking for ELSTER adapter implementations..."); //$NON-NLS-1$
		IConfigurationElement[] configElements = 
				RegistryFactory.getRegistry().getConfigurationElementsFor(ADAPTER_EXTENSION_POINT_ID);
		
		LOG.debug("Number of defined extensions found: " + configElements.length); //$NON-NLS-1$
				
		for (IConfigurationElement ce : configElements) {
			try {
				final Object o = ce.createExecutableExtension("class"); //$NON-NLS-1$
				if (o instanceof ElsterInterfaceAdapter) {
					ElsterInterfaceAdapter adapter = (ElsterInterfaceAdapter) o;
					LOG.debug(String.format("Adding adapter of type [%s] valid from [%s]", adapter.getClass().getName(), adapter.validFrom().toString()));
					adapterMap.put(adapter.validFrom(), adapter);
				}
			} catch (CoreException e) {
				LOG.error("Could not create defined adapter implementation", e);
			}
		}
		
		if (adapterMap.isEmpty()) {
			LOG.warn("No suitable adapters were found!"); //$NON-NLS-1$
		}
	}
	
	/**
	 * DS bind method.
	 * @param accountingService
	 */
	public synchronized void setAccountingService(AccountingService accountingService) {
		LOG.debug("Accounting Service was set");
		this.accountingService = accountingService;
	}
	
	/**
	 * DS unbind method.
	 * @param accountingService
	 */
	public synchronized void unsetAccountingService(AccountingService accountingService) {
		if (this.accountingService == accountingService) {
			LOG.debug("Unbinding accounting service");
			this.accountingService = null;
		}
	}
	
	/**
	 * @see de.tfsw.accounting.elster.ElsterService#getAvailableYears()
	 */
	@Override
	public Year[] getAvailableYears() {
		if (adapterMap.isEmpty()) {
			return new Year[0];
		}
		
		Set<Year> years = new HashSet<Year>();
		for (YearMonth ym : adapterMap.keySet()) {
			years.add(Year.from(ym));
		}
		
		return years.toArray(new Year[years.size()]);
	}

	/**
	 * @see de.tfsw.accounting.elster.ElsterService#getElsterDTO(java.time.YearMonth)
	 */
	@Override
	public ElsterDTO getElsterDTO(YearMonth yearMonth) {
		return new ElsterDTOBuilder(accountingService).createDTO(yearMonth);
	}

	/**
	 * @see de.tfsw.accounting.elster.ElsterService#adaptToPeriod(de.tfsw.accounting.elster.ElsterDTO, java.time.YearMonth)
	 */
	@Override
	public ElsterDTO adaptToPeriod(ElsterDTO data, YearMonth yearMonth) {
		return new ElsterDTOBuilder(accountingService).adaptToPeriod(data, yearMonth);
	}

	/**
	 * @see de.tfsw.accounting.elster.ElsterService#writeToXmlFile(de.tfsw.accounting.elster.ElsterDTO, java.lang.String)
	 */
	@Override
	public void writeToXmlFile(ElsterDTO data, String targetFilePath) {
		Object rootElement = getAdapterForPeriod(data.getFilingPeriod()).mapToInterfaceModel(data);
		try {
			getMarshaller(rootElement).marshal(rootElement, new File(targetFilePath));
		} catch (JAXBException e) {
			LOG.error("Error marshalling to XML", e);
			throw new AccountingException("Error generating ELSTER XML", e);
		}
	}

	/**
	 * @see de.tfsw.accounting.elster.ElsterService#generateXML(de.tfsw.accounting.elster.ElsterDTO)
	 */
	@Override
	public String generateXML(ElsterDTO data) {
		StringWriter writer = new StringWriter();
		
		try {
			Object rootElement = getAdapterForPeriod(data.getFilingPeriod()).mapToInterfaceModel(data);
			getMarshaller(rootElement).marshal(rootElement, writer);
		} catch (JAXBException e) {
			LOG.error("Error marshalling to XML", e);
			writer.append(e.toString());
		} catch (AccountingException e) {
			writer.append(e.toString());
		}
		
		return writer.toString();
	}
	
	/**
	 * 
	 * @param period
	 * @return
	 */
	private ElsterInterfaceAdapter getAdapterForPeriod(YearMonth period) {
		ElsterInterfaceAdapter adapter = null;
		
		for (YearMonth validFrom : adapterMap.keySet()) {
			// we're gonna trust the TreeMap implementation here and assume that the keys are returned in ascending
			// order, so the last man standing wins - if multiple adapters are valid, the last one is assumed to be
			// the most current
			if (period.isAfter(validFrom)) {
				adapter = adapterMap.get(validFrom);
			}
		}
		
		return adapter;
	}
	
	/**
	 * 
	 * @param rootElement
	 * @return
	 */
	private Marshaller getMarshaller(Object rootElement) {
		try {
			if (!jaxbContextMap.containsKey(rootElement.getClass())) {
				jaxbContextMap.put(rootElement.getClass(), JAXBContext.newInstance(rootElement.getClass()));
			}		
		} catch (JAXBException e) {
			LOG.error("Unable to create JAXBContext for class " + rootElement.getClass().getName(), e); //$NON-NLS-1$
			throw new AccountingException("Error setting up Marshaller", e);
		}

		Marshaller marshaller;
		try {
			marshaller = jaxbContextMap.get(rootElement.getClass()).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
			return marshaller;
		} catch (PropertyException e) {
			LOG.error("Error setting property for marshaller", e); //$NON-NLS-1$
			throw new AccountingException("Error setting property for marshaller", e);
		} catch (JAXBException e) {
			LOG.error("Error creating marshaller for class " + rootElement.getClass().getName());
			throw new AccountingException("Error creating marshaller", e);
		}
	}
}
