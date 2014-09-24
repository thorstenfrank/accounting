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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author tfrank1
 *
 */
class UserNameAndDbFileWizardPage extends WizardPage implements KeyListener {

	private boolean newFile;
	private Text userName;
	private Text dbFile;
	
	/**
	 * 
	 * @param newFile
	 */
	UserNameAndDbFileWizardPage(boolean newFile) {
		super("UserNameAndDbFileWizardPage"); //$NON-NLS-1$
		this.newFile = newFile;
		setTitle(Messages.labelBasicInformation);
		StringBuilder sb = new StringBuilder(Messages.UserNameAndDbFileWizardPage_desc);
		if (newFile) {
			sb.append(Messages.UserNameAndDbFileWizardPage_descNewFile);
		} else {
			sb.append(Messages.UserNameAndDbFileWizardPage_descOldFile);
		}
		setMessage(sb.toString());
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getSelectedUserName() {
		return userName.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getSelectedDbFileName() {
		return dbFile.getText();
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
		
		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
		
		// User name
		WidgetHelper.createLabel(composite, Messages.UserNameAndDbFileWizardPage_yourName);
		
		userName = new Text(composite, SWT.BORDER | SWT.SINGLE);
		userName.setText(System.getProperty("user.name")); //$NON-NLS-1$
		grabHorizontal.applyTo(userName);
		userName.addKeyListener(this);
		
		// add an empty label as a spacer
		WidgetHelper.createLabel(composite, null);
		
		// Data file
		WidgetHelper.createLabel(composite, Messages.UserNameAndDbFileWizardPage_dataFile);
		
		dbFile = new Text(composite, SWT.BORDER | SWT.SINGLE);
		grabHorizontal.applyTo(dbFile);
		if (newFile) {
			dbFile.setText(AccountingUI.buildDefaultDbFileLocation());
		}
		
		dbFile.addKeyListener(this);
		
		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("..."); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(composite.getShell(), newFile ? SWT.SAVE : SWT.OPEN);
				fd.setText(Messages.UserNameAndDbFileWizardPage_selectFile);
				fd.setFileName(dbFile.getText());
				String selected = fd.open();
				if (selected != null) {
					dbFile.setText(selected);
				}
				updateIsPageComplete();
			}
		});
		
		setControl(composite);
		updateIsPageComplete();
	}
    
	/**
     * {@inheritDoc}.
     * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
	    // nothing to do here...
    }

	/**
     * {@inheritDoc}.
     * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
    	updateIsPageComplete();
    }

    /**
     * 
     */
    private void updateIsPageComplete() {
		if (!userName.getText().isEmpty() && !dbFile.getText().isEmpty()) {
			File file = new File(dbFile.getText());
			if (newFile && file.exists()) {
				setErrorMessage(Messages.UserNameAndDbFileWizardPage_errorExistingFile);
				setPageComplete(false);
			} else if (!newFile && !file.exists()) {
				setErrorMessage(Messages.UserNameAndDbFileWizardPage_errorNonExistingFile);
				setPageComplete(false);
			} else {
				setErrorMessage(null);
				setPageComplete(true);
			}
		} else {
			setPageComplete(false);
		}
		
		if (getWizard() instanceof SetupWizard) {
			((SetupWizard) getWizard()).setCanFinish(isPageComplete());
		}
    }
}
