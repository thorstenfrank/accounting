/*
 *  Copyright 2011 thorsten frank (thorsten.frank@tfsw.de).
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
package de.tfsw.accounting.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import de.tfsw.accounting.ui.user.UserEditorInput;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final Logger LOG = LogManager.getLogger(ApplicationWorkbenchAdvisor.class);
	
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		
		// if this is the first run, let the perspective build the default initial layout, otherwise try to 
		// load the setup from a previous session
		configurer.setSaveAndRestore(!AccountingUI.getDefault().isFirstRun());
	}

	/**
	 * 
	 * {@inheritDoc}
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	public String getInitialWindowPerspectiveId() {
		return AccountingPerspective.ID;
	}
	
	/**
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#postStartup()
	 */
	@Override
	public void postStartup() {
		LOG.debug("postStartup()"); //$NON-NLS-1$
		if (AccountingUI.getDefault().isFirstRun()) {
			LOG.info("First run of application, opening user editor"); //$NON-NLS-1$

			// make sure the workbench state is saved when the application closes
			enableSaveAndRestore();
				
			// now open the user editor to start off the application
			openUserEditor();
		}
	}
	
	/**
	 * 
	 */
	private void enableSaveAndRestore() {
		try {
			getWorkbenchConfigurer().setSaveAndRestore(true);
		} catch (Exception e) {
			LOG.error("Could not enable save and restore", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 */
	private void openUserEditor() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
							new UserEditorInput(
									AccountingUI.getAccountingService().getCurrentUser()),
									IDs.EDIT_USER_ID);
				} catch (Exception e) {
					LOG.error("Couldn't open user editor", e); //$NON-NLS-1$
				}
			}
		});
	}
}
