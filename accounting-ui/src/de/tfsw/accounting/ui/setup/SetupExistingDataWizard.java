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
package de.tfsw.accounting.ui.setup;

import org.eclipse.jface.wizard.Wizard;

import de.tfsw.accounting.ui.Messages;


/**
 * @author tfrank1
 *
 */
public class SetupExistingDataWizard extends Wizard {

	private String userName;
	private String dbFileLocation;
	
	private UserNameAndDbFileWizardPage page;
	
	/**
	 * 
	 */
	public SetupExistingDataWizard() {
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.SetupExistingDataWizard_windowTitle);
		setHelpAvailable(false);
    }

	/**
     * {@inheritDoc}.
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
    	page = new UserNameAndDbFileWizardPage(false);
	    addPage(page);
    }
    
	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		this.userName = page.getSelectedUserName();
		this.dbFileLocation = page.getSelectedDbFileName();
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFileLocation() {
		return dbFileLocation;
	}
}
