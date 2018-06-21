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

/**
 * @deprecated use {@link AccountingContext} directly
 */
@Deprecated
public final class AccountingContextFactory {
	
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
		return new AccountingContext(userName, dbFileName);
	}
}
