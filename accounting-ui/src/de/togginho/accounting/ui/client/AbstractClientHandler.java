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
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
abstract class AbstractClientHandler extends AbstractHandler {

	private static final Logger LOG = Logger.getLogger(AbstractClientHandler.class);
	
	private IWorkbenchPage activePage;
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LOG.debug("Opening client editor"); //$NON-NLS-1$

		activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart part = activePage.getActivePart();
		
		LOG.debug("Active part: " + part.getTitle()); //$NON-NLS-1$
		
		ISelectionProvider selectionProvider = part.getSite().getSelectionProvider();
		
		if (selectionProvider != null) {
			LOG.debug("Selection provider found, now querying for active selection"); //$NON-NLS-1$
			
			if (selectionProvider.getSelection().isEmpty()) {
				LOG.debug("No selection"); //$NON-NLS-1$
				MessageBox msgBox = 
					new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
				msgBox.setMessage(Messages.AbstractClientHandler_message);
				msgBox.setText(Messages.AbstractClientHandler_text);
				msgBox.open();
			} else if (selectionProvider.getSelection() instanceof IStructuredSelection) {

				IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
				
				for (Iterator<Object> iter = structuredSelection.iterator(); iter.hasNext(); ){
					Object selected = iter.next();
					if (selected instanceof Client) {						
						handleClient((Client) selected);
					}
				}
			}
		} else {
			LOG.debug("No selection provider found."); //$NON-NLS-1$
		}
		return null;
	}

	protected abstract void handleClient(Client client) throws ExecutionException ;
	
	protected IWorkbenchPage getActivePage() {
		return activePage;
	}
}
