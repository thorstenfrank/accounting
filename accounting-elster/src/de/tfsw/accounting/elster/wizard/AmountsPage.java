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

import de.tfsw.accounting.elster.adapter.ElsterDTO;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
class AmountsPage extends AbstractElsterExportWizardPage {
	
	private Text txtRevenue19;
	private Text txtRevenue19tax;
	private Text txtRevenue7;
	private Text txtRevenue7tax;
	private Text txtInputTax;
	private Text txtSum;

	/**
	 * 
	 */
	AmountsPage() {
		super(AmountsPage.class.getName(), "Beträge", "Übersicht der zu übermittelnden Umsatz- und Vorsteuerbeträge.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(5, false));
		
		Label lblRevenue = new Label(container, SWT.NONE);
		lblRevenue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));
		lblRevenue.setText("Steuerpflichtige Umsätze");
		
		Label lbRevenuel19 = new Label(container, SWT.NONE);
		lbRevenuel19.setText("zu 19% USt:");
		
		txtRevenue19 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		
		Label lblKz81 = new Label(container, SWT.NONE);
		lblKz81.setText("(Kz 81)");
		
		Label lblRevenue19tax = new Label(container, SWT.NONE);
		lblRevenue19tax.setText("Steuersumme:");
		
		txtRevenue19tax = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		
		Label lblRevenue7 = new Label(container, SWT.NONE);
		lblRevenue7.setText("zu 7% USt:");
		
		txtRevenue7 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		
		Label lblKz86 = new Label(container, SWT.NONE);
		lblKz86.setText("(Kz 86)");
		
		Label lblRevenue7tax = new Label(container, SWT.NONE);
		lblRevenue7tax.setText("Steuersumme:");
		
		txtRevenue7tax = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		
		Label horiSep1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		horiSep1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		Label lblInputTax = new Label(container, SWT.NONE);
		lblInputTax.setText("Vorsteuerbeträge:");
		
		txtInputTax = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		
		Label lblKz66 = new Label(container, SWT.NONE);
		lblKz66.setText("(Kz 66)");
		
		// spacer
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label horiSep2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		horiSep2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));
		
		Label lblSum = new Label(container, SWT.NONE);
		lblSum.setText("Summe:");
		
		txtSum = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		
		Label lblKz83 = new Label(container, SWT.NONE);
		lblKz83.setText("(Kz 83)");
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
		// don't need proper bindings, just copy values into the textfields
		ElsterDTO data = getWizard().getAdapter().getData();
		txtRevenue19.setText(data.getRevenue19());
		txtRevenue19tax.setText(data.getRevenue19tax());
		txtRevenue7.setText(data.getRevenue7());
		txtRevenue7tax.setText(data.getRevenue7tax());
		txtInputTax.setText(data.getInputTax());
		txtSum.setText(data.getTaxSum());
	}
}
