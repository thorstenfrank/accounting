/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.client;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.ModelChangeListener;

/**
 * @author thorsten
 *
 */
public class ClientsView extends ViewPart implements IDoubleClickListener, ModelChangeListener {
	
	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".ClientsView"; //$NON-NLS-1$
	
	private static final Logger LOG = LogManager.getLogger(ClientsView.class);
	
	private IContextActivation contextActivation;
	
	private TableViewer viewer;

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		AccountingUI.addModelChangeListener(this);		
		
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextActivation = contextService.activateContext(getClass().getPackage().getName());
		
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
		
		// unregister the context
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.deactivateContext(contextActivation);
		
		super.dispose();
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.ModelChangeListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
    	LOG.debug("Model data changed, now refreshing view..."); //$NON-NLS-1$
    	viewer.setInput(getClients());
	    viewer.refresh();
	}
}
