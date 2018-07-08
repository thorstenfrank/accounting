/*
 *  Copyright 2011, 2018 Thorsten Frank (accounting@tfsw.de).
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

import org.eclipse.jface.wizard.Wizard;

import de.tfsw.accounting.model.TaxRate;

/**
 * Wizard to create a {@link TaxRate} entity.
 *
 */
class NewTaxRateWizard extends Wizard {
	
	// TODO inject this if the wizard needs to be used in other parts of the application
	private Messages messages;
	
	// business object
	private TaxRate newTaxRate;
	
	// wizard page
	private TaxRateWizardPage taxRateWizardPage;
	
	NewTaxRateWizard(Messages messages) {
		setNeedsProgressMonitor(false);
		setWindowTitle(messages.taxRateWizard_pageTitle);
		this.messages = messages;
		newTaxRate = new TaxRate();
	}

	/**
	 * 
	 */
	@Override
	public void addPages() {
		taxRateWizardPage = new TaxRateWizardPage(messages);
		addPage(taxRateWizardPage);
	}

	/**
	 * 
	 */
	@Override
	public boolean performFinish() {
		newTaxRate.setShortName(taxRateWizardPage.getAbbreviation());
		newTaxRate.setLongName(taxRateWizardPage.getLongName());
		newTaxRate.setRate(taxRateWizardPage.getRate());
		newTaxRate.setIsVAT(taxRateWizardPage.isVAT());
		return true;
	}
	
	TaxRate getNewTaxRate() {
		return newTaxRate;
	}
}
