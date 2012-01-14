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
package de.togginho.accounting.ui.wizard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;

/**
 * @author thorsten
 *
 */
class NewInvoiceWizardPage extends WizardPage {
	
	private Text invoiceNumber;
	private Combo clientCombo;
	private Map<String, Client> nameToClientMap;
	
	NewInvoiceWizardPage() {
		super("NewInvoiceWizardPage"); //$NON-NLS-1$
		setTitle(Messages.NewInvoiceWizardPage_title);
		setDescription(Messages.NewInvoiceWizardPage_desc);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, NewInvoiceWizard.HELP_CONTEXT_ID);
		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		
		// INVOICE NO
		WidgetHelper.createLabel(composite, Messages.labelInvoiceNo);
		
		invoiceNumber = new Text(composite, SWT.BORDER | SWT.SINGLE);
		invoiceNumber.setText(AccountingUI.getAccountingService().getNextInvoiceNumber());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(invoiceNumber);
		invoiceNumber.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				checkIfPageComplete();
			}
		});
		
		Button generateButton = new Button(composite, SWT.PUSH);
		generateButton.setText(Messages.NewInvoiceWizardPage_generate);

		// CLIENT
		WidgetHelper.createLabel(composite, Messages.NewInvoiceWizardPage_chooseClient);
		
		clientCombo = new Combo(composite, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(clientCombo);
		String[] clientNames = null;
		Set<Client> clients = AccountingUI.getAccountingService().getClients();
		if (clients != null) {
			nameToClientMap = new HashMap<String, Client>();
			clientNames = new String[clients.size()];
			int counter = 0;
			for (Iterator<Client> iter = clients.iterator(); iter.hasNext(); counter++) {
				Client client = iter.next();
				clientNames[counter] = client.getName();
				nameToClientMap.put(client.getName(), client);
			}
		} else {
			clientNames = new String[0];
		}
		clientCombo.setItems(clientNames);
		clientCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkIfPageComplete();
			}
		});
		
		setControl(composite);
		setPageComplete(false);
	}
	
	/**
     * {@inheritDoc}.
     * @see org.eclipse.jface.dialogs.DialogPage#performHelp()
     */
    @Override
    public void performHelp() {
    	PlatformUI.getWorkbench().getHelpSystem().displayHelp(NewInvoiceWizard.HELP_CONTEXT_ID);
    }
	
    /**
     * 
     */
	private void checkIfPageComplete() {
		if (invoiceNumber.getText().isEmpty() || clientCombo.getSelectionIndex() < 0) {
			setPageComplete(false);
		} else {
			setPageComplete(true);
		}		
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getInvoiceNumber() {
		return invoiceNumber.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	protected Client getSelectedClient() {
		if (nameToClientMap == null || clientCombo.getSelectionIndex() < 0) {
			return null;
		}
		
		String[] clientNames = clientCombo.getItems();
		return nameToClientMap.get(clientNames[clientCombo.getSelectionIndex()]);
	}
}
