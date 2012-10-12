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
package de.togginho.accounting.ui.user;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
public class NewTaxRateWizard extends Wizard implements IWorkbenchWizard {

	/**
	 * 
	 */
	protected static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".NewTaxRateWizard"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private TaxRate newTaxRate;
	
	/**
	 * 
	 */
	private TaxRateWizardPage taxRateWizardPage;
	
	/**
	 * 
	 */
	public NewTaxRateWizard() {
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.NewTaxRateWizard_windowTitle);
		setHelpAvailable(true);
		newTaxRate = new TaxRate();
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// nothing to do here...
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		taxRateWizardPage = new TaxRateWizardPage();
		addPage(taxRateWizardPage);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean finished = false;
		try {
			newTaxRate.setShortName(taxRateWizardPage.getAbbreviation());
			newTaxRate.setLongName(taxRateWizardPage.getLongName());
			newTaxRate.setRate(taxRateWizardPage.getRate());
			finished = true;
		} catch (Exception e) {
			MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			msgBox.setMessage(e.getLocalizedMessage());
			msgBox.setText(Messages.NewTaxRateWizard_errorMessageTitle);
			msgBox.open();
		}

		return finished;
	}

	/**
	 * 
	 * @return
	 */
	public TaxRate getNewTaxRate() {
		return newTaxRate;
	}
}
