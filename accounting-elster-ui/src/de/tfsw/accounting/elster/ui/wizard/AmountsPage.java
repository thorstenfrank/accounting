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
 * ELSTER export wizard page dealing with the actual monetary and VAT amounts being reported for a given filing period.
 *  
 * @author Thorsten Frank
 *
 */
class AmountsPage extends AbstractElsterWizardPage {

	/**
	 * Creates a new wizard page.
	 */
	AmountsPage() {
		super(AmountsPage.class.getName(), Messages.AmountsPage_Title, Messages.AmountsPage_Description);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(5, false));
		
		Label lblRevenue = new Label(container, SWT.NONE);
		lblRevenue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));
		lblRevenue.setText(Messages.AmountsPage_Revenue);
		
		Label lbRevenuel19 = new Label(container, SWT.NONE);
		lbRevenuel19.setText(Messages.AmountsPage_VAT19);
		
		Text txtRevenue19 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		txtRevenue19.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(txtRevenue19, "revenue19"); //$NON-NLS-1$
		
		Label lblKz81 = new Label(container, SWT.NONE);
		lblKz81.setText(Messages.AmountsPage_Kz81);
		
		Label lblRevenue19tax = new Label(container, SWT.NONE);
		lblRevenue19tax.setText(Messages.AmountsPage_TaxSum);
		
		Text txtRevenue19tax = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		txtRevenue19tax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(txtRevenue19tax, "revenue19tax"); //$NON-NLS-1$
		
		Label lblRevenue7 = new Label(container, SWT.NONE);
		lblRevenue7.setText(Messages.AmountsPage_VAT7);
		
		Text txtRevenue7 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		txtRevenue7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(txtRevenue7, "revenue7"); //$NON-NLS-1$
		
		Label lblKz86 = new Label(container, SWT.NONE);
		lblKz86.setText(Messages.AmountsPage_Kz86);
		
		Label lblRevenue7tax = new Label(container, SWT.NONE);
		lblRevenue7tax.setText(Messages.AmountsPage_TaxSum);
		
		Text txtRevenue7tax = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		txtRevenue7tax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(txtRevenue7tax, "revenue7tax"); //$NON-NLS-1$
		
		Label horiSep1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		horiSep1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		Label lblInputTax = new Label(container, SWT.NONE);
		lblInputTax.setText(Messages.AmountsPage_InputTax);
		
		Text txtInputTax = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		txtInputTax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(txtInputTax, "inputTax"); //$NON-NLS-1$
		
		Label lblKz66 = new Label(container, SWT.NONE);
		lblKz66.setText(Messages.AmountsPage_Kz66);
		
		// spacer
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label horiSep2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		horiSep2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));
		
		Label lblSum = new Label(container, SWT.NONE);
		lblSum.setText(Messages.AmountsPage_Sum);
		
		Text txtSum = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		txtSum.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createBinding(txtSum, "taxSum"); //$NON-NLS-1$
		
		Label lblKz83 = new Label(container, SWT.NONE);
		lblKz83.setText(Messages.AmountsPage_Kz83);
	}
}
