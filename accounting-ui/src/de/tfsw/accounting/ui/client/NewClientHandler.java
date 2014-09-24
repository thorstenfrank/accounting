/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.client;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;

import de.tfsw.accounting.ui.AbstractAccountingHandler;

/**
 * @author thorsten
 *
 */
public class NewClientHandler extends AbstractAccountingHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(NewClientHandler.class);
	
	/**
     * {@inheritDoc}.
     * @see de.tfsw.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
		LOG.debug("Running new client wizard"); //$NON-NLS-1$
		
    	NewClientWizard wizard = new NewClientWizard();
    	WizardDialog dialog = new WizardDialog(getShell(event), wizard);
    	
		dialog.open();
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
