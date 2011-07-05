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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.togginho.accounting.Constants;
import de.togginho.accounting.ReportGenerationMonitor;
import de.togginho.accounting.ReportingService;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.Revenue;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;
import de.togginho.accounting.ui.reports.ReportGenerationHandler;
import de.togginho.accounting.ui.reports.ReportGenerationUtil;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * 
 * @author thorsten
 *
 */
public class RevenueDialog extends Dialog {
	
	private static final int COLUMN_INDEX_INVOICE_NUMBER = 0;
	private static final int COLUMN_INDEX_INVOICE_DATE = 1;
	private static final int COLUMN_INDEX_PAYMENT_DATE = 2;
	private static final int COLUMN_INDEX_CLIENT = 3;
	private static final int COLUMN_INDEX_NET_PRICE = 4;
	private static final int COLUMN_INDEX_TAX_AMOUNT = 5;
	private static final int COLUMN_INDEX_GROSS_PRICE = 6;
	
	private FormToolkit formToolkit;
	private Text netTotal;
	private Text grossTotal;
	private Text taxTotal;
	private Table table;
	private TableViewer tableViewer;
	private Revenue revenue;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RevenueDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		formToolkit = new FormToolkit(parent.getDisplay());
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Section querySection = formToolkit.createSection(container, Section.TITLE_BAR);
		querySection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(querySection);
		querySection.setText(Messages.RevenueDialog_revenueTimeframe);
		
		Composite composite = new Composite(querySection, SWT.NONE);
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);
		querySection.setClient(composite);
		composite.setLayout(new GridLayout(5, false));
		
		Label lblFrom = new Label(composite, SWT.NONE);
		formToolkit.adapt(lblFrom, true, true);
		lblFrom.setText(Messages.RevenueDialog_from);
		
		final DateTime fromDate = new DateTime(composite, SWT.BORDER | SWT.DROP_DOWN);
		formToolkit.adapt(fromDate);
		formToolkit.paintBordersFor(fromDate);
		fromDate.setDay(1);
		fromDate.setMonth(0);
		
		Label lblUntil = new Label(composite, SWT.NONE);
		formToolkit.adapt(lblUntil, true, true);
		lblUntil.setText(Messages.RevenueDialog_until);
		
		final DateTime untilDate = new DateTime(composite, SWT.BORDER | SWT.DROP_DOWN);
		formToolkit.adapt(untilDate);
		formToolkit.paintBordersFor(untilDate);
		untilDate.setDay(31);
		untilDate.setMonth(11);
		
		Button btnSearch = new Button(composite, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateInvoices(fromDate, untilDate);
			}
		});
		btnSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		formToolkit.adapt(btnSearch, true, true);
		btnSearch.setText(Messages.RevenueDialog_search);
		
		Section totalsSection = formToolkit.createSection(container, Section.TITLE_BAR);
		totalsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		formToolkit.paintBordersFor(totalsSection);
		totalsSection.setText(Messages.RevenueDialog_totals);
		
		Composite composite_2 = new Composite(totalsSection, SWT.NONE);
		formToolkit.adapt(composite_2);
		formToolkit.paintBordersFor(composite_2);
		totalsSection.setClient(composite_2);
		composite_2.setLayout(new GridLayout(6, false));
		
		Label netTotalLabel = new Label(composite_2, SWT.NONE);
		netTotalLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		formToolkit.adapt(netTotalLabel, true, true);
		netTotalLabel.setText(Messages.labelTotalNet);
		
		netTotal = new Text(composite_2, SWT.BORDER);
		netTotal.setEnabled(false);
		netTotal.setEditable(false);
		netTotal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(netTotal, true, true);
		
		Label taxTotalLabel = new Label(composite_2, SWT.NONE);
		formToolkit.adapt(taxTotalLabel, true, true);
		taxTotalLabel.setText(Messages.labelTotalTax);
		
		taxTotal = new Text(composite_2, SWT.BORDER);
		taxTotal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		taxTotal.setEnabled(false);
		taxTotal.setEditable(false);
		formToolkit.adapt(taxTotal, true, true);
		
		Label grossTotalLabel = new Label(composite_2, SWT.NONE);
		formToolkit.adapt(grossTotalLabel, true, true);
		grossTotalLabel.setText(Messages.labelTotalGross);
		
		grossTotal = new Text(composite_2, SWT.BORDER);
		grossTotal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grossTotal.setEnabled(false);
		grossTotal.setEditable(false);
		formToolkit.adapt(grossTotal, true, true);
		
		Section invoiceSection = formToolkit.createSection(container, Section.TITLE_BAR);
		invoiceSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(invoiceSection);
		invoiceSection.setText(Messages.RevenueDialog_invoices);
		invoiceSection.setExpanded(true);
		
		Composite tableComposite = new Composite(invoiceSection, SWT.NONE);
		formToolkit.adapt(tableComposite);
		formToolkit.paintBordersFor(tableComposite);
		invoiceSection.setClient(tableComposite);
		TableColumnLayout tcl = new TableColumnLayout();
		tableComposite.setLayout(tcl);
		
		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		formToolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableViewerColumn invoiceNoColumn = new TableViewerColumn(tableViewer, SWT.NONE, COLUMN_INDEX_INVOICE_NUMBER);
		TableColumn tblclmnInvoiceNo = invoiceNoColumn.getColumn();
		tcl.setColumnData(tblclmnInvoiceNo, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnInvoiceNo.setText(Messages.RevenueDialog_invoiceNo);
		
		TableViewerColumn invoiceDateColumn = new TableViewerColumn(tableViewer, SWT.NONE, COLUMN_INDEX_INVOICE_DATE);
		TableColumn tblclmnInvoiceDate = invoiceDateColumn.getColumn();
		tcl.setColumnData(tblclmnInvoiceDate, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnInvoiceDate.setText(Messages.RevenueDialog_invoiceDate);
		
		TableViewerColumn paymentDateColumn = new TableViewerColumn(tableViewer, SWT.NONE, COLUMN_INDEX_PAYMENT_DATE);
		TableColumn tblclmnPaymentDate = paymentDateColumn.getColumn();
		tcl.setColumnData(tblclmnPaymentDate, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnPaymentDate.setText(Messages.RevenueDialog_paymentDate);
		
		TableViewerColumn clientColumn = new TableViewerColumn(tableViewer, SWT.NONE, COLUMN_INDEX_CLIENT);
		TableColumn tblclmnClient = clientColumn.getColumn();
		tcl.setColumnData(tblclmnClient, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnClient.setText(Messages.RevenueDialog_client);
		
		TableViewerColumn netColumn = new TableViewerColumn(tableViewer, SWT.NONE, COLUMN_INDEX_NET_PRICE);
		TableColumn tblclmnNet = netColumn.getColumn();
		tcl.setColumnData(tblclmnNet, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnNet.setText(Messages.RevenueDialog_net);
		tblclmnNet.setAlignment(SWT.RIGHT);
		
		TableViewerColumn taxColumn = new TableViewerColumn(tableViewer, SWT.NONE, COLUMN_INDEX_TAX_AMOUNT);
		TableColumn tblclmnTax = taxColumn.getColumn();
		tcl.setColumnData(tblclmnTax, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnTax.setText(Messages.RevenueDialog_tax);
		tblclmnTax.setAlignment(SWT.RIGHT);
		
		TableViewerColumn grossColumn = new TableViewerColumn(tableViewer, SWT.NONE, COLUMN_INDEX_GROSS_PRICE);
		TableColumn tblclmnGross = grossColumn.getColumn();
		tcl.setColumnData(tblclmnGross, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnGross.setText(Messages.RevenueDialog_gross);
		tblclmnGross.setAlignment(SWT.RIGHT);
		
		tableViewer.setLabelProvider(new InvoiceLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());

		// get an initial revenue for the current year
		updateInvoices(fromDate, untilDate);
		
		return container;
	}

	/**
	 * 
	 * @param from
	 * @param until
	 */
	private void updateInvoices(DateTime from, DateTime until) {
		revenue = ModelHelper.getRevenue(getDateFromWidget(from), getDateFromWidget(until));
		tableViewer.setInput(revenue.getInvoices());
		tableViewer.refresh();
		
		netTotal.setText(FormatUtil.formatCurrency(revenue.getRevenueNet()));
		grossTotal.setText(FormatUtil.formatCurrency(revenue.getRevenueGross()));
		taxTotal.setText(FormatUtil.formatCurrency(revenue.getRevenueTax()));
	}
	
	/**
	 * 
	 * @param dateTime
	 * @return
	 */
	private Date getDateFromWidget(DateTime dateTime) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
		cal.set(Calendar.MONTH, dateTime.getMonth());
		cal.set(Calendar.YEAR, dateTime.getYear());
		return cal.getTime();
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button exportButton = createButton(parent, -1, Messages.RevenueDialog_export, false);
		exportButton.setImage(AccountingUI.getImageDescriptor(Messages.iconsExportToPdf).createImage());
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == -1) {
			ReportGenerationUtil.executeReportGeneration(new RevenueExportHandler(), getShell());
		} else {
			super.buttonPressed(buttonId);
		}
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 500);
	}

	/**
	 *
	 */
	private class InvoiceLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

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
        	if (element == null || !(element instanceof Invoice)) {
        		return Constants.HYPHEN;
        	}
        	
        	final Invoice invoice = (Invoice) element;
        	switch (columnIndex) {
			case COLUMN_INDEX_INVOICE_NUMBER:
				return invoice.getNumber();
			case COLUMN_INDEX_INVOICE_DATE:
				return FormatUtil.formatDate(invoice.getInvoiceDate());
			case COLUMN_INDEX_PAYMENT_DATE:
				return FormatUtil.formatDate(invoice.getPaymentDate());
			case COLUMN_INDEX_CLIENT:
				return invoice.getClient().getName();
			case COLUMN_INDEX_NET_PRICE:
				return FormatUtil.formatCurrency(CalculationUtil.calculateTotalNetPrice(invoice));
			case COLUMN_INDEX_TAX_AMOUNT:
				return FormatUtil.formatCurrency(CalculationUtil.calculateTotalTaxAmount(invoice));
			case COLUMN_INDEX_GROSS_PRICE:
				return FormatUtil.formatCurrency(CalculationUtil.calculateTotalGrossPrice(invoice));
			default:
				return Constants.HYPHEN;
			}
        }
	}
	
	/**
	 *
	 */
	private class RevenueExportHandler implements ReportGenerationHandler {

		/**
         * {@inheritDoc}.
         * @see ReportGenerationHandler#getTargetFileNameSuggestion()
         */
        @Override
        public String getTargetFileNameSuggestion() {
	        return Messages.RevenueDialog_defaultFilename;
        }

		/**
         * {@inheritDoc}.
         * @see ReportGenerationHandler#handleReportGeneration(ReportingService, String, ReportGenerationMonitor)
         */
        @Override
        public void handleReportGeneration(ReportingService reportingService, String targetFileName,
                ReportGenerationMonitor monitor) {
	        reportingService.generateRevenueToPdf(revenue, targetFileName, monitor);
        }
		
	}
}
