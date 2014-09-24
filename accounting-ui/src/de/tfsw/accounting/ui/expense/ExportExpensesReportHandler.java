/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;

import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.ui.AbstractAccountingHandler;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.reports.ReportGenerationUtil;

/**
 * @author thorsten
 *
 */
public class ExportExpensesReportHandler extends AbstractAccountingHandler {

	private static final Logger LOG = Logger.getLogger(ExportExpensesReportHandler.class);

	private ExpenseCollection expenseCollection;
	
	private String baseFileName;
	
	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		IViewPart viewPart = getActivePage(event).findView(IDs.VIEW_EXPENSES_ID);
		ExpensesView expensesView = null;
		if (viewPart != null && viewPart instanceof ExpensesView) {
			expensesView = (ExpensesView) viewPart;
		} else {
			LOG.warn("No active invoice view found! This command should not have been fired..."); //$NON-NLS-1$
			return;
		}
		
		expenseCollection = expensesView.getExpenseCollection();
		Set<ExpenseType> types = expensesView.getSelectedTypes();
		if (types == null || types.size() != 1) {
			baseFileName = Messages.labelExpenses;
		} else {
			baseFileName = types.iterator().next().getTranslatedString();
		}
		
		ReportGenerationUtil.executeReportGeneration(
				new ExpensesReportGenerationHandler(expenseCollection, baseFileName), getShell(event));
	}

	/**
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
