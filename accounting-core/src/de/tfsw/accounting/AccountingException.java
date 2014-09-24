/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
 * Base exception thrown by the public class of this plugin.
 * @author thorsten
 *
 */
public class AccountingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7752164687249119067L;

	/**
	 * @param message
	 */
	public AccountingException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public AccountingException(String message, Throwable cause) {
		super(message, cause);
	}
}