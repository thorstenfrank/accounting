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

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.model.DepreciationMethod;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;
import de.togginho.accounting.ui.conversion.CurrencyToStringConverter;
import de.togginho.accounting.ui.conversion.StringToBigDecimalConverter;
import de.togginho.accounting.util.CalculationUtil;

/**
 * @author thorsten
 *
 */
class ExpenseEditingHelper implements IValueChangeListener {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(ExpenseEditingHelper.class);
	
	/**
	 * 
	 * @author thorsten
	 *
	 */
	private enum EditedPriceType {
		NET, GROSS;
	};
	
	/**
	 * 
	 */
	private ExpenseEditingHelperCallback callback;
	
	/**
	 * 
	 */
	private Expense expense;
	
	/**
	 * 
	 */
	private EditedPriceType editedPrice = EditedPriceType.NET;
	
	// data binding
	private DataBindingContext bindingContext;
	private UpdateValueStrategy toPrice;
	private UpdateValueStrategy fromPrice;
	
	private boolean changeByUserInProgress = false;
	private Price price;
	private Text taxAmount;
	private Text grossAmount;
	
	/**
	 * @param expense
	 */
	ExpenseEditingHelper(Expense expense, ExpenseEditingHelperCallback expenseEditingHelperCallback) {
		this.expense = expense;
		this.callback = expenseEditingHelperCallback;
		
		bindingContext = new DataBindingContext();
		toPrice = new UpdateValueStrategy();
		toPrice.setConverter(StringToBigDecimalConverter.getInstance());
		fromPrice = new UpdateValueStrategy();
		fromPrice.setConverter(CurrencyToStringConverter.getInstance());		
	}
	
	/**
	 * 
	 * @param parent
	 */
	protected void createBasicSection(Composite parent) {

		// TYPE
		callback.createLabel(parent, Messages.labelExpenseType);
		ComboViewer expenseTypeCombo = new ComboViewer(parent, SWT.READ_ONLY);
		WidgetHelper.grabHorizontal(expenseTypeCombo.getCombo());
		expenseTypeCombo.setContentProvider(new ArrayContentProvider());
		expenseTypeCombo.setInput(ExpenseType.values());
		expenseTypeCombo.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				return ((ExpenseType) element).getTranslatedString();
			}
			
		});
		
		expenseTypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ExpenseType newType = (ExpenseType) ((StructuredSelection) event.getSelection()).getFirstElement();
				
				if (newType == null) {
					if (expense.getExpenseType() != null) {
						LOG.debug("Expense type was set to <null>"); //$NON-NLS-1$
						callback.modelHasChanged();
					}
				} else if (newType.equals(expense.getExpenseType()) == false) {
					LOG.debug(
							String.format("Type changed from [%s] to [%s]", expense.getExpenseType(), newType)); //$NON-NLS-1$
					callback.modelHasChanged();					
				}
			}
		});
		
		bindingContext.bindValue(
				ViewersObservables.observeSinglePostSelection(expenseTypeCombo), 
				PojoObservables.observeValue(expense, Expense.FIELD_TYPE));
		
		// DATE
		callback.createLabel(parent, Messages.labelDate);
		final DateTime paymentDate = new DateTime(parent, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		WidgetHelper.dateToWidget(expense.getPaymentDate(), paymentDate);
		paymentDate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				expense.setPaymentDate(WidgetHelper.widgetToDate(paymentDate));
				callback.modelHasChanged();
			}
		});
		
		// DESCRIPTION
		callback.createLabel(parent, Messages.labelDescription);
		final Text description = callback.createText(parent,  SWT.MULTI | SWT.BORDER);
		WidgetHelper.grabBoth(description);
		bindingContext.bindValue(
				SWTObservables.observeText(description, SWT.Modify), 
				PojoObservables.observeValue(expense, Expense.FIELD_DESCRIPTION));
		description.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				callback.modelHasChanged();
			}
		});
	}

	/**
	 * 
	 * @param parent
	 */
	protected void createPriceSection(Composite parent) {
		this.price = CalculationUtil.calculatePrice(expense);
		
		// NET VALUE
		callback.createLabel(parent, Messages.labelNet);
		final Text netAmount = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		WidgetHelper.grabHorizontal(netAmount);
		IObservableValue netObservable = SWTObservables.observeText(netAmount, SWT.Modify);
		bindingContext.bindValue(
				netObservable, 
				BeansObservables.observeValue(price, "net"), 
				toPrice, fromPrice);
		netObservable.addValueChangeListener(new IValueChangeListener() {
			
			@Override
			public void handleValueChange(ValueChangeEvent event) {
				if (!changeByUserInProgress) {
					editedPrice = EditedPriceType.NET;					
				}
			}
		});
		netObservable.addValueChangeListener(this);
		
		// TAX RATE
		callback.createLabel(parent, Messages.labelTaxRate);
		final ComboViewer taxRateCombo = new ComboViewer(parent, SWT.READ_ONLY);
		WidgetHelper.grabHorizontal(taxRateCombo.getCombo());
		taxRateCombo.setContentProvider(new ArrayContentProvider());
		taxRateCombo.setInput(AccountingUI.getAccountingService().getCurrentUser().getTaxRates());
		taxRateCombo.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				return ((TaxRate) element).toShortString();
			}
			
		});
		bindingContext.bindValue(
				ViewersObservables.observeSinglePostSelection(taxRateCombo), 
				PojoObservables.observeValue(expense, Expense.FIELD_TAX_RATE));
		taxRateCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!changeByUserInProgress) {
					handleValueChange(null);
				}
			}
		});
		
		// TAX AMOUNT
		callback.createLabel(parent, Messages.labelTaxes);
		taxAmount = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		taxAmount.setEditable(false);
		taxAmount.setEnabled(false);
		WidgetHelper.grabHorizontal(taxAmount);
		IObservableValue taxObservable = SWTObservables.observeText(taxAmount, SWT.None);
		bindingContext.bindValue(
				taxObservable, 
				BeansObservables.observeValue(price, Price.FIELD_TAX), toPrice, fromPrice);
		
		// GROSS PRICE
		callback.createLabel(parent, Messages.labelGross);
		grossAmount = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		WidgetHelper.grabHorizontal(grossAmount);
		IObservableValue grossObservable = SWTObservables.observeText(grossAmount, SWT.Modify);
		bindingContext.bindValue(grossObservable, BeansObservables.observeValue(price, Price.FIELD_GROSS), toPrice, fromPrice);
		grossObservable.addValueChangeListener(new IValueChangeListener() {
			@Override
			public void handleValueChange(ValueChangeEvent event) {
				if (!changeByUserInProgress) {
					editedPrice = EditedPriceType.GROSS;					
				}
			}
		});
		grossObservable.addValueChangeListener(this);
	}
	
	/**
	 * 
	 * @param parent
	 */
	protected void createDepreciationSection(Composite parent) {
		// DEPRECIATION METHOD
		
		callback.createLabel(parent, Messages.labelDepreciationMethod);
		
		final ComboViewer depreciationMethod = new ComboViewer(parent, SWT.READ_ONLY);
		WidgetHelper.grabHorizontal(depreciationMethod.getCombo());
		depreciationMethod.setContentProvider(new ArrayContentProvider());
		depreciationMethod.setInput(DepreciationMethod.values());
		depreciationMethod.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DepreciationMethod) element).getTranslatedString();
			}
		});
		
		// DEPRECIATION PERIOD
		callback.createLabel(parent, Messages.labelDepreciationPeriodInYears);
		final Text depreciationPeriod = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		WidgetHelper.grabHorizontal(depreciationPeriod);
		
		// SALVAGE VALUE
		callback.createLabel(parent, Messages.labelScrapValue);
		final Text salvageValue = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		WidgetHelper.grabHorizontal(salvageValue);
//		
//		if (expense.getExpenseType() != null) {
//			enableOrDisableDepreciation(expense.getExpenseType().isDepreciationPossible());
//		} else {
//			enableOrDisableDepreciation(false);
//		}
	}
	
//	/**
//	 * 
//	 * @param enable
//	 */
//	private void enableOrDisableDepreciation(boolean enable) {
//		depreciationMethod.setEnabled(enable);
//		depreciationPeriod.setEnabled(enable);
//		salvageValue.setEnabled(enable);
//		
//		if (false == enable) {
//			depreciationMethod.select(0);
//			depreciationPeriod.setText("");
//			salvageValue.setText("");
//		} else {
//			depreciationMethod.select(1);
//			depreciationPeriod.setText("3");
//			salvageValue.setText("1");			
//		}
//	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.databinding.observable.value.IValueChangeListener#handleValueChange(org.eclipse.core.databinding.observable.value.ValueChangeEvent)
	 */
	@Override
	public void handleValueChange(ValueChangeEvent event) {
		changeByUserInProgress = true;
		switch (editedPrice) {
		case NET:
			price.calculateGrossFromNet(expense.getTaxRate());
			break;
		case GROSS:
			price.calculateNetFromGross(expense.getTaxRate());
			break;
		}
		
		expense.setNetAmount(price.getNet());
		changeByUserInProgress = false;
		
		callback.modelHasChanged();
	}
}
