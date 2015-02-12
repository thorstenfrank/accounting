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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.elster.Bundesland;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
class CompanyNamePage extends AbstractElsterExportWizardPage {

	private Text companyName;
	private Text firstName;
	private Text lastName;
	private Text email;
	private Text phone;
	private Text steuerNummerOrig;
	private Text steuerNummer;
	
	/**
	 * 
	 */
	CompanyNamePage() {
		super(CompanyNamePage.class.getName(), "Basisdaten", "Namen und Steuernummer des Unternehmenes");
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		setControl(control);
		
		control.setLayout(new GridLayout(2, false));
		
		Label lblCompanyName = new Label(control, SWT.NONE);
		lblCompanyName.setText("Unternehmensname:");
		companyName = new Text(control, SWT.BORDER);
		companyName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblFirstName = new Label(control, SWT.NONE);
		lblFirstName.setText("Vorname:");
		firstName = new Text(control, SWT.BORDER);
		firstName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblLastName = new Label(control, SWT.NONE);
		lblLastName.setText("Nachname:");
		lastName = new Text(control, SWT.BORDER);
		lastName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblEmail = new Label(control, SWT.NONE);
		lblEmail.setText("Email:");
		email = new Text(control, SWT.BORDER);
		email.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblPhone = new Label(control, SWT.NONE);
		lblPhone.setText("Phone");
		phone = new Text(control, SWT.BORDER);
		phone.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblBundesland = new Label(control, SWT.NONE);
		lblBundesland.setText("Bundesland\ndes Finanzamts (*)");
		Combo bundeslandCombo = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
		bundeslandCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		for (Bundesland bundesland : Bundesland.values()) {
			bundeslandCombo.add(bundesland.getOfficialName());
		}
		bundeslandCombo.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				getElsterAdapter().getData().setFinanzAmtBL(
						Bundesland.valueOfOfficialName(bundeslandCombo.getItem(bundeslandCombo.getSelectionIndex())));
				setPageComplete(true);
			}
		});
		
		Label lblTaxNo = new Label(control, SWT.NONE);
		lblTaxNo.setText("Steuernummer (original):");
		steuerNummerOrig = new Text(control, SWT.BORDER);
		steuerNummerOrig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblTaxNoGen = new Label(control, SWT.NONE);
		lblTaxNoGen.setText("Steuernummer (maschinell):");
		steuerNummer = new Text(control, SWT.BORDER | SWT.READ_ONLY);
		steuerNummer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		setPageComplete(false);
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
		createBinding(companyName, "companyName");
		createBinding(firstName, "userFirstName");
		createBinding(lastName, "userLastName");
		createBinding(email, "companyEmail");
		createBinding(phone, "companyPhone");
		createBinding(steuerNummerOrig, "companyTaxNumberOrig");
		createBidirectionalBinding(steuerNummer, "companyTaxNumberGenerated");
	}
}
