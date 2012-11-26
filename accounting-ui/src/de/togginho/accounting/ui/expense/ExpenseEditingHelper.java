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
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.DepreciationMethod;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.conversion.CurrencyToStringConverter;
import de.togginho.accounting.ui.conversion.FromStructuredSelectionConverter;
import de.togginho.accounting.ui.conversion.StringToBigDecimalConverter;
import de.togginho.accounting.ui.conversion.ToStructuredSelectionConverter;
import de.togginho.accounting.ui.util.CollectionContentProvider;
import de.togginho.accounting.ui.util.WidgetHelper;
import de.togginho.accounting.util.CalculationUtil;

/**
 * @author thorsten
 *
 */
class ExpenseEditingHelper implements IValueChangeListener, ISelectionChangedListener {

	/** */
	private static final Logger LOG = Logger.getLogger(ExpenseEditingHelper.class);

	/** Key for identifying the widget that has fired an event. */
	private static final String KEY_WIDGET_DATA = "ExpenseEditingHelper.KEY_WIDGET_DATA"; //$NON-NLS-1$
	
	/** */
	private enum EditedPriceType {
		NET, GROSS;
	};
	
	/** */
	private ExpenseEditingHelperCallback callback;
	
	/** */
	private Expense expense;
		
	// data binding
	private DataBindingContext bindingContext;
	private UpdateValueStrategy toPrice;
	private UpdateValueStrategy fromPrice;
	
	// price calculating
	private EditedPriceType editedPriceType = EditedPriceType.NET; // net is the default
	private boolean changeByUserInProgress = false;
	private Price price;
	
	/**
	 * 
	 * @param expense
	 * @param expenseEditingHelperCallback
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
		expenseTypeCombo.setData(KEY_WIDGET_DATA, Expense.FIELD_TYPE);
		WidgetHelper.grabHorizontal(expenseTypeCombo.getCombo());
		expenseTypeCombo.setContentProvider(new CollectionContentProvider(true));
		expenseTypeCombo.setInput(ExpenseType.values());
		expenseTypeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ExpenseType) {
					return ((ExpenseType) element).getTranslatedString();
				}
				return Constants.HYPHEN;
			}
		});
		
		UpdateValueStrategy from = new UpdateValueStrategy();
		from.setConverter(new FromStructuredSelectionConverter(ExpenseType.class));
		UpdateValueStrategy to = new UpdateValueStrategy();
		to.setConverter(new ToStructuredSelectionConverter(ExpenseType.class));
		
		bindingContext.bindValue(
				ViewersObservables.observeSinglePostSelection(expenseTypeCombo), 
				PojoObservables.observeValue(expense, Expense.FIELD_TYPE), from, to);
		expenseTypeCombo.addSelectionChangedListener(this);
		
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
		description.setData(KEY_WIDGET_DATA, Expense.FIELD_DESCRIPTION);
		WidgetHelper.grabBoth(description);
		IObservableValue descObservable = PojoObservables.observeValue(expense, Expense.FIELD_DESCRIPTION);
		bindingContext.bindValue(
				SWTObservables.observeText(description, SWT.Modify), 
				descObservable);
		descObservable.addValueChangeListener(this);
		
		// CATEGORY
		callback.createLabel(parent, Messages.labelCategory);
		final Text category = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		category.setData(KEY_WIDGET_DATA, Expense.FIELD_CATEGORY);
		WidgetHelper.grabHorizontal(category);
		IObservableValue categoryObservable = PojoObservables.observeValue(expense, Expense.FIELD_CATEGORY);
		bindingContext.bindValue(
				SWTObservables.observeText(category, SWT.Modify), categoryObservable);
		categoryObservable.addValueChangeListener(this);
	}

	/**
	 * 
	 * @param parent
	 */
	protected void createPriceSection(Composite parent) {
		this.price = CalculationUtil.calculatePrice(expense);
		
		// NET VALUE
		callback.createLabel(parent, Messages.labelNet);
		Text netAmount = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		netAmount.setData(KEY_WIDGET_DATA, EditedPriceType.NET);
		WidgetHelper.grabHorizontal(netAmount);
		IObservableValue netObservable = SWTObservables.observeText(netAmount, SWT.Modify);
		bindingContext.bindValue(
				netObservable, 
				BeansObservables.observeValue(price, "net"), 
				toPrice, fromPrice);
		netObservable.addValueChangeListener(this);
		
		// TAX RATE
		callback.createLabel(parent, Messages.labelTaxRate);
		ComboViewer taxRateCombo = 
				WidgetHelper.createTaxRateCombo(parent, bindingContext, expense, Expense.FIELD_TAX_RATE);
		taxRateCombo.setData(KEY_WIDGET_DATA, Expense.FIELD_TAX_RATE);
		taxRateCombo.addPostSelectionChangedListener(this);
		
		// TAX AMOUNT
		callback.createLabel(parent, Messages.labelTaxes);
		Text taxAmount = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		taxAmount.setEditable(false);
		taxAmount.setEnabled(false);
		WidgetHelper.grabHorizontal(taxAmount);
		IObservableValue taxObservable = SWTObservables.observeText(taxAmount, SWT.None);
		bindingContext.bindValue(
				taxObservable, 
				BeansObservables.observeValue(price, Price.FIELD_TAX), toPrice, fromPrice);
		
		// GROSS PRICE
		callback.createLabel(parent, Messages.labelGross);
		Text grossAmount = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		grossAmount.setData(KEY_WIDGET_DATA, EditedPriceType.GROSS);
		WidgetHelper.grabHorizontal(grossAmount);
		IObservableValue grossObservable = SWTObservables.observeText(grossAmount, SWT.Modify);
		bindingContext.bindValue(grossObservable, BeansObservables.observeValue(price, Price.FIELD_GROSS), toPrice, fromPrice);
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
	 * 
	 */
	private void recalculatePrice() {
		if (!changeByUserInProgress) {
			changeByUserInProgress = true;
			switch (editedPriceType) {
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
	
	/**
	 * {@inheritDoc}
	 * @see IValueChangeListener#handleValueChange(ValueChangeEvent)
	 */
	@Override
	public void handleValueChange(ValueChangeEvent event) {
		// let a calculation of net/gross/tax based on a user change go through before reacting to further value change
		// events again
		if (changeByUserInProgress || event == null || event.getSource() == null) {
			return;
		}
		
		if (event.getSource() instanceof ISWTObservable) {
			Widget sourceWidget = ((ISWTObservableValue) event.getSource()).getWidget();
			if (sourceWidget.getData(KEY_WIDGET_DATA) instanceof EditedPriceType) {
				editedPriceType = (EditedPriceType) sourceWidget.getData(KEY_WIDGET_DATA);
				recalculatePrice();
			} else {
				LOG.debug(event.getSource());
			}
		} else if (event.getSource() instanceof IObservableValue) {
			callback.modelHasChanged();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSource() instanceof ComboViewer) {
			Object data = ((ComboViewer) event.getSource()).getData(KEY_WIDGET_DATA);
			if (data == null) {
				LOG.warn("No source specified"); //$NON-NLS-1$
			} else if (data.equals(Expense.FIELD_TYPE)) {
				LOG.debug("Expense type changed"); //$NON-NLS-1$
				Object selection = ((StructuredSelection) event.getSelection()).getFirstElement();
				ExpenseType newType = selection instanceof ExpenseType ? newType = (ExpenseType) selection : null;
				if (newType == null) {
					if (expense.getExpenseType() != null) {
						LOG.debug("Expense type was set to <null>"); //$NON-NLS-1$
						callback.modelHasChanged();
					}
				} else if (newType.equals(expense.getExpenseType()) == false) {
					LOG.debug(String.format(
							"Type changed from [%s] to [%s]", expense.getExpenseType(), newType)); //$NON-NLS-1$
					callback.modelHasChanged();					
				}
			} else if (data.equals(Expense.FIELD_TAX_RATE)) {
				LOG.debug("Tax Rate changed"); //$NON-NLS-1$
				recalculatePrice();
			}
		}
	}
}