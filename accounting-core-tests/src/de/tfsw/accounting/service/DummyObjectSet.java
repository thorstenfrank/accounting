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
package de.tfsw.accounting.service;

import java.util.ArrayList;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectSet;

/**
 * @author thorsten
 *
 */
public class DummyObjectSet<T> extends ArrayList<T> implements ObjectSet<T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7363984662455082514L;

	/**
	 * {@inheritDoc}
	 * @see com.db4o.ObjectSet#ext()
	 */
	@Override
	public ExtObjectSet ext() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see com.db4o.ObjectSet#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see com.db4o.ObjectSet#next()
	 */
	@Override
	public T next() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see com.db4o.ObjectSet#reset()
	 */
	@Override
	public void reset() {
	}
}
