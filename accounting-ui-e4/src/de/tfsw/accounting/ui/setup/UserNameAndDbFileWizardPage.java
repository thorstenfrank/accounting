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
package de.tfsw.accounting.ui.setup;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.FileUtil;

/**
 * Wizard page for basic information (name and DB file location).
 * 
 * @author thorsten
 */
class UserNameAndDbFileWizardPage extends WizardPage {

	private boolean newFile;
	private Text userName;
	private Text dbFile;
	private Messages messages;
	
	/**
	 * 
	 * @param newFile
	 */
	UserNameAndDbFileWizardPage(boolean newFile, Messages messages) {
		super("UserNameAndDbFileWizardPage"); //$NON-NLS-1$
		this.messages = messages;
		this.newFile = newFile;
		
		StringBuilder sb = new StringBuilder(messages.userNameAndDbFile_MessagePrefix);
		if (newFile) {
			setTitle(messages.createNewWizard_windowTitle);
			sb.append(messages.userNameAndDbFile_messageNew);
		} else {
			sb.append(messages.userNameAndDbFile_messageExisting);
		}
		setMessage(sb.toString());
	}
	
	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		
		// User name
		final KeyListener listener = KeyListener.keyReleasedAdapter(e -> updateIsPageComplete());
		WidgetHelper.createLabel(composite, messages.userNameAndDbFile_yourName);
		
		userName = WidgetHelper.createSingleBorderText(composite, System.getProperty("user.name"));
		userName.addKeyListener(listener);
		
		// add an empty label as a spacer
		WidgetHelper.createLabel(composite, null);
		
		// Data file
		WidgetHelper.createLabel(composite, 
				newFile ? messages.setupWizards_dataFileLabelNew : messages.setupWizards_dataFileLabelExisting);
		
		dbFile = WidgetHelper.createSingleBorderText(
				composite, 
				FileUtil.defaultDataPath());
		dbFile.setEditable(false);
		dbFile.addKeyListener(listener);
		
		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("..."); //$NON-NLS-1$
		browseButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
			DirectoryDialog dd = new DirectoryDialog(composite.getShell(), newFile ? SWT.SAVE : SWT.OPEN);
			dd.setText(messages.setupWizards_dataFileSelectText);
			dd.setMessage(messages.setupWizards_dataFileSelectText);
			dd.setFilterPath(dbFile.getText());
			String selected = dd.open();
			
//			FileDialog fd = new FileDialog(composite.getShell(), newFile ? SWT.SAVE : SWT.OPEN);
//			fd.setText(messages.setupWizards_dataFileSelectText);
//			fd.setFileName(dbFile.getText());
//			String selected = fd.open();
			if (selected != null) {
				dbFile.setText(selected);
			}
			updateIsPageComplete();			
		}));
		
		setControl(composite);
		updateIsPageComplete();
	}

	/**
	 * 
	 * @return
	 */
	AccountingContext buildContext() {
		return isPageComplete() ? new AccountingContext(userName.getText(), dbFile.getText()) : null;
	}
	
    /**
     * 
     */
    private void updateIsPageComplete() {
		if (!userName.getText().isEmpty() && !dbFile.getText().isEmpty()) {
			File file = new File(dbFile.getText());
			if (newFile && file.exists()) {
				setErrorMessage(messages.setupWizards_errorDataFileExists);
				setPageComplete(false);
			} else if (!newFile && !file.exists()) {
				setErrorMessage(messages.userNameAndDbFile_errorNonExistingFile);
				setPageComplete(false);
			} else {
				setErrorMessage(null);
				setPageComplete(true);
			}
		} else {
			setPageComplete(false);
		}
		
		((AbstractSetupWizard) getWizard()).setCanFinish(isPageComplete());
    }
}
