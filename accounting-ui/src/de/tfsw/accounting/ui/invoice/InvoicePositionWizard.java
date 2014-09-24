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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoicePosition;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.conversion.BigDecimalToStringConverter;
import de.tfsw.accounting.ui.conversion.StringToBigDecimalConverter;
import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
class InvoicePositionWizard extends Wizard implements Constants {
	
	private boolean isNewPosition;
	
	private Invoice invoice;
	private InvoicePosition position;
	private InvoicePositionWizardPage page;
	private DataBindingContext bindingContext;	
	private String pageTitle;
	private String pageDesc;
	
	/**
	 * 
	 * @param invoice
	 */
	protected InvoicePositionWizard(Invoice invoice) {
		isNewPosition = true;
		init(invoice, new InvoicePosition(), 
				Messages.InvoicePositionWizard_newTitle, 
				Messages.InvoicePositionWizard_newTitle, 
				Messages.InvoicePositionWizard_newDesc);
	}
	
	/**
	 * 
	 * @param invoice
	 * @param position
	 */
	protected InvoicePositionWizard(Invoice invoice, InvoicePosition position) {
		isNewPosition = false;
		init(invoice, position, 
				Messages.InvoicePositionWizard_editTitle, 
				Messages.InvoicePositionWizard_editTitle, 
				Messages.InvoicePositionWizard_editDesc);
	}
	
	/**
	 * 
	 * @param invoice
	 * @param position
	 * @param windowTitle
	 * @param pageTitle
	 * @param pageDesc
	 */
	private void init(Invoice invoice, InvoicePosition position, String windowTitle, String pageTitle, String pageDesc) {
		this.invoice = invoice;
		this.position = position;
		bindingContext = new DataBindingContext();
		setWindowTitle(windowTitle);
		this.pageTitle = pageTitle;
		this.pageDesc = pageDesc;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		page = new InvoicePositionWizardPage(pageTitle, pageDesc);
		addPage(page);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (isNewPosition) {
			invoice.getInvoicePositions().add(position);
		}
		return true;
	}

	private class InvoicePositionWizardPage extends WizardPage implements IValueChangeListener {
		
		private Text netAmount;
		private Text taxAmount;
		private Text grossAmount;
		
		/**
		 * 
		 * @param title
		 * @param desc
		 */
		InvoicePositionWizardPage(String title, String desc) {
			super("InvoicePositionWizardPage"); //$NON-NLS-1$
			setTitle(title);
			setDescription(desc);
		}

		/**
		 * {@inheritDoc}
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout(2, false));
			
			final boolean editable = invoice.canBeEdited();
			
			// REVENUE RELEVANT
			WidgetHelper.createLabel(composite, EMPTY_STRING);
			Button revenueRelevant = new Button(composite, SWT.CHECK);
			revenueRelevant.setText(Messages.labelRevenueRelevant);
			revenueRelevant.setEnabled(editable);
			IObservableValue revenueWidgetObservable = SWTObservables.observeSelection(revenueRelevant);
			bindingContext.bindValue(
					revenueWidgetObservable, 
					PojoObservables.observeValue(position, InvoicePosition.FIELD_REVENUE_RELEVANT));
			revenueWidgetObservable.addValueChangeListener(this);
			
			// QUANTITY
			WidgetHelper.createLabel(composite, Messages.labelQuantity);
			Text quantity = WidgetHelper.createSingleBorderText(composite, null);
			quantity.setEnabled(editable);
			
			IObservableValue quantityWidgetObservable = SWTObservables.observeText(quantity, SWT.Modify);
			IObservableValue quantityPojoObservable = PojoObservables.observeValue(position, InvoicePosition.FIELD_QUANTITY);
			UpdateValueStrategy toQuantity = new UpdateValueStrategy();
			toQuantity.setConverter(StringToBigDecimalConverter.getInstance());
			UpdateValueStrategy fromQuantity = new UpdateValueStrategy();
			fromQuantity.setConverter(BigDecimalToStringConverter.getInstance());
			bindingContext.bindValue(quantityWidgetObservable, quantityPojoObservable, toQuantity, fromQuantity);
			quantityWidgetObservable.addValueChangeListener(this);
			
			// UNIT
			WidgetHelper.createLabel(composite, Messages.labelUnit);
			Text unit = WidgetHelper.createSingleBorderText(composite, null);
			unit.setEnabled(editable);
			IObservableValue unitWidgetObservable = SWTObservables.observeText(unit, SWT.Modify);
			bindingContext.bindValue(
					unitWidgetObservable, 
					PojoObservables.observeValue(position, InvoicePosition.FIELD_UNIT));
			unitWidgetObservable.addValueChangeListener(this);
			
			// PRICE PER UNIT
			WidgetHelper.createLabel(composite, Messages.labelPricePerUnit);
			Text pricePerUnit = WidgetHelper.createSingleBorderText(composite, null);
			pricePerUnit.setEnabled(editable);
			
			IObservableValue priceWidgetObservable = SWTObservables.observeText(pricePerUnit, SWT.Modify);
			IObservableValue pricePojoObservable = PojoObservables.observeValue(position, InvoicePosition.FIELD_PRICE_PER_UNIT);
			UpdateValueStrategy toPrice = new UpdateValueStrategy();
			toPrice.setConverter(StringToBigDecimalConverter.getInstance());
			UpdateValueStrategy fromPrice = new UpdateValueStrategy();
			fromPrice.setConverter(BigDecimalToStringConverter.getInstance()); //CurrencyToStringConverter.getInstance());
			bindingContext.bindValue(priceWidgetObservable, pricePojoObservable, toPrice, fromPrice);
			priceWidgetObservable.addValueChangeListener(this);

			// NET AMOUNT (INFO ONLY)
			WidgetHelper.createLabel(composite, Messages.labelNet);
			netAmount = WidgetHelper.createSingleBorderText(composite, null);
			netAmount.setEnabled(false);
			netAmount.setEditable(false);
			
			// TAX RATE
			WidgetHelper.createLabel(composite, Messages.labelTaxRate);
			ComboViewer taxRateCombo = 
					WidgetHelper.createTaxRateCombo(composite, bindingContext, position, InvoicePosition.FIELD_TAX_RATE);
			taxRateCombo.addPostSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					handleValueChange(null);
				}
			});
			
			// TAX AMOUNT (INFO ONLY)
			WidgetHelper.createLabel(composite, Messages.labelTaxes);
			taxAmount = WidgetHelper.createSingleBorderText(composite, null);
			taxAmount.setEnabled(false);
			taxAmount.setEditable(false);
			
			// GROSS AMOUNT (INFO ONLY)
			WidgetHelper.createLabel(composite, Messages.labelGross);
			grossAmount = WidgetHelper.createSingleBorderText(composite, null);
			grossAmount.setEnabled(false);
			grossAmount.setEditable(false);
			
			// DESCRIPTION
			WidgetHelper.createLabel(composite, Messages.labelDescription);
			Text description = new Text(composite, SWT.MULTI | SWT.BORDER);
			description.setEnabled(editable);
			description.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(200, 100).create());
			IObservableValue descriptionWidgetObservable = SWTObservables.observeText(description, SWT.Modify);
			bindingContext.bindValue(
					descriptionWidgetObservable, 
					PojoObservables.observeValue(position, InvoicePosition.FIELD_DESCRIPTION));
			descriptionWidgetObservable.addValueChangeListener(this);			
			
			setControl(composite);
			//setPageComplete(false);			
			handleValueChange(null);
		}
		
		/**
		 * 
		 */
		private void checkIfPageComplete() {
			if (position.getDescription() != null && !position.getDescription().isEmpty()
				&& position.getUnit() != null && !position.getUnit().isEmpty()
				&& position.getQuantity() != null 
				&& position.getPricePerUnit() != null ) {
				
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}
		}
		
		/**
		 * @see IValueChangeListener#handleValueChange(ValueChangeEvent)
		 */
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			final Price price = CalculationUtil.calculatePrice(position);
			netAmount.setText(FormatUtil.formatCurrency(price.getNet()));
			if (price.getTax() != null) {
				taxAmount.setText(FormatUtil.formatCurrency(price.getTax()));
			} else {
				taxAmount.setText(HYPHEN);
			}
			
			grossAmount.setText(FormatUtil.formatCurrency(price.getGross()));
			checkIfPageComplete();
		}
	}
}