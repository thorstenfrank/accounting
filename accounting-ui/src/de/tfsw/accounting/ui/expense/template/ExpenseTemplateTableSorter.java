/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.expense.template;

import org.apache.log4j.Logger;

import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.ui.AbstractTableSorter;

/**
 * @author Thorsten Frank
 *
 */
class ExpenseTemplateTableSorter extends AbstractTableSorter<ExpenseTemplate> {
	
	private static final Logger LOG = Logger.getLogger(ExpenseTemplateTableSorter.class);
	
	protected static final int COL_ACTIVE = 0;
	protected static final int COL_FIRST_APPLICATION = 1;
	protected static final int COL_DESC = 2;
	protected static final int COL_NET = 3;
	protected static final int COL_FREQUENCY = 4;
	
	/**
	 * 
	 */
	ExpenseTemplateTableSorter() {
		super(ExpenseTemplate.class);
	}
	
	/**
	 * @see de.tfsw.accounting.ui.AbstractTableSorter#doCompare(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected int doCompare(ExpenseTemplate e1, ExpenseTemplate e2, int columnIndex) {
		switch (columnIndex) {
		case COL_ACTIVE:
			return Boolean.compare(e1.isActive(), e2.isActive());
		case COL_FIRST_APPLICATION:
			return e1.getFirstApplication().compareTo(e2.getFirstApplication());
		case COL_DESC:
			return e1.getDescription().compareTo(e2.getDescription());
		case COL_NET:
			return e1.getNetAmount().compareTo(e2.getNetAmount());
		case COL_FREQUENCY:
			int comp = e1.getRule().getFrequency().compareTo(e2.getRule().getFrequency());
			if (comp == 0) {
				comp = Integer.compare(e1.getRule().getInterval(), e2.getRule().getInterval());
			}
			return comp;
		default:
			return 0;			
		}

	}

	/**
	 * @see de.tfsw.accounting.ui.AbstractTableSorter#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

}
