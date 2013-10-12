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

import java.util.HashSet;
import java.util.Set;

import com.db4o.query.Predicate;

import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
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

    private TimeFrame timeFrame = null;
    
    private Set<ExpenseType> expenseTypes = null;
    
    /**
     * Creates a predicate with only the supplied types as parameters.
     * 
     * @param types expense types to include in matching. May be <code>null</code>
     */
    FindExpensesPredicate(ExpenseType...types) {
    	this(null, types);
    }
    
    /**
     * Creates a predicate that matches only expenses within the suppplied timeframe and  of the supplied types.
     * 
     * @param timeFrame {@link Expense#getPaymentDate()} must be within this timeframe
     * @param types expense types to include in matching. May be <code>null</code>
     */
    FindExpensesPredicate(TimeFrame timeFrame, ExpenseType...types) {
    	this.timeFrame = timeFrame;
    	
    	if (types != null && types.length > 0) {
    		expenseTypes = new HashSet<ExpenseType>();
    		for (ExpenseType type : types) {
    			expenseTypes.add(type);
    		}
    	}
    }
    
	/**
	 * {@inheritDoc}.
	 * @see com.db4o.query.Predicate#match(java.lang.Object)
	 */
	@Override
	public boolean match(Expense candidate) {
		boolean matches = false;
		if (timeFrame == null) {
			matches = true;
		} else {
			matches = timeFrame.isInTimeFrame(candidate.getPaymentDate());
		}
		
		if (matches && expenseTypes != null) {
			matches = expenseTypes.contains(candidate.getExpenseType());
		}
		
		return matches;
	}
}