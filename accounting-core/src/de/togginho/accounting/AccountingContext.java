/*
 *  Copyright 2010 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting;

import java.io.Serializable;

import de.togginho.accounting.model.User;

/**
 * Serves as a general configuration container for the accounting application.
 * 
 * <p>Implementations of this interface ensure that {@link #equals(Object)} will always return <code>true</code>
 * if two instances' user name and DB file name are equal. {@link #hashCode()} should always return the same value in
 * this instance.</p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @author thorsten frank
 * 
 */
public interface AccountingContext extends Serializable {

	/**
	 * The full name of the current user.
	 * 
	 * @return the full name of the current user
	 * 
	 * @see	User#getName()
	 */
	String getUserName();

	/**
	 * Full path to the db file.
	 * 
	 * <p>Note that the syntax of this value depends on the platform and the actual persistence implementation.</p>
	 * 
	 * @return full path to the db file
	 */
	String getDbFileName();
}
