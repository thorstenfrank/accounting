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

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.User;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;
import de.togginho.accounting.ui.client.ClientEditorInput;

/**
 * @author thorsten
 *
 */
public class NewClientWizard extends Wizard implements IWorkbenchWizard {

	private static final Logger LOG = Logger.getLogger(NewClientWizard.class);
	
	private ClientNameWizardPage clientNameWizardPage;
	private AddressWizardPage addressWizardPage;
	
	private Client newClient;
		
	/**
	 * 
	 */
	public NewClientWizard() {
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.NewClientWizard_windowTitle);
	}
	
	
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		LOG.debug("Init"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		LOG.debug("Adding pages..."); //$NON-NLS-1$
		clientNameWizardPage = new ClientNameWizardPage();
		addressWizardPage = new AddressWizardPage();
		
		addPage(clientNameWizardPage);
		addPage(addressWizardPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		LOG.debug("Performing finish"); //$NON-NLS-1$
		newClient = new Client();
		newClient.setName(clientNameWizardPage.getClientName());
		newClient.setAddress(addressWizardPage.getAddress());
		
		LOG.debug("Getting current user from Plugin"); //$NON-NLS-1$
		User user = ModelHelper.getCurrentUser();
		if (user.getClients() == null) {
			user.setClients(new HashSet<Client>());
		} else {
			LOG.debug("Checking for existing client with name " + newClient.getName()); //$NON-NLS-1$
			if (user.getClients().contains(newClient)) {
				MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
				msgBox.setMessage(Messages.bind(Messages.NewClientWizard_alreadyExistsMsg, newClient.getName()));
				msgBox.setText(Messages.NewClientWizard_alreadyExistsText);
				msgBox.open();
				return false;
			}
		}

		user.getClients().add(newClient);
		
		LOG.info("Saving current user with newly added client " + newClient.getName());  //$NON-NLS-1$
		
		ModelHelper.saveCurrentUser(user);

		LOG.debug("Opening editor for newly created client"); //$NON-NLS-1$
		ClientEditorInput input = new ClientEditorInput(newClient);
		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, IDs.EDIT_CLIENT_ID);
		} catch (PartInitException e) {
			LOG.error("Error opening editor for newly created client", e); //$NON-NLS-1$
		}
		
		return true;
	}
	
	/**
	 * @return the newClient
	 */
	public Client getNewClient() {
		return newClient;
	}
}