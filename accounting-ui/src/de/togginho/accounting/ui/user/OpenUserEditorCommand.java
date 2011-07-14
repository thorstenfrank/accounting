/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.user;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;

import de.togginho.accounting.ui.AbstractAccountingHandler;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;

/**
 * Opens the editor for the current user.
 * 
 * @author tfrank1
 *
 */
public class OpenUserEditorCommand extends AbstractAccountingHandler {

	private static final Logger LOG = Logger.getLogger(OpenUserEditorCommand.class);
	
	private static final String ERROR_MSG = "Error opening user editor"; //$NON-NLS-1$
	
	/**
     * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
    	LOG.debug("Opening user editor"); //$NON-NLS-1$
	    
    	UserEditorInput input = new UserEditorInput(AccountingUI.getAccountingService().getCurrentUser());
    	
		try {
			getActivePage(event).openEditor(input, IDs.EDIT_USER_ID);
		} catch (PartInitException e) {
			LOG.error(ERROR_MSG, e);
			throw new ExecutionException(ERROR_MSG, e);
		}
    }
    
	/**
     * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
     */
    @Override
    protected Logger getLogger() {
	    return LOG;
    }
}
