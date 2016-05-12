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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.elster.ElsterDTO;
import de.tfsw.accounting.elster.ElsterInterfaceAdapter;
import de.tfsw.accounting.elster.ElsterService;

/**
 * ELSTER service implementation.
 * 
 * <p>
 * This class is meant to be instantiated and set up by OSGi declarative services, and instances of this class cannot 
 * function without a valid {@link AccountingService} instance to use for querying model data.
 * </p>
 * 
 * @author Thorsten Frank
 */
@SuppressWarnings("rawtypes")
public class ElsterServiceImpl implements ElsterService {
	
	/**
	 * Logger.
	 */
	private static final Logger LOG = LogManager.getLogger(ElsterServiceImpl.class);

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
	 * Accounting Service instance.
	 */
	private AccountingService accountingService;
	
	/**
	 * Default constructor.
	 */
	public ElsterServiceImpl() {
		// nothing to do here, really
	}
	
	/**
	 * Direct injection constructor, used for testing purposes.
	 * This constructor is not meant to be used in a production environment.
	 *  
	 * @param accountingService	the {@link AccountingService} implementation to use
	 * @param adapters a collection of adapters
	 */
	protected ElsterServiceImpl(AccountingService accountingService, Collection<ElsterInterfaceAdapter> adapters) {
		this.accountingService = accountingService;
		registerAdapters(adapters);
	}
	
	
	/**
	 * DS bind method.
	 * @param accountingService
	 */
	public synchronized void setAccountingService(AccountingService accountingService) {
		if (this.accountingService != null) {
			LOG.warn("An attempt to bind Accounting Service was made, but I already had an instance!"); //$NON-NLS-1$
		} else {
			LOG.debug("Binding Accounting Service"); //$NON-NLS-1$
			this.accountingService = accountingService;			
		}

	}
	
	/**
	 * DS unbind method.
	 * @param accountingService
	 */
	public synchronized void unsetAccountingService(AccountingService accountingService) {
		if (this.accountingService == accountingService) {
			LOG.debug("Unbinding accounting service"); //$NON-NLS-1$
			this.accountingService = null;
		} else {
			LOG.debug("An attempt to unbind an unknown Accounting Service instance was made, will ignore."); //$NON-NLS-1$
		}
	}
	
	/**
	 * @see de.tfsw.accounting.elster.ElsterService#getAvailableYears()
	 */
	@Override
	public Year[] getAvailableYears() {
		checkInit(); // do this to make sure the adapter map has been properly initialised
		
		if (adapterMap.isEmpty()) {
			return new Year[0];
		}
		
		Year oldestKnown = null;
		Year youngestKnown = Year.now();
		
		for (YearMonth ym : adapterMap.keySet()) {
			Year year = Year.from(ym);
			if (oldestKnown == null || oldestKnown.isAfter(year)) {
				oldestKnown = year;
			}
			if (youngestKnown.isBefore(year)) {
				youngestKnown = year;
			}
		}
		
		// if there's only one year available, don't go through the hassle of putting together a sorted set
		if (oldestKnown.equals(youngestKnown)) {
			return new Year[]{youngestKnown};
		}
		
		// add all years from the oldest known adapter available up to the current year
		Set<Year> years = new TreeSet<Year>();
		Year cursor = oldestKnown;
		while (cursor.compareTo(youngestKnown) < 1) {
			years.add(cursor);
			cursor = cursor.plusYears(1);
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
			LOG.error("Error marshalling to XML", e); //$NON-NLS-1$
			throw new AccountingException(Messages.ElsterServiceImpl_ErrorGeneratingXML, e);
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
			LOG.error("Error marshalling to XML", e); //$NON-NLS-1$
			writer.append(e.toString());
		} catch (AccountingException e) {
			// no need to log this exception, that was done downstream already
			writer.append(e.toString());
		}
		
		return writer.toString();
	}
	
	/**
	 * 
	 */
	private void checkInit() {
		if (adapterMap == null) {
			LOG.debug("Initialising ELSTER adapter map"); //$NON-NLS-1$
			checkInitMaps();
			lookupAdapterImplementations();
		}
	}
	
	/**
	 * 
	 */
	private void checkInitMaps() {
		if (adapterMap == null) {
			LOG.debug("Initialising ELSTER adapter map"); //$NON-NLS-1$
			adapterMap = new TreeMap<YearMonth, ElsterInterfaceAdapter>();
		}
		
		if (jaxbContextMap == null) {
			LOG.debug("Initialising JAXB context map"); //$NON-NLS-1$
			jaxbContextMap = new HashMap<Class, JAXBContext>();
		}
	}
	
	/**
	 * Looks for adapter implementations defined as eclipse plugin extensions.
	 */
	private void lookupAdapterImplementations() {
		LOG.debug("Looking for ELSTER adapter implementations..."); //$NON-NLS-1$
		IConfigurationElement[] configElements = 
				RegistryFactory.getRegistry().getConfigurationElementsFor(ElsterInterfaceAdapter.EXTENSION_POINT_ID);
		
		LOG.debug("Number of defined extensions found: " + configElements.length); //$NON-NLS-1$
		
		final List<ElsterInterfaceAdapter> adapters = new ArrayList<ElsterInterfaceAdapter>();
		
		for (IConfigurationElement ce : configElements) {
			try {
				final Object o = ce.createExecutableExtension("class"); //$NON-NLS-1$
				if (o instanceof ElsterInterfaceAdapter) {
					adapters.add((ElsterInterfaceAdapter) o);
				}
			} catch (CoreException e) {
				LOG.error("Could not create defined adapter implementation", e); //$NON-NLS-1$
			}
		}
		
		registerAdapters(adapters);
		
		if (adapterMap.isEmpty()) {
			LOG.warn("No suitable adapters were found!"); //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 * @param adapters
	 */
	private void registerAdapters(Collection<ElsterInterfaceAdapter> adapters) {
		if (adapters == null) {
			LOG.warn("registerAdapters was called with null param, will ignore"); //$NON-NLS-1$
			return;
		}
		
		checkInitMaps(); // make sure the adapter and context maps have been created
		
		for (ElsterInterfaceAdapter adapter : adapters) {
			YearMonth validFrom = adapter.validFrom();
			if (validFrom == null) {
				LOG.warn(String.format("Defined adapter [%s] defines validFrom as null - will ignore!", //$NON-NLS-1$
						adapter.getClass().getName()));
				continue;
			} else if (adapterMap.containsKey(validFrom)) {
				LOG.warn(String.format("Defined adapter [%s] is valid from [%s], existing adapter [%s] with identical date will be overwritten",  //$NON-NLS-1$
						adapter.getClass().getName(), validFrom.toString(), adapterMap.get(validFrom).getClass().getName()));
			} else {
				LOG.debug(String.format("Adding adapter [%s] valid from [%s]", //$NON-NLS-1$
						adapter.getClass().getName(), validFrom.toString()));
			}			
			adapterMap.put(validFrom, adapter);
		}
	}
	
	/**
	 * 
	 * @param period
	 * @return
	 */
	private ElsterInterfaceAdapter getAdapterForPeriod(YearMonth period) {
		ElsterInterfaceAdapter adapter = null;
		
		if (period != null) {
			checkInit();
			
			for (YearMonth validFrom : adapterMap.keySet()) {
				// we're gonna trust the TreeMap implementation here and assume that the keys are returned in ascending
				// order, so the last man standing wins - if multiple adapters are valid, the last one is assumed to be
				// the most current
				if (period.equals(validFrom) || period.isAfter(validFrom)) {
					adapter = adapterMap.get(validFrom);
				}
			}
		} else {
			throw new AccountingException(Messages.ElsterServiceImpl_ErrorFilingPeriodIsNull);
		}
		
		if (adapter == null) {
			throw new AccountingException(
					Messages.bind(Messages.ElsterServiceImpl_ErrorNoAdapterForFilingPeriod, period.toString()));
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
			throw new AccountingException(Messages.ElsterServiceImpl_ErrorInitMarshaller, e);
		}

		Marshaller marshaller;
		try {
			marshaller = jaxbContextMap.get(rootElement.getClass()).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
			return marshaller;
		} catch (PropertyException e) {
			LOG.error("Error setting property for marshaller", e); //$NON-NLS-1$
			throw new AccountingException(Messages.ElsterServiceImpl_ErrorInitMarshaller, e);
		} catch (JAXBException e) {
			LOG.error("Error creating marshaller for class " + rootElement.getClass().getName(), e); //$NON-NLS-1$
			throw new AccountingException(Messages.ElsterServiceImpl_ErrorInitMarshaller, e);
		}
	}
}
