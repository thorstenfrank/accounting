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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;
import de.togginho.accounting.ui.conversion.BigDecimalToStringConverter;
import de.togginho.accounting.ui.conversion.StringToBigDecimalConverter;

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
	private Map<String, TaxRate> descToRateMap;
	
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
			
			// QUANTITY
			WidgetHelper.createLabel(composite, Messages.labelQuantity);
			Text quantity = new Text(composite, SWT.SINGLE | SWT.BORDER);
			quantity.setEnabled(editable);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(quantity);
			
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
			Text unit = new Text(composite, SWT.SINGLE | SWT.BORDER);
			unit.setEnabled(editable);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(unit);
			IObservableValue unitWidgetObservable = SWTObservables.observeText(unit, SWT.Modify);
			bindingContext.bindValue(
					unitWidgetObservable, 
					PojoObservables.observeValue(position, InvoicePosition.FIELD_UNIT));
			unitWidgetObservable.addValueChangeListener(this);
			
			// PRICE PER UNIT
			WidgetHelper.createLabel(composite, Messages.labelPricePerUnit);
			Text pricePerUnit = new Text(composite, SWT.SINGLE | SWT.BORDER);
			pricePerUnit.setEnabled(editable);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(pricePerUnit);
			
			IObservableValue priceWidgetObservable = SWTObservables.observeText(pricePerUnit, SWT.Modify);
			IObservableValue pricePojoObservable = PojoObservables.observeValue(position, InvoicePosition.FIELD_PRICE_PER_UNIT);
			UpdateValueStrategy toPrice = new UpdateValueStrategy();
			toPrice.setConverter(StringToBigDecimalConverter.getInstance());
			UpdateValueStrategy fromPrice = new UpdateValueStrategy();
			fromPrice.setConverter(BigDecimalToStringConverter.getInstance()); //CurrencyToStringConverter.getInstance());
			bindingContext.bindValue(priceWidgetObservable, pricePojoObservable, toPrice, fromPrice);
			priceWidgetObservable.addValueChangeListener(this);
			
			// TAX RATE
			WidgetHelper.createLabel(composite, Messages.labelTaxRate);
			Combo taxRate = new Combo(composite, SWT.READ_ONLY);
			taxRate.setEnabled(editable);
			taxRate.add(EMPTY_STRING);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(taxRate);
			
			TaxRate existing = position != null && position.getTaxRate() != null ? position.getTaxRate() : null;
			int index = 1;
			descToRateMap = new HashMap<String, TaxRate>();
			for (TaxRate rate : invoice.getUser().getTaxRates()) {
				final String desc = rate.toLongString();
				descToRateMap.put(desc, rate);
				taxRate.add(desc);
				if (rate.equals(existing)) {
					taxRate.select(index);
				} else {
					index++;
				}
			}
			taxRate.addSelectionListener(new SelectionAdapter() {
				/**
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Combo combo = (Combo) e.getSource();
					final String selected = combo.getItem(combo.getSelectionIndex());
					if (selected.isEmpty()) {
						position.setTaxRate(null);
					} else {
						position.setTaxRate(descToRateMap.get(selected));
					}
					checkIfPageComplete();
				}
			});
			
			// DESCRIPTION
			WidgetHelper.createLabel(composite, Messages.labelDescription);
			Text description = new Text(composite, SWT.MULTI | SWT.BORDER);
			description.setEnabled(editable);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(description);
			IObservableValue descriptionWidgetObservable = SWTObservables.observeText(description, SWT.Modify);
			bindingContext.bindValue(
					descriptionWidgetObservable, 
					PojoObservables.observeValue(position, InvoicePosition.FIELD_DESCRIPTION));
			descriptionWidgetObservable.addValueChangeListener(this);
			
			setControl(composite);
			setPageComplete(false);
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
		 * @see org.eclipse.core.databinding.observable.value.IValueChangeListener#handleValueChange(org.eclipse.core.databinding.observable.value.ValueChangeEvent)
		 */
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			checkIfPageComplete();
		}
	}
}