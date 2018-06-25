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
 * <p>Implementations of this interface ensure that {@link #equals(Object)} will always return <code>true</code>
 * if two instances' user name and DB file name are equal. {@link #hashCode()} should always return the same value in
 * this instance.</p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @author thorsten frank
 * 
 */
public class AccountingContext implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String dbFileName;
	
	/**
	 * Creates a new context.
	 * 
	 * @param userName {@link #getUserName()}, must not be empty
	 * @param dbFileName {@link #getDbFileName()}, must not be empty
	 * @throws AccountingException if a parameter is <code>null</code> or empty
	 */
	public AccountingContext(String userName, String dbFileName) {
		if (userName == null || userName.isEmpty()) {
			throw new AccountingException("User name must not be empty");
		}
		
		// TODO add a check if the value actually is a valid file path
		if (dbFileName == null || dbFileName.isEmpty()) {
			throw new AccountingException("DB file name must not be empty");
		}
		
		this.userName = userName;
		this.dbFileName = dbFileName;
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
	 * Full path to the db file.
	 * 
	 * <p>Note that the syntax of this value depends on the platform and the actual persistence implementation.</p>
	 * 
	 * @return full path to the db file
	 */
	public String getDbFileName() {
		return dbFileName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dbFileName == null) ? 0 : dbFileName.hashCode());
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

		if (dbFileName == null) {
			if (other.getDbFileName() != null) {
				return false;
			}
		} else if (!dbFileName.equals(other.getDbFileName())) {
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
