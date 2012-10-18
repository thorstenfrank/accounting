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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.menus.MenuUtil;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.model.PaymentType;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.ui.AbstractAccountingEditor;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelChangeListener;
import de.togginho.accounting.ui.WidgetHelper;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class InvoiceEditor extends AbstractAccountingEditor implements Constants, ModelChangeListener {

	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".InvoiceEditor"; //$NON-NLS-1$
	
	private static final Logger LOG = Logger.getLogger(InvoiceEditor.class);

	// Column indices for the invoice position table
	private static final int COLUMN_INDEX_REVENUE_RELEVANT = 0;
	private static final int COLUMN_INDEX_QUANTITY = 1;
	private static final int COLUMN_INDEX_UNIT = 2;
	private static final int COLUMN_INDEX_DESCRIPTION = 3;
	private static final int COLUMN_INDEX_PRICE_PER_UNIT = 4;
	private static final int COLUMN_INDEX_TAX_RATE = 5;
	private static final int COLUMN_INDEX_NET_PRICE = 6;
	
	// form 
	private FormToolkit toolkit;
	private ScrolledForm form;

	// invoice position table
	private TableViewer invoicePositionViewer;
	
	// tax rate map...
	private Map<String, TaxRate> shortStringToTaxRateMap;
	private String[] taxRateShortNames;

	// totals (read-only)
	private Text totalNet;
	private Text totalTax;
	private Text totalGross;
	private DateTime dueDate;
	
	private boolean invoiceCanBeEdited;
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.rcp.AbstractAccountingEditor#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.rcp.AbstractAccountingEditor#getForm()
	 */
	@Override
	protected Form getForm() {
		return form.getForm();
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		LOG.debug("Creating editor"); //$NON-NLS-1$
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		AccountingUI.addModelChangeListener(this);
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.InvoiceEditor_header);
		form.getBody().setLayout(new GridLayout(2, true));
		
		Invoice invoice = getEditorInput().getInvoice();
		
		this.invoiceCanBeEdited = invoice.canBeEdited();
		
		Set<TaxRate> taxRates = invoice.getUser().getTaxRates();
		if (taxRates != null && !taxRates.isEmpty()) {
			shortStringToTaxRateMap = new HashMap<String, TaxRate>();
			taxRateShortNames = new String[taxRates.size()];
			int index = 0;
			for (TaxRate rate : taxRates) {
				final String shortString = rate.toShortString();
				shortStringToTaxRateMap.put(shortString, rate);
				taxRateShortNames[index] = shortString;
			}
		}
		
		if (invoice.getPaymentTerms() == null) {
			if (invoice.getClient() == null || invoice.getClient().getDefaultPaymentTerms() == null) {
				invoice.setPaymentTerms(PaymentTerms.getDefault());
			} else {
				invoice.setPaymentTerms(invoice.getClient().getDefaultPaymentTerms());
			}
		}
		
		createOverviewSection();
		
		//createClientSection();
		
		createPaymentTermsSection(invoice);
		
		createInvoicePositionsSection();
		
		// calculate the prices for display
		calculateTotals();
		
		updateDueDate();
		
//		CommandContributionItemParameter deleteInvoiceParam = new CommandContributionItemParameter(
//				getSite().getWorkbenchWindow(), 
//				"de.togginho.accounting.ui.invoice.DeleteInvoiceCommandItem", 
//				IDs.CMD_DELETE_INVOICE, 
//				CommandContributionItem.STYLE_PUSH);
//		deleteInvoiceParam.icon = AccountingUI.getImageDescriptor(Messages.iconsDeleteInvoice);
		//deleteInvoiceParam.visibleEnabled = true;
//		CommandContributionItem deleteInvoice = new CommandContributionItem(deleteInvoiceParam);
//		form.getToolBarManager().add(deleteInvoice);
		
		// build the toolbar
		IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
		menuService.populateContributionManager(
				(ToolBarManager) form.getToolBarManager(), MenuUtil.toolbarUri(IDs.EDIT_INVOIDCE_ID));
		
//		form.getToolBarManager().add(new SimpleCommandCallingAction(IDs.CMD_DELETE_INVOICE, Messages.iconsDeleteInvoice));
//		form.getToolBarManager().add(new SimpleCommandCallingAction(IDs.CMD_INVOICE_TO_PDF, Messages.iconsInvoiceToPdf));
//		form.getToolBarManager().add(new SimpleCommandCallingAction(IDs.CMD_SEND_INVOICE, Messages.iconsSendInvoice));
//		form.getToolBarManager().add(new SimpleCommandCallingAction(IDs.CMD_MARK_INVOICE_AS_PAID, Messages.iconsMarkInvoiceAsPaid));
		
//		form.getToolBarManager().add(ActionFactory.SAVE.create(getSite().getWorkbenchWindow()));
		form.getToolBarManager().update(true);
		
		toolkit.decorateFormHeading(form.getForm());
		form.reflow(true);
		
		// if this invoice isn't saved yet, mark myself as dirty
		if (InvoiceState.UNSAVED.equals(invoice.getState())) {
			setIsDirty(true);
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingEditor#dispose()
	 */
	@Override
	public void dispose() {
		AccountingUI.removeModelChangeListener(this);
	    super.dispose();
	}
	
	/**
	 * 
	 */
	private void createOverviewSection() {
		Section overviewSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		overviewSection.setText(Messages.InvoiceEditor_overview);
		
		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
		grabHorizontal.applyTo(overviewSection);
		
		Composite sectionClient = toolkit.createComposite(overviewSection);
		sectionClient.setLayout(new GridLayout(2, false));
		
		Invoice invoice = getEditorInput().getInvoice();
		
		Composite left = toolkit.createComposite(sectionClient);
		left.setLayout(new GridLayout(2, false));
		
		// INVOICE NUMBER
		toolkit.createLabel(left, Messages.labelInvoiceNo);
		Text number = getToolkit().createText(left, invoice.getNumber(), SWT.SINGLE | SWT.BORDER);
		grabHorizontal.applyTo(number);
		number.setEnabled(false);
		number.setEditable(false);

		// CLIENT
		toolkit.createLabel(left, Messages.labelClient);
		final Text clientName = createText(left, invoice.getClient().getName());
		clientName.setEnabled(false);
		clientName.setEditable(false);
		
		// INVOICE DATE
		if (invoice.getInvoiceDate() == null) {
			invoice.setInvoiceDate(new Date());
		}
		toolkit.createLabel(left, Messages.labelInvoiceDate);
		DateTime invoiceDate = new DateTime(left, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		WidgetHelper.dateToWidget(invoice.getInvoiceDate(), invoiceDate);
		invoiceDate.setEnabled(invoiceCanBeEdited);
		invoiceDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Date newInvoiceDate = WidgetHelper.widgetToDate((DateTime) e.getSource());
				Invoice invoice = getEditorInput().getInvoice();								
				if (invoice.getInvoiceDate().compareTo(newInvoiceDate) != 0) {
					invoice.setInvoiceDate(newInvoiceDate);
					updateDueDate();
					setIsDirty(true);
				}
			}
		});
		
		Composite right = toolkit.createComposite(sectionClient);
		right.setLayout(new GridLayout(2, false));
		grabHorizontal.applyTo(right);
				
		toolkit.createLabel(right, Messages.labelTotalNet);
		totalNet = getToolkit().createText(
				right, 
				EMPTY_STRING, 
				SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
		
		grabHorizontal.applyTo(totalNet);
		totalNet.setEnabled(false);
		totalNet.setEditable(false);
		
		toolkit.createLabel(right, Messages.labelTotalTax);
		totalTax = getToolkit().createText(
				right, 
				EMPTY_STRING, 
				SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
		
		grabHorizontal.applyTo(totalTax);
		totalTax.setEnabled(false);
		totalTax.setEditable(false);
		
		toolkit.createLabel(right, Messages.labelTotalGross);
		totalGross = getToolkit().createText(
				right, 
				EMPTY_STRING, 
				SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
		grabHorizontal.applyTo(totalGross);
		totalGross.setEnabled(false);
		
		overviewSection.setClient(sectionClient);
	}
	
	/**
	 * 
	 * @param client
	 */
	private void createPaymentTermsSection(Invoice invoice) {		
		final PaymentTerms paymentTerms = invoice.getPaymentTerms();
		
	    Section paymentTermsSection = toolkit.createSection(getForm().getBody(), Section.TITLE_BAR);
		paymentTermsSection.setText(Messages.labelPaymentTerms);
		
		paymentTermsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Composite sectionClient = toolkit.createComposite(paymentTermsSection);
		sectionClient.setLayout(new GridLayout(2, false));
				
		toolkit.createLabel(sectionClient, Messages.labelPaymentType);
		
		final Combo paymentTypes = new Combo(sectionClient, SWT.READ_ONLY);
		toolkit.adapt(paymentTypes);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(paymentTypes);
		final List<PaymentType> paymentTypeList = new ArrayList<PaymentType>();
		int index = 0;
		for (PaymentType element : PaymentType.values()) {
			paymentTypes.add(element.getTranslatedString());
			paymentTypeList.add(index, element);
			if (element.name().equals(paymentTerms.getPaymentType().name())) {
				paymentTypes.select(index);
			} else {
				index++;
			}
		}
		
		paymentTypes.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	final Combo combo = (Combo) e.getSource();
            	PaymentType newType = paymentTypeList.get(combo.getSelectionIndex());
            	if ( ! newType.name().equals(paymentTerms.getPaymentType().name())) {
                	paymentTerms.setPaymentType(newType);
                	updateDueDate();
                	setIsDirty(true);
            	}
            }
		});
		
		toolkit.createLabel(sectionClient, Messages.labelPaymentTarget);
		final Spinner spinner = new Spinner(sectionClient, SWT.BORDER);
		//toolkit.adapt(spinner);
		spinner.setIncrement(1);
		spinner.setMinimum(0);
		spinner.setSelection(paymentTerms.getFullPaymentTargetInDays());
		
		spinner.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				paymentTerms.setFullPaymentTargetInDays(spinner.getSelection());
				updateDueDate();
				setIsDirty(true);
			}
		});
		
		toolkit.createLabel(sectionClient, Messages.InvoiceView_dueDate);
		dueDate = new DateTime(sectionClient, SWT.DATE | SWT.BORDER);
		dueDate.setEnabled(false);
		if (invoice.getDueDate() != null) {
			WidgetHelper.dateToWidget(invoice.getDueDate(), dueDate);
		}
		
		
		paymentTermsSection.setClient(sectionClient);
	}
	
	/**
	 * Invoice position table
	 */
	private void createInvoicePositionsSection() {
		Invoice invoice = getEditorInput().getInvoice();
		
		if (invoice.getInvoicePositions() == null) {
			invoice.setInvoicePositions(new ArrayList<InvoicePosition>());
		}		
		
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText(Messages.InvoiceEditor_details);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(section);
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		
		Composite tableComposite = toolkit.createComposite(sectionClient);
		WidgetHelper.grabBoth(tableComposite);
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableComposite.setLayout(tableLayout);
		
		invoicePositionViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);
		final Table table = invoicePositionViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final int defaultWidth = 13;
		
		// REVENUE RELEVANT
		TableViewerColumn revenueRelevantCol = 
				new TableViewerColumn(invoicePositionViewer, SWT.NONE, COLUMN_INDEX_REVENUE_RELEVANT);
		revenueRelevantCol.getColumn().setText(Messages.labelRevenueRelevant);
		revenueRelevantCol.getColumn().setAlignment(SWT.CENTER);
		tableLayout.setColumnData(revenueRelevantCol.getColumn(), new ColumnWeightData(11));
		
		// QUANTITY
		TableViewerColumn quantityCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE, COLUMN_INDEX_QUANTITY);
		quantityCol.getColumn().setText(Messages.labelQuantity);
		quantityCol.getColumn().setAlignment(SWT.CENTER);
		tableLayout.setColumnData(quantityCol.getColumn(), new ColumnWeightData(defaultWidth));

		// UNIT
		TableViewerColumn unitCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE, COLUMN_INDEX_UNIT);
		unitCol.getColumn().setText(Messages.labelUnit);
		unitCol.getColumn().setAlignment(SWT.CENTER);
		tableLayout.setColumnData(unitCol.getColumn(), new ColumnWeightData(defaultWidth));
		
		// DESCRIPTION
		TableViewerColumn descCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE, COLUMN_INDEX_DESCRIPTION);
		descCol.getColumn().setText(Messages.labelDescription);
		descCol.getColumn().setAlignment(SWT.CENTER);
		tableLayout.setColumnData(descCol.getColumn(), new ColumnWeightData(24));

		// PRICE PER UNIT
		TableViewerColumn priceCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE, COLUMN_INDEX_PRICE_PER_UNIT);
		priceCol.getColumn().setText(Messages.labelPricePerUnit);
		priceCol.getColumn().setAlignment(SWT.RIGHT);
		tableLayout.setColumnData(priceCol.getColumn(), new ColumnWeightData(defaultWidth));
		
		// TAX RATE
		TableViewerColumn taxRateCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE, COLUMN_INDEX_TAX_RATE);
		taxRateCol.getColumn().setText(Messages.labelTaxRate);
		taxRateCol.getColumn().setAlignment(SWT.CENTER);
		tableLayout.setColumnData(taxRateCol.getColumn(), new ColumnWeightData(defaultWidth));
		
		// NET PRICE
		TableViewerColumn netCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE, COLUMN_INDEX_NET_PRICE);
		netCol.getColumn().setText(Messages.labelSubtotal);
		netCol.getColumn().setAlignment(SWT.RIGHT);
		tableLayout.setColumnData(netCol.getColumn(), new ColumnWeightData(defaultWidth));
				
		invoicePositionViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				InvoicePosition pos = (InvoicePosition) selection.getFirstElement();
				InvoicePositionWizard wizard = new InvoicePositionWizard(getEditorInput().getInvoice(), pos);
				
				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				if (dialog.open() == WizardDialog.OK) {
					refreshInvoicePositionsAndFireDirty();
				}
			}
		});
		
		invoicePositionViewer.setLabelProvider(new InvoicePositionCellLabelProvider());
		invoicePositionViewer.setContentProvider(ArrayContentProvider.getInstance());
		invoicePositionViewer.setInput(invoice.getInvoicePositions());
				
		// buttons for adding / removing invoice positions
		Composite buttons = toolkit.createComposite(sectionClient);
		buttons.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		buttons.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button add = toolkit.createButton(buttons, Messages.labelAdd, SWT.PUSH);
		add.setEnabled(invoiceCanBeEdited);
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InvoicePositionWizard wizard = new InvoicePositionWizard(getEditorInput().getInvoice());
				
				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				if (dialog.open() == WizardDialog.OK) {
					refreshInvoicePositionsAndFireDirty();
				}
			}
		});
		
		Button remove = toolkit.createButton(buttons, Messages.labelRemove, SWT.PUSH);
		remove.setEnabled(invoiceCanBeEdited);
		remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) invoicePositionViewer.getSelection();
				if (!selection.isEmpty()) {
					InvoicePosition pos = (InvoicePosition) selection.getFirstElement();
					LOG.info("Removing invoice position " + pos.getDescription()); //$NON-NLS-1$
					getEditorInput().getInvoice().getInvoicePositions().remove(pos);
					refreshInvoicePositionsAndFireDirty();
				}
			}
		});
		
		section.setClient(sectionClient);
		
		Button up = toolkit.createButton(buttons, Messages.labelUp, SWT.PUSH);
		up.setEnabled(invoiceCanBeEdited);
		up.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) invoicePositionViewer.getSelection();
				if (!selection.isEmpty()) {
					InvoicePosition pos = (InvoicePosition) selection.getFirstElement();
					LOG.info("Moving invoice position up" + pos.getDescription()); //$NON-NLS-1$
					List<InvoicePosition> ips = getEditorInput().getInvoice().getInvoicePositions();
					int index = ips.indexOf(pos);
					InvoicePosition ip = ips.remove(index);
					ips.add(index - 1, ip);
					refreshInvoicePositionsAndFireDirty();
				}				
			}
		});
		
		Button down = toolkit.createButton(buttons, Messages.labelDown, SWT.PUSH);
		down.setEnabled(invoiceCanBeEdited);
		down.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) invoicePositionViewer.getSelection();
				if (!selection.isEmpty()) {
					InvoicePosition pos = (InvoicePosition) selection.getFirstElement();
					LOG.info("Moving invoice position up" + pos.getDescription()); //$NON-NLS-1$
					List<InvoicePosition> ips = getEditorInput().getInvoice().getInvoicePositions();
					int index = ips.indexOf(pos);
					InvoicePosition ip = ips.remove(index);
					ips.add(index + 1, ip);
					refreshInvoicePositionsAndFireDirty();
				}				
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		Invoice invoice = getEditorInput().getInvoice();
		
		try {
			AccountingUI.getAccountingService().saveInvoice(invoice);
			setIsDirty(false);
		} catch (Exception e) {
			showError(e);
		}
	}
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public InvoiceEditorInput getEditorInput() {
		return (InvoiceEditorInput) super.getEditorInput();
	}
	
	/**
	 * 
	 * {@inheritDoc}.
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	
    public void propertyChange(PropertyChangeEvent evt) {
		refreshInvoicePositions();
    }

	/**
     * @see de.togginho.accounting.ui.ModelChangeListener#modelChanged()
     */
    @Override
    public void modelChanged() {
	    refreshInvoicePositions();
    }

	/**
	 * 
	 */
	private void refreshInvoicePositions() {
		invoicePositionViewer.refresh();
		calculateTotals();
	}
	
	/**
	 * 
	 */
	private void refreshInvoicePositionsAndFireDirty() {
		refreshInvoicePositions();
		setIsDirty(true);		
	}
	
	/**
	 * Calculates the total values for this editor's invoice and sets the text on their respective widgets.
	 */
	private void calculateTotals() {
		Invoice invoice = getEditorInput().getInvoice();
		final Price total = CalculationUtil.calculateTotalPrice(invoice);
		totalNet.setText(FormatUtil.formatCurrency(total.getNet()));
		totalTax.setText(FormatUtil.formatCurrency(total.getTax()));
		totalGross.setText(FormatUtil.formatCurrency(total.getGross()));
	}
	
	/**
	 * 
	 */
	private void updateDueDate() {
		Invoice invoice = getEditorInput().getInvoice();
		WidgetHelper.dateToWidget(invoice.getDueDate(), dueDate);
	}
	
	/**
	 *
	 */
	private class InvoicePositionCellLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
		
        /**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
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
        	if (element == null) {
        		return HYPHEN;
        	}
        	InvoicePosition pos = (InvoicePosition) element;
        	switch (columnIndex) {
			case COLUMN_INDEX_REVENUE_RELEVANT:
				return pos.isRevenueRelevant() ? Messages.labelYes : Messages.labelNo;
			case COLUMN_INDEX_QUANTITY:
				return FormatUtil.formatDecimalValue(pos.getQuantity());
			case COLUMN_INDEX_UNIT:
				return pos.getUnit();
			case COLUMN_INDEX_DESCRIPTION:
				return pos.getDescription();
			case COLUMN_INDEX_PRICE_PER_UNIT:
				 return FormatUtil.formatCurrency(pos.getPricePerUnit());
			case COLUMN_INDEX_TAX_RATE:
				 return pos.isTaxApplicable() ? pos.getTaxRate().toShortString() : HYPHEN;
			case COLUMN_INDEX_NET_PRICE:
				 return FormatUtil.formatCurrency(CalculationUtil.calculateNetPrice(pos));
			default:
				break;
			}
        	
	        return HYPHEN;
        }
	}
}
