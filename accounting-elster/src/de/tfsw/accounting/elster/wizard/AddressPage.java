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
package de.tfsw.accounting.elster.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
class AddressPage extends AbstractElsterExportWizardPage {

	private Text street;
	private Text streetNo;
	private Text streetNoAdd;
	private Text postCode;
	private Text city;
	private Text country;
	
	/**
	 * Create the wizard.
	 */
	AddressPage() {
		super(AddressPage.class.getName(), Messages.AddressPage_Title, Messages.AddressPage_Description);
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(4, false));
		
		Label lblStreet = new Label(container, SWT.NONE);
		lblStreet.setText(Messages.AddressPage_Street);
		street = new Text(container, SWT.BORDER);
		street.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblStreetNo = new Label(container, SWT.NONE);
		lblStreetNo.setText(Messages.AddressPage_HouseNo);
		streetNo = new Text(container, SWT.BORDER);
		streetNo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		Label lblStreetNoAdd = new Label(container, SWT.NONE);
		lblStreetNoAdd.setText(Messages.AddressPage_HouseAdd);
		streetNoAdd = new Text(container, SWT.BORDER);
		streetNoAdd.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		Label lblPlz = new Label(container, SWT.NONE);
		lblPlz.setText(Messages.AddressPage_ZIPCode);
		postCode = new Text(container, SWT.BORDER);
		postCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		
		Label lblCity = new Label(container, SWT.NONE);
		lblCity.setText(Messages.AddressPage_City);
		city = new Text(container, SWT.BORDER);
		city.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblCountry = new Label(container, SWT.NONE);
		lblCountry.setText(Messages.AddressPage_Country);
		country = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		country.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
	}

	/**
	 * @see de.tfsw.accounting.elster.wizard.AbstractElsterExportWizardPage#needsDataBindings()
	 */
	@Override
	protected boolean needsDataBindings() {
		return true;
	}
	
	/**
	 * @see de.tfsw.accounting.elster.wizard.AbstractElsterExportWizardPage#initDataBindings()
	 */
	@Override
	protected void initDataBindings() {
		createBinding(street, "companyStreetName"); //$NON-NLS-1$
		createBinding(streetNo, "companyStreetNumber"); //$NON-NLS-1$
		createBinding(streetNoAdd, "companyStreetAddendum"); //$NON-NLS-1$
		createBinding(postCode, "companyPostCode"); //$NON-NLS-1$
		createBinding(city, "companyCity"); //$NON-NLS-1$
		createBinding(country, "companyCountry"); //$NON-NLS-1$
	}
}
