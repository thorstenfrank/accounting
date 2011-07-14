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
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

import de.togginho.accounting.model.User;
import de.togginho.accounting.ui.wizard.ImportFromXmlWizard;
import de.togginho.accounting.ui.wizard.SetupExistingDataWizard;
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
	
	private SetupMode setupMode;
	
	/**
	 * 
	 */
	@Override
	public void init(Shell splash) {
		super.init(splash);
		
		
		try {
			LOG.debug("reading preferences and reading stored data..."); //$NON-NLS-1$
			initialised = AccountingUI.getDefault().initContext();
			
			if (!initialised) {
				// no prefs found - assume we're running for the first time
				LOG.warn("Context not properly initialised, running wizard now..."); //$NON-NLS-1$
				runSetup();
				//runSetupWizard();				
			}
		} catch (Exception e) {
			LOG.error("Error during init", e); //$NON-NLS-1$
			initialised = false;
			errorMessage = e.getLocalizedMessage();
		}
		
		// something went wrong, don't start up the application
		if (!initialised) {
			MessageBox msgBox = new MessageBox(splash, SWT.ICON_ERROR | SWT.OK);
			msgBox.setText(Messages.labelError);
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
	private void runSetup() {
		final int returnCode = new WelcomeDialog(getSplash()).open();
		if (returnCode == WelcomeDialog.CANCEL) {
			handleSetupCancelledByUser();
		} else {
			switch (setupMode) {
			case CREATE_NEW:
				runSetupWizard();
				break;
			case USE_EXISTING:
				runUseExistingWizard();
				break;
			case IMPORT_XML:
				runImportFromXmlWizard();
				break;
			}
			
			AccountingUI.getDefault().setFirstRun(true);
		}
	}
	
	/**
	 * 
	 */
	private void runSetupWizard() {
		LOG.info("Running setup wizard..."); // $NON-NLS-1$ //$NON-NLS-1$
		SetupWizard wizard = new SetupWizard();
		WizardDialog dialog = new WizardDialog(getSplash(), wizard);
		if (dialog.open() == WizardDialog.CANCEL) {
			handleSetupCancelledByUser();
		} else {
			LOG.info("Wizard finished normally, now starting up application core"); //$NON-NLS-1$
			User user = wizard.getConfiguredUser();
			
			AccountingUI.getDefault().initContext(user.getName(), wizard.getDbFileLocation());
			
			try {
				LOG.info("Initial save of user " +user.getName()); //$NON-NLS-1$
				AccountingUI.getAccountingService().saveCurrentUser(user);
				initialised = true;
			} catch (Exception e) {
				LOG.error("Error saving user", e);  //$NON-NLS-1$
				errorMessage = e.getLocalizedMessage();
			}
		}
	}
	
	/**
	 * 
	 */
	private void runUseExistingWizard() {
		LOG.info("Running wizard to use existing data..."); // $NON-NLS-1$ //$NON-NLS-1$
		SetupExistingDataWizard wizard = new SetupExistingDataWizard();
		WizardDialog dialog = new WizardDialog(getSplash(), wizard);
		
		if (dialog.open() == WizardDialog.CANCEL) {
			handleSetupCancelledByUser();
		} else {
			LOG.info("Now running useExisting stuff..."); //$NON-NLS-1$
			
			AccountingUI.getDefault().initContext(wizard.getUserName(), wizard.getFileLocation());
			
			User user = AccountingUI.getAccountingService().getCurrentUser();
			if (user == null) {
				LOG.warn(String.format("User [%s] not found in data file [%s]", //$NON-NLS-1$
						wizard.getUserName(), wizard.getFileLocation()));
				errorMessage = Messages.bind(Messages.AccountingSplashHandler_errorUserNotFoundInDbFile, 
						wizard.getUserName(), wizard.getFileLocation());
				initialised = false;
			} else {
				initialised = true;				
			}
		}
	}
	
	/**
	 * 
	 */
	private void runImportFromXmlWizard() {
		LOG.info("Running wizard to import from XML..."); //$NON-NLS-1$
		ImportFromXmlWizard wizard = new ImportFromXmlWizard();
		WizardDialog dialog = new WizardDialog(getSplash(), wizard);
		
		if (dialog.open() == WizardDialog.CANCEL) {
			handleSetupCancelledByUser();
		} else {
			try {
	            AccountingUI.getDefault().initContextFromImport(wizard.getXmlFile(), wizard.getDbFile());
	            initialised = true;
            } catch (Exception e) {
            	errorMessage = e.getMessage();
            	initialised = false;
            }
		}
	}
	
	/**
	 * 
	 */
	private void handleSetupCancelledByUser() {
		LOG.warn("Cannot continue application without necessary info - exiting now...");  //$NON-NLS-1$
		errorMessage = Messages.AccountingSplashHandler_setupDialogCancelledMsg;
	}

	/**
	 * Setup mode used when no existing preferences are found...
	 */
	private enum SetupMode {
		CREATE_NEW, USE_EXISTING, IMPORT_XML;
	}
	
	/**
	 *
	 */
	private class WelcomeDialog extends TitleAreaDialog {

		private WelcomeDialog(Shell parentShell) {
	        super(parentShell);
	        setupMode = SetupMode.CREATE_NEW;
        }
		
		@Override
		public void create() {
		    super.create();
	        setTitle(Messages.AccountingSplashHandler_welcomeDialogTitle);
	        setMessage(Messages.AccountingSplashHandler_welcomeDialogMessage);
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);
			GridLayout layout = new GridLayout();
			layout.marginTop = 10;
			layout.marginLeft = 10;
			layout.marginRight = 10;
			layout.marginBottom = 10;
			
			composite.setLayout(new GridLayout());
			
			final Button newData = new Button(composite, SWT.RADIO);
			newData.setText(Messages.AccountingSplashHandler_welcomeDialogCreateNew);
			newData.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setupMode = SetupMode.CREATE_NEW;
				}
			});
						
			final Button useExisting = new Button(composite, SWT.RADIO);
			useExisting.setText(Messages.AccountingSplashHandler_welcomeDialogUseExisting);
			useExisting.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setupMode = SetupMode.USE_EXISTING;
				}
			});
			
			final Button importFromXml = new Button(composite, SWT.RADIO);
			importFromXml.setText(Messages.AccountingSplashHandler_welcomeDialogImportXml);
			importFromXml.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setupMode = SetupMode.IMPORT_XML;
				}
			});
			
			return composite;
		}
	}
}