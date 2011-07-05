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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.BankAccount;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.model.User;
import de.togginho.accounting.ui.AbstractAccountingEditor;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;
import de.togginho.accounting.ui.wizard.NewTaxRateWizard;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author tfrank1
 *
 */
public class UserEditor extends AbstractAccountingEditor {

	private static final Logger LOG = Logger.getLogger(UserEditor.class);
	
	private static final int COLUMN_TAX_RATE_ABBREVIATION = 0;
	private static final int COLUMN_TAX_RATE_NAME = 1;
	private static final int COLUMN_TAX_RATE = 2;

	private FormToolkit toolkit;
	private ScrolledForm form;
	private TableViewer taxRateViewer;
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		LOG.debug("Creating editor"); //$NON-NLS-1$
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.UserEditor_header);
		
		GridLayout layout = new GridLayout(2, true);
		form.getBody().setLayout(layout);
		
		User user = getEditorInput().getUser();
		
		createBasicInfoSection(user);

		if (user.getAddress() == null) {
			user.setAddress(new Address());
		}
		createAddressSection(user.getAddress());
		
		if (user.getBankAccount() == null) {
			user.setBankAccount(new BankAccount());
		}
		createBankAccountSection(user.getBankAccount());
		
//		if (user.getClients() == null) {
//			user.setClients(new HashSet<Client>());
//		}
//		createClientList();
		
		createTaxRateSection();
		
		form.getToolBarManager().add(ActionFactory.SAVE.create(getSite().getWorkbenchWindow()));
		form.getToolBarManager().update(true);
		
		toolkit.decorateFormHeading(form.getForm());
		
		form.reflow(true);
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.rcp.AbstractAccountingEditor#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return toolkit;
	}
		
	/**
	 * @see de.togginho.accounting.ui.rcp.AbstractAccountingEditor#getForm()
	 */
	@Override
	protected Form getForm() {
		return form.getForm();
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		form.getBody().setFocus();
	}
	
	/**
	 * 
	 * @param user
	 */
	private void createBasicInfoSection(User user) {
		Section basicSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		basicSection.setText(Messages.labelBasicInformation);
		
		basicSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Composite basicSectionClient = toolkit.createComposite(basicSection);
		basicSectionClient.setLayout(new GridLayout(2, false));
		
		toolkit.createLabel(basicSectionClient, Messages.labelName);
		Text userName = createText(basicSectionClient, user.getName(), user, User.FIELD_NAME);
		userName.setEditable(false);
		userName.setEnabled(false);
		
		toolkit.createLabel(basicSectionClient, Messages.labelTaxId);
		createText(basicSectionClient, user.getTaxNumber(), user, User.FIELD_TAX_NUMBER);
		
		toolkit.createLabel(basicSectionClient, Messages.labelDescription);
		Text description = toolkit.createText(basicSectionClient, user.getDescription(), SWT.MULTI | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(description);
		createBindings(description, user, User.FIELD_DESCRIPTION);
		
		basicSection.setClient(basicSectionClient);
	}
	
	/**
	 * 
	 * @param account
	 */
	private void createBankAccountSection(BankAccount account) {
		Section accountSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		accountSection.setText(Messages.labelBankAccount);
		accountSection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Composite client = toolkit.createComposite(accountSection);
		client.setLayout(new GridLayout(2, false));
		
		toolkit.createLabel(client, Messages.labelAccountNumber);
		createText(client, account.getAccountNumber(), account, BankAccount.FIELD_ACCOUNT_NUMBER);
		
		toolkit.createLabel(client, Messages.labelBankCode);
		createText(client, account.getBankCode(), account, BankAccount.FIELD_BANK_CODE);
		
		toolkit.createLabel(client, Messages.labelBankName);
		createText(client, account.getBankName(), account, BankAccount.FIELD_BANK_NAME);
		
		accountSection.setClient(client);
	}
	
//	private void createClientList() {
//		Section clientSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
//		clientSection.setText(Messages.UserEditor_clientSectionHeader);
//		clientSection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
//		
//		Composite client = toolkit.createComposite(clientSection);
//		client.setLayout(new GridLayout(2, false));
//		clientSection.setClient(client);
//		
//		ClientsListViewer clientViewer = new ClientsListViewer(client);
//		clientViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		getSite().setSelectionProvider(clientViewer);
//		
//		Composite buttons = toolkit.createComposite(client);
//		buttons.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
//		buttons.setLayout(new FillLayout(SWT.VERTICAL));
//		
//		Button add = toolkit.createButton(buttons, Messages.labelAdd, SWT.PUSH);
//		add.addSelectionListener(new SimpleCommandCallingSelectionListener(IDs.CMD_NEW_CLIENT_WIZARD));
//		
//		Button remove = toolkit.createButton(buttons, Messages.labelRemove, SWT.PUSH);
//		remove.addSelectionListener(new SimpleCommandCallingSelectionListener(IDs.CMD_DELETE_CLIENT));
//	}
	
	/**
	 * 
	 */
	private void createTaxRateSection() {
		Section taxRateSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.DESCRIPTION);
		taxRateSection.setText(Messages.UserEditor_taxRates);
		taxRateSection.setDescription(Messages.UserEditor_taxeRateDescription);
		taxRateSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite client = toolkit.createComposite(taxRateSection);
		client.setLayout(new GridLayout(2, false));
		
		Composite tableComposite = toolkit.createComposite(client);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableComposite.setLayout(tableLayout);
		
		taxRateViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);
		
		Table table = taxRateViewer.getTable();
		table.setHeaderVisible(true);
		
		TableViewerColumn col1 = new TableViewerColumn(taxRateViewer, SWT.NONE, COLUMN_TAX_RATE_ABBREVIATION);
		col1.getColumn().setText(Messages.UserEditor_taxRateAbbreviation);
		col1.getColumn().setAlignment(SWT.CENTER);
		tableLayout.setColumnData(col1.getColumn(), new ColumnWeightData(30));
		
		TableViewerColumn col2 = new TableViewerColumn(taxRateViewer, SWT.NONE, COLUMN_TAX_RATE_NAME);
		col2.getColumn().setText(Messages.UserEditor_taxRateName);
		col2.getColumn().setAlignment(SWT.CENTER);
		tableLayout.setColumnData(col2.getColumn(), new ColumnWeightData(50));
		
		TableViewerColumn col3 = new TableViewerColumn(taxRateViewer, SWT.NONE, COLUMN_TAX_RATE);
		col3.getColumn().setText(Messages.UserEditor_taxRate);
		col3.getColumn().setAlignment(SWT.CENTER);
		tableLayout.setColumnData(col3.getColumn(), new ColumnWeightData(20));
		
		Set<TaxRate> taxRates = getEditorInput().getUser().getTaxRates();
		if (taxRates == null) {
			taxRates = new HashSet<TaxRate>();
			getEditorInput().getUser().setTaxRates(taxRates);
		}

		taxRateViewer.setLabelProvider(new TaxRateCellLabelProvider());
		taxRateViewer.setContentProvider(ArrayContentProvider.getInstance());
		taxRateViewer.setInput(taxRates);
		
		Composite buttons = toolkit.createComposite(client);
		buttons.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		buttons.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button add = toolkit.createButton(buttons, Messages.labelAdd, SWT.PUSH);
		//add.addSelectionListener(new SimpleCommandCallingSelectionListener(IDs.CMD_NEW_TAX_RATE));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewTaxRateWizard wizard = new NewTaxRateWizard();
				
				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				
				if (WizardDialog.OK == dialog.open()) {
					taxRateViewer.getTable().setRedraw(false);
					getEditorInput().getUser().addTaxRate(wizard.getNewTaxRate());
					taxRateViewer.refresh();
					taxRateViewer.getTable().setRedraw(true);
					setIsDirty(true);
				}
			}
		});
		
		// TODO implement remove tax rate
		//Button remove = toolkit.createButton(buttons, Messages.labelRemove, SWT.PUSH);
		
		taxRateSection.setClient(client);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		LOG.debug("doSave"); //$NON-NLS-1$
		
		User user = getEditorInput().getUser();
		
		ModelHelper.saveCurrentUser(user);
		
		setIsDirty(false);
	}	
		
	/**
	 * 
	 */
	@Override
	public UserEditorInput getEditorInput() {
		return (UserEditorInput) super.getEditorInput();
	}
	
	/**
	 *
	 */
	private class TaxRateCellLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
		
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
	        return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
        	if (element == null || !(element instanceof TaxRate)) {
        		return Constants.HYPHEN;
        	}
        	
        	final TaxRate tr = (TaxRate) element;

        	switch (columnIndex) {
			case COLUMN_TAX_RATE_ABBREVIATION:
				return tr.getShortName();
			case COLUMN_TAX_RATE_NAME:
				return tr.getLongName();
			case COLUMN_TAX_RATE:
				return FormatUtil.formatPercentValue(tr.getRate());
			default:
				return Constants.HYPHEN;
			}
        }
	}
}