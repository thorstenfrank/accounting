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
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
import de.togginho.accounting.ui.conversion.BigDecimalToStringConverter;
import de.togginho.accounting.ui.conversion.StringToBigDecimalConverter;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
class ExpenseWizardPage extends WizardPage implements IValueChangeListener {

	/**
	 * 
	 */
	private Expense expense;
	private Text grossAmount;
	
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
	
		DataBindingContext bindingContext = new DataBindingContext();
		
		WidgetHelper.createLabel(composite, "Expense Type");
		final Combo expenseTypeCombo = new Combo(composite, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(expenseTypeCombo);

		final List<ExpenseType> expenseTypes = new ArrayList<ExpenseType>();
		expenseTypes.add(null);
		int index = 0;
		for (ExpenseType expenseType : ExpenseType.values()) {
			expenseTypes.add(expenseType);
			expenseTypeCombo.add(expenseType.getTranslatedString());
			if (expenseType.equals(expense.getExpenseType())) {
				expenseTypeCombo.select(index);
			} else {
				index++;
			}
		}
		
		expenseTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Combo combo = (Combo) e.getSource();
				ExpenseType expenseType = expenseTypes.get(combo.getSelectionIndex());
				expense.setExpenseType(expenseType);
			}
		});
		
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
		
		WidgetHelper.createLabel(composite, Messages.labelDescription);
		final Text description = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(description);
		bind(bindingContext, description, Expense.FIELD_DESCRIPTION, false);
		
		WidgetHelper.createLabel(composite, Messages.labelNet);
		final Text net = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(net);
		
		IObservableValue widgetObservable = SWTObservables.observeText(net, SWT.Modify);
		IObservableValue pojoObservable = PojoObservables.observeValue(expense, Expense.FIELD_NET_AMOUNT);
		UpdateValueStrategy toPrice = new UpdateValueStrategy();
		toPrice.setConverter(StringToBigDecimalConverter.getInstance());
		UpdateValueStrategy fromPrice = new UpdateValueStrategy();
		fromPrice.setConverter(BigDecimalToStringConverter.getInstance());
		bindingContext.bindValue(widgetObservable, pojoObservable, toPrice, fromPrice);
		widgetObservable.addValueChangeListener(this);
		
		WidgetHelper.createLabel(composite, Messages.labelTaxRate);
		final Combo taxRate = new Combo(composite, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(taxRate);
		
		Set<TaxRate> origRates = AccountingUI.getAccountingService().getCurrentUser().getTaxRates();
		final List<TaxRate> taxRates = new ArrayList<TaxRate>(origRates.size() + 1);
		
		String[] taxRateItems = new String[origRates.size() + 1]; 
		taxRateItems[0] = Constants.EMPTY_STRING;
		taxRates.add(null);
		index = 1;
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
				expense.setTaxRate(taxRates.get(taxRate.getSelectionIndex()));
				handleValueChange(null);
			}
			
		});
		
		WidgetHelper.createLabel(composite, Messages.labelGross);
		grossAmount = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(grossAmount);
		grossAmount.setEditable(false);
		
		setControl(composite);
		handleValueChange(null);
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
	 * @see org.eclipse.core.databinding.observable.value.IValueChangeListener#handleValueChange(org.eclipse.core.databinding.observable.value.ValueChangeEvent)
	 */
	@Override
	public void handleValueChange(ValueChangeEvent event) {
		final Price price = CalculationUtil.calculatePrice(expense);
		grossAmount.setText(FormatUtil.formatCurrency(price.getGross()));
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
