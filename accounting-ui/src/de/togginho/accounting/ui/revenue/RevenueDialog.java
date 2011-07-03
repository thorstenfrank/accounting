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
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	private FormToolkit formToolkit;
	private Text netTotal;
	private Text grossTotal;
	private Text taxTotal;
	private Table table;
	private TableViewer tableViewer;
	private InvoiceLabelProvider labelProvider;
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
		labelProvider = new InvoiceLabelProvider();
		
		Composite composite_1 = new Composite(invoiceSection, SWT.NONE);
		formToolkit.adapt(composite_1);
		formToolkit.paintBordersFor(composite_1);
		invoiceSection.setClient(composite_1);
		TableColumnLayout tcl_composite_1 = new TableColumnLayout();
		composite_1.setLayout(tcl_composite_1);
		
		tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		formToolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableViewerColumn invoiceNoColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		invoiceNoColumn.setLabelProvider(labelProvider);
		TableColumn tblclmnInvoiceNo = invoiceNoColumn.getColumn();
		tcl_composite_1.setColumnData(tblclmnInvoiceNo, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnInvoiceNo.setText(Messages.RevenueDialog_invoiceNo);
		
		TableViewerColumn invoiceDateColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		invoiceDateColumn.setLabelProvider(labelProvider);
		TableColumn tblclmnInvoiceDate = invoiceDateColumn.getColumn();
		tcl_composite_1.setColumnData(tblclmnInvoiceDate, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnInvoiceDate.setText(Messages.RevenueDialog_invoiceDate);
		
		TableViewerColumn paymentDateColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		paymentDateColumn.setLabelProvider(labelProvider);
		TableColumn tblclmnPaymentDate = paymentDateColumn.getColumn();
		tcl_composite_1.setColumnData(tblclmnPaymentDate, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnPaymentDate.setText(Messages.RevenueDialog_paymentDate);
		
		TableViewerColumn clientColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		clientColumn.setLabelProvider(labelProvider);
		TableColumn tblclmnClient = clientColumn.getColumn();
		tcl_composite_1.setColumnData(tblclmnClient, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnClient.setText(Messages.RevenueDialog_client);
		
		TableViewerColumn netColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		netColumn.setLabelProvider(labelProvider);
		TableColumn tblclmnNet = netColumn.getColumn();
		tcl_composite_1.setColumnData(tblclmnNet, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnNet.setText(Messages.RevenueDialog_net);
		
		TableViewerColumn taxColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		taxColumn.setLabelProvider(labelProvider);
		TableColumn tblclmnTax = taxColumn.getColumn();
		tcl_composite_1.setColumnData(tblclmnTax, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnTax.setText(Messages.RevenueDialog_tax);
		
		TableViewerColumn grossColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		grossColumn.setLabelProvider(labelProvider);
		TableColumn tblclmnGross = grossColumn.getColumn();
		tcl_composite_1.setColumnData(tblclmnGross, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnGross.setText(Messages.RevenueDialog_gross);
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
	
	private class InvoiceLabelProvider extends CellLabelProvider {

		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.CellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
         */
        @Override
        public void update(ViewerCell cell) {
        	String text = Constants.EMPTY_STRING;
        	Invoice invoice = (Invoice) cell.getElement();
        	switch (cell.getColumnIndex()) {
			case 0:
				text = invoice.getNumber();
				break;
			case 1:
				text = FormatUtil.formatDate(invoice.getInvoiceDate());
				break;
			case 2:
				text = FormatUtil.formatDate(invoice.getPaymentDate());
				break;
			case 3:
				text = invoice.getClient().getName();
				break;
			case 4:
				text = FormatUtil.formatCurrency(CalculationUtil.calculateTotalNetPrice(invoice));
				break;
			case 5:
				text = FormatUtil.formatCurrency(CalculationUtil.calculateTotalTaxAmount(invoice));
				break;
			case 6:
				text = FormatUtil.formatCurrency(CalculationUtil.calculateTotalGrossPrice(invoice));
				break;
			default:
				text = Constants.HYPHEN;
				break;
			}
        	
        	cell.setText(text);
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
