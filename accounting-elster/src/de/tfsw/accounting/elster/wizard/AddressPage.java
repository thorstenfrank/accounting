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
		super(AddressPage.class.getName(), "Addressdaten", "Anschrift und Kontaktdaten spezifisch für die Vornameldung überprüfen.\nHinweis: aktuell werden nur Adressen in Deutschland unterstützt");
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
		lblStreet.setText("Strasse:");
		street = new Text(container, SWT.BORDER);
		street.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblStreetNo = new Label(container, SWT.NONE);
		lblStreetNo.setText("Hausnr:");
		streetNo = new Text(container, SWT.BORDER);
		streetNo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		Label lblStreetNoAdd = new Label(container, SWT.NONE);
		lblStreetNoAdd.setText("Hausnr-Zusatz:");
		streetNoAdd = new Text(container, SWT.BORDER);
		streetNoAdd.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		Label lblPlz = new Label(container, SWT.NONE);
		lblPlz.setText("PLZ:");
		postCode = new Text(container, SWT.BORDER);
		postCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		
		Label lblCity = new Label(container, SWT.NONE);
		lblCity.setText("Stadt:");
		city = new Text(container, SWT.BORDER);
		city.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblCountry = new Label(container, SWT.NONE);
		lblCountry.setText("Land:");
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
		createBinding(street, "companyStreetName");
		createBinding(streetNo, "companyStreetNumber");
		createBinding(streetNoAdd, "companyStreetAddendum");
		createBinding(postCode, "companyPostCode");
		createBinding(city, "companyCity");
		createBinding(country, "companyCountry");
	}
}
