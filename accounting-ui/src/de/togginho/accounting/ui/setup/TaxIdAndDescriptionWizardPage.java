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
package de.togginho.accounting.ui.setup;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;

/**
 * @author thorsten
 *
 */
class TaxIdAndDescriptionWizardPage extends WizardPage {

	private Composite composite;
	
	private Text taxId;
	private Text userDescription;
	
	TaxIdAndDescriptionWizardPage() {
		super("SetupBasicInfoWizardPage"); //$NON-NLS-1$
		setTitle(Messages.labelBasicInformation);
		setDescription(Messages.TaxIdAndDescriptionWizardPage_desc);
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
		
		// Tax ID
		WidgetHelper.createLabel(composite, Messages.labelTaxId);
		taxId = new Text(composite, SWT.BORDER | SWT.SINGLE);
		grabHorizontal.applyTo(taxId);
		
		// add an empty label as a spacer
		WidgetHelper.createLabel(composite, null);
		
		// User description
		WidgetHelper.createLabel(composite, Messages.labelDescription);
		userDescription = new Text(composite, SWT.BORDER | SWT.MULTI);
		WidgetHelper.grabBoth(userDescription);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getTaxId() {
		return taxId.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getUserDescription() {
		return userDescription.getText();
	}
}
