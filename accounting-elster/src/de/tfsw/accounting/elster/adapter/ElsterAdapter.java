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


/**
 * Public interface for message adapters to the ELSTER system for clients.
 * 
 * <p>
 * This interface is not meant to be implemented by clients. New versions of ELSTER adapter implementations must
 * extend {@link AbstractElsterAdapter}.
 * </p>
 * 
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public interface ElsterAdapter {
	
	/**
	 * Returns the data transfer object for manipulation by a client. This object is pre-filled based on the time frame
	 * used for aquiring a concrete adapter by the {@link ElsterAdapterFactory}.
	 * 
	 * @return the data transfer object
	 */
	ElsterDTO getData();
	
	/**
	 * Generates and writes an XML message for the ELSTER based on the current state of data as contained in the
	 * transfer object returned by {@link #getData()}.
	 * 
	 * @param targetFilePath file name of the XML file to generate
	 */
	void writeDataToXML(String targetFilePath);
	
	/**
	 * Generates an XML message for the ELSTER system based on the current state of data as contained in the transfer
	 * object returned by {@link #getData()}.
	 * 
	 * @return a formatted XML string representing a message to the ELSTER system
	 */
	String writeDataToXML();
}
