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

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;

import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.ui.AbstractAccountingHandler;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;

/**
 * @author Thorsten Frank
 *
 */
public class FindApplicableTemplatesHandler extends AbstractAccountingHandler {

	private static final Logger LOG = LogManager.getLogger(FindApplicableTemplatesHandler.class);
	
	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		Set<ExpenseTemplate> templates = AccountingUI.getAccountingService().findApplicableExpenseTemplates();
		LOG.debug("Number of applicable templates found: " + templates.size()); //$NON-NLS-1$
		
		if (templates.size() < 1) {
			showWarningMessage(event, 
					Messages.FindApplicableTemplatesHandler_noTemplatesMsg, 
					Messages.FindApplicableTemplatesHandler_noTemplatesTitle, 
					false);
		} else {
			WizardDialog dlg = new WizardDialog(getShell(event), new ApplyTemplatesWizard(templates));
			dlg.open();			
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
