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
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;

/**
 * Deletes a client provided by the active {@link IWorkbenchPart}, or more precisely it's {@link ISelectionProvider}.
 * 
 * @author tfrank1
 */
public class DeleteClientHandler extends AbstractClientHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(DeleteClientHandler.class);
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see AbstractClientHandler#handleClient(Client, ExecutionEvent)
	 */
	@Override
	protected void handleClient(Client clientToDelete, ExecutionEvent event) throws ExecutionException {
		MessageBox messageBox = new MessageBox(getShell(event), SWT.ICON_WARNING | SWT.YES | SWT.NO);
		
		LOG.debug("Confirming deletion of client " + clientToDelete.getName()); //$NON-NLS-1$
		
		messageBox.setMessage(Messages.bind(Messages.DeleteClientCommand_confirmMessage, clientToDelete.getName()));
		messageBox.setText(Messages.DeleteClientCommand_confirmText);
		
		if (messageBox.open() == SWT.YES) {
			
			LOG.debug(String.format("Removing client [%s]", clientToDelete.getName())); //$NON-NLS-1$
			
			// save the current user
			AccountingUI.getAccountingService().deleteClient(clientToDelete);
			
			// close open editors for the deleted client, if open
			removeOpenEditorForClient(clientToDelete, event);
		} else {
			LOG.debug(String.format("Deleting client [%s] was cancelled.", clientToDelete.getName())); //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
    protected Logger getLogger() {
	    return LOG;
    }
	
	/**
	 * @param deletedClient
	 */
	private void removeOpenEditorForClient(Client deletedClient, ExecutionEvent event) {
		LOG.debug("Checking for open editors for client " + deletedClient.getName()); //$NON-NLS-1$
		IWorkbenchPage page = getActivePage(event);
		
		for (IEditorReference editorRef : page.findEditors(null, IDs.EDIT_CLIENT_ID, IWorkbenchPage.MATCH_ID)) {
			if (editorRef.getName().equals(deletedClient.getName())) {
				LOG.debug("Closing editor for deleted client: " + editorRef.getName()); //$NON-NLS-1$
				page.closeEditor(editorRef.getEditor(false), false);
			}
		}		
	}
	
	
}