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

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.User;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;

/**
 * Deletes a client provided by the active {@link IWorkbenchPart}, or more precisely it's {@link ISelectionProvider}.
 * 
 * @author tfrank1
 */
public class DeleteClientCommand extends AbstractClientHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(DeleteClientCommand.class);
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.rcp.client.AbstractClientHandler#handleClient(de.togginho.accounting.model.Client)
	 */
	@Override
	protected void handleClient(Client client) throws ExecutionException {
		deleteClientFromCurrentUser(client);
	}
	
	/**
	 * Does the actual work.
	 * @param toBeDeleted
	 */
	private void deleteClientFromCurrentUser(Client toBeDeleted) {
		MessageBox messageBox = new MessageBox(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				SWT.ICON_WARNING | SWT.YES | SWT.NO);
		LOG.debug("Confirming deletion of client " + toBeDeleted.getName()); //$NON-NLS-1$
		messageBox.setMessage(Messages.bind(Messages.DeleteClientCommand_confirmMessage, toBeDeleted.getName()));
		messageBox.setText(Messages.DeleteClientCommand_confirmText);
		
		if (messageBox.open() == SWT.YES) {
			User currentUser = ModelHelper.getCurrentUser();
			
			LOG.debug(String.format("Removing client [%s] from user [%s]",  //$NON-NLS-1$
					toBeDeleted.getName(), currentUser.getName()));
			
			Iterator<Client> iter = currentUser.getClients().iterator();
			while (iter.hasNext()) {
				Client client = iter.next();
				if (client.equals(toBeDeleted)) {
					LOG.debug("Found client, now removing"); //$NON-NLS-1$
					iter.remove();
					break;
				}
			}
			
			ModelHelper.saveCurrentUser();
			
			removeOpenEditorForClient(toBeDeleted);
		} else {
			LOG.debug(String.format("Deleting client [%s] was cancelled.", toBeDeleted.getName())); //$NON-NLS-1$
		}
	}
	
	/**
	 * @param toBeDeleted
	 */
	private void removeOpenEditorForClient(Client toBeDeleted) {
		LOG.debug("Checking for open editors for client " + toBeDeleted.getName()); //$NON-NLS-1$
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		for (IEditorReference editorRef : page.findEditors(null, IDs.EDIT_CLIENT_ID, IWorkbenchPage.MATCH_ID)) {
			if (editorRef.getName().equals(toBeDeleted.getName())) {
				LOG.debug("Closing editor for deleted client: " + editorRef.getName()); //$NON-NLS-1$
				page.closeEditor(editorRef.getEditor(false), false);
			}
		}		
	}
}