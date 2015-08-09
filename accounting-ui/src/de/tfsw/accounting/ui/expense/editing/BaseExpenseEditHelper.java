/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.expense.editing;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.AbstractExpense;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.conversion.BigDecimalToStringConverter;
import de.tfsw.accounting.ui.conversion.FromStructuredSelectionConverter;
import de.tfsw.accounting.ui.conversion.StringToBigDecimalConverter;
import de.tfsw.accounting.ui.conversion.ToStructuredSelectionConverter;
import de.tfsw.accounting.ui.util.CollectionContentProvider;
import de.tfsw.accounting.ui.util.GenericLabelProvider;
import de.tfsw.accounting.ui.util.StringLabelProvider;
import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.CalculationUtil;

/**
 * @author Thorsten Frank
 *
 */
public class BaseExpenseEditHelper implements ISelectionChangedListener, IValueChangeListener {

	/** */
	private static final Logger LOG = Logger.getLogger(BaseExpenseEditHelper.class);

	/** Key for identifying the widget that has fired an event. */
	protected static final String KEY_WIDGET_DATA = "BaseExpenseEditingHelper.KEY_WIDGET_DATA"; //$NON-NLS-1$
	
	/** The model. */
	private AbstractExpense expense;
	private Set<String> expenseCategories;
	
	/** Callback interface. */
	private ExpenseEditingHelperClient client;
	
	/** Calucated amounts of the expense. */
	private Price price;
	private String priceCalculationBase = Price.FIELD_NET;
	private boolean priceCalculationInProgress = false;
	
	// Data Binding
	private DataBindingContext bindingCtx;
	private UpdateValueStrategy toPrice;
	private UpdateValueStrategy fromPrice;
	
	/**
	 * @param expense
	 * @param client
	 */
	public BaseExpenseEditHelper(AbstractExpense expense, ExpenseEditingHelperClient client) {
		this.expense = expense;
		this.client = client;
		
		// data binding
		bindingCtx = new DataBindingContext();
		toPrice = new UpdateValueStrategy();
		toPrice.setConverter(StringToBigDecimalConverter.getInstance());
		fromPrice = new UpdateValueStrategy();
		fromPrice.setConverter(BigDecimalToStringConverter.getInstance());
	}
		
	/**
	 * @return the client
	 */
	protected ExpenseEditingHelperClient getClient() {
		return client;
	}
			
	/**
	 * @return the bindingCtx
	 */
	protected DataBindingContext getBindingCtx() {
		return bindingCtx;
	}

	/**
	 * Creates widgets for the following properties:
	 * 
	 * <ul>
	 * <li>{@link AbstractExpense#getExpenseType()}</li>
	 * <li>{@link AbstractExpense#getDescription()}</li>
	 * <li>{@link AbstractExpense#getCategory()}</li>
	 * </ul>
	 * 
	 * @param container the {@link Composite} to add the widgets to 
	 */
	public void createBasicSection(Composite container) {
		// TYPE
		createComboViewer(container, SWT.READ_ONLY, Messages.labelExpenseType, Expense.FIELD_TYPE, ExpenseType.class, ExpenseType.values(), new GenericLabelProvider(ExpenseType.class, "getTranslatedString"));
				
		// DESCRIPTION
		createAndBindText(container, Messages.labelDescription, expense, Expense.FIELD_DESCRIPTION, false);

		// CATEGORY
		expenseCategories = AccountingUI.getAccountingService().getModelMetaInformation().getExpenseCategories();
		ComboViewer catCombo = createComboViewer(container, SWT.DROP_DOWN, Messages.labelCategory, Expense.FIELD_CATEGORY, String.class, expenseCategories, StringLabelProvider.DEFAULT);
		catCombo.getCombo().addTraverseListener(new TraverseListener() {
			
			@Override
			public void keyTraversed(TraverseEvent e) {
				String text = catCombo.getCombo().getText();
				if (!expenseCategories.contains(text) && !Constants.HYPHEN.equals(text)) {
					LOG.debug(String.format("Adding new value to category dropdown: [%s]", text));
					catCombo.add(text);
					catCombo.setSelection(new StructuredSelection(text));
				}
			}
		});
	}
	
	/**
	 * Creates a visual representation of the {@link Price} of an {@link AbstractExpense} that allows "interactive"
	 * editing, i.e. all changes will cause the price to be updated. This allows users to edit either net or gross
	 * amounts as well as the tax rate, and the expense will be updated accordingly.
	 * 
	 * @param container the {@link Composite} to add the widgets to
	 * 
	 * @see AbstractExpense#setNetAmount(java.math.BigDecimal)
	 * @see AbstractExpense#setTaxRate(de.tfsw.accounting.model.TaxRate)
	 * @see CalculationUtil#calculatePrice(AbstractExpense)
	 */
	public void createPriceSection(Composite container) {
		this.price = CalculationUtil.calculatePrice(expense);
		
		KeyListener kl = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				Text source = (Text)e.getSource();
				priceCalculationBase = source.getData(KEY_WIDGET_DATA).toString();
				recalculatePrice();
			}
		};
		
		Text net = createText(container, Messages.labelNet, Price.FIELD_NET);
		net.addKeyListener(kl);
		// NET VALUE
		bindPrice(net, Price.FIELD_NET);
		
		// TAX RATE
		client.createLabel(container, Messages.labelTaxRate);
		ComboViewer taxRateCombo = 
				WidgetHelper.createTaxRateCombo(container, bindingCtx, expense, Expense.FIELD_TAX_RATE);
		taxRateCombo.setData(KEY_WIDGET_DATA, Expense.FIELD_TAX_RATE);
		bind(TaxRate.class, taxRateCombo, Expense.FIELD_TAX_RATE, true);
		
		// TAX AMOUNT
		Text taxAmount = createText(container, Messages.labelTaxes, Price.FIELD_TAX);
		taxAmount.setEditable(false);
		taxAmount.setEnabled(false);
		bindPrice(taxAmount, Price.FIELD_TAX);
		
		// GROSS VALUE
		Text gross = createText(container, Messages.labelGross, Price.FIELD_GROSS);
		gross.addKeyListener(kl);
		bindPrice(gross, Price.FIELD_GROSS);
	}
	
	/**
	 * 
	 */
	protected void recalculatePrice() {
		// wait for the calculation to finish
		if (priceCalculationInProgress) {
			return;
		}
		
		priceCalculationInProgress = true;
		if (Price.FIELD_NET.equals(priceCalculationBase)) {
			price.calculateGrossFromNet(expense.getTaxRate());
		} else if (Price.FIELD_GROSS.equals(priceCalculationBase)) {
			price.calculateNetFromGross(expense.getTaxRate());
		} else {
			LOG.error("Unknown calculation base: " + priceCalculationBase); //$NON-NLS-1$
			return;
		}
		expense.setNetAmount(price.getNet());
		priceCalculationInProgress = false;
	}
	
	/**
	 * 
	 * @param parent
	 * @param label
	 * @param property will be stored using {@link Text#setData(String, Object)} using {@link #KEY_WIDGET_DATA} as key
	 * @return
	 */
	protected Text createText(Composite parent, String label, String property) {
		client.createLabel(parent, label);
		Text text = client.createText(parent, SWT.SINGLE | SWT.BORDER);
		text.setData(KEY_WIDGET_DATA, property);
		WidgetHelper.grabHorizontal(text);
		return text;
	}
	
	/**
	 * Creates and binds.
	 * 
	 * @param parent
	 * @param label
	 * @param model
	 * @param property
	 * @param isBean
	 * @return
	 */
	protected Text createAndBindText(Composite parent, String label, Object model, String property, boolean isBean) {
		Text text = createText(parent, label, property);
		bind(text, model, property, isBean);
		return text;
	}
	
	/**
	 * 
	 * @param parent
	 * @param style
	 * @param label
	 * @param property
	 * @param input
	 * @param labelProvider
	 * @return
	 */
	protected ComboViewer createComboViewer(Composite parent, int style, String label, String property, Class<?> contentType, Object input, LabelProvider labelProvider) {
		client.createLabel(parent, label);
		ComboViewer viewer = new ComboViewer(parent, style);
		viewer.setData(KEY_WIDGET_DATA, property);
		WidgetHelper.grabHorizontal(viewer.getCombo());
		viewer.setContentProvider(new CollectionContentProvider(true));
		viewer.setInput(input);
		viewer.setLabelProvider(labelProvider);
		bind(contentType, viewer, property, true);
		return viewer;
	}
	
	/**
	 * 
	 * @param <T>
	 * @param contentTypeClass
	 * @param viewer
	 * @param property
	 * @param addSelectionListener
	 */
	protected <T> void bind(Class<T> contentTypeClass, StructuredViewer viewer, String property, boolean addSelectionListener) {
		bind(contentTypeClass, viewer, expense, property, addSelectionListener);
	}
	
	/**
	 * 
	 * @param contentTypeClass
	 * @param viewer
	 * @param pojo
	 * @param property
	 * @param addSelectionListener
	 */
	protected <T> void bind(Class<T> contentTypeClass, StructuredViewer viewer, Object pojo, String property, boolean addSelectionListener) {
		UpdateValueStrategy from = new UpdateValueStrategy();
		from.setConverter(new FromStructuredSelectionConverter(contentTypeClass));
		UpdateValueStrategy to = new UpdateValueStrategy();
		to.setConverter(new ToStructuredSelectionConverter(contentTypeClass));
		
		bindingCtx.bindValue(
				ViewersObservables.observeSinglePostSelection(viewer), 
				PojoProperties.value(property).observe(pojo), 
				from, to);
		
		if (addSelectionListener) {
			viewer.addPostSelectionChangedListener(this);
		}		
	}
	
	/**
	 * 
	 * @param text
	 * @param pojo
	 * @param property
	 * @param isBean
	 */
	protected void bind(Text text, Object pojo, String property, boolean isBean) {
		IObservableValue swtObservable = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue pojoObservable = isBean ? BeanProperties.value(property).observe(pojo) : PojoProperties.value(property).observe(pojo);
		bindingCtx.bindValue(swtObservable, pojoObservable);
		pojoObservable.addValueChangeListener(this);
	}
	
	/**
	 * 
	 * @param text
	 * @param property
	 */
	private void bindPrice(Text text, String property) {
		bindMonetaryText(text, price, property, true);
	}
	
	/**
	 * 
	 * @param text
	 * @param pojo
	 * @param property
	 * @param isBean
	 */
	protected void bindMonetaryText(Text text, Object pojo, String property, boolean isBean) {
		IObservableValue swtObservable = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue pojoObservable = isBean ? BeanProperties.value(property).observe(pojo) : PojoProperties.value(property).observe(pojo);
		bindingCtx.bindValue(swtObservable, pojoObservable, toPrice, fromPrice);
		pojoObservable.addValueChangeListener(this);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSource() instanceof ComboViewer) {
			String data = ((ComboViewer) event.getSource()).getData(KEY_WIDGET_DATA).toString();			
			Object selection = ((StructuredSelection) event.getSelection()).getFirstElement();
			LOG.debug(String.format("SELECTION for property [%s] is [%s]", data, selection));
			
			if (Expense.FIELD_TAX_RATE.equals(data)) {
				recalculatePrice();
			}
		}
	}
	
	/**
	 * @see org.eclipse.core.databinding.observable.value.IValueChangeListener#handleValueChange(org.eclipse.core.databinding.observable.value.ValueChangeEvent)
	 */
	@Override
	public void handleValueChange(ValueChangeEvent event) {
		//LOG.debug("modelHasChanged " + event.diff.toString());
		client.modelHasChanged();
	}
}
