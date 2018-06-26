/*
 *  Copyright 2018 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog shown when the application is run for the first time (or stored preferences cannot be found).
 * Gives the user the chance to start fresh, re-use or import previous data.
 * 
 * @author thorsten
 *
 */
final class WelcomeDialog extends TitleAreaDialog {
	
	private static final Logger LOG = LogManager.getLogger(WelcomeDialog.class);
	
	private static enum SetupMode {
		CREATE_NEW("welcomeDialog_createNew", CreateNewWizard.class), 
		USE_EXISTING("welcomeDialog_useExisting", SelectExistingWizard.class), 
		IMPORT_XML("welcomeDialog_importXml", ImportFromXmlWizard.class);
		
		private String translationKey;
		
		private Class<? extends AbstractSetupWizard> wizardType;

		private SetupMode(String translationKey, Class<? extends AbstractSetupWizard> wizardType) {
			this.translationKey = translationKey;
			this.wizardType = wizardType;
		}		
	}
	
	private SetupMode setupMode;
	private Messages messages;
	
	WelcomeDialog(Shell parentShell, Messages messages) {
        super(parentShell);
        setupMode = SetupMode.CREATE_NEW;
        this.messages = messages;
    }
	
	Class<? extends AbstractSetupWizard> getSelectedWizardType() {
		return setupMode.wizardType;
	}
	
	@Override
	public void create() {
	    super.create();
        setTitle(messages.welcomeDialog_title);
        setMessage(messages.welcomeDialog_message);
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
		addButton(composite, SetupMode.CREATE_NEW, true);
		addButton(composite, SetupMode.USE_EXISTING, false);
		addButton(composite, SetupMode.IMPORT_XML, false);
		
		return composite;
	}
	
	private void addButton(final Composite parent, final SetupMode targetMode, final boolean selected) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setSelection(selected);
		
		// not pretty, but it works... and we don't have to keep information in several different places
		String buttonText = targetMode.translationKey;
		try {
			buttonText = Messages.class.getField(buttonText).get(messages).toString();
		} catch (Exception e) {
			LOG.error("Problems translating key " + targetMode.translationKey, e);
		}
				
		button.setText(buttonText);
		button.addSelectionListener(SelectionListener.widgetSelectedAdapter(c -> setupMode = targetMode));
	}
}
