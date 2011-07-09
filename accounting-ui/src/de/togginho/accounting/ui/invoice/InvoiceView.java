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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.ui.AbstractTableSorter;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class InvoiceView extends ViewPart implements IDoubleClickListener, PropertyChangeListener, Constants {
	
	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".InvoiceView";
	
	/** Logger. */
	private static final Logger LOG = Logger.getLogger(InvoiceView.class);
	
	/**
	 * 
	 */
	private static final Map<InvoiceState, Image> INVOICE_STATE_TO_IMAGE_MAP = new HashMap<InvoiceState, Image>();
	
	// column indices
	protected static final int COL_INDEX_INVOICE_NUMBER = 0;
	protected static final int COL_INDEX_INVOICE_STATE = 1;
	protected static final int COL_INDEX_CLIENT = 2;
	protected static final int COL_INDEX_DUE_DATE = 3;
	
	/** The viewer. */
	private TableViewer tableViewer;
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		
		initInvoiceStateImageMap();
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
		
		TableViewerColumn col1 = new TableViewerColumn(tableViewer, SWT.NONE, COL_INDEX_INVOICE_NUMBER);
		final TableColumn col1col = col1.getColumn();
		col1col.setText(Messages.InvoiceView_number);
		tcl.setColumnData(col1col, new ColumnWeightData(10, true));
		col1col.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.getTable().setSortColumn(col1col);
				((AbstractTableSorter) tableViewer.getSorter()).setSortColumnIndex(COL_INDEX_INVOICE_NUMBER);
			    tableViewer.refresh();
			}
		});
		
		
		TableViewerColumn col2 = new TableViewerColumn(tableViewer, SWT.NONE, COL_INDEX_INVOICE_STATE);
		final TableColumn col2col = col2.getColumn();
		col2col.setText(Messages.InvoiceView_state);
		col2col.setAlignment(SWT.CENTER);
		tcl.setColumnData(col2col, new ColumnWeightData(10, true));
		col2col.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.getTable().setSortColumn(col2col);
				((AbstractTableSorter) tableViewer.getSorter()).setSortColumnIndex(COL_INDEX_INVOICE_STATE);
			    tableViewer.refresh();
			}
		});
		
		TableViewerColumn col3 = new TableViewerColumn(tableViewer, SWT.NONE, COL_INDEX_CLIENT);
		final TableColumn col3col = col3.getColumn();
		col3col.setText(Messages.labelClient);
		tcl.setColumnData(col3col, new ColumnWeightData(15, true));
		col3col.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.getTable().setSortColumn(col3col);
				((AbstractTableSorter) tableViewer.getSorter()).setSortColumnIndex(COL_INDEX_CLIENT);
			    tableViewer.refresh();
			}
		});
		
		TableViewerColumn col4 = new TableViewerColumn(tableViewer, SWT.NONE, COL_INDEX_DUE_DATE);
		final TableColumn col4col = col4.getColumn();
		col4col.setText(Messages.InvoiceView_dueDate);
		tcl.setColumnData(col4col, new ColumnWeightData(10, true));
		col4col.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.getTable().setSortColumn(col4col);
				((AbstractTableSorter) tableViewer.getSorter()).setSortColumnIndex(COL_INDEX_DUE_DATE);
			    tableViewer.refresh();
			}
		});
		
		// get the invoices to display
		final Set<Invoice> invoices = ModelHelper.findInvoices();
		
		tableViewer.setLabelProvider(new InvoiceLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(invoices);
		
		InvoiceViewTableSorter sorter = new InvoiceViewTableSorter();
		sorter.setSortColumnIndex(COL_INDEX_INVOICE_NUMBER);
		tableViewer.setComparator(sorter);
		table.setSortColumn(col1col);
		
		// make sure double-clicks are processed
		tableViewer.addDoubleClickListener(this);
		
		// create the view menu
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
		
		for (InvoiceState state : INVOICE_STATE_TO_IMAGE_MAP.keySet()) {
			INVOICE_STATE_TO_IMAGE_MAP.get(state).dispose();
		}
		
		INVOICE_STATE_TO_IMAGE_MAP.clear();
		
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
	private void initInvoiceStateImageMap() {
		if (INVOICE_STATE_TO_IMAGE_MAP.isEmpty()) {
			INVOICE_STATE_TO_IMAGE_MAP.put(InvoiceState.CANCELLED, 
					AccountingUI.getImageDescriptor(Messages.iconsInvoiceCancelled).createImage());
			INVOICE_STATE_TO_IMAGE_MAP.put(InvoiceState.CREATED, 
					AccountingUI.getImageDescriptor(Messages.iconsInvoiceCreated).createImage());
			INVOICE_STATE_TO_IMAGE_MAP.put(InvoiceState.OVERDUE, 
					AccountingUI.getImageDescriptor(Messages.iconsInvoiceOverdue).createImage());
			INVOICE_STATE_TO_IMAGE_MAP.put(InvoiceState.PAID,
					AccountingUI.getImageDescriptor(Messages.iconsInvoicePaid).createImage());
			INVOICE_STATE_TO_IMAGE_MAP.put(InvoiceState.SENT, 
					AccountingUI.getImageDescriptor(Messages.iconsInvoiceSend).createImage());			
		}
	}
	
	/**
	 *
	 */
	private class InvoiceLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
        	if (columnIndex == COL_INDEX_INVOICE_STATE) {
        		final Invoice invoice = (Invoice) element;
        		return INVOICE_STATE_TO_IMAGE_MAP.get(invoice.getState());
        	}
        	
	        return null;
        }
        
		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
        	if (element == null || !(element instanceof Invoice)) {
        		return HYPHEN;
        	}
        	
        	final Invoice invoice = (Invoice) element;
        	switch (columnIndex) {
			case COL_INDEX_INVOICE_NUMBER:
				return invoice.getNumber();
			case COL_INDEX_INVOICE_STATE:
				return invoice.getState().getTranslatedString();
			case COL_INDEX_CLIENT:
				return invoice.getClient() != null ? invoice.getClient().getName() : Constants.HYPHEN;
			case COL_INDEX_DUE_DATE:
				return invoice.getDueDate() != null ? FormatUtil.formatDate(invoice.getDueDate()) : Constants.HYPHEN;
			default:
				return HYPHEN;
			}
        }
	}
}
