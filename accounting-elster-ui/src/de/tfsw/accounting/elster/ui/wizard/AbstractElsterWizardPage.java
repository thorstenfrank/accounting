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
package de.tfsw.accounting.elster.ui.wizard;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.elster.ElsterDTO;
import de.tfsw.accounting.elster.ui.ElsterUI;

/**
 * Abstract base class for all ELSTER export wizard pages, supplying both easy access to relevant model elements
 * (wizard page, DTO) and UI relevant information (JFace databinding).
 * 
 * @author Thorsten Frank
 *
 */
abstract class AbstractElsterWizardPage extends WizardPage {
	
	/**
	 * The JFace databinding context for this instance.
	 */
	private DataBindingContext bindingContext;
	
	/**
	 * Creates a new wizard page with a name, title and description, along with a unified icon.
	 * 
	 * @param pageName		the unique name of this wizard page (usually {@link Class#getName()}
	 * @param title			the localized title of this wizard page
	 * @param description	the localized description of this wizard page
	 */
	AbstractElsterWizardPage(String pageName, String title, String description) {
		super(pageName, title, ElsterUI.getImageDescriptor(Messages.AbstractElsterWizardPage_icon));
		setDescription(description);
		this.bindingContext = new DataBindingContext();
	}
	
	/**
	 * Returns the {@link ElsterExportWizard} instance this page is instantiated by.
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#getWizard()
	 */
	@Override
	public ElsterExportWizard getWizard() {
		return (ElsterExportWizard)super.getWizard();
	}
	
	/**
	 * Disposes of this page - subclasses may override, but should call <code>super()</code> to ensure proper disposal
	 * of all data bindings.
	 * 
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
	 * Shorthand for <code>getWizard().getElsterDTO()</code>.
	 * 
	 * @return the {@link ElsterDTO} instance holding data displayed
	 */
	protected ElsterDTO getDTO() {
		return getWizard().getElsterDTO();
	}
	
	/**
	 * Creates a JFace data binding between the supplied text and the property of {@link #getDTO()}.
	 * 
	 * @param text		the {@link Text} element to be bound
	 * @param property	name of the property of {@link ElsterDTO} to be bound
	 * @return the binding for further use (if necessary)
	 */
	protected Binding createBinding(Text text, String property) {
		return bindingContext.bindValue(
				SWTObservables.observeText(text), 
				BeansObservables.observeValue(getDTO(), property));
	}
	
	/**
	 * Creates a JFace data binding between the supplied viewer and the property of {@link #getDTO()}.
	 * 
	 * @param viewer	the {@link StructuredViewer} to be bound
	 * @param property	name of the property of {@link ElsterDTO} to be bound
	 * @return the binding for further use (if necessary)
	 */
	protected Binding createBinding(StructuredViewer viewer, String property) {
		return bindingContext.bindValue(
				ViewersObservables.observeSingleSelection(viewer), 
				BeansObservables.observeValue(getDTO(), property));
	}
}
