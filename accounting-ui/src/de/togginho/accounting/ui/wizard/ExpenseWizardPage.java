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
package de.togginho.accounting.ui.wizard;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.Constants;
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
class ExpenseWizardPage extends WizardPage implements IValueChangeListener {

	private enum EditedPriceType {
		NET, GROSS;
	};
	
	/**
	 * 
	 */
	private Expense expense;
	private Text taxAmount;
	private Text grossAmount;
	private Combo depreciationMethod;
	private Text depreciationPeriod;
	private Text salvageValue;
	
	private Price price;
	
	private EditedPriceType editedPrice = EditedPriceType.NET;
	private boolean changeByUserInProgress = false;
	
	/**
	 * 
	 */
	ExpenseWizardPage() {
		super(ExpenseWizardPage.class.getName());
		this.expense = new Expense();
		setTitle(Messages.EditExpenseWizard_newTitle);
		setDescription(Messages.EditExpenseWizard_newDesc);
	}
	
	/**
	 * 
	 * @param expense
	 */
	ExpenseWizardPage(Expense expense) {
		super(ExpenseWizardPage.class.getName());
		this.expense = expense;
		setTitle(Messages.EditExpenseWizard_editTitle);
		setDescription(Messages.EditExpenseWizard_editDesc);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		price = CalculationUtil.calculatePrice(expense);
		
		DataBindingContext bindingContext = new DataBindingContext();
		UpdateValueStrategy toPrice = new UpdateValueStrategy();
		toPrice.setConverter(StringToBigDecimalConverter.getInstance());
		UpdateValueStrategy fromPrice = new UpdateValueStrategy();
		fromPrice.setConverter(CurrencyToStringConverter.getInstance());
		
		GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, false);
		
		// EXPENSE TYPE
		WidgetHelper.createLabel(composite, Messages.labelExpenseType);
		ComboViewer expenseTypeCombo = new ComboViewer(composite, SWT.READ_ONLY);
		gdf.applyTo(expenseTypeCombo .getCombo());
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
				checkIfPageComplete();
			}
		});
		bindingContext.bindValue(
				ViewersObservables.observeSinglePostSelection(expenseTypeCombo), 
				PojoObservables.observeValue(expense, Expense.FIELD_TYPE));

		
		// PAYMENT DATE
		WidgetHelper.createLabel(composite, Messages.labelDate);
		if (expense.getPaymentDate() == null) {
			expense.setPaymentDate(new Date());
		}
		
		final DateTime paymentDate = new DateTime(composite, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		WidgetHelper.dateToWidget(expense.getPaymentDate(), paymentDate);
		paymentDate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				expense.setPaymentDate(WidgetHelper.widgetToDate(paymentDate));
			}
		});
		
		// DESCRIPTION
		WidgetHelper.createLabel(composite, Messages.labelDescription);
		final Text description = WidgetHelper.createSingleBorderText(composite, null);
		gdf.applyTo(description);
		bind(bindingContext, description, Expense.FIELD_DESCRIPTION, false);
		description.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				checkIfPageComplete();
			}
		});
		
		
		// NET VALUE
		WidgetHelper.createLabel(composite, Messages.labelNet);
		final Text net = WidgetHelper.createSingleBorderText(composite, null);
		gdf.applyTo(net);
		
		IObservableValue netObservable = SWTObservables.observeText(net, SWT.Modify);
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
		
		WidgetHelper.createLabel(composite, Messages.labelTaxRate);
		final Combo taxRate = new Combo(composite, SWT.READ_ONLY);
		gdf.applyTo(taxRate);
		
		// TAX RATE
		Set<TaxRate> origRates = AccountingUI.getAccountingService().getCurrentUser().getTaxRates();
		final List<TaxRate> taxRates = new ArrayList<TaxRate>(origRates.size() + 1);
		
		String[] taxRateItems = new String[origRates.size() + 1]; 
		taxRateItems[0] = Constants.EMPTY_STRING;
		taxRates.add(null);
		int index = 1;
		Iterator<TaxRate> iter = origRates.iterator();
		int selection = 0;
		while (iter.hasNext()) {
			TaxRate rate = iter.next();
			if (rate.equals(expense.getTaxRate())) {
				selection = index;
			}
			taxRateItems[index] = rate.toShortString();
			taxRates.add(rate);
			index++;
		}
		
		taxRate.setItems(taxRateItems);
		taxRate.select(selection);
		taxRate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!changeByUserInProgress) {
					expense.setTaxRate(taxRates.get(taxRate.getSelectionIndex()));
					handleValueChange(null);					
				}
			}
			
		});
		
		// TAX AMOUNT
		WidgetHelper.createLabel(composite, Messages.labelTaxes);
		taxAmount = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gdf.applyTo(taxAmount);
		taxAmount.setEditable(false);
		taxAmount.setEnabled(false);
		IObservableValue taxObservable = SWTObservables.observeText(taxAmount, SWT.None);
		bindingContext.bindValue(taxObservable, BeansObservables.observeValue(price, "tax"), toPrice, fromPrice);
		
		// GROSS AMOUNT
		WidgetHelper.createLabel(composite, Messages.labelGross);
		grossAmount = WidgetHelper.createSingleBorderText(composite, null);
		gdf.applyTo(grossAmount);
		IObservableValue grossObservable = SWTObservables.observeText(grossAmount, SWT.Modify);
		bindingContext.bindValue(grossObservable, BeansObservables.observeValue(price, "gross"), toPrice, fromPrice);
		grossObservable.addValueChangeListener(new IValueChangeListener() {
			@Override
			public void handleValueChange(ValueChangeEvent event) {
				if (!changeByUserInProgress) {
					editedPrice = EditedPriceType.GROSS;					
				}
			}
		});
		grossObservable.addValueChangeListener(this);
		
		// DEPRECIATION METHOD
		WidgetHelper.createLabel(composite, Messages.labelDepreciationMethod);
		depreciationMethod = new Combo(composite, SWT.READ_ONLY);
		gdf.applyTo(depreciationMethod);
		depreciationMethod.add("Keine Abschreibung");
		depreciationMethod.add("Linear");
		
		// DEPRECIATION PERIOD
		WidgetHelper.createLabel(composite, Messages.labelDepreciationPeriodInYears);
		depreciationPeriod = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gdf.applyTo(depreciationPeriod);
		
		// SALVAGE VALUE
		WidgetHelper.createLabel(composite, Messages.labelScrapValue);
		salvageValue = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gdf.applyTo(salvageValue);
		
		setControl(composite);
		
		if (expense.getExpenseType() != null) {
			enableOrDisableDepreciation(expense.getExpenseType().isDepreciationPossible());
		} else {
			enableOrDisableDepreciation(false);
		}
		
		handleValueChange(null);
	}
	
	/**
	 * 
	 * @param enable
	 */
	private void enableOrDisableDepreciation(boolean enable) {
		depreciationMethod.setEnabled(enable);
		depreciationPeriod.setEnabled(enable);
		salvageValue.setEnabled(enable);
		
		if (false == enable) {
			depreciationMethod.select(0);
			depreciationPeriod.setText("");
			salvageValue.setText("");
		} else {
			depreciationMethod.select(1);
			depreciationPeriod.setText("3");
			salvageValue.setText("1");			
		}
	}
	
	/**
	 * 
	 * @param bindingContext
	 * @param text
	 * @param field
	 */
	private void bind(DataBindingContext bindingContext, Text text, String field, boolean registerListener) {
		IObservableValue widgetObservable = SWTObservables.observeText(text, SWT.Modify);
		bindingContext.bindValue(
				widgetObservable, 
				PojoObservables.observeValue(expense, field));
		if (registerListener) {
			widgetObservable.addValueChangeListener(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see IValueChangeListener#handleValueChange(ValueChangeEvent)
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
		
		checkIfPageComplete();
	}

	/**
	 * 
	 */
	private void checkIfPageComplete() {
		if (expense.getDescription() != null && !expense.getDescription().isEmpty() &&
			expense.getPaymentDate() != null && expense.getNetAmount() != null) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected Expense getExpense() {
		return expense;
	}
}
