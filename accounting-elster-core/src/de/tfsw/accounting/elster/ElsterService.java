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

import java.time.Year;
import java.time.YearMonth;

import de.tfsw.accounting.AccountingException;

/**
 * Client interface for access to the accounting ELSTER subsystem, which provides the ability to generate XML files
 * suitable for periodic upload of VAT reports to the German revenue service (ELSTER). This service represents the 
 * single point of entry for clients, and is made available using the OSGi declarative services infrastructure.
 * 
 * <p>
 * The typical process of working with this interface is as follows:
 * 
 * <ol>
 * <li>{@link #getAvailableYears()} provides an overview of years for which XML reports can be generated. This is
 * constrained by the availability of schemas published by the German electronic tax interface.</li>
 * <li>{@link #getElsterDTO(YearMonth)} provides the client with a pre-filled data transfer object for inspection and
 * manipulation where human changes are necessary. The data is split into period-independent (e.g. address data) and 
 * period-dependent data, such as the actual input and output tax amounts.</li>
 * <li>{@link #adaptTo(YearMonth)} (optional) allows clients to change the desired reporting period, retaining as much
 * of the previous data as possible.</li>
 * <li>{@link #generateXML(ElsterDTO)} returns a formatted XML document as a string suitable for upload to the German
 * electronic tax system. Clients may use this either as a preview or export the XML to a file themselves.</li>
 * <li>{@link #writeToXmlFile(ElsterDTO, String)} will also generate an XML document and save it directly to the 
 * specified file.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * The necessity for a transfer object lies in the fact that the XML schema used by ELSTER differs slightly from the
 * accounting object model in several instances, such as more detailed address information, monetary amounts having to
 * be scaled and rounded differently, etc. This affords both clients and adapter implementations an independent data
 * interchange format without having to worry about the mentioned formatting and calculating.  
 * </p>
 * 
 * @see ElsterDTO
 * 
 * @author Thorsten Frank
 */
public interface ElsterService {
	
	/**
	 * Returns a simple array of years for which adapters are available. Clients may assume that for every year 
	 * returned, all months within that year are covered as well.
	 * 
	 * @return an array of {@link Year} instances for which adapters are available
	 */
	Year[] getAvailableYears();
	
	/**
	 * Returns a pre-filled data transfer object for the supplied period. Clients may change the actual data before
	 * generating an XML message, but should be aware that none of these changes will be made persistent with the
	 * regular accounting object model, and that  especially monetary amounts are calculated as per specification of 
	 * the German revenue service, so any changes made are subject to be inconsistend with the data as captured in the
	 * accounting application.
	 *  
	 * @param yearMonth the reporting period for which VAT input and output are to be filed
	 * 
	 * @return a pre-filled data transfer object for the supplied period
	 * 
	 * @throws AccountingException if the supplied period is <code>null</code> or not supported
	 */
	ElsterDTO getElsterDTO(YearMonth yearMonth);
	
	/**
	 * Adjusts the contents of the supplied data transfer object to a new reporting period while attempting to preserve
	 * as much information of the previous version. Ideally, if a DTO previously obtained through 
	 * {@link #getElsterDTO(YearMonth)} has been changed by a user (e.g. by making changes to the address information),
	 * only the monetary amounts will be changed. DTOs may be adapted multiple times.
	 * 
	 * <p>There are no guarantees made as to whether or not data within the DTO is changed, i.e. clients should expect
	 * any number and kind of changes.</p>
	 * 
	 * @param data the data transfer object to adjust, previously obtained through {@link #getElsterDTO(YearMonth)}
	 * 
	 * @param yearMonth the new filing period to be adjusted to
	 * 
	 * @return the adjusted transfer object
	 * 
	 * @throws AccountingException if the supplied period is <code>null</code> or not supported
	 */
	ElsterDTO adaptToPeriod(ElsterDTO data, YearMonth yearMonth);
	
	/**
	 * Exports the supplied data transfer object to an XML file as specified. The contents of the resulting file will
	 * always be an XML file using an encoding as specified by the German Electronic Tax Declaration System ELSTER,
	 * which has been <code>ISO-8859-15</code> since its inception.  
	 * 
	 * @param data the data to write to XML
	 * 
	 * @param targetFilePath fully qualified file name where to export XML data to. Relative paths may be specified but
	 * 						 are dependent on the application runtime and should be avoided. Also, there is no 
	 * 						 validation of the file name syntax - it is highly recommended to append an 
	 * 					     <code>.xml</code> suffix to the file name to avoid any problems
	 * 
	 * @throws AccountingException if the supplied data could not be generated as an XML document, or if the requested
	 * 							   file could not be written
	 */
	void writeToXmlFile(ElsterDTO data, String targetFilePath);
	
	/**
	 * Exports the supplied data transfer object to a formatted String that represents a valid XML document suitable
	 * for upload to the German Elextronic Tax Declaration System (ELSTER). 
	 * 
	 * <p>
	 * Clients may choose to write this string to a file themselves, but are encouraged to use 
	 * {@link #writeToXmlFile(ElsterDTO, String)} for encoding reasons.
	 * </p>
	 * 
	 * @param data the data to write to XML
	 * 
	 * @return a string representing a formatted XML message containing the supplied data
	 * 
	 * @throws AccountingException if the supplied data could not be generated as an XML document
	 */
	String generateXML(ElsterDTO data);
}
