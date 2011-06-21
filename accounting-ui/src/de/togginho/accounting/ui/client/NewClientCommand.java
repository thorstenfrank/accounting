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
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.ui.wizard.NewClientWizard;

/**
 * @author thorsten
 *
 */
public class NewClientCommand extends AbstractHandler {

	private static final Logger LOG = Logger.getLogger(NewClientCommand.class);
	
	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LOG.debug("Running new client wizard"); //$NON-NLS-1$
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		NewClientWizard wizard = new NewClientWizard();
		
		WizardDialog dialog = new WizardDialog(shell, wizard);
		
		int returnCode = dialog.open();
		
		if (returnCode == WizardDialog.CANCEL) {
			LOG.debug("NewClientWizard was cancelled"); //$NON-NLS-1$
		} else {
			LOG.debug("NewClientWizard finished successfully"); //$NON-NLS-1$
		}
		
		return null;
	}

}
