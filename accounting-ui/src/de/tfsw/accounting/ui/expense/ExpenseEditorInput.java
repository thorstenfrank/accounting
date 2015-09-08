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
package de.tfsw.accounting.ui.expense;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
public class ExpenseEditorInput implements IEditorInput {

	private Expense expense;
		
	/**
	 * @param expense
	 */
	public ExpenseEditorInput(Expense expense) {
		this.expense = expense;
	}
	
	/**
	 * @return the expense
	 */
	protected Expense getExpense() {
		return expense;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return AccountingUI.getImageDescriptor(Messages.iconsExpenseEdit);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		return expense.getDescription() != null ? expense.getDescription() : Constants.BLANK_STRING;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return Messages.ExpenseEditor_title;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ExpenseEditorInput) {
			return expense.equals(((ExpenseEditorInput) obj).getExpense());
		}
	    return super.equals(obj);
	}
}