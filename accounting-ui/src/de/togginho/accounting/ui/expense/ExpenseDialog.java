/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.expense;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.reporting.ReportGenerationMonitor;
import de.togginho.accounting.reporting.ReportingService;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.reports.AbstractReportDialog;
import de.togginho.accounting.ui.reports.ReportGenerationHandler;
import de.togginho.accounting.ui.reports.ReportGenerationUtil;
import de.togginho.accounting.util.FormatUtil;
import de.togginho.accounting.util.TimeFrame;
import de.togginho.accounting.util.TimeFrameType;

/**
 * @author thorsten
 *
 */
public class ExpenseDialog extends AbstractReportDialog {

	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".ExpenseDialog"; //$NON-NLS-1$

	private FormToolkit formToolkit;
	private Text netTotal;
	private Text grossTotal;
	private Text taxTotal;
	private TableViewer tableViewer;
	private ExpenseCollection expenseCollection;
	
	private ExpenseType selectedType;
	
	/**
	 * 
	 * @param shell
	 */
	public ExpenseDialog(Shell shell) {
		super(shell);
	}
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.labelExpenses);
		getShell().setImage(AccountingUI.getImageDescriptor(Messages.iconsExpenses).createImage());
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		
		formToolkit = new FormToolkit(parent.getDisplay());
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, false));
		
		createQuerySection(composite);
		
		createTotalsSection(composite);
		
		createExpensesTableSection(composite);
		
		updateModel();
		
		return composite;
	}
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see AbstractReportDialog#needsCustomQueryParameters()
	 */
	@Override
	protected boolean needsCustomQueryParameters() {
	    return true;
	}
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see AbstractReportDialog#addCustomQueryParameters(Composite)
	 */
	@Override
	protected void addCustomQueryParameters(Composite customParams) {
		customParams.setLayout(new GridLayout(4, false));
		
		final Button all = formToolkit.createButton(customParams, Messages.labelAll, SWT.RADIO);
		all.setSelection(true);
		all.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedType = null;
				updateModel();
			}
		});
		
		for (final ExpenseType type : ExpenseType.values()) {
			final Button button = formToolkit.createButton(customParams, type.getTranslatedString(), SWT.RADIO);
			button.setSelection(false);
			button.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                	if (button.getSelection()) {
                		selectedType = type;
                	}                	
                	updateModel();
                }
			});
		}
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
	 */
	private void createExpensesTableSection(Composite parent) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(section);
		//formToolkit.paintBordersFor(invoiceSection);
		section.setText(Messages.labelExpenses);
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
		ExpenseTableSorter sorter = new ExpenseTableSorter();
		
		sorter.addSortingSupport(
				tableViewer, 
				createColumn(ExpenseTableSorter.COL_INDEX_DATE, Messages.labelDate, SWT.CENTER, tcl).getColumn(), 
				ExpenseTableSorter.COL_INDEX_DATE);

		sorter.addSortingSupport(
				tableViewer, 
				createColumn(ExpenseTableSorter.COL_INDEX_DESC, Messages.labelDescription, SWT.LEFT, tcl).getColumn(), 
				ExpenseTableSorter.COL_INDEX_DESC);
		
		sorter.addSortingSupport(
				tableViewer, 
				createColumn(ExpenseTableSorter.COL_INDEX_CATEGORY, Messages.labelCategory, SWT.CENTER, tcl).getColumn(), 
				ExpenseTableSorter.COL_INDEX_CATEGORY);
		
		sorter.addSortingSupport(
				tableViewer, 
				createColumn(ExpenseTableSorter.COL_INDEX_NET, Messages.labelNet, SWT.RIGHT, tcl).getColumn(), 
				ExpenseTableSorter.COL_INDEX_NET);

		sorter.addSortingSupport(
				tableViewer, 
				createColumn(ExpenseTableSorter.COL_INDEX_TAX, Messages.labelTaxes, SWT.RIGHT, tcl).getColumn(), 
				ExpenseTableSorter.COL_INDEX_TAX);
		
		sorter.addSortingSupport(
				tableViewer, 
				createColumn(ExpenseTableSorter.COL_INDEX_GROSS, Messages.labelGross, SWT.RIGHT, tcl).getColumn(), 
				ExpenseTableSorter.COL_INDEX_GROSS);
		
		tableViewer.setLabelProvider(new ExpenseTableLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		sorter.setSortColumnIndex(ExpenseTableSorter.COL_INDEX_DATE);
		tableViewer.setComparator(sorter);
		table.setSortColumn(table.getColumn(ExpenseTableSorter.COL_INDEX_DATE));
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
		layout.setColumnData(column, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		
		return viewerColumn;
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
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
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#getDefaultTimeFrameType()
	 */
	@Override
	protected TimeFrameType getDefaultTimeFrameType() {
		return TimeFrameType.CURRENT_YEAR;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#updateModel(de.togginho.accounting.util.TimeFrame)
	 */
	@Override
	protected void updateModel(TimeFrame timeFrame) {
		if (selectedType == null) {
			expenseCollection = AccountingUI.getAccountingService().findExpenses(timeFrame);
		} else {
			expenseCollection = AccountingUI.getAccountingService().findExpenses(timeFrame, selectedType);
		}
		
		List<ExpenseWrapper> wrappers = new ArrayList<ExpenseWrapper>();
		for (Expense expense : expenseCollection.getExpenses()) {
			wrappers.add(new ExpenseWrapper(expense));
		}
		
		tableViewer.setInput(wrappers);
		tableViewer.refresh();
		
		final Price total = expenseCollection.getTotalCost();
		netTotal.setText(FormatUtil.formatCurrency(total.getNet()));
		grossTotal.setText(FormatUtil.formatCurrency(total.getGross()));
		taxTotal.setText(FormatUtil.formatCurrency(total.getTax()));
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#handleExport()
	 */
	@Override
	protected void handleExport() {
		ReportGenerationUtil.executeReportGeneration(new ExpensesExportHandler(), getShell());
	}
	
	/**
	 *
	 */
	class ExpensesExportHandler implements ReportGenerationHandler {

		/**
		 * {@inheritDoc}
		 * @see de.togginho.accounting.ui.reports.ReportGenerationHandler#getTargetFileNameSuggestion()
		 */
		@Override
		public String getTargetFileNameSuggestion() {
			if (selectedType == null) {
				return ReportGenerationUtil.appendTimeFrameToFileNameSuggestion(
						Messages.labelExpenses, expenseCollection.getTimeFrame());
			} else {
				return ReportGenerationUtil.appendTimeFrameToFileNameSuggestion(
						selectedType.getTranslatedString(), expenseCollection.getTimeFrame());
			}
		}

		/**
		 * {@inheritDoc}
		 * @see de.togginho.accounting.ui.reports.ReportGenerationHandler#handleReportGeneration(de.togginho.accounting.reporting.ReportingService, java.lang.String, de.togginho.accounting.reporting.ReportGenerationMonitor)
		 */
		@Override
		public void handleReportGeneration(ReportingService reportingService, String targetFileName, ReportGenerationMonitor monitor) {
			reportingService.generateExpensesToPdf(expenseCollection, targetFileName, monitor);
		}
		
	}
}
