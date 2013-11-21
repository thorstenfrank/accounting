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
package de.togginho.accounting.ui.revenue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.Revenue;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.reports.AbstractReportDialog;
import de.togginho.accounting.ui.reports.ReportGenerationHandler;
import de.togginho.accounting.ui.reports.ReportGenerationUtil;
import de.togginho.accounting.util.FormatUtil;
import de.togginho.accounting.util.TimeFrame;

/**
 * 
 * @author thorsten
 *
 */
public class RevenueDialog extends AbstractReportDialog {
	
	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".RevenueDialog"; //$NON-NLS-1$
	
	protected static final int COLUMN_INDEX_INVOICE_NUMBER = 0;
	protected static final int COLUMN_INDEX_INVOICE_DATE = 1;
	protected static final int COLUMN_INDEX_PAYMENT_DATE = 2;
	protected static final int COLUMN_INDEX_CLIENT = 3;
	protected static final int COLUMN_INDEX_NET_PRICE = 4;
	protected static final int COLUMN_INDEX_TAX_AMOUNT = 5;
	protected static final int COLUMN_INDEX_GROSS_PRICE = 6;
	
	private FormToolkit formToolkit;
	private Text netTotal;
	private Text grossTotal;
	private Text taxTotal;
	private TableViewer tableViewer;
	private Revenue revenue;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RevenueDialog(Shell parentShell) {
		super(parentShell, TimeFrame.currentYear());
	}
		
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.RevenueDialog_title);
		getShell().setImage(AccountingUI.getImageDescriptor(Messages.iconsRevenue).createImage());
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		
		formToolkit = new FormToolkit(parent.getDisplay());
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		createQuerySection(container);
		
		createTotalsSection(container);
		
		createInvoicesSection(container);

		// get an initial revenue for the current year
		updateModel();
		
		return container;
	}
	
	/**
	 * 
	 * @param parent
	 */
	private void createTotalsSection(Composite parent) {
		Section totalsSection = formToolkit.createSection(parent, Section.TITLE_BAR);
		totalsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		formToolkit.paintBordersFor(totalsSection);
		totalsSection.setText(Messages.labelTotals);
		
		Composite sectionClient = new Composite(totalsSection, SWT.NONE);
		formToolkit.adapt(sectionClient);
		formToolkit.paintBordersFor(sectionClient);
		totalsSection.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(6, false));
		
		formToolkit.createLabel(sectionClient, Messages.labelTotalNet);
		netTotal = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		netTotal.setEnabled(false);
		netTotal.setEditable(false);
		
		formToolkit.createLabel(sectionClient, Messages.labelTotalTax);
		taxTotal = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		taxTotal.setEnabled(false);
		taxTotal.setEditable(false);
		
		formToolkit.createLabel(sectionClient, Messages.labelTotalGross);
		grossTotal = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		grossTotal.setEnabled(false);
		grossTotal.setEditable(false);
	}
	
	/**
	 * 
	 * @param parent
	 */
	private void createInvoicesSection(Composite parent) {
		Section invoiceSection = formToolkit.createSection(parent, Section.TITLE_BAR);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(invoiceSection);
		//formToolkit.paintBordersFor(invoiceSection);
		invoiceSection.setText(Messages.RevenueDialog_invoices);
		invoiceSection.setExpanded(true);
		
		Composite tableComposite = new Composite(invoiceSection, SWT.NONE);
		formToolkit.adapt(tableComposite);
		formToolkit.paintBordersFor(tableComposite);
		invoiceSection.setClient(tableComposite);
		TableColumnLayout tcl = new TableColumnLayout();
		tableComposite.setLayout(tcl);
		
		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		final Table table = tableViewer.getTable();
		formToolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		createColumn(COLUMN_INDEX_INVOICE_NUMBER, Messages.RevenueDialog_invoiceNo, SWT.CENTER, tcl);
		createColumn(COLUMN_INDEX_INVOICE_DATE, Messages.RevenueDialog_invoiceDate, SWT.CENTER, tcl);
		createColumn(COLUMN_INDEX_PAYMENT_DATE, Messages.RevenueDialog_paymentDate, SWT.CENTER, tcl);
		createColumn(COLUMN_INDEX_CLIENT, Messages.RevenueDialog_client, SWT.CENTER, tcl);
		createColumn(COLUMN_INDEX_NET_PRICE, Messages.labelNet, SWT.RIGHT, tcl);
		createColumn(COLUMN_INDEX_TAX_AMOUNT, Messages.labelTaxes, SWT.RIGHT, tcl);
		createColumn(COLUMN_INDEX_GROSS_PRICE, Messages.labelGross, SWT.RIGHT, tcl);
		
		tableViewer.setLabelProvider(new InvoiceLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		InvoiceTableSorter sorter = new InvoiceTableSorter();
		sorter.setSortColumnIndex(COLUMN_INDEX_INVOICE_NUMBER);
		tableViewer.setComparator(sorter);
		table.setSortColumn(table.getColumn(COLUMN_INDEX_INVOICE_NUMBER));
	}

	/**
	 * 
	 * @param index
	 * @param text
	 * @param alignment
	 * @return
	 */
	private TableViewerColumn createColumn(final int index, final String text, final int alignment, final TableColumnLayout layout) {
		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE, index);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(text);
		column.setAlignment(alignment);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			    tableViewer.getTable().setSortColumn(column);
			    ((InvoiceTableSorter) tableViewer.getSorter()).setSortColumnIndex(index);
			    tableViewer.refresh();
			}
		});
		
		layout.setColumnData(column, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		
		return viewerColumn;
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#updateModel()
	 */
	@Override
	protected void updateModel() {
		revenue = AccountingUI.getAccountingService().getRevenue(getTimeFrame());
		List<InvoiceWrapper> invoices = new ArrayList<InvoiceWrapper>();
		for (Invoice invoice : revenue.getInvoices()) {
			invoices.add(new InvoiceWrapper(invoice));
		}
		
		tableViewer.setInput(invoices);
		tableViewer.refresh();
		
		netTotal.setText(FormatUtil.formatCurrency(revenue.getRevenueNet()));
		grossTotal.setText(FormatUtil.formatCurrency(revenue.getRevenueGross()));
		taxTotal.setText(FormatUtil.formatCurrency(revenue.getRevenueTax()));
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return formToolkit;
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#handleExport()
	 */
	@Override
	protected void handleExport() {
		ReportGenerationUtil.executeReportGeneration(new RevenueExportHandler(), getShell());
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 500);
	}

	/**
	 * Label provider for the table of invoices.
	 */
	private class InvoiceLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

		/**
		 * 
		 */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
	        return null;
        }

		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
        	if (element == null || !(element instanceof InvoiceWrapper)) {
        		return Constants.HYPHEN;
        	}
        	
        	final InvoiceWrapper invoice = (InvoiceWrapper) element;
        	switch (columnIndex) {
			case COLUMN_INDEX_INVOICE_NUMBER:
				return invoice.getNumber();
			case COLUMN_INDEX_INVOICE_DATE:
				return invoice.getInvoiceDate();
			case COLUMN_INDEX_PAYMENT_DATE:
				return invoice.getPaymentDate();
			case COLUMN_INDEX_CLIENT:
				return invoice.getClient();
			case COLUMN_INDEX_NET_PRICE:
				return invoice.getNetAmount();
			case COLUMN_INDEX_TAX_AMOUNT:
				return invoice.getTaxAmount();
			case COLUMN_INDEX_GROSS_PRICE:
				return invoice.getGrossAmount();
			default:
				return Constants.HYPHEN;
			}
        }
	}
	
	/**
	 * Export handler for creating a PDF revenue report.
	 */
	private class RevenueExportHandler implements ReportGenerationHandler {

		/**
         * {@inheritDoc}.
         * @see ReportGenerationHandler#getTargetFileNameSuggestion()
         */
        @Override
        public String getTargetFileNameSuggestion() {
	        return ReportGenerationUtil.appendTimeFrameToFileNameSuggestion(
	        		Messages.RevenueDialog_defaultFilename, revenue.getTimeFrame());
        }

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ui.reports.ReportGenerationHandler#getModelObject()
         */
        @Override
        public Object getModelObject() {
	        return revenue;
        }

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ui.reports.ReportGenerationHandler#getReportId()
         */
        @Override
        public String getReportId() {
	        return "Revenue";
        }
	}
}
