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

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
class ExpensesLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

	protected static final int COL_INDEX_DATE = 1;
	protected static final int COL_INDEX_DESC = 2;
	protected static final int COL_INDEX_CAT = 3;
	protected static final int COL_INDEX_TYPE = 4;
	protected static final int COL_INDEX_NET = 5;
	protected static final int COL_INDEX_TAX = 6;
	
	/**
     * {@inheritDoc}.
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

	/**
     * {@inheritDoc}.
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
    	Expense expense = (Expense) element;
    	
    	switch (columnIndex) {
		case COL_INDEX_DATE:
			return expense.getPaymentDate() != null ? FormatUtil.formatDate(expense.getPaymentDate()) : null;
		case COL_INDEX_DESC:
			return expense.getDescription();
		case COL_INDEX_CAT:
			return expense.getCategory();
		case COL_INDEX_TYPE:
			return expense.getExpenseType() != null ? expense.getExpenseType().getTranslatedString() : Constants.BLANK_STRING;
		case COL_INDEX_NET:
			return expense.getNetAmount() != null ? FormatUtil.formatDecimalValue(expense.getNetAmount()) : Constants.BLANK_STRING;
		case COL_INDEX_TAX:
			return expense.getTaxRate() != null ? expense.getTaxRate().toShortString() : Constants.BLANK_STRING;
		default:
			return null;
		}
    }

}
