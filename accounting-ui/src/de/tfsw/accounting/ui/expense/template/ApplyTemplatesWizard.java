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
package de.tfsw.accounting.ui.expense.template;

import java.util.Collection;

import org.eclipse.jface.wizard.Wizard;

import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.ui.Messages;

/**
 * @author Thorsten Frank
 *
 */
public class ApplyTemplatesWizard extends Wizard {

	private Collection<ExpenseTemplate> templates;
	private ChooseTemplatesWizardPage chooseTemplatesPage;
	
	/**
	 * 
	 */
	ApplyTemplatesWizard(Collection<ExpenseTemplate> templates) {
		this.templates = templates;
		setWindowTitle(Messages.ApplyTemplatesWizard_title);
	}
	
	/**
	 * 
	 */
	@Override
	public void addPages() {
		chooseTemplatesPage = new ChooseTemplatesWizardPage(templates);
		addPage(chooseTemplatesPage);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		chooseTemplatesPage.applySelectedTemplates();
		
		return true;
	}

}
