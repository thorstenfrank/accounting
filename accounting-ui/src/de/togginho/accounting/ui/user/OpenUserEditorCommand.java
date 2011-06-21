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
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.ModelHelper;

/**
 * @author tfrank1
 *
 */
public class OpenUserEditorCommand extends AbstractHandler {

	private static final String ERROR_MSG = "Error opening user editor"; //$NON-NLS-1$
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Logger.getLogger(this.getClass()).debug("Opening user editor"); //$NON-NLS-1$
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		//HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		
		UserEditorInput input = new UserEditorInput(ModelHelper.getCurrentUser());
		
		try {
			page.openEditor(input, IDs.EDIT_USER_ID);
		} catch (PartInitException e) {
			Logger.getLogger(this.getClass()).error(ERROR_MSG, e);
			throw new ExecutionException(ERROR_MSG, e);
		}
		
		
		return null;
	}

}
