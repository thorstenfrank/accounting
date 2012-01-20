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
package de.togginho.accounting.ui.expense;

import org.apache.log4j.Logger;

import de.togginho.accounting.ui.AbstractTableSorter;

/**
 * 
 * @author thorsten
 *
 */
class ExpensesViewTableSorter extends AbstractTableSorter<ExpenseWrapper> {

	private static final Logger LOG = Logger.getLogger(ExpensesViewTableSorter.class);
	
	/**
	 * Creates a new sorter for the expenses view.
	 */
	protected ExpensesViewTableSorter() {
		super(ExpenseWrapper.class);
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractTableSorter#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	/**
	 * {@inheritDoc}
	 * @see AbstractTableSorter#doCompare(Object, Object, int)
	 */
	@Override
	protected int doCompare(ExpenseWrapper e1, ExpenseWrapper e2, int columnIndex) {
		switch (columnIndex) {
		case ExpensesView.COL_INDEX_DATE:
			return e1.getPaymentDate().compareTo(e2.getPaymentDate());
		case ExpensesView.COL_INDEX_DESC:
			return e1.getDescription().compareTo(e2.getDescription());
		case ExpensesView.COL_INDEX_NET:
			return e1.getNetAmount().compareTo(e2.getNetAmount());
		case ExpensesView.COL_INDEX_TAX:
			if (e1.getTaxAmount() == null) {
				return e2.getTaxAmount() != null ? -1 : 0;
			} else if (e2.getTaxAmount() == null) {
				return 1;
			}
			return e1.getTaxAmount().compareTo(e2.getTaxAmount());
		case ExpensesView.COL_INDEX_GROSS:
			return e1.getGrossAmount().compareTo(e2.getGrossAmount());
		default:
			return 0;
		}
		
	}
	
}