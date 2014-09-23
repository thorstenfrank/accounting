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

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.ui.AbstractAccountingHandler;
import de.togginho.accounting.ui.Messages;

/**
 * Abstract base class for command handlers for {@link Client} entities.
 * 
 * <p>The {@link #doExecute(ExecutionEvent)} method will retrieve all clients from the current selection provider and
 * call {@link #handleClient(Client, ExecutionEvent)} for each of these selected client objects.</p>
 * 
 * @author thorsten
 *
 */
abstract class AbstractClientHandler extends AbstractAccountingHandler {
	
	/**
	 * Extracts the {@link Client} for which this handler/command was fired and then calls 
	 * {@link #handleClient(Client, ExecutionEvent)}.
	 * 
	 * @see AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 * 
	 * @see #handleClient(Client, ExecutionEvent)
	 */
	@SuppressWarnings("unchecked")
    @Override
    protected void doExecute(ExecutionEvent event) throws ExecutionException {
	    ISelectionProvider selectionProvider = getSelectionProvider(event);
	    
		if (selectionProvider != null) {
			if (selectionProvider.getSelection().isEmpty()) {
				getLogger().warn("No active selection, cannot run command"); //$NON-NLS-1$
				showWarningMessage(event, Messages.AbstractClientHandler_message, Messages.AbstractClientHandler_text, false);
			} else if (selectionProvider.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
				
				for (Iterator<Object> iter = structuredSelection.iterator(); iter.hasNext(); ){
					Object selected = iter.next();
					if (selected instanceof Client) {						
						handleClient((Client) selected, event);
					}
				}
			}
		} else {
			getLogger().warn("No selection provider found, ignoring this command request"); //$NON-NLS-1$
		}
    }

	/**
	 * Called to start actual processing of a command for the supplied client.
	 * 
	 * <p>Implementations should throw any exceptions that occur, the superclass will catch them and display an
	 * appropriate error message box to the user.</p>
	 * 
	 * @param client the {@link Client} to process the command for
	 * @param event the event this command was executed with
	 * @throws ExecutionException if an error occurs during processing
	 */
	protected abstract void handleClient(Client client, ExecutionEvent event) throws ExecutionException;
}
