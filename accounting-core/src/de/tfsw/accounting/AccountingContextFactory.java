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

import org.apache.log4j.Logger;

/**
 * A factory to create instances of {@link AccountingContext}.
 * 
 * <p>Clients of the <code>accounting-core</code> plugin should use this factory to create a context, which in turn
 * needs to be used to init the {@link AccountingService}.</p>
 * 
 * <p>While this class is not technically a Singleton, the static methods use a default
 * instance of this class, so there is no need to instantiate another factory.</p>
 * 
 * @author thorsten frank
 * @see #buildContext(String, String)
 */
public final class AccountingContextFactory {
	
	/** Logger. */
	private static final Logger LOG = Logger.getLogger(AccountingContextFactory.class);
	
	/**
	 * The default instance of this factory.
	 */
	private static final AccountingContextFactory DEFAULT_INSTANCE = new AccountingContextFactory();
	
	/**
	 * Builds an {@link AccountingContext} instance for the supplied values.
	 *  
	 * @param userName	 {@link AccountingContext#getUserName()}
	 * @param dbFileName {@link AccountingContext#getDbFileName()}
	 * @return a context instance
	 * @throws AccountingException if one of the two values are <code>null</code> or empty
	 */
	public static AccountingContext buildContext(String userName, String dbFileName) {
		return DEFAULT_INSTANCE.doBuildContext(userName, dbFileName);
	}
	
	/**
	 * Builds an {@link AccountingContext} instance with the supplied values.
	 * @param userName the user's name ({@link AccountingContext#getUserName()})
	 * @param dbFileName the data file location ({@link AccountingContext#getDbFileName()})
	 * @return
	 */
	private AccountingContext doBuildContext(String userName, String dbFileName) {
		AccountingContextImpl ctx = new AccountingContextImpl();
		
		checkMandatoryValue("userName", userName);
		ctx.setUserName(userName);
		
		checkMandatoryValue("dbFileLocation", dbFileName);
		ctx.setDbFileName(dbFileName);
		
		return ctx;
	}
	
	/**
	 * Checks that <code>value</code> is neither <code>null</code> nor empty.
	 * If that is not the case, a new {@link AccountingException} is thrown.
	 * 
	 * @param key the key of the property to check
	 * @param value the value to check
	 */
	private void checkMandatoryValue(String key, String value) {
		if (value == null || value.isEmpty()) {
			LOG.error("Missing mandatory context property: " + key);
			throw new AccountingException(
					Messages.bind(Messages.AccountingContextFactory_errorMissingContextProperty, key));
		}
	}
	
	/** 
	 * A simple implementation of the {@link AccountingContext} interface.
	 * 
	 * <p> This is a private inner class to force clients to use the factory for creating context instances.</p> 
	 */
	private class AccountingContextImpl implements AccountingContext {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3938465616389538289L;
		
		private String userName;
		private String dbFileName;

		@Override
		public String getUserName() {
			return userName;
		}
		
		private void setUserName(String userName) {
			this.userName = userName;
		}
		
		@Override
		public String getDbFileName() {
			return dbFileName;
		}
		
		private void setDbFileName(String dbFileName) {
			this.dbFileName = dbFileName;
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
}
