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
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;

import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.ui.AbstractAccountingHandler;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;

/**
 * @author Thorsten Frank
 *
 */
public class DeleteExpenseTemplateHandler extends AbstractAccountingHandler {

	private static final Logger LOG = Logger.getLogger(DeleteExpenseTemplateHandler.class);
	
	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		ExpenseTemplate toBeDeleted = getCurrentSelection(event, ExpenseTemplate.class);
		
		if (showWarningMessage(
				event, 
				Messages.bind(Messages.DeleteExpenseTemplateCommand_confirmMessage, toBeDeleted.getDescription()), 
				Messages.DeleteExpenseTemplateCommand_confirmText, 
				true)) {
			
			LOG.info("Deleting expense template " + toBeDeleted.getDescription()); //$NON-NLS-1$
			
			AccountingUI.getAccountingService().deleteExpenseTemplate(toBeDeleted);
			
			closeOpenEditorForExpense(toBeDeleted, event);
		}
	}

	/**
	 * 
	 * @param closeMe
	 * @param event
	 */
	private void closeOpenEditorForExpense(ExpenseTemplate closeMe, ExecutionEvent event) {
		getLogger().debug("Checking for open editors for expense template: " + closeMe.getDescription()); //$NON-NLS-1$
		IWorkbenchPage page = getActivePage(event);
		
		for (IEditorReference editorRef : page.findEditors(null, IDs.EDIT_EXPENSE_TEMPLATE_ID, IWorkbenchPage.MATCH_ID)) {
			if (editorRef.getName().equals(closeMe.getDescription())) {
				getLogger().debug("Closing editor for expense template: " + editorRef.getName()); //$NON-NLS-1$
				page.closeEditor(editorRef.getEditor(false), false);
			}
		}		
	}
	
	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

}
