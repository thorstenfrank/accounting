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

import com.db4o.query.Predicate;

import de.togginho.accounting.model.User;

/**
 * @author thorsten
 *
 */
class FindCurrentUserPredicate extends Predicate<User> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1325854897879815773L;
	
	/** The context. */
	private AccountingContext context;
	
	/**
	 * @param context the context
	 */
	FindCurrentUserPredicate(AccountingContext context) {
		this.context = context;
	}

	/**
	 * {@inheritDoc}.
	 * @see com.db4o.query.Predicate#match(java.lang.Object)
	 */
	@Override
	public boolean match(User candidate) {
		return candidate.getName().equals(context.getUserName());
	}

}
