/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.service;

import java.util.Date;

import com.db4o.query.Predicate;

import de.togginho.accounting.model.Expense;
import de.togginho.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
class FindExpensesPredicate extends Predicate<Expense> {

    /**
     * 
     */
    private static final long serialVersionUID = -4505472471157331493L;

	private Date from;
    
    private Date until;
    
    /**
     * 
     * @param timeFrame
     */
    FindExpensesPredicate(TimeFrame timeFrame) {
    	if (timeFrame != null) {
    	    this.from = timeFrame.getFrom();
    	    this.until = timeFrame.getUntil();
    	}
    }
    
	/**
	 * {@inheritDoc}.
	 * @see com.db4o.query.Predicate#match(java.lang.Object)
	 */
	@Override
	public boolean match(Expense candidate) {
		if (from == null || until == null) {
			return true;
		}
		if (candidate.getPaymentDate() != null) {
			return candidate.getPaymentDate().compareTo(from) >= 0
					&& candidate.getPaymentDate().compareTo(until) <= 0;
		}
		
		return false;
	}

}
