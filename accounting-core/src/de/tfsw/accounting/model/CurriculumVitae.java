/*
 *  Copyright 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.model;

import java.util.List;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class CurriculumVitae extends AbstractBaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public static final String FIELD_REFERENCES = "references";
	
	/**
	 * 
	 */
	private List<CVEntry> references;

	/**
	 * @return the references
	 */
	public List<CVEntry> getReferences() {
		return references;
	}

	/**
	 * @param references the references to set
	 */
	public void setReferences(List<CVEntry> references) {
		this.references = references;
	}
}
