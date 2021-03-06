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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.elster.Bundesland;
import de.tfsw.accounting.elster.ElsterDTO;

/**
 * ELSTER export wizard page dealing with company and individual names and tax numbers,
 * along with the (German) state or origin of the revenue service branch.
 * 
 * @author Thorsten Frank
 *
 */
class CompanyNamePage extends AbstractElsterWizardPage {

	/**
	 * Creates a new wizard page.
	 */
	CompanyNamePage() {
		super(CompanyNamePage.class.getName(), Messages.CompanyNamePage_Title, Messages.CompanyNamePage_Description);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		setControl(control);
		
		control.setLayout(new GridLayout(1, false));
		
		createNamesGroup(control);
		
		Label hSep1 = new Label(control, SWT.HORIZONTAL);
		hSep1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		createContactInfoGroup(control);

		Label hSep2 = new Label(control, SWT.HORIZONTAL);
		hSep2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		createTaxGroup(control);
		
		setPageComplete(false);
	}

	/**
	 * @param control
	 */
	private void createNamesGroup(Composite control) {
		Group namesGroup = new Group(control, SWT.SHADOW_ETCHED_IN);
		namesGroup.setText(Messages.CompanyNamePage_GroupName);
		namesGroup.setLayout(new GridLayout(4, false));
		namesGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label lblCompanyName = new Label(namesGroup, SWT.NONE);
		lblCompanyName.setText(Messages.CompanyNamePage_CompanyName);
		Text companyName = new Text(namesGroup, SWT.BORDER);
		companyName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		createBinding(companyName, "companyName"); //$NON-NLS-1$
		
		Label lblFirstName = new Label(namesGroup, SWT.NONE);
		lblFirstName.setText(Messages.CompanyNamePage_FirstName);
		Text firstName = new Text(namesGroup, SWT.BORDER);
		firstName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(firstName, "userFirstName"); //$NON-NLS-1$
		
		Label lblLastName = new Label(namesGroup, SWT.NONE);
		lblLastName.setText(Messages.CompanyNamePage_LastName);
		Text lastName = new Text(namesGroup, SWT.BORDER);
		lastName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(lastName, "userLastName"); //$NON-NLS-1$
	}
	
	/**
	 * @param control
	 */
	private void createContactInfoGroup(Composite control) {
		Group contactInfoGroup = new Group(control, SWT.SHADOW_ETCHED_IN);
		contactInfoGroup.setText(Messages.CompanyNamePage_GroupContactInfo);
		contactInfoGroup.setLayout(new GridLayout(4, false));
		contactInfoGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label lblEmail = new Label(contactInfoGroup, SWT.NONE);
		lblEmail.setText(Messages.CompanyNamePage_Email);
		Text email = new Text(contactInfoGroup, SWT.BORDER);
		email.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(email, "companyEmail"); //$NON-NLS-1$
		
		Label lblPhone = new Label(contactInfoGroup, SWT.NONE);
		lblPhone.setText(Messages.CompanyNamePage_Phone);
		Text phone = new Text(contactInfoGroup, SWT.BORDER);
		phone.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(phone, "companyPhone"); //$NON-NLS-1$
	}
	
	/**
	 * @param control
	 */
	private void createTaxGroup(Composite control) {
		Group taxGroup = new Group(control, SWT.SHADOW_ETCHED_IN);
		taxGroup.setText(Messages.CompanyNamePage_GroupTax);
		taxGroup.setLayout(new GridLayout(4, false));
		taxGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label lblBundesland = new Label(taxGroup, SWT.NONE);
		lblBundesland.setText(Messages.CompanyNamePage_State);
		final ComboViewer bundeslandViewer = new ComboViewer(taxGroup, SWT.READ_ONLY);
		bundeslandViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		bundeslandViewer.setContentProvider(ArrayContentProvider.getInstance());
		bundeslandViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Bundesland) {
					return ((Bundesland)element).getOfficialName();
				}
				return super.getText(element);
			}
		});
		bundeslandViewer.setInput(Bundesland.values());
		bundeslandViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(getDTO().getFinanzAmtBL() != null);
			}
		});
		createBinding(bundeslandViewer, "finanzAmtBL"); //$NON-NLS-1$
		
		Label lblTaxNo = new Label(taxGroup, SWT.NONE);
		lblTaxNo.setText(Messages.CompanyNamePage_TaxNoOrig);
		Text steuerNummerOrig = new Text(taxGroup, SWT.BORDER);
		steuerNummerOrig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(steuerNummerOrig, "companyTaxNumberOrig"); //$NON-NLS-1$
		
		Label lblTaxNoGen = new Label(taxGroup, SWT.NONE);
		lblTaxNoGen.setText(Messages.CompanyNamePage_TaxNoGenerated);
		Text steuerNummer = new Text(taxGroup, SWT.BORDER | SWT.READ_ONLY);
		steuerNummer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(steuerNummer, "companyTaxNumberGenerated"); //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			ElsterDTO dto = getDTO();
			setPageComplete(dto.getFinanzAmtBL() != null && dto.getCompanyTaxNumberGenerated() != null);
		}
		super.setVisible(visible);
	}
}
