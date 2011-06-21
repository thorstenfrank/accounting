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

import org.eclipse.jface.wizard.Wizard;

import de.togginho.accounting.model.User;
import de.togginho.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
public class SetupWizard extends Wizard {

	private SetupBasicInfoWizardPage basicInfoPage;
	private AddressWizardPage userAddressPage;
	private SetupBankAccountWizardPage bankAccountPage;
	
	private boolean canFinish = false;

	private User configuredUser;
	private String dbFileLocation;
	
	/**
	 * Creates a new setup wizard.
	 */
	public SetupWizard() {
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.SetupWizard_windowTitle);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		basicInfoPage = new SetupBasicInfoWizardPage();
		userAddressPage = new AddressWizardPage();
		bankAccountPage = new SetupBankAccountWizardPage();
		
		addPage(basicInfoPage);
		addPage(userAddressPage);
		addPage(bankAccountPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return canFinish;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	@Override
	public boolean performCancel() {
		return super.performCancel();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		configuredUser = basicInfoPage.getUser();
		configuredUser.setAddress(userAddressPage.getAddress());
		configuredUser.setBankAccount(bankAccountPage.getBankAccount());
		
		dbFileLocation = basicInfoPage.getDbFileLocation();
		
		return true;
	}
	
	/**
	 * 
	 * @param canFinish
	 */
	protected void setCanFinish(boolean canFinish) {
		this.canFinish = canFinish;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDbFileLocation() {
		return dbFileLocation;
	}
	
	/**
	 * 
	 * @return
	 */
	public User getConfiguredUser() {
		return configuredUser;
	}
}