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
package de.togginho.accounting.ui.client;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
public class EditClientCommand extends AbstractClientHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(EditClientCommand.class);
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see AbstractClientHandler#handleClient(Client, ExecutionEvent)
	 */
	@Override
    protected void handleClient(Client client, ExecutionEvent event) throws ExecutionException {
		ClientEditorInput input = new ClientEditorInput(client);
		
		try {
	        getActivePage(event).openEditor(input, IDs.EDIT_CLIENT_ID);
        } catch (PartInitException e) {
        	LOG.error("Error opening client editor for client " + client.getName(), e);
        	throw new ExecutionException(Messages.bind(Messages.EditClientCommand_errorOpeningEditor, client), e);
        }
    }

	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
    protected Logger getLogger() {
	    return LOG;
    }
}
