/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.elster.ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * ELSTER export wizard page dealing with address information.
 * 
 * @author Thorsten Frank
 *
 */
class AddressPage extends AbstractElsterWizardPage {

	/**
	 * Creates a new wizard page.
	 */
	AddressPage() {
		super(AddressPage.class.getName(), Messages.AddressPage_Title, Messages.AddressPage_Description);
		setPageComplete(true);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(4, false));
		
		Label lblStreet = new Label(container, SWT.NONE);
		lblStreet.setText(Messages.AddressPage_Street);
		Text street = new Text(container, SWT.BORDER);
		street.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		createBinding(street, "companyStreetName"); //$NON-NLS-1$
		
		Label lblStreetNo = new Label(container, SWT.NONE);
		lblStreetNo.setText(Messages.AddressPage_HouseNo);
		Text streetNo = new Text(container, SWT.BORDER);
		streetNo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		createBinding(streetNo, "companyStreetNumber"); //$NON-NLS-1$
		
		Label lblStreetNoAdd = new Label(container, SWT.NONE);
		lblStreetNoAdd.setText(Messages.AddressPage_HouseAdd);
		Text streetNoAdd = new Text(container, SWT.BORDER);
		streetNoAdd.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		createBinding(streetNoAdd, "companyStreetAddendum"); //$NON-NLS-1$
		
		Label lblPlz = new Label(container, SWT.NONE);
		lblPlz.setText(Messages.AddressPage_ZIPCode);
		Text postCode = new Text(container, SWT.BORDER);
		postCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		createBinding(postCode, "companyPostCode"); //$NON-NLS-1$
		
		Label lblCity = new Label(container, SWT.NONE);
		lblCity.setText(Messages.AddressPage_City);
		Text city = new Text(container, SWT.BORDER);
		city.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		createBinding(city, "companyCity"); //$NON-NLS-1$
		
		Label lblCountry = new Label(container, SWT.NONE);
		lblCountry.setText(Messages.AddressPage_Country);
		Text country = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		country.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		createBinding(country, "companyCountry"); //$NON-NLS-1$
	}
}
