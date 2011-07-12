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
package de.togginho.accounting.ui.client;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.ui.AbstractAccountingEditor;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;

/**
 * @author thorsten
 *
 */
public class ClientEditor extends AbstractAccountingEditor {
	
	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".ClientEditor"; //$NON-NLS-1$
	
	private static final Logger LOG = Logger.getLogger(ClientEditor.class);
	
	private FormToolkit toolkit;
	
	private ScrolledForm form;
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		LOG.debug("Creating editor"); //$NON-NLS-1$
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.labelClientDetails);
		
		GridLayout layout = new GridLayout(2, false);
		form.getBody().setLayout(layout);
		
		Client client = getEditorInput().getClient();
		
		Section basicSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		basicSection.setText(Messages.labelBasicInformation);
		
		basicSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Composite basicSectionClient = toolkit.createComposite(basicSection);
		basicSectionClient.setLayout(new GridLayout(2, false));
		
		toolkit.createLabel(basicSectionClient, Messages.labelName);
		final Text clientName = createText(basicSectionClient, client.getName());
		clientName.setEditable(false);
		clientName.setEnabled(false);
		
		if (client.getAddress() == null) {
			client.setAddress(new Address());
		}
		basicSection.setClient(basicSectionClient);
		
		createAddressSection(client.getAddress());
		
		form.getToolBarManager().add(ActionFactory.SAVE.create(getSite().getWorkbenchWindow()));
		form.getToolBarManager().update(true);
		
		toolkit.decorateFormHeading(form.getForm());
		
		form.reflow(true);
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.rcp.AbstractAccountingEditor#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.rcp.AbstractAccountingEditor#getForm()
	 */
	@Override
	protected Form getForm() {
		return form.getForm();
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		LOG.debug("Saving client " + getEditorInput().getClient().getName()); //$NON-NLS-1$
		
		ModelHelper.saveCurrentUser();
		
		setIsDirty(false);
		
		setPartName(getEditorInput().getName());
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		form.getContent().setFocus();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public ClientEditorInput getEditorInput() {
		return (ClientEditorInput) super.getEditorInput();
	}
}
