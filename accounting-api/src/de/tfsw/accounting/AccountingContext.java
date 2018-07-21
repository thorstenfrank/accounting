/*
 *  Copyright 2010 , 2014 Thorsten Frank (accounting@tfsw.de).
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

import java.io.Serializable;

/**
 * Serves as a general configuration container for the accounting application.
 * 
 * <p>Instances of this class is immutable.</p>
 * 
 * @author thorsten frank
 * 
 */
public final class AccountingContext implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String dbLocation;
	
	/**
	 * Creates a new context.
	 * 
	 * @param userName {@link #getUserName()}, must not be empty
	 * @param dbLocation {@link #getDbFileName()}, must not be empty
	 * @throws AccountingException if a parameter is <code>null</code> or empty
	 */
	public AccountingContext(String userName, String dbLocation) {
		if (userName == null || userName.isEmpty()) {
			throw new AccountingException("User name must not be empty");
		}
		
		// TODO add a check if the value actually is a valid file path
		if (dbLocation == null || dbLocation.isEmpty()) {
			throw new AccountingException("DB file name must not be empty");
		}
		
		this.userName = userName;
		this.dbLocation = dbLocation;
	}

	/**
	 * The full name of the current user.
	 * 
	 * @return the full name of the current user
	 * 
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Full path to the DB.
	 * 
	 * <p>Note that the syntax of this value depends on the platform and the actual persistence implementation.</p>
	 * 
	 * @return full path to the DB
	 */
	public String getDbLocation() {
		return dbLocation;
	}

	@Override
	public String toString() {
		return String.format("AccountingContext{user: %s, data location: %s}", userName, dbLocation);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dbLocation == null) ? 0 : dbLocation.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountingContext other = (AccountingContext) obj;

		if (dbLocation == null) {
			if (other.getDbLocation() != null) {
				return false;
			}
		} else if (!dbLocation.equals(other.getDbLocation())) {
			return false;
		}
		
		if (userName == null) {
			if (other.getUserName() != null) {
				return false;
			}
		} else if (!userName.equals(other.getUserName())) {
			return false;
		}
			
		return true;
	}
}
