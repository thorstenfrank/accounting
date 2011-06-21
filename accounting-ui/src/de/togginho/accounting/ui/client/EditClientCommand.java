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
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.ui.IDs;

/**
 * @author thorsten
 *
 */
public class EditClientCommand extends AbstractClientHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(EditClientCommand.class);

	private static final String ERROR_MSG = "Error opening client editor for client [%s]"; //$NON-NLS-1$
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.rcp.client.AbstractClientHandler#handleClient(de.togginho.accounting.model.Client)
	 */
	@Override
	protected void handleClient(Client client) throws ExecutionException {
		ClientEditorInput input = new ClientEditorInput(client);
		
		try {
			getActivePage().openEditor(input, IDs.EDIT_CLIENT_ID);
		} catch (PartInitException e) {
			final String msg = String.format(ERROR_MSG, client);
			LOG.error(msg, e);
			throw new ExecutionException(msg, e);
		}
	}
}
