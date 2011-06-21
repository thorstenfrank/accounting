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
package de.togginho.accounting.ui.wizard;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.User;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;

/**
 * @author thorsten
 *
 */
class SetupBasicInfoWizardPage extends WizardPage implements KeyListener, Constants {

	private Composite composite;
	private Text userName;
	private Text dbFile;	
	private Text taxId;
	private Text userDescription;
	
	SetupBasicInfoWizardPage() {
		super("SetupBasicInfoWizardPage"); //$NON-NLS-1$
		setTitle(Messages.labelBasicInformation);
		setDescription(Messages.SetupBasicInfoWizardPage_desc);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		
		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
		
		// User name
		WidgetHelper.createLabel(composite, Messages.SetupBasicInfoWizardPage_yourName);
		
		userName = new Text(composite, SWT.BORDER | SWT.SINGLE);
		userName.setText(EMPTY_STRING);
		grabHorizontal.applyTo(userName);
		userName.addKeyListener(this);
		
		// add an empty label as a spacer
		WidgetHelper.createLabel(composite, null);
		
		// Data file
		WidgetHelper.createLabel(composite, Messages.SetupBasicInfoWizardPage_dataFile);
		
		dbFile = new Text(composite, SWT.BORDER | SWT.SINGLE);
		grabHorizontal.applyTo(dbFile);
		dbFile.setText(System.getProperty("user.home") + File.separator + "accounting.data"); //$NON-NLS-1$ //$NON-NLS-2$
		dbFile.addKeyListener(this);
		
		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("..."); //$NON-NLS-1$
		
		browseButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(composite.getShell(), SWT.SAVE);
				fd.setText(Messages.SetupBasicInfoWizardPage_selectFile);
				fd.setFileName(dbFile.getText());
				String selected = fd.open();
				if (selected != null) {
					dbFile.setText(selected);
				}
				updateIsPageComplete();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {				
			}
		});
		
		// Tax ID
		WidgetHelper.createLabel(composite, Messages.labelTaxId);
		taxId = new Text(composite, SWT.BORDER | SWT.SINGLE);
		grabHorizontal.applyTo(taxId);
		
		// add an empty label as a spacer
		WidgetHelper.createLabel(composite, null);
		
		// User description
		WidgetHelper.createLabel(composite, Messages.labelDescription);
		userDescription = new Text(composite, SWT.BORDER | SWT.MULTI);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(userDescription);
		
		setControl(composite);
		setPageComplete(false);
	}

	/**
	 * 
	 */
	private void updateIsPageComplete() {
		if (!userName.getText().isEmpty() && !dbFile.getText().isEmpty()) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
		
		if (getWizard() instanceof SetupWizard) {
			((SetupWizard)getWizard()).setCanFinish(isPageComplete());
		}
	}
	
	/**
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// nothing to do here...
	}

	/**
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		updateIsPageComplete();
	}

	protected User getUser() {
		User user = new User();
		
		user.setName(userName.getText());
		user.setTaxNumber(taxId.getText());
		user.setDescription(userDescription.getText());
		
		return user; 
	}
	
	protected String getDbFileLocation() {
		return dbFile.getText();
	}
}
