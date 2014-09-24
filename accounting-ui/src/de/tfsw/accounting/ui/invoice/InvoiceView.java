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
package de.tfsw.accounting.ui.invoice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoiceState;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.ui.AbstractTableSorter;
import de.tfsw.accounting.ui.AbstractTableView;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.ModelChangeListener;
import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.FormatUtil;
import de.tfsw.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class InvoiceView extends AbstractTableView implements ModelChangeListener {
	
	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".InvoiceView"; //$NON-NLS-1$
	
	/** Logger. */
	private static final Logger LOG = Logger.getLogger(InvoiceView.class);
	private static final Logger SORTER_LOG = Logger.getLogger(InvoiceViewTableSorter.class);
	
	/**
	 * 
	 */
	private static final Map<InvoiceState, Image> INVOICE_STATE_TO_IMAGE_MAP = new HashMap<InvoiceState, Image>();
	
	// column indices
	private static final int COL_INDEX_INVOICE_NUMBER = 0;
	private static final int COL_INDEX_INVOICE_STATE = 1;
	private static final int COL_INDEX_CLIENT = 2;
	private static final int COL_INDEX_DUE_DATE = 3;
	private static final int COL_INDEX_AMOUNT_NET = 4;
	private static final int COL_INDEX_AMOUNT_GROSS = 5;
	
	private IContextActivation contextActivation;
	
	/** The viewer. */
	private TableViewer tableViewer;
	private InvoiceViewTableSorter sorter;
	private Set<InvoiceState> invoiceStateFilter;
	private TimeFrame timeFrameFilter;
	private Map<Invoice, Price> invoicePrices;
	
	/**
	 * 
	 */
	public InvoiceView() {
		this.invoiceStateFilter = new HashSet<InvoiceState>();
		// fill invoice filter with default values
		invoiceStateFilter.add(InvoiceState.CREATED);
		invoiceStateFilter.add(InvoiceState.OVERDUE);
		invoiceStateFilter.add(InvoiceState.SENT);
    }

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		
		AccountingUI.addModelChangeListener(this);
		
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextActivation = contextService.activateContext(getClass().getPackage().getName());
		
		initInvoiceStateImageMap();
		
		Composite tableComposite = new Composite(parent, SWT.NONE);
		TableColumnLayout tcl = new TableColumnLayout();
		tableComposite.setLayout(tcl);
		
		tableViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);
		
		getSite().setSelectionProvider(tableViewer);
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		sorter = new InvoiceViewTableSorter();
		
		TableColumn firstColumn = addColumn(COL_INDEX_INVOICE_NUMBER, Messages.InvoiceView_number, tcl, 10);
		addColumn(COL_INDEX_INVOICE_STATE, Messages.InvoiceView_state, tcl, 10);
		addColumn(COL_INDEX_CLIENT, Messages.labelClient, tcl, 15);
		addColumn(COL_INDEX_DUE_DATE, Messages.InvoiceView_dueDate, tcl, 10);
		addColumn(COL_INDEX_AMOUNT_NET, Messages.labelNet, SWT.RIGHT, tcl, 10, true);
		addColumn(COL_INDEX_AMOUNT_GROSS, Messages.labelGross, SWT.RIGHT, tcl, 10, true);
		
		tableViewer.setLabelProvider(new InvoiceLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		sorter.setSortColumnIndex(COL_INDEX_INVOICE_NUMBER);
		tableViewer.setComparator(sorter);
		table.setSortColumn(firstColumn);
		
		// make sure double-clicks are processed
		tableViewer.addDoubleClickListener(this);
		
		// create the view menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
		
		modelChanged();
	}
	
	/**
     * {@inheritDoc}.
     * @see de.tfsw.accounting.ui.AbstractTableView#getLogger()
     */
    @Override
    protected Logger getLogger() {
	    return LOG;
    }

	/**
     * {@inheritDoc}.
     * @see de.tfsw.accounting.ui.AbstractTableView#getDoubleClickCommand()
     */
    @Override
    protected String getDoubleClickCommand() {
	    return IDs.CMD_EDIT_INVOICE;
    }

	/**
     * {@inheritDoc}.
     * @see de.tfsw.accounting.ui.AbstractTableView#getTableViewer()
     */
    @Override
    protected TableViewer getTableViewer() {
	    return tableViewer;
    }

	/**
     * {@inheritDoc}.
     * @see de.tfsw.accounting.ui.AbstractTableView#getTableSorter()
     */
    @Override
    protected AbstractTableSorter<?> getTableSorter() {
	    return sorter;
    }

	/**
	 * 
	 */
	@Override
	public void dispose() {
		// unregister myself as a listener
		AccountingUI.removeModelChangeListener(this);
		
		// unregister the context
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.deactivateContext(contextActivation);
		
		// dispose the images used for visualizing the invoice states
		for (InvoiceState state : INVOICE_STATE_TO_IMAGE_MAP.keySet()) {
			INVOICE_STATE_TO_IMAGE_MAP.get(state).dispose();
		}
		
		INVOICE_STATE_TO_IMAGE_MAP.clear();
		
		super.dispose();
	}
	
	/**
     * @see de.tfsw.accounting.ui.ModelChangeListener#modelChanged()
     */
    @Override
    public void modelChanged() {
		LOG.info("Model data changed, refreshing invoices..."); //$NON-NLS-1$

		Set<Invoice> invoices = null;
		
		InvoiceState[] states = null;
		if (invoiceStateFilter != null && !invoiceStateFilter.isEmpty()) {
			invoices = AccountingUI.getAccountingService().findInvoices();
			states = (InvoiceState[]) invoiceStateFilter.toArray(new InvoiceState[invoiceStateFilter.size()]);
		} 
		
		invoices = AccountingUI.getAccountingService().findInvoices(timeFrameFilter, states);
		
		LOG.debug("Number of invoices found: " + invoices.size()); //$NON-NLS-1$
		
		// re-create the price map for viewing and sorting so the prices don't have to be re-calculated constantly
		invoicePrices = new HashMap<Invoice, Price>();
		
		for (Invoice invoice : invoices) {
			invoicePrices.put(invoice, CalculationUtil.calculateTotalPrice(invoice));
		}
		
		// reload open invoices
		tableViewer.setInput(invoices);
		tableViewer.refresh();
		
		getViewSite().getActionBars().getStatusLineManager().setMessage(
				AccountingUI.getImageDescriptor(Messages.iconsInvoices).createImage(),
				Messages.bind(Messages.InvoiceView_statusLine, invoices.size()));
    }
    
	/**
     * @return the invoiceStateFilter
     */
    protected Set<InvoiceState> getInvoiceStateFilter() {
    	return invoiceStateFilter;
    }
    
	/**
	 * @param invoiceStateFilter the invoiceStateFilter to set
	 */
	protected void setInvoiceStateFilter(Set<InvoiceState> invoiceStateFilter) {
		this.invoiceStateFilter = invoiceStateFilter;
	}

	/**
	 * @return the timeFrameFilter
	 */
	protected TimeFrame getTimeFrameFilter() {
		return timeFrameFilter;
	}

	/**
	 * @param timeFrameFilter the timeFrameFilter to set
	 */
	protected void setTimeFrameFilter(TimeFrame timeFrameFilter) {
		this.timeFrameFilter = timeFrameFilter;
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
        	if (element != null && element instanceof Invoice) {        		
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
				case COL_INDEX_AMOUNT_NET:
					return FormatUtil.formatCurrency(invoicePrices.get(invoice).getNet());
				case COL_INDEX_AMOUNT_GROSS:
					return FormatUtil.formatCurrency(invoicePrices.get(invoice).getGross());
		    	}
        	}
        	
        	return Constants.HYPHEN;
        }
	}
	
	/**
	 * 
	 * @author thorsten
	 *
	 */
	private class InvoiceViewTableSorter extends AbstractTableSorter<Invoice> {
		
		
		/**
		 * 
		 */
		protected InvoiceViewTableSorter() {
			super(Invoice.class);
		}
		
		/**
		 * {@inheritDoc}
		 * @see de.tfsw.accounting.ui.AbstractTableSorter#getLogger()
		 */
		@Override
		protected Logger getLogger() {
			return SORTER_LOG;
		}

		/**
		 * {@inheritDoc}.
		 * @see AbstractTableSorter#doCompare(Object, Object, int)
		 */
		@Override
		protected int doCompare(Invoice i1, Invoice i2, int columnIndex) {
			int result = 0;
			switch (columnIndex) {
			case InvoiceView.COL_INDEX_INVOICE_NUMBER:
				result = i1.getNumber().compareTo(i2.getNumber());
				break;
			case InvoiceView.COL_INDEX_INVOICE_STATE:
				result = i1.getState().compareTo(i2.getState());
				break;
			case InvoiceView.COL_INDEX_CLIENT:
				result = compareClients(i1.getClient(), i2.getClient());
				break;
			case InvoiceView.COL_INDEX_DUE_DATE:
				result = i1.getDueDate().compareTo(i2.getDueDate());
				break;
			case InvoiceView.COL_INDEX_AMOUNT_NET:
				result = invoicePrices.get(i1).getNet().compareTo(invoicePrices.get(i2).getNet());
				break;
			case InvoiceView.COL_INDEX_AMOUNT_GROSS:
				result = invoicePrices.get(i1).getGross().compareTo(invoicePrices.get(i2).getGross());
				break;
			default:
				break;
			}
			
			return result;
		}

		/**
		 * 
		 * @param c1
		 * @param c2
		 * @return
		 */
		private int compareClients(Client c1, Client c2) {
			if (c1 == null && c2 != null) {
				return 1;
			} else if (c1 != null && c2 == null) {
				return -1;
			}
			
			return c1.getName().compareTo(c2.getName());
		}
	}
}
