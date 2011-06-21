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
package de.togginho.accounting.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

import de.togginho.accounting.model.User;
import de.togginho.accounting.ui.wizard.SetupWizard;

/**
 * Handles basic init of the application.
 * Reads needed user-scoped preferences, initialises the core, runs the setup wizard if this is the first application
 * run, and so on.
 * 
 * This handler is plugged into the application via the plugin configuration element
 * <code>org.eclipse.ui.splashHandlers</code>.
 * 
 * @author tfrank1
 *
 */
public class AccountingSplashHandler extends AbstractSplashHandler {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(AccountingSplashHandler.class);
	
	/** Flag to signal init state. */
	private boolean initialised = false;
	
	/** Error message to display at the end. */
	private String errorMessage = null;
	
	/**
	 * 
	 */
	@Override
	public void init(Shell splash) {
		super.init(splash);
		
		LOG.debug("reading preferences and reading stored data..."); //$NON-NLS-1$
		
		try {
			initialised = AccountingUI.getDefault().initContext();
			
			if (!initialised) {
				// no prefs found - assume we're running for the first time
				LOG.warn("Context not properly initialised, running wizard now..."); //$NON-NLS-1$
				runSetupWizard();				
			}
			
		} catch (Exception e) {
			LOG.error("Error during init", e); //$NON-NLS-1$
			initialised = false;
			errorMessage = e.getLocalizedMessage();
		}
		
		// something went wrong, don't start up the application
		if (!initialised) {
			MessageBox msgBox = new MessageBox(splash, SWT.ICON_ERROR | SWT.OK);
			msgBox.setText("Error!"); //$NON-NLS-1$
			msgBox.setMessage(errorMessage);
			msgBox.open();
			
			// exit the app before the main event loop starts running...
			splash.getDisplay().close();
			System.exit(0);
		}
	}
	
	/**
	 * 
	 */
	private void runSetupWizard() {
		SetupWizard wizard = new SetupWizard();
		WizardDialog dialog = new WizardDialog(getSplash(), wizard);
		int returnCode = dialog.open();

		if (returnCode == WizardDialog.CANCEL) {
			LOG.warn("Cannot continue application without necessary info - exiting now...");  //$NON-NLS-1$
			errorMessage = Messages.AccountingSplashHandler_setupDialogCancelledMsg;
		} else {
			LOG.info("Wizard finished normally, now starting up application core"); //$NON-NLS-1$
			User user = wizard.getConfiguredUser();
			
			AccountingUI.getDefault().initContext(user.getName(), wizard.getDbFileLocation());
			
			handleUserSetup(user);
		}
	}
	
	/**
	 * 
	 * @param userFromWizard
	 */
	private void handleUserSetup(User userFromWizard) {
		User currentUser = ModelHelper.getCurrentUser();
		
		if (currentUser != null) {
			MessageBox msgBox = new MessageBox(getSplash(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			msgBox.setText(Messages.AccountingSplashHandler_reuseDataText);
			msgBox.setMessage(Messages.bind(Messages.AccountingSplashHandler_reuseDataMsg, currentUser.getName()));
			int result = msgBox.open();
			
			if (result == SWT.YES) {
				LOG.debug("Reusing existing data for user " + currentUser.getName()); //$NON-NLS-1$
				initialised = true;
			}
		}
		
		if (!initialised) {
			try {
				LOG.info("Initial save of user " +userFromWizard.getName()); //$NON-NLS-1$
				ModelHelper.saveCurrentUser(userFromWizard);
				initialised = true;
			} catch (Exception e) {
				LOG.error("Error saving user", e);  //$NON-NLS-1$
				errorMessage = e.getLocalizedMessage();
				return;
			}    				
		}
		
		AccountingUI.getDefault().setFirstRun(true);
	}
}