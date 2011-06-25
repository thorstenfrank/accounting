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
package de.togginho.accounting;

import java.util.HashSet;
import java.util.Set;

import com.db4o.query.Predicate;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.User;

/**
 * A predicate to search for invoices belonging to a user.
 * 
 * @author thorsten
 *
 */
class FindInvoicesPredicate extends Predicate<Invoice> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1990476393693145937L;

	/**
	 * The context for this predicate.
	 */
	private AccountingContext context;
	
	/**
	 * All invoice states to match - may be <code>null</code> or empty.
	 */
	private Set<InvoiceState> states;
	
	/**
	 * Creates a new predicate.
	 * 
	 * @param user the {@link User} whose invoices are matched
	 * @param states all invoice states to match - may be <code>null</code>
	 */
	FindInvoicesPredicate(AccountingContext context, InvoiceState... states) {
		this.context = context;
		if (states != null) {
			this.states = new HashSet<InvoiceState>();
			for (InvoiceState state : states) {
				this.states.add(state);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.db4o.query.Predicate#match(java.lang.Object)
	 */
	@Override
	public boolean match(Invoice invoice) {
		if (invoice.getUser() == null) {
			return false;
		}
		
		if (context.getUserName().equals(invoice.getUser().getName())) {
			if (states == null || states.isEmpty()) {
				return true;
			} else {
				return states.contains(invoice.getState());
			}
		}
		
		return false;
	}
}