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

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.elster.AccountingElsterPlugin;
import de.tfsw.accounting.elster.adapter.ElsterAdapter;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
abstract class AbstractElsterExportWizardPage extends WizardPage {

	/** Shared logging instance. */
	protected static final Logger LOG = Logger.getLogger(ElsterExportWizard.class);
	
	/**
	 * 
	 */
	private DataBindingContext bindingContext;
	
	/**
	 * 
	 * @param pageName
	 * @param title
	 * @param description
	 */
	AbstractElsterExportWizardPage(String pageName, String title, String description) {
		super(pageName, title, AccountingElsterPlugin.getImageDescriptor(Messages.AbstractElsterExportWizardPage_icon));
		setDescription(description);
		setPageComplete(true);
	}
	
	/**
	 * Default implementation, always returns <code>false</code>.
	 * @return whether or not this instance needs a data binding context
	 */
	protected boolean needsDataBindings() {
		return false;
	}
	
	/**
	 * Default implementation does nothing - subclasses should override and create bindings here.
	 * This method is called once, the first time {@link #setVisible(boolean)} is called on the page.
	 */
	protected void initDataBindings() {
		// nada...
	}
	
	/**
	 * If {@link #needsDataBindings()} returns <code>false</code>, this method will throw a {@link NullPointerException}.
	 * @param text
	 * @param dataProperty
	 * @return
	 */
	protected Binding createBinding(Text text, String dataProperty) {
		return bindingContext.bindValue(
				SWTObservables.observeText(text, SWT.Modify), 
				PojoObservables.observeValue(getElsterAdapter().getData(), dataProperty));
	}
	
	/**
	 * If {@link #needsDataBindings()} returns <code>false</code>, this method will throw a {@link NullPointerException}.
	 * @param text
	 * @param dataProperty
	 * @return
	 */
	protected Binding createBidirectionalBinding(Text text, String dataProperty) {
		return bindingContext.bindValue(
				SWTObservables.observeText(text, SWT.Modify), 
				BeansObservables.observeValue(getElsterAdapter().getData(), dataProperty));
	}
	
	/**
	 * If you override this method, you <b>must</b> call <code>super</code>!
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		if (needsDataBindings() && bindingContext == null) {
			bindingContext = new DataBindingContext();
			initDataBindings();
		}
		super.setVisible(visible);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		if (bindingContext != null) {
			bindingContext.dispose();
		}
		super.dispose();
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getWizard()
	 */
	@Override
	public ElsterExportWizard getWizard() {
		return (ElsterExportWizard)super.getWizard();
	}
	
	/**
	 * @return the elsterAdapter
	 */
	protected ElsterAdapter getElsterAdapter() {
		return getWizard().getAdapter();
	}
}
