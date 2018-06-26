/*
 *  Copyright 2018 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.setup;

/**
 * A wizard to init the application with new data - user name and DB file location.
 * 
 * @author thorsten
 */
final class CreateNewWizard extends AbstractSetupWizard {

	private UserNameAndDbFileWizardPage wizardPage;

	@Override
	void messagesAvailable() {
		setWindowTitle(getMessages().createNewWizard_windowTitle);
	}
	
	@Override
	public void addPages() {
		wizardPage = new UserNameAndDbFileWizardPage(true, getMessages());
		addPage(wizardPage);
	}
	
	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		setAccountingContext(wizardPage.buildContext());
		return true;
	}
}
