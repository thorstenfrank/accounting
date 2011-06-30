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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.menus.MenuUtil;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.ui.AbstractAccountingEditor;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class InvoiceEditor extends AbstractAccountingEditor implements Constants, PropertyChangeListener {

	private static final Logger LOG = Logger.getLogger(InvoiceEditor.class);
	
	private FormToolkit toolkit;
	
	private ScrolledForm form;

	private TableViewer invoicePositionViewer;

	private Map<String, TaxRate> shortStringToTaxRateMap;
	private String[] taxRateShortNames;

	private Text totalNet;

	private Text totalTax;

	private Text totalGross;
	
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
		
		ModelHelper.addPropertyChangeListener(ModelHelper.MODEL_INVOICES, this);
		
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
		
		createOverviewSection();
		
		createClientSection();
		
		createInvoicePositionsSection();
		
		// calculate the prices for display
		calculateTotals();
		
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
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingEditor#dispose()
	 */
	@Override
	public void dispose() {
		ModelHelper.removePropertyChangeListener(ModelHelper.MODEL_INVOICES, this);
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
		//left.setLayoutData(WidgetHelper.FILL_GRID_DATA);
		
		toolkit.createLabel(left, Messages.labelInvoiceNo);
		Text number = getToolkit().createText(left, invoice.getNumber(), SWT.SINGLE | SWT.BORDER);
		grabHorizontal.applyTo(number);
		number.setEnabled(false);
		number.setEditable(false);

		toolkit.createLabel(left, Messages.labelInvoiceDate);		
		DateTime invoiceDate = new DateTime(left, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		
		if (invoice.getInvoiceDate() == null) {
			invoice.setInvoiceDate(new Date());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(invoice.getInvoiceDate());
		invoiceDate.setDay(cal.get(Calendar.DAY_OF_MONTH));
		invoiceDate.setMonth(cal.get(Calendar.MONTH));
		invoiceDate.setYear(cal.get(Calendar.YEAR));

		invoiceDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DateTime dateTime = (DateTime) e.getSource();
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
				cal.set(Calendar.MONTH, dateTime.getMonth());
				cal.set(Calendar.YEAR, dateTime.getYear());
				
				Invoice invoice = getEditorInput().getInvoice();
				Date currentInvoiceDate = invoice.getInvoiceDate();
				
				invoice.setInvoiceDate(cal.getTime());
				
				if (invoice.getInvoiceDate().compareTo(currentInvoiceDate) != 0) {
					setIsDirty(true);
					firePropertyChange(IEditorPart.PROP_DIRTY);
				}
			}
			
		});
		invoiceDate.setEnabled(invoiceCanBeEdited);

		if (invoice.getDueDate() == null) {
			// add 30 days per default
			cal.add(Calendar.DAY_OF_MONTH, 30);
			invoice.setDueDate(cal.getTime());
		} else {
			cal.setTime(invoice.getDueDate());
		}
		toolkit.createLabel(left, Messages.labelInvoiceDueDate);
		
		DateTime dueDate = new DateTime(left, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		dueDate.setDay(cal.get(Calendar.DAY_OF_MONTH));
		dueDate.setMonth(cal.get(Calendar.MONTH));
		dueDate.setYear(cal.get(Calendar.YEAR));
		
		dueDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DateTime dateTime = (DateTime) e.getSource();
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
				cal.set(Calendar.MONTH, dateTime.getMonth());
				cal.set(Calendar.YEAR, dateTime.getYear());
				
				Invoice invoice = getEditorInput().getInvoice();
				Date currentDueDate = invoice.getInvoiceDate();
				
				invoice.setDueDate(cal.getTime());
				
				if (invoice.getDueDate().compareTo(currentDueDate) != 0) {
					setIsDirty(true);
					firePropertyChange(IEditorPart.PROP_DIRTY);
				}
			}
			
		});
		dueDate.setEnabled(invoiceCanBeEdited);
		
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
				FormatUtil.formatCurrency(CalculationUtil.calculateTotalNetPrice(invoice)), 
				SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
		
		grabHorizontal.applyTo(totalTax);
		totalTax.setEnabled(false);
		totalTax.setEditable(false);
		
		toolkit.createLabel(right, Messages.labelTotalGross);
		totalGross = getToolkit().createText(
				right, 
				FormatUtil.formatCurrency(CalculationUtil.calculateTotalGrossPrice(invoice)), 
				SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
		grabHorizontal.applyTo(totalGross);
		totalGross.setEnabled(false);
		
		overviewSection.setClient(sectionClient);
	}
	
	/**
	 * 
	 */
	private void createClientSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText(Messages.labelClient);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		
		Invoice invoice = getEditorInput().getInvoice();
		Client theClient = invoice.getClient();
		
		toolkit.createLabel(sectionClient, Messages.labelClientName);
		Combo clientCombo = new Combo(sectionClient, SWT.READ_ONLY);
		clientCombo.setEnabled(invoiceCanBeEdited);
		
		GridDataFactory.fillDefaults().grab(true, false).applyTo(clientCombo);

		Address address = theClient != null ? theClient.getAddress() : null;
		
		toolkit.createLabel(sectionClient, Messages.labelStreet);
		final Text street = createText(sectionClient, address != null ? address.getStreet() : EMPTY_STRING);
		street.setEditable(false);
		street.setEnabled(false);
		
		toolkit.createLabel(sectionClient, Messages.labelPostalCode);
		final Text postCode = createText(sectionClient, address != null ? address.getPostalCode() : EMPTY_STRING);
		postCode.setEditable(false);
		postCode.setEnabled(false);
		
		toolkit.createLabel(sectionClient, Messages.labelCity);
		final Text city = createText(sectionClient, address != null ? address.getCity() : EMPTY_STRING);
		city.setEditable(false);
		city.setEnabled(false);
		
		int currentClientIndex = 0;
		int counter = 1;
		clientCombo.add(EMPTY_STRING);
		for (Client client : invoice.getUser().getClients()) {
			clientCombo.add(client.getName());
			if (theClient != null && theClient.equals(client)) {
				currentClientIndex = counter;
			}
			counter++;
		}
		
		if (currentClientIndex >= 0) {
			clientCombo.select(currentClientIndex);
		}
		
		clientCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo combo = (Combo) e.getSource();
				final String selected = combo.getItem(combo.getSelectionIndex());
				Invoice invoice = getEditorInput().getInvoice();
				Client newClient = null;
				boolean changed = false;
				if (selected.isEmpty() && invoice.getClient() != null) {
					invoice.setClient(null);
					changed = true;
				} else {
					Client currentClient = invoice.getClient();
					newClient = invoice.getUser().getClientByName(selected);
					if (currentClient == null || currentClient.equals(newClient) == false) {
						invoice.setClient(newClient);
						changed = true;
					}
				}
				if (changed) {
					if (newClient != null && newClient.getAddress() != null) {
						Address address = newClient.getAddress();
						street.setText(address.getStreet() != null ? address.getStreet() : EMPTY_STRING);
						postCode.setText(address.getPostalCode() != null ? address.getPostalCode() : EMPTY_STRING);
						city.setText(address.getCity() != null ? address.getCity() : EMPTY_STRING);						
					} else {
						street.setText(EMPTY_STRING);
						postCode.setText(EMPTY_STRING);
						city.setText(EMPTY_STRING);
					}

					setIsDirty(true);
					firePropertyChange(IEditorPart.PROP_DIRTY);
				}
			}
			
		});
		
		section.setClient(sectionClient);
		
	}
	
	private void createInvoicePositionsSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText(Messages.InvoiceEditor_details);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(section);
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		
		Composite tableComposite = toolkit.createComposite(sectionClient);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableComposite.setLayout(tableLayout);
		
		invoicePositionViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);
		invoicePositionViewer.setContentProvider(ArrayContentProvider.getInstance());
		Table table = invoicePositionViewer.getTable();
		table.setHeaderVisible(true);
				
		TableViewerColumn quantityCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE);
		quantityCol.getColumn().setText(Messages.labelQuantity);
		tableLayout.setColumnData(quantityCol.getColumn(), new ColumnWeightData(15));
		quantityCol.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				InvoicePosition pos = (InvoicePosition) cell.getElement();
				cell.setText(FormatUtil.formatDecimalValue(pos.getQuantity()));
			}
		});

		TableViewerColumn unitCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE);
		unitCol.getColumn().setText(Messages.labelUnit);
		tableLayout.setColumnData(unitCol.getColumn(), new ColumnWeightData(15));
		unitCol.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				InvoicePosition pos = (InvoicePosition) cell.getElement();
				cell.setText(pos.getUnit());
			}
		});
//		unitCol.setEditingSupport(new EditingSupport(invoicePositionViewer) {
//			@Override
//			protected void setValue(Object element, Object value) {
//				String theValue = null;
//				if (value != null) {
//					theValue = value.toString();
//					if (theValue.isEmpty())
//						theValue = null;
//				}
//				
//				((InvoicePosition) element).setUnit(theValue);
//				invoicePositionViewer.refresh();
//			}
//			
//			@Override
//			protected Object getValue(Object element) {
//				return ((InvoicePosition) element).getUnit();
//			}
//			
//			@Override
//			protected CellEditor getCellEditor(Object element) {
//				return new TextCellEditor(invoicePositionViewer.getTable());
//			}
//			
//			@Override
//			protected boolean canEdit(Object element) {
//				return true;
//			}
//		});
		
		TableViewerColumn descCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE);
		descCol.getColumn().setText(Messages.labelDescription);
		tableLayout.setColumnData(descCol.getColumn(), new ColumnWeightData(25));
		descCol.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				InvoicePosition pos = (InvoicePosition) cell.getElement();
				cell.setText(pos.getDescription());
			}
		});
//		descCol.setEditingSupport(new EditingSupport(invoicePositionViewer) {
//			
//			@Override
//			protected void setValue(Object element, Object value) {
//				((InvoicePosition) element).setDescription(String.valueOf(value));
//			}
//			
//			@Override
//			protected Object getValue(Object element) {
//				return ((InvoicePosition) element).getDescription();
//			}
//			
//			@Override
//			protected CellEditor getCellEditor(Object element) {
//				return new TextCellEditor(invoicePositionViewer.getTable(), SWT.MULTI);
//			}
//			
//			@Override
//			protected boolean canEdit(Object element) {
//				return true;
//			}
//		});
		
		TableViewerColumn priceCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE);
		priceCol.getColumn().setText(Messages.labelPricePerUnit);
		tableLayout.setColumnData(priceCol.getColumn(), new ColumnWeightData(15));
		priceCol.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				InvoicePosition pos = (InvoicePosition) cell.getElement();
				cell.setText(FormatUtil.formatCurrency(pos.getPricePerUnit()));
			}
		});		
		
		TableViewerColumn taxRateCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE);
		taxRateCol.getColumn().setText(Messages.labelTaxRate);
		tableLayout.setColumnData(taxRateCol.getColumn(), new ColumnWeightData(15));
		taxRateCol.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				InvoicePosition pos = (InvoicePosition) cell.getElement();
				if (pos.isTaxApplicable()) {
					cell.setText(pos.getTaxRate().toShortString());
				} else {
					cell.setText(HYPHEN);
				}
//				InvoicePosition pos = (InvoicePosition) cell.getElement();
//				if (pos.isTaxApplicable()) {
//					cell.setText(FormatUtil.formatPercentValue(ModelHelper.getLocale(), pos.getTaxRate().getRate()));
//				} 
//				else {
//					cell.setText("-");
//				}
				
			}
		});
//		taxRateCol.setEditingSupport(new EditingSupport(invoicePositionViewer) {
//			
//			@Override
//			protected void setValue(Object element, Object value) {
//				System.out.println("setValue: " + element.toString() + " | " + value.toString());
//			}
//			
//			@Override
//			protected Object getValue(Object element) {
//				System.out.println("getValue: " + element.toString());
//				return new Integer(1);
//			}
//			
//			@Override
//			protected CellEditor getCellEditor(Object element) {
//				return new ComboBoxCellEditor(invoicePositionViewer.getTable(), taxRateShortNames, SWT.READ_ONLY);
//			}
//			
//			@Override
//			protected boolean canEdit(Object element) {
//				return shortStringToTaxRateMap != null;
//			}
//		});
		
		TableViewerColumn netCol = new TableViewerColumn(invoicePositionViewer, SWT.NONE);
		netCol.getColumn().setText(Messages.labelSubtotal);
		tableLayout.setColumnData(netCol.getColumn(), new ColumnWeightData(15));
		netCol.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				InvoicePosition pos = (InvoicePosition) cell.getElement();
				cell.setText(FormatUtil.formatCurrency(CalculationUtil.calculateNetPrice(pos)));
			}
		});
		
		Invoice invoice = getEditorInput().getInvoice();
		
		if (invoice.getInvoicePositions() == null) {
			invoice.setInvoicePositions(new ArrayList<InvoicePosition>());
		}
		
		invoicePositionViewer.setInput(invoice.getInvoicePositions());
		
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
	}
	

	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		Invoice invoice = getEditorInput().getInvoice();
		
		ModelHelper.saveInvoice(invoice);
		
		setIsDirty(false);
		firePropertyChange(PROP_DIRTY);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		form.getToolBarManager().update(true);
		form.getBody().setFocus();
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
	@Override
    public void propertyChange(PropertyChangeEvent evt) {
		refreshInvoicePositions();
		getForm().getToolBarManager().markDirty();
		getForm().getToolBarManager().update(true);
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
		firePropertyChange(IEditorPart.PROP_DIRTY);		
	}
	
	/**
	 * Calculates the total values for this editor's invoice and sets the text on their respective widgets.
	 */
	private void calculateTotals() {
		Invoice invoice = getEditorInput().getInvoice();
		totalNet.setText(FormatUtil.formatCurrency(CalculationUtil.calculateTotalNetPrice(invoice)));
		totalTax.setText(FormatUtil.formatCurrency(CalculationUtil.calculateTotalTaxAmount(invoice)));
		totalGross.setText(FormatUtil.formatCurrency(CalculationUtil.calculateTotalGrossPrice(invoice)));
	}
}
