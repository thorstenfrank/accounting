/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.reporting.internal;


/**
 * @author thorsten
 *
 */
public class KeyValueModelWrapper extends ModelWrapper {

	private static final String VALUE = "VALUE";
	private static final String KEY = "KEY";
	private Object key;
	
	/**
	 * 
	 */
	protected KeyValueModelWrapper(Object key, Object value) {
		super(value);
		this.key = key;
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.reporting.internal.ModelWrapper#get(java.lang.Object, java.lang.String)
	 */
	@Override
	protected Object get(Object object, String property) throws Exception {
		if (property.equals(KEY)) {
			return key;
		} else if (property.startsWith(VALUE)) {
			if (property.contains(DOT) == false) {
				return getModel();
			} else {
				return super.get(getModel(), property.substring(property.indexOf(DOT) + 1));
			}
		}
		return null;
	}
}
