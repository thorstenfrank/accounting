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
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
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
import de.togginho.accounting.util.FormatUtil;

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
		
		Composite tableComposite = new Composite(parent, SWT.NONE);
		TableColumnLayout tcl = new TableColumnLayout();
		tableComposite.setLayout(tcl);
		
		tableViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);
		getSite().setSelectionProvider(tableViewer);
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		InvoiceLabelProvider labelProvider = new InvoiceLabelProvider();
		
		TableViewerColumn col1 = new TableViewerColumn(tableViewer, SWT.NONE);
		col1.getColumn().setText(Messages.InvoiceView_number);
		col1.setLabelProvider(labelProvider);
		tcl.setColumnData(col1.getColumn(), new ColumnWeightData(10, true));
		
		TableViewerColumn col2 = new TableViewerColumn(tableViewer, SWT.NONE);
		col2.getColumn().setText(Messages.InvoiceView_state);
		col2.getColumn().setAlignment(SWT.CENTER);
		col2.setLabelProvider(labelProvider);
		tcl.setColumnData(col2.getColumn(), new ColumnWeightData(10, true));
		
		TableViewerColumn col3 = new TableViewerColumn(tableViewer, SWT.NONE);
		col3.getColumn().setText(Messages.labelClient);
		col3.setLabelProvider(labelProvider);
		tcl.setColumnData(col3.getColumn(), new ColumnWeightData(15, true));
		
		TableViewerColumn col4 = new TableViewerColumn(tableViewer, SWT.NONE);
		col4.getColumn().setText(Messages.InvoiceView_dueDate);
		col4.setLabelProvider(labelProvider);
		tcl.setColumnData(col4.getColumn(), new ColumnWeightData(10, true));
		
		tableViewer.addDoubleClickListener(this);
		
		Set<Invoice> invoices = ModelHelper.findInvoices();
		
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
		LOG.info("Refreshing invoices..."); //$NON-NLS-1$
		Set<Invoice> invoices = ModelHelper.findInvoices();

		LOG.debug("Number of invoices: " + invoices.size()); //$NON-NLS-1$
		
		// reload open invoices
		tableViewer.setInput(invoices);
		tableViewer.refresh();
	}
	
	/**
	 *
	 */
	private class InvoiceLabelProvider extends CellLabelProvider {

		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.CellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
         */
        @Override
        public void update(ViewerCell cell) {
        	String text = Constants.HYPHEN;
        	final Invoice invoice = (Invoice) cell.getElement();
        	switch (cell.getColumnIndex()) {
			case 0:
				text = invoice.getNumber();
				break;
			case 1:
				text = invoice.getState().getTranslatedString();
				break;
			case 2:
				text = invoice.getClient() != null ? invoice.getClient().getName() : Constants.HYPHEN;
				break;
			case 3:
				text = invoice.getDueDate() != null ? FormatUtil.formatDate(invoice.getDueDate()) : Constants.HYPHEN;
				break;
			default:
				break;
			}
        	
        	cell.setText(text);
        }
		
	}
}
