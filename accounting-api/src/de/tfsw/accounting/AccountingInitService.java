/*
 *  Copyright 2018 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting;

/**
 * Outsources the init functions of the accounting service. This is (probably) an intermediate solution to
 * be removed when the entire service and model have been migrated to the API plugin.
 * 
 * @author thorsten
 */
public interface AccountingInitService {
	
	static final String EVENT_TOPIC_SERVICE_INIT = "de/tfsw/accounting/core/service/init";
	
	static final String EVENT_PROPERTY_INIT_SERVICE = "accounting.service";
	
	/**
	 * Initialises this service with the supplied context, which contains the necessary minimal information needed to
	 * use this service.
	 * 
	 * <p>This method must be the very first call by a client. Subsequent calls to this method will have no effect.
	 * Until this method finishes processing successfully, all calls to other methods of this service will fail with an
	 * exception.</p>
	 * 
	 * @param context the context to use for init of this service - must <b>NOT</b> be <code>null</code>, and all
	 * 		  properties of the context need to be non-empty
	 * 
	 * @throws AccountingException if the supplied context or one of its properties are <code>null</code>, or if the 
	 * 		   database cannot be opened
	 */
	void init(AccountingContext context);
	
	/**
	 * Creates a new database that is filled with the information provided in the XML file.
	 * 
	 * <p>The XML source file must adhere to the schema definition <code>AccountingModel.xsd</code> and should be
	 * created using <code>exportModelToXml(String)</code>.</p> 
	 * 
	 * @param sourceXmlFile  the XML file to import
	 * @param dbFileLocation location of the DB file where imported information will be stored
	 * 
	 * @return the context to use filled with imported data
	 * 
	 * see exportModelToXml(String)
	 */
	AccountingContext importModelFromXml(String sourceXmlFile, String dbFileLocation);
}
