/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.user;

import java.math.BigDecimal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.Constants;
import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.FormatUtil;

/**
 * TODO maybe move this into the {@link NewTaxRateWizard}.
 */
class TaxRateWizardPage extends WizardPage implements Constants {

	private Text abbreviation;
	private Text longName;
	private Text rate;
	private Button vat;
	private Messages messages;
	
	TaxRateWizardPage(Messages messages) {
		super("TaxRateWizardPage"); //$NON-NLS-1$
		setTitle(messages.taxRateWizard_pageTitle);
		setDescription(messages.taxRateWizard_pageDesc);
		this.messages = messages;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		
		KeyListener keyListener = KeyListener.keyReleasedAdapter(e -> updatePageComplete());
		
		WidgetHelper.createLabel(composite, messages.userEditorTaxRateAbbreviation);
		abbreviation = new Text(composite, SWT.SINGLE | SWT.BORDER);
		WidgetHelper.grabHorizontal(abbreviation);
		abbreviation.addKeyListener(keyListener);
		WidgetHelper.createLabel(composite, EMPTY_STRING); // filler widget
		
		WidgetHelper.createLabel(composite, messages.labelName);
		longName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		WidgetHelper.grabHorizontal(longName);
		longName.addKeyListener(keyListener);
		WidgetHelper.createLabel(composite, EMPTY_STRING); // filler widget
		
		WidgetHelper.createLabel(composite, messages.userEditorTaxRate);
		Composite rateComposite = new Composite(composite, SWT.NULL);
		rateComposite.setLayout(new FillLayout());
		rate = new Text(rateComposite, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
		rate.addKeyListener(keyListener);
		WidgetHelper.createLabel(rateComposite, PERCENTAGE_SIGN);
		WidgetHelper.createLabel(composite, Constants.BLANK_STRING); // spacer label
		
		vat = new Button(composite, SWT.CHECK);
		vat.setText(messages.taxRateWizard_isVat);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(vat);
		vat.setSelection(true);
		
		setControl(composite);
		setPageComplete(false);
	}
	
	private void updatePageComplete() {
		boolean missingData = true;
		
		if (!rate.getText().isEmpty()) {
			try {
				getRate();
				setErrorMessage(null);
				missingData = false;
			} catch (AccountingException e) {
				setErrorMessage(messages.taxRateWizard_errorMustBeNumber);
				missingData = true;
			}
		}

		missingData = abbreviation.getText().isEmpty() || longName.getText().isEmpty();
		setPageComplete(!missingData);
	}

	String getAbbreviation() {
		return abbreviation.getText();
	}
	
	String getLongName() {
		return longName.getText();
	}
	
	BigDecimal getRate() {
		final String fullString = rate.getText() + PERCENTAGE_SIGN;
		return FormatUtil.parsePercentValue(fullString);
	}
	
	boolean isVAT() {
		return vat.getSelection();
	}
}
