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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.DepreciationMethod;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.ui.AccountingUI;
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

	// Depreciation
	private ComboViewer depreciationMethod;
	private Spinner depreciationPeriod;
	private Text salvageValue;
	
	private Collection<String> expenseCategories;
	
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
		bind(ExpenseType.class, expenseTypeCombo, expense, Expense.FIELD_TYPE);
				
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
		bind(description, expense, Expense.FIELD_DESCRIPTION, false);
		
		// CATEGORY
		expenseCategories = AccountingUI.getAccountingService().findExpenseCategories();
		
		callback.createLabel(parent, Messages.labelCategory);
		final ComboViewer categoryCombo = new ComboViewer(parent, SWT.DROP_DOWN);
		categoryCombo.setData(KEY_WIDGET_DATA, Expense.FIELD_CATEGORY);
		WidgetHelper.grabHorizontal(categoryCombo.getCombo());
		categoryCombo.setContentProvider(new CollectionContentProvider(true));
		categoryCombo.setInput(expenseCategories);
		categoryCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					return (String) element;
				}
				return Constants.HYPHEN;
			}
		});
		
		categoryCombo.getCombo().addTraverseListener(new TraverseListener() {
			
			@Override
			public void keyTraversed(TraverseEvent e) {
				String text = categoryCombo.getCombo().getText();
				
				if (!expenseCategories.contains(text)) {
					LOG.debug("Adding new value to dropdown: " + text); //$NON-NLS-1$
					categoryCombo.add(text);
					categoryCombo.setSelection(new StructuredSelection(text));
				}
			}
		});
		
		bind(String.class, categoryCombo, expense, Expense.FIELD_CATEGORY);
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
		bind(netAmount, price, Price.FIELD_NET, toPrice, fromPrice, true);
		
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
		bind(taxAmount, price, Price.FIELD_TAX, toPrice, fromPrice, true);
		
		// GROSS PRICE
		callback.createLabel(parent, Messages.labelGross);
		Text grossAmount = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		grossAmount.setData(KEY_WIDGET_DATA, EditedPriceType.GROSS);
		WidgetHelper.grabHorizontal(grossAmount);
		bind(grossAmount, price, Price.FIELD_GROSS, toPrice, fromPrice, true);
	}
	
	/**
	 * 
	 * @param parent
	 */
	protected void createDepreciationSection(Composite parent) {
		// DEPRECIATION METHOD
		callback.createLabel(parent, Messages.labelDepreciationMethod);
		depreciationMethod = new ComboViewer(parent, SWT.READ_ONLY);
		WidgetHelper.grabHorizontal(depreciationMethod.getCombo());
		depreciationMethod.setData(KEY_WIDGET_DATA, Expense.FIELD_DEPRECIATION_METHOD);
		depreciationMethod.setContentProvider(new CollectionContentProvider(true));
		depreciationMethod.setInput(DepreciationMethod.values());
		depreciationMethod.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DepreciationMethod) {
					return ((DepreciationMethod) element).getTranslatedString();
				}
				return Constants.HYPHEN;
			}
		});
		
		bind(DepreciationMethod.class, depreciationMethod, expense, Expense.FIELD_DEPRECIATION_METHOD);
		
		// DEPRECIATION PERIOD
		callback.createLabel(parent, Messages.labelDepreciationPeriodInYears);
		depreciationPeriod = new Spinner(parent, SWT.BORDER);
		depreciationPeriod.setMinimum(0);
		depreciationPeriod.setMaximum(100);
		depreciationPeriod.setIncrement(1);
		WidgetHelper.grabHorizontal(depreciationPeriod);
		IObservableValue periodObservable = SWTObservables.observeSelection(depreciationPeriod);
		bindingContext.bindValue(
				periodObservable, 
				PojoObservables.observeValue(expense, Expense.FIELD_DEPRECIATION_PERIOD));
		periodObservable.addValueChangeListener(this);
		
		// SALVAGE VALUE
		callback.createLabel(parent, Messages.labelScrapValue);
		salvageValue = callback.createText(parent, SWT.SINGLE | SWT.BORDER);
		WidgetHelper.grabHorizontal(salvageValue);
		bind(salvageValue, expense, Expense.FIELD_SALVAGE_VALUE, toPrice, fromPrice, false);

		enableOrDisableDepreciation(expense.getExpenseType());
	}
	
	/**
	 * 
	 */
	private void enableOrDisableDepreciation(ExpenseType expenseType) {
		if (depreciationMethod == null) {
			return;
		}
		
		boolean enable = expenseType != null && expenseType.isDepreciationPossible();

		if (!enable) {
			depreciationMethod.setSelection(StructuredSelection.EMPTY);
			depreciationPeriod.setSelection(0);
			salvageValue.setText(Constants.EMPTY_STRING);
		}
		
		depreciationMethod.getCombo().setEnabled(enable);
		depreciationPeriod.setEnabled(enable);
		salvageValue.setEnabled(enable);
	}

	/**
	 * 
	 */
	private void recalculatePrice() {
		StringBuilder debugMsg = new StringBuilder();
		
		if (!changeByUserInProgress) {
			debugMsg.append("Recalculating price "); //$NON-NLS-1$
			changeByUserInProgress = true;
			switch (editedPriceType) {
			case NET:
				price.calculateGrossFromNet(expense.getTaxRate());
				debugMsg.append("from net"); //$NON-NLS-1$
				break;
			case GROSS:
				price.calculateNetFromGross(expense.getTaxRate());
				debugMsg.append("from gross"); //$NON-NLS-1$
				break;
			}
						
			expense.setNetAmount(price.getNet());
			changeByUserInProgress = false;			
		}
		
		LOG.debug(debugMsg.toString());
		LOG.debug(price.toString());
	}
	
	/**
	 * 
	 * @param contentTypeClass
	 * @param viewer
	 * @param entity
	 * @param field
	 * @return
	 */
	private Binding bind(Class<?> contentTypeClass, StructuredViewer viewer, Object entity, String field) {
		UpdateValueStrategy from = new UpdateValueStrategy();
		from.setConverter(new FromStructuredSelectionConverter(contentTypeClass));
		UpdateValueStrategy to = new UpdateValueStrategy();
		to.setConverter(new ToStructuredSelectionConverter(contentTypeClass));
		
		Binding binding = bindingContext.bindValue(
				ViewersObservables.observeSinglePostSelection(viewer), 
				PojoObservables.observeValue(entity, field), from, to);
		viewer.addSelectionChangedListener(this);
		//viewer.addPostSelectionChangedListener(this);
		
		return binding;
	}
	
	/**
	 * 
	 * @param text
	 * @param pojo
	 * @param field
	 * @param isBean
	 * @return
	 */
	private Binding bind(Text text, Object pojo, String field, boolean isBean) {
		return bind(text, pojo, field, new UpdateValueStrategy(), new UpdateValueStrategy(), isBean);
	}
	
	/**
	 * 
	 * @param text
	 * @param pojo
	 * @param field
	 * @param toModel
	 * @param fromModel
	 * @param isBean
	 * @return
	 */
	private Binding bind(Text text, Object pojo, String field, UpdateValueStrategy toModel, UpdateValueStrategy fromModel, boolean isBean) {
		IObservableValue swtObservable = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue beanObservable = isBean ? BeansObservables.observeValue(pojo, field) : PojoObservables.observeValue(pojo, field);
		Binding binding = bindingContext.bindValue(swtObservable, beanObservable, toModel, fromModel);
		swtObservable.addValueChangeListener(this);
		return binding;
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
			}
		}
//			} else {
//				LOG.debug(event.getSource());
//				callback.modelHasChanged();
//			}
//		} else if (event.getSource() instanceof IObservableValue) {
//			callback.modelHasChanged();
//		}
		
		callback.modelHasChanged();
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
				return;
			} 
			
			Object selection = ((StructuredSelection) event.getSelection()).getFirstElement();
			
			if (data.equals(Expense.FIELD_TYPE)) {
				LOG.debug("Expense type changed"); //$NON-NLS-1$
				
				ExpenseType newType = selection instanceof ExpenseType ? newType = (ExpenseType) selection : null;
				if (newType == null) {
					if (expense.getExpenseType() != null) {
						LOG.debug("Expense type was set to <null>"); //$NON-NLS-1$
						callback.modelHasChanged();
					}
				} else if (newType.equals(expense.getExpenseType()) == false) {
					LOG.debug(String.format(
							"Expense type changed from [%s] to [%s]", expense.getExpenseType(), newType)); //$NON-NLS-1$
					callback.modelHasChanged();					
				}
				enableOrDisableDepreciation(newType);
			} else if (data.equals(Expense.FIELD_TAX_RATE)) {
				LOG.debug("Tax Rate changed"); //$NON-NLS-1$
				recalculatePrice();
			} else if (data.equals(Expense.FIELD_CATEGORY)) {
				LOG.debug("Expense category changed"); //$NON-NLS-1$	
				
				String newCat = selection instanceof String ? newCat = (String) selection : null;
				if (newCat == null) {
					if (expense.getCategory() != null) {
						LOG.debug("Expense category was set to <null>"); //$NON-NLS-1$
						callback.modelHasChanged();
					}
				} else if (newCat.equals(expense.getCategory()) == false) {
						LOG.debug(String.format(
								"Expense category changed from [%s] to [%s]", expense.getCategory(), newCat)); //$NON-NLS-1$
						callback.modelHasChanged();
				}
			} else if (data.equals(Expense.FIELD_DEPRECIATION_METHOD)) {
				LOG.debug("Depreciation Method changed"); //$NON-NLS-1$
				DepreciationMethod method = selection instanceof DepreciationMethod ? method = (DepreciationMethod) selection : null;
				if (method == null) {
					if (expense.getDepreciationMethod() != null) {
						LOG.debug("Depreciation method was set to <null>"); //$NON-NLS-1$
						callback.modelHasChanged();						
					}
				} else if (method.equals(expense.getDepreciationMethod()) == false) {
					LOG.debug(String.format(
							"Depreciation method changed from [%s] to [%s]", 
							expense.getDepreciationMethod(), method)); //$NON-NLS-1$
					callback.modelHasChanged();
				}
			}
		}
	}
}