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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.tfsw.accounting.model.Expense;

/**
 * @author thorsten
 *
 */
public class CopyExpenseHandler extends AbstractExpenseHandler {
	
	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(CopyExpenseHandler.class);
	
	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		Expense expense = getExpenseFromSelection(event);
		
		Expense newExpense = new Expense();
		newExpense.setDescription(expense.getDescription());
		newExpense.setCategory(expense.getCategory());
		newExpense.setExpenseType(expense.getExpenseType());
		newExpense.setNetAmount(expense.getNetAmount());
		newExpense.setPaymentDate(expense.getPaymentDate());
		newExpense.setTaxRate(expense.getTaxRate());
		
		openExpenseWizard(event, newExpense);
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
