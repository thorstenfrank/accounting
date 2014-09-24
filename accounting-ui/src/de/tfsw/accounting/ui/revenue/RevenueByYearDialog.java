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
package de.tfsw.accounting.ui.revenue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.dialogs.TrayDialog;
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

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.model.Revenue;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.util.FormatUtil;

/**
 * 
 * @author thorsten
 *
 */
public class RevenueByYearDialog extends TrayDialog {
	
	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".RevenueByYearDialog"; //$NON-NLS-1$
	
	private static final int COLUMN_INDEX_YEAR = 0;
	private static final int COLUMN_INDEX_INVOICES = 1;
	private static final int COLUMN_INDEX_NET = 2;
	private static final int COLUMN_INDEX_TAX = 3;
	private static final int COLUMN_INDEX_GROSS = 4;
	
	private FormToolkit formToolkit;
	private Text numberOfYears;
	private Text netTotal;
	private Text grossTotal;
	private Text taxTotal;
	private TableViewer tableViewer;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RevenueByYearDialog(Shell parentShell) {
		super(parentShell);
	}
		
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.RevenueByYearDialog_title);
		getShell().setImage(AccountingUI.getImageDescriptor(Messages.iconsRevenue).createImage());
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		
		formToolkit = new FormToolkit(parent.getDisplay());
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
				
		createTotalsSection(container);
		
		createRevenueByYearSection(container);
		
		List<Revenue> all = AccountingUI.getAccountingService().getRevenueByYears();
		List<RevenueByYearWrapper> wrappers = new ArrayList<RevenueByYearWrapper>();
		Calendar cal = Calendar.getInstance();
		int years = 0;
		Price grandTotal = new Price();
		
		for (Revenue revenue : all) {
			cal.setTime(revenue.getTimeFrame().getFrom());
			RevenueByYearWrapper wrapper = new RevenueByYearWrapper(cal.get(Calendar.YEAR), revenue);
			wrappers.add(wrapper);
			years++;
			grandTotal.add(revenue.getTotalRevenue());
		}
		
		numberOfYears.setText(Integer.toString(years));
		netTotal.setText(FormatUtil.formatCurrency(grandTotal.getNet()));
		taxTotal.setText(FormatUtil.formatCurrency(grandTotal.getTax()));
		grossTotal.setText(FormatUtil.formatCurrency(grandTotal.getGross()));
		tableViewer.setInput(wrappers);
		
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
		sectionClient.setLayout(new GridLayout(2, false));
		
		formToolkit.createLabel(sectionClient, Messages.RevenueByYearDialog_years);
		numberOfYears = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		numberOfYears.setEnabled(false);
		numberOfYears.setEditable(false);
		
		formToolkit.createLabel(sectionClient, Messages.labelTotalNet);
		netTotal = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		netTotal.setEnabled(false);
		netTotal.setEditable(false);
		netTotal.setOrientation(SWT.RIGHT_TO_LEFT);
		
		formToolkit.createLabel(sectionClient, Messages.labelTotalTax);
		taxTotal = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		taxTotal.setEnabled(false);
		taxTotal.setEditable(false);
		taxTotal.setOrientation(SWT.RIGHT_TO_LEFT);
		
		formToolkit.createLabel(sectionClient, Messages.labelTotalGross);
		grossTotal = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		grossTotal.setEnabled(false);
		grossTotal.setEditable(false);
		grossTotal.setOrientation(SWT.RIGHT_TO_LEFT);
	}
	
	/**
	 * 
	 * @param parent
	 */
	private void createRevenueByYearSection(Composite parent) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(section);
		section.setText(Messages.RevenueByYearDialog_details);
		section.setExpanded(true);
		
		Composite tableComposite = new Composite(section, SWT.NONE);
		formToolkit.adapt(tableComposite);
		formToolkit.paintBordersFor(tableComposite);
		section.setClient(tableComposite);
		TableColumnLayout tcl = new TableColumnLayout();
		tableComposite.setLayout(tcl);
		
		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		final Table table = tableViewer.getTable();
		formToolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		createColumn(COLUMN_INDEX_YEAR, Messages.labelYear, SWT.CENTER, tcl);
		createColumn(COLUMN_INDEX_INVOICES, Messages.RevenueDialog_invoices, SWT.CENTER, tcl);
		createColumn(COLUMN_INDEX_NET, Messages.labelNet, SWT.RIGHT, tcl);
		createColumn(COLUMN_INDEX_TAX, Messages.labelTaxes, SWT.RIGHT, tcl);
		createColumn(COLUMN_INDEX_GROSS, Messages.labelGross, SWT.RIGHT, tcl);
		
		tableViewer.setLabelProvider(new RevenueByYearLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
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
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 500);
	}
	
	private class RevenueByYearLabelProvider  extends BaseLabelProvider implements ITableLabelProvider {
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
	        return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
        	RevenueByYearWrapper wrapper = (RevenueByYearWrapper) element;
        	switch (columnIndex) {
			case COLUMN_INDEX_YEAR:
				return wrapper.getYear();
			case COLUMN_INDEX_INVOICES:
				return wrapper.getNumberOfInvoices();
			case COLUMN_INDEX_NET:
				return wrapper.getNetTotal();
			case COLUMN_INDEX_TAX:
				return wrapper.getTaxTotal();
			case COLUMN_INDEX_GROSS:
				return wrapper.getGrossTotal();
			}
	        return null;
        }
		
	};
}
