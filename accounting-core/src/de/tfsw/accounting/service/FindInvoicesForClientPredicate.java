/*
 *  Copyright 2012 , 2014 Thorsten Frank (accounting@tfsw.de).
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

import com.db4o.query.Predicate;

import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.Invoice;

/**
 * @author thorsten
 *
 */
class FindInvoicesForClientPredicate extends Predicate<Invoice> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7431190415483875487L;

	private Client client;
	
	FindInvoicesForClientPredicate(Client client) {
		this.client = client;
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.db4o.query.Predicate#match(java.lang.Object)
	 */
	@Override
	public boolean match(Invoice candidate) {
		return client.equals(candidate.getClient());
	}

}
