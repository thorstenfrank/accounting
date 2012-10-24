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
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;

import de.togginho.accounting.model.Expense;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
public class DeleteExpenseHandler extends AbstractExpenseHandler {

	/** */
	private static final Logger LOG = Logger.getLogger(DeleteExpenseHandler.class);
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		Expense expense = getExpenseFromSelection(event);
		
		final boolean areYouSure =  showWarningMessage(
				event, 
				Messages.bind(Messages.DeleteExpenseCommand_confirmMessage, expense.getDescription()), 
				Messages.DeleteExpenseCommand_confirmText);
				
		if (areYouSure) {
			getLogger().info("Deleting expense " + expense.getDescription()); //$NON-NLS-1$
			
			// do the actual work
			AccountingUI.getAccountingService().deleteExpense(expense);
			
			// close any open editors for the deleted invoice
			closeOpenEditorForExpense(expense, event);
		} else {
			getLogger().info("Delete was cancelled by user"); //$NON-NLS-1$
		}
	}

	/**
	 * 
	 * @param closeMe
	 * @param event
	 */
	private void closeOpenEditorForExpense(Expense closeMe, ExecutionEvent event) {
		getLogger().debug("Checking for open editors for expense " + closeMe.getDescription()); //$NON-NLS-1$
		IWorkbenchPage page = getActivePage(event);
		
		for (IEditorReference editorRef : page.findEditors(null, IDs.EDIT_EXPENSE_ID, IWorkbenchPage.MATCH_ID)) {
			if (editorRef.getName().equals(closeMe.getDescription())) {
				getLogger().debug("Closing editor for expense: " + editorRef.getName()); //$NON-NLS-1$
				page.closeEditor(editorRef.getEditor(false), false);
			}
		}		
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

}
