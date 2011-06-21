/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.model;

import java.io.Serializable;

import de.togginho.accounting.Messages;

/**
 * @author thorsten
 *
 */
public enum InvoiceState implements Serializable {

	UNSAVED,
	
	CREATED,
	
	SENT,
	
	OVERDUE,
	
	CANCELLED,
	
	PAID;
	
	/**
	 * 
	 * @return
	 */
	public String getTranslatedString() {
		switch (this) {
		case UNSAVED:
			return Messages.InvoiceState_UNSAVED;
		case CREATED:
			return Messages.InvoiceState_CREATED;
		case SENT:
			return Messages.InvoiceState_SENT;
		case OVERDUE:
			return Messages.InvoiceState_OVERDUE;
		case CANCELLED:
			return Messages.InvoiceState_CANCELLED;
		case PAID:
			return Messages.InvoiceState_PAID;
		}
		return null;
	}
}
