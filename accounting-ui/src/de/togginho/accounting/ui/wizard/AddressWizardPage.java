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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.model.Address;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;

/**
 * @author thorsten
 *
 */
class AddressWizardPage extends WizardPage {

	private Text street;
	private Text postCode;
	private Text city;
	private Text phone;
	private Text mobile;
	private Text email;
	private Text fax;
	
	/**
	 * @param pageName
	 */
	AddressWizardPage() {
		super("AddressWizardPage"); //$NON-NLS-1$
		setTitle(Messages.AddressWizardPage_title);
		setDescription(Messages.AddressWizardPage_desc);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		
		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
		
		WidgetHelper.createLabel(composite, Messages.labelStreet);
		street = WidgetHelper.createSingleBorderText(composite, null);
		grabHorizontal.applyTo(street);
		
		WidgetHelper.createLabel(composite, Messages.labelPostalCode);
		postCode = WidgetHelper.createSingleBorderText(composite, null);
		grabHorizontal.applyTo(postCode);
		
		WidgetHelper.createLabel(composite, Messages.labelCity);
		city = WidgetHelper.createSingleBorderText(composite, null);
		grabHorizontal.applyTo(city);
		
		WidgetHelper.createLabel(composite, Messages.labelPhone);
		phone = WidgetHelper.createSingleBorderText(composite, null);
		grabHorizontal.applyTo(phone);
		
		WidgetHelper.createLabel(composite, Messages.labelMobile);
		mobile = WidgetHelper.createSingleBorderText(composite, null);
		grabHorizontal.applyTo(mobile);
		
		WidgetHelper.createLabel(composite, Messages.labelFax);
		fax = WidgetHelper.createSingleBorderText(composite, null);
		grabHorizontal.applyTo(fax);
		
		WidgetHelper.createLabel(composite, Messages.labelEmail);
		email = WidgetHelper.createSingleBorderText(composite, null);
		grabHorizontal.applyTo(email);
		
		setControl(composite);
		setPageComplete(true);
	}

	/**
	 * 
	 * @return
	 */
	protected Address getAddress() {
		Address address = new Address();
		boolean hasValue = false;
		
		if (!street.getText().isEmpty()) {
			address.setStreet(street.getText());
			hasValue = true;
		}
		if (!postCode.getText().isEmpty()) {
			address.setPostalCode(postCode.getText());
			hasValue = true;
		}
		if (!city.getText().isEmpty()) {
			address.setCity(city.getText());
			hasValue = true;
		}
		if (!phone.getText().isEmpty()) {
			address.setPhoneNumber(phone.getText());
			hasValue = true;
		}
		if (!mobile.getText().isEmpty()) {
			address.setMobileNumber(mobile.getText());
			hasValue = true;
		}
		if (!email.getText().isEmpty()) {
			address.setEmail(email.getText());
			hasValue = true;
		}
		if (!fax.getText().isEmpty()) {
			address.setFaxNumber(fax.getText());
			hasValue = true;
		}
		
		return hasValue ? address : null;
	}
}
