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
package de.togginho.accounting.service;

import com.db4o.query.Predicate;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
class FindInvoicesForRevenuePredicate extends Predicate<Invoice> {

	/**
     * 
     */
    private static final long serialVersionUID = -7375474511194166527L;

    private TimeFrame timeFrame;
        
	/**
     * @param timeFrame
     */
    FindInvoicesForRevenuePredicate(TimeFrame timeFrame) {
    	this.timeFrame = timeFrame;
    }

	/**
	 * {@inheritDoc}.
	 * @see com.db4o.query.Predicate#match(java.lang.Object)
	 */
	@Override
	public boolean match(Invoice candidate) {
		if (InvoiceState.PAID.equals(candidate.getState())) {
			return timeFrame.isInTimeFrame(candidate.getPaymentDate());
		}
		return false;
	}

}
