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

import de.togginho.accounting.model.BankAccount;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;

/**
 * 
 * @author tfrank1
 *
 */
public class SetupBankAccountWizardPage extends WizardPage {

	private Text accountNumber;
	private Text bankCode;
	private Text bankName;
	private Text bic;
	private Text iban;
	
	/**
	 * 
	 */
	SetupBankAccountWizardPage() {
		super("SetupBankAccountWizardPage"); //$NON-NLS-1$
		setTitle(Messages.labelBankAccount);
		setDescription(Messages.SetupBankAccountWizardPage_desc);
	}
	
	/**
	 * 
	 */
	@Override
	public void createControl(Composite parent) {		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
		
		// User name
		WidgetHelper.createLabel(composite, Messages.labelAccountNumber);
		accountNumber = new Text(composite, SWT.BORDER | SWT.SINGLE);
		grabHorizontal.applyTo(accountNumber);
		
		WidgetHelper.createLabel(composite, Messages.labelBankCode);
		bankCode = new Text(composite, SWT.BORDER | SWT.SINGLE);
		grabHorizontal.applyTo(bankCode);
		
		WidgetHelper.createLabel(composite, Messages.labelBankName);
		bankName = new Text(composite, SWT.BORDER | SWT.SINGLE);
		grabHorizontal.applyTo(bankName);
		
		WidgetHelper.createLabel(composite, Messages.labelBIC);
		bic = new Text(composite, SWT.BORDER | SWT.SINGLE);
		grabHorizontal.applyTo(bic);

		WidgetHelper.createLabel(composite, Messages.labelIBAN);
		iban = new Text(composite, SWT.BORDER | SWT.SINGLE);
		grabHorizontal.applyTo(iban);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	/**
	 * 
	 * @return
	 */
	protected BankAccount getBankAccount() {
		BankAccount account = new BankAccount();
		boolean hasValue = false;
		
		if (!accountNumber.getText().isEmpty()) {
			account.setAccountNumber(accountNumber.getText());
			hasValue = true;
		}
		if (!bankCode.getText().isEmpty()) {
			account.setBankCode(bankCode.getText());
			hasValue = true;
		}
		if (!bankName.getText().isEmpty()) {
			account.setBankName(bankName.getText());
			hasValue = true;
		}
		if (!bic.getText().isEmpty()) {
			account.setBic(bic.getText());
			hasValue = true;
		}
		if (!iban.getText().isEmpty()) {
			account.setIban(iban.getText());
			hasValue = true;
		}
		
		return hasValue ? account : null;
	}
}
