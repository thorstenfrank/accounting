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

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.util.FormatUtil;

/**
 * @author Thorsten Frank
 *
 */
class ExpenseTemplateLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
	
	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		ExpenseTemplate re = (ExpenseTemplate) element;
		
		switch (columnIndex) {
		case ExpenseTemplateTableSorter.COL_ACTIVE:
			return Boolean.toString(re.isActive());
		case ExpenseTemplateTableSorter.COL_FIRST_APPLICATION:
			return FormatUtil.formatDate(re.getFirstApplication());
		case ExpenseTemplateTableSorter.COL_DESC:
			return re.getDescription();
		case ExpenseTemplateTableSorter.COL_NET:
			return FormatUtil.formatCurrency(re.getNetAmount());
		case ExpenseTemplateTableSorter.COL_FREQUENCY:
			StringBuilder sb = new StringBuilder();
			sb.append(re.getRule().getInterval());
			sb.append(Constants.BLANK_STRING).append(Constants.HYPHEN).append(Constants.BLANK_STRING);
			sb.append(re.getRule().getFrequency().getTranslatedString());
			return sb.toString();
		default:
			return null;			
		}
	}


}
