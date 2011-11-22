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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.ModelChangeListener;

/**
 * @author thorsten
 *
 */
public class ClientsView extends ViewPart implements IDoubleClickListener, ModelChangeListener {
	
	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".ClientsView"; //$NON-NLS-1$
	
	private static final Logger LOG = Logger.getLogger(ClientsView.class);
	
	private TableViewer viewer;

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		AccountingUI.addModelChangeListener(this);		
		
		viewer = new TableViewer(parent, SWT.FULL_SELECTION);
		getSite().setSelectionProvider(viewer);
		
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(getClients());
		viewer.setLabelProvider(new LabelProvider() {

			/**
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof Client) {
					return ((Client)element).getName();
				}
				return super.getText(element);
			}
		});
		
		viewer.addDoubleClickListener(this);
		
		// build the context menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
	}
	
	/**
	 * 
	 * @return
	 */
	private Set<Client> getClients() {
		Set<Client> clients = null;
		
		try {
			clients = AccountingUI.getAccountingService().getClients();
		} catch (Exception e) {
			LOG.error("Error getting list of clients from DB", e); //$NON-NLS-1$
		}
	
		if (clients == null) {
			clients = new HashSet<Client>();
		}
		
		return clients;
	}
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		IHandlerService handlerService = 
			(IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		
		try {
			handlerService.executeCommand(IDs.CMD_EDIT_CLIENT, new Event());
		} catch (Exception e) {
			LOG.error("Error executing editClientCommand", e); //$NON-NLS-1$
		}
	}

	/** 
	 * Removes this view as a listener from the service.
	 */
	@Override
	public void dispose() {
		LOG.debug("Disposing client list viewer"); //$NON-NLS-1$
		AccountingUI.removeModelChangeListener(this);
		super.dispose();
	}
	
	/**
     * @see de.togginho.accounting.ui.ModelChangeListener#currentUserChanged()
     */
    @Override
    public void currentUserChanged() {
    	LOG.debug("Current User changed, now refreshing view..."); //$NON-NLS-1$
	    viewer.refresh();
    }

	/**
     * @see de.togginho.accounting.ui.ModelChangeListener#invoicesChanged()
     */
    @Override
    public void invoicesChanged() {
	    // we're not really interested in this event...
    }
}
