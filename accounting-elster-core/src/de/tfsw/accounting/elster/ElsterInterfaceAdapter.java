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
package de.tfsw.accounting.elster;

import java.time.YearMonth;

import javax.xml.bind.JAXBContext;

import de.tfsw.accounting.AccountingException;

/**
 * Interface for adapter implementations. Must be defined as an extension.
 * 
 * <p>Concrete implementations should register as an extension inside their respective <code>plugin.xml</code> like so:
 * 
 * <pre>
 * &lt;extension point="de.tfsw.accounting.elster.elsterInterfaceApdapter">
 *     &lt;elsterAdapter class="my.concrete.AdapterClassName"/>
 * &lt;/extension>
 * </pre>
 * </p>
 * 
 * @param T the class representing the root element of a JAXB annotated model used for marshalling to XML
 * 
 * @author Thorsten Frank
 * 
 * @see #EXTENSION_POINT_ID
 * 
 */
public interface ElsterInterfaceAdapter<T> {

	/**
	 * The extension point ID implementations need to register under.
	 */
	public static final String EXTENSION_POINT_ID = "de.tfsw.accounting.elster.elsterInterfaceApdapter"; //$NON-NLS-1$
	
	/**
	 * The earliest period for which this adapter can supply an XML model. 
	 * 
	 * <p>
	 * The {@link ElsterService} collects all defined extensions and sorts them according to their valid from
	 * dates. An adapter is assumed to be responsible for any period starting with this value, until another adapter
	 * reports a later valid from date.
	 * </p>
	 * 
	 * @return the earliest period (inclusive) this adapter can supply an XML model
	 */
	public YearMonth validFrom();
	
	/**
	 * Uses the source data supplied to build and return an interface model suitable for JAXB marshalling.
	 * 
	 * <p>
	 * Implementations do not need to worry about creating a {@link JAXBContext} or marshalling, this is handled by the
	 * {@link ElsterService}. They must, however, ensure that the returned object is marked as a 
	 * <code>XMLRootElement</code> and that it and all subordinate classes are properly annotated.
	 * </p>
	 * 
	 * <p>
	 * Implementations should also refrain from manipulating the supplied source data unless absolutely necessary to
	 * ensure a valid XML document.
	 * </p>
	 * 
	 * @param dto data source
	 * 
	 * @return the root element of a JAXB element tree
	 * 
	 * @throws AccountingException if the supplied data transfer object does not contain sufficient data to build a
	 * 							   valid interface model
	 * 
	 * @see ElsterService#generateXML(ElsterDTO)
	 * @see ElsterService#writeToXmlFile(ElsterDTO, String)
	 */
	public T mapToInterfaceModel(ElsterDTO dto);
}
