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
package de.tfsw.accounting.ui.expense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;

import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.ui.expense.template.ExpenseTemplateWizard;

/**
 * @author Thorsten Frank
 *
 */
public class ConvertToTemplateHandler extends AbstractExpenseHandler {

	private static final Logger LOG = LogManager.getLogger(ConvertToTemplateHandler.class);
	
	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		Expense base = getExpenseFromSelection(event);
		if (base != null) {
			LOG.debug("Submitting expense as base for new template: " + base.getDescription()); //$NON-NLS-1$
			new WizardDialog(getShell(event), new ExpenseTemplateWizard(base)).open();
		} else {
			LOG.warn("No expense selected, this command should not have been enabled!"); //$NON-NLS-1$
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
