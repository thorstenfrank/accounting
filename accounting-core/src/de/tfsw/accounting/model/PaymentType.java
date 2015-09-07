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
package de.tfsw.accounting.model;

import java.io.Serializable;

import de.tfsw.accounting.Messages;

/**
 * Describes how a payment is to be made.
 * 
 * @author thorsten
 *
 */
public enum PaymentType implements Serializable {

	/**
	 * Credit purchase (German: Zielkauf).
	 * 
	 * Net payments are payments of the total due amount of an invoice, usually by way of bank transfer.
	 */
	TRADE_CREDIT;
	
	/**
	 * Returns a translated String describing this type. The locale used is the default locale derived from the
	 * framework.
	 * 
	 * @return a locale-sensitive string representation of this payment type
	 */
	public String getTranslatedString() {
		return Messages.translate(this);
	}
}
