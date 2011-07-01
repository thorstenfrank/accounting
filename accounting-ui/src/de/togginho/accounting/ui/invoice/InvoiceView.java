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
package de.togginho.accounting.ui.invoice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;

/**
 * @author thorsten
 *
 */
public class InvoiceView extends ViewPart implements IDoubleClickListener, PropertyChangeListener, Constants {
	
	/** Logger. */
	private static final Logger LOG = Logger.getLogger(InvoiceView.class);
	
	/** The viewer. */
	private TableViewer tableViewer;
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		ModelHelper.addPropertyChangeListener(ModelHelper.MODEL_INVOICES, this);
		ModelHelper.addPropertyChangeListener(ModelHelper.MODEL_INVOICE_FILTER, this);
		
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		getSite().setSelectionProvider(tableViewer);
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		TableViewerColumn col1 = new TableViewerColumn(tableViewer, SWT.NONE);
		col1.getColumn().setText(Messages.InvoiceView_number);
		col1.getColumn().setWidth(100);
		col1.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				Invoice invoice = (Invoice) cell.getElement();
				cell.setText(invoice.getNumber());
			}
		});
		
		TableViewerColumn col2 = new TableViewerColumn(tableViewer, SWT.NONE);
		col2.getColumn().setText(Messages.InvoiceView_state);
		col2.getColumn().setWidth(75);
		col2.getColumn().setAlignment(SWT.CENTER);
		col2.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				Invoice invoice = (Invoice) cell.getElement();
				cell.setText(invoice.getState().getTranslatedString());
//				if (invoice.getState() == InvoiceState.CREATED) {
//					cell.setImage(createdImage);
//				}
			}
		});
		
		TableViewerColumn col3 = new TableViewerColumn(tableViewer, SWT.NONE);
		col3.getColumn().setText(Messages.labelClient);
		col3.getColumn().setWidth(200);
		col3.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				Invoice invoice = (Invoice) cell.getElement();
				if (invoice.getClient() != null) {
					cell.setText(invoice.getClient().getName());
				} else {
					cell.setText(HYPHEN); //$NON-NLS-1$
				}
			}
		});
		
		tableViewer.addDoubleClickListener(this);
		
		Set<Invoice> invoices = ModelHelper.findInvoices();
		LOG.debug("adding open invoices: " + invoices.size()); //$NON-NLS-1$
		
		tableViewer.setInput(invoices);
		
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
	
	/**
	 * 
	 */
	@Override
	public void dispose() {
		ModelHelper.removePropertyChangeListener(ModelHelper.MODEL_INVOICES, this);
		ModelHelper.removePropertyChangeListener(ModelHelper.MODEL_INVOICE_FILTER, this);
		super.dispose();
	}
	
	/**
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		String invoiceNumber = event.getSelection().toString();
		LOG.debug("DoubleClick: " + invoiceNumber); //$NON-NLS-1$
		
		IHandlerService handlerService = 
			(IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		
		try {
			handlerService.executeCommand(IDs.CMD_EDIT_INVOICE, new Event());
		} catch (Exception e) {
			LOG.error("Error opening invoice editor", e); //$NON-NLS-1$
		}
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		LOG.info("Refreshing open invoices..."); //$NON-NLS-1$
		Set<Invoice> invoices = ModelHelper.findInvoices();

		LOG.debug("Number of invoices: " + invoices.size()); //$NON-NLS-1$
		
		// reload open invoices
		tableViewer.setInput(invoices);
		tableViewer.refresh();
	}
}
