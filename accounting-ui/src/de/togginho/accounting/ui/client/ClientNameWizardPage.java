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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;

/**
 * @author thorsten
 *
 */
public class ClientNameWizardPage extends WizardPage {
	
	private Text clientName;
	private Text clientNumber;
	
	/**
	 * 
	 */
	public ClientNameWizardPage() {
		super("ClientNameWizardPage"); //$NON-NLS-1$
		setTitle(Messages.ClientNameWizardPage_title);
		setDescription(Messages.ClientNameWizardPage_desc);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, NewClientWizard.HELP_CONTEXT_ID);
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		WidgetHelper.createLabel(composite, Messages.labelClientName);
		clientName = new Text(composite, SWT.BORDER | SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(clientName);
		
		clientName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				checkIfPageComplete();
			}
		});
		
		WidgetHelper.createLabel(composite, Messages.labelClientNumber);
		clientNumber = new Text(composite, SWT.BORDER | SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(clientNumber);
		
		setControl(composite);
		setPageComplete(false);
	}
	
	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(NewClientWizard.HELP_CONTEXT_ID);
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getClientName() {
		return clientName.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getClientNumber() {
		return clientNumber.getText();
	}
	
	/**
	 * 
	 */
	private void checkIfPageComplete() {
		if (clientName.getText().isEmpty()) {
			setPageComplete(false);
		} else {
			setPageComplete(true);
		}
	}
}
