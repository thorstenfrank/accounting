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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.PaymentType;
import de.togginho.accounting.ui.AbstractAccountingEditor;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;

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
		Client client = getEditorInput().getClient();
		
		LOG.debug("Creating editor for client " + client.getName()); //$NON-NLS-1$
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		
		form.setText(String.format("%s: %s", Messages.labelClientDetails, client.getName()));
		
		GridLayout layout = new GridLayout(2, false);
		form.getBody().setLayout(layout);
		
		if (client.getAddress() == null) {
			client.setAddress(new Address());
		}
		createAddressSection(client.getAddress());
		
		createPaymentTermsSection(client);
		
		form.getToolBarManager().add(ActionFactory.SAVE.create(getSite().getWorkbenchWindow()));
		form.getToolBarManager().update(true);
		
		toolkit.decorateFormHeading(form.getForm());
		
		form.reflow(true);
	}

	/**
	 * 
	 * @param client
	 */
	private void createPaymentTermsSection(Client client) {
		if (client.getDefaultPaymentTerms() == null) {
			client.setDefaultPaymentTerms(PaymentTerms.getDefault());
			setIsDirty(true);
		}
		
		final PaymentTerms paymentTerms = client.getDefaultPaymentTerms();
		
	    Section paymentTermsSection = toolkit.createSection(getForm().getBody(), Section.TITLE_BAR);
		paymentTermsSection.setText(Messages.labelPaymentTerms);
		
		paymentTermsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Composite sectionClient = toolkit.createComposite(paymentTermsSection);
		sectionClient.setLayout(new GridLayout(2, false));
				
		toolkit.createLabel(sectionClient, Messages.labelPaymentType);
		
		final Combo paymentTypes = new Combo(sectionClient, SWT.READ_ONLY);
		toolkit.adapt(paymentTypes);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(paymentTypes);
		final List<PaymentType> paymentTypeList = new ArrayList<PaymentType>();
		int index = 0;
		for (PaymentType element : PaymentType.values()) {
			paymentTypes.add(element.getTranslatedString());
			paymentTypeList.add(index, element);
			if (element.name().equals(paymentTerms.getPaymentType().name())) {
				paymentTypes.select(index);
			} else {
				index++;
			}
		}
		
		paymentTypes.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	final Combo combo = (Combo) e.getSource();
            	PaymentType newType = paymentTypeList.get(combo.getSelectionIndex());
            	if ( ! newType.name().equals(paymentTerms.getPaymentType().name())) {
                	paymentTerms.setPaymentType(newType);
                	setIsDirty(true);            		
            	}
            }
		});
		
		toolkit.createLabel(sectionClient, Messages.labelPaymentTarget);
		final Spinner spinner = new Spinner(sectionClient, SWT.BORDER);
		//toolkit.adapt(spinner);
		spinner.setIncrement(1);
		spinner.setMinimum(0);
		spinner.setSelection(paymentTerms.getFullPaymentTargetInDays());
		
		spinner.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				paymentTerms.setFullPaymentTargetInDays(spinner.getSelection());
				setIsDirty(true);
			}
		});
		
		paymentTermsSection.setClient(sectionClient);
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
		final Client updatedClient = getEditorInput().getClient();
		
		LOG.debug("Saving client " + updatedClient.getName()); //$NON-NLS-1$
		
		AccountingUI.getAccountingService().saveClient(updatedClient);
		
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
