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

import java.time.YearMonth;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.tfsw.accounting.elster.adapter.ElsterAdapter;
import de.tfsw.accounting.elster.adapter.ElsterAdapterFactory;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class ElsterExportWizard extends Wizard implements IWorkbenchWizard {

	/** Logging instance (shared across wizard pages). */
	protected static final Logger LOG = Logger.getLogger(ElsterExportWizard.class);
	
	protected static final String ELSTER_EXPORT_WIZARD_HELP_ID = "de.tfsw.accounting.elster.ElsterExportWizard";
	
	private ElsterAdapter adapter;
	
	/**
	 * 
	 */
	public ElsterExportWizard() {
		setHelpAvailable(true);
		setWindowTitle("ELSTER UStVA Wizard");
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(new TimeFrameSelectionPage());
		addPage(new CompanyNamePage());
		addPage(new AddressPage());
		addPage(new AmountsPage());
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		LOG.debug("INIT");
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return false;
	}

	/**
	 * @return the adapter
	 */
	protected ElsterAdapter getAdapter() {
		return adapter;
	}
	
	/**
	 * 
	 * @param yearMonth
	 */
	protected void timeFrameChanged(YearMonth yearMonth) {
		adapter = ElsterAdapterFactory.getAdapter(yearMonth);
	}
}
