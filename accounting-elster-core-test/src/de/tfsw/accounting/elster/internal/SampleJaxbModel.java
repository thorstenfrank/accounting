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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Thorsten Frank
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sampleJaxbModel", propOrder = {
    "name",
    "type",
    "whatever"
})
@XmlRootElement(name = "SampleJaxbModel")
public class SampleJaxbModel {

	@XmlElement(name = "name", required = true)
	protected String name;
	
	@XmlElement(name = "type", required = true)
	protected String type;
	
	@XmlElement(name = "whatever")
	protected String whatever;
	
	@XmlAttribute(name = "version", required = true)
	protected String version;

	/**
	 * @return the name
	 */
	protected String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	protected String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	protected void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the whatever
	 */
	protected String getWhatever() {
		return whatever;
	}

	/**
	 * @param whatever the whatever to set
	 */
	protected void setWhatever(String whatever) {
		this.whatever = whatever;
	}

	/**
	 * @return the version
	 */
	protected String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	protected void setVersion(String version) {
		this.version = version;
	}
}
