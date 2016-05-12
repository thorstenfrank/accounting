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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

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
public class BaseExpenseEditHelper implements ISelectionChangedListener, KeyListener, SelectionListener {

	/** */
	private static final Logger LOG = LogManager.getLogger(BaseExpenseEditHelper.class);

	/** Key for identifying the widget that has fired an event. */
	protected static final String KEY_WIDGET_DATA = "ExpenseEditHelper.KEY_WIDGET_DATA"; //$NON-NLS-1$
	
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
		createComboViewer(container, SWT.READ_ONLY, Messages.labelExpenseType, Expense.FIELD_TYPE, ExpenseType.class, true, ExpenseType.values(), new GenericLabelProvider(ExpenseType.class, "getTranslatedString"));
				
		// DESCRIPTION
		createAndBindText(container, Messages.labelDescription, expense, Expense.FIELD_DESCRIPTION, false);

		// CATEGORY
		expenseCategories = AccountingUI.getAccountingService().getModelMetaInformation().getExpenseCategories();
		ComboViewer catCombo = createComboViewer(container, SWT.DROP_DOWN, Messages.labelCategory, Expense.FIELD_CATEGORY, String.class, true, expenseCategories, StringLabelProvider.DEFAULT);
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
		
		// NET VALUE
		bindPrice(createText(container, Messages.labelNet, Price.FIELD_NET), Price.FIELD_NET);
		
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
		bindPrice(createText(container, Messages.labelGross, Price.FIELD_GROSS), Price.FIELD_GROSS);
	}
	
	/**
	 * 
	 */
	protected void notifyModelChange(String origin) {
		if (origin == null) {
			LOG.warn("Origin was null, model change notification aborted"); //$NON-NLS-1$
			return;
		}
		
		LOG.debug(String.format("MODEL_CHANGE: [%s]", origin));
		
		if (Expense.FIELD_TAX_RATE.equals(origin)) {
			recalculatePrice();
		} else if (Price.FIELD_NET.equals(origin) || Price.FIELD_GROSS.equals(origin)) {
			priceCalculationBase = origin;
			recalculatePrice();
		}
		
		notifiyModelChangeInternal(origin);
		
		client.modelHasChanged();
	}
	
	/**
	 * Subclasses may override this method. Default implementation is empty.
	 * 
	 * @param origin
	 */
	protected void notifiyModelChangeInternal(String origin) {
		
	}
	
	/**
	 * 
	 */
	protected void recalculatePrice() {
		// we're setting multiple values within a calculation, causing other events to be fired that want to trigger
		// a recalulation - this is to prevent that from happening. While a calculation is taking place, we're not
		// accepting requests
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
	 * 
	 * @param parent
	 * @param label
	 * @param style
	 * @param model
	 * @param property
	 * @param isBean
	 * @return
	 */
	protected Button createButton(Composite parent, String label, int style, Object model, String property, boolean isBean) {
		Button button = new Button(parent, style);
		button.setText(label);
		button.setData(KEY_WIDGET_DATA, property);
		bind(button, model, property, isBean);
		return button;
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
	 * Creates and binds a {@link ComboViewer} to the supplied property of this helper's {@link AbstractExpense} model
	 * object.
	 * 
	 * @param parent
	 * @param style
	 * @param label
	 * @param property
	 * @param contentType
	 * @param includeEmptyElement
	 * @param input
	 * @param labelProvider
	 * @return
	 */
	protected ComboViewer createComboViewer(Composite parent, int style, String label, String property, Class<?> contentType, boolean includeEmptyElement, Object input, LabelProvider labelProvider) {
		client.createLabel(parent, label);
		ComboViewer viewer = new ComboViewer(parent, style);
		viewer.setData(KEY_WIDGET_DATA, property);
		WidgetHelper.grabHorizontal(viewer.getCombo());
		viewer.setContentProvider(new CollectionContentProvider(includeEmptyElement));
		viewer.setInput(input);
		viewer.setLabelProvider(labelProvider);
		bind(contentType, viewer, property, true);
		return viewer;		
	}
	
	/**
	 * 
	 * @param parent
	 * @param style
	 * @param label
	 * @param model
	 * @param property
	 * @param contentType
	 * @param includeEmptyElement
	 * @param input
	 * @param labelProvider
	 * @return
	 */
	protected ComboViewer createComboViewer(Composite parent, int style, String label, Object model, String property, Class<?> contentType, boolean includeEmptyElement, Object input, LabelProvider labelProvider) {
		client.createLabel(parent, label);
		ComboViewer viewer = new ComboViewer(parent, style);
		viewer.setData(KEY_WIDGET_DATA, property);
		WidgetHelper.grabHorizontal(viewer.getCombo());
		viewer.setContentProvider(new CollectionContentProvider(includeEmptyElement));
		viewer.setInput(input);
		viewer.setLabelProvider(labelProvider);
		bind(contentType, viewer, model, property, true);
		return viewer;		
	}
	
	/**
	 * 
	 * @param parent
	 * @param style
	 * @param label may be <code>null</code>
	 * @param model
	 * @param property
	 * @param isBean
	 * @return
	 */
	protected Spinner createSpinner(Composite parent, int style, String label, Object model, String property, boolean isBean) {
		if (label != null) {
			client.createLabel(parent, label);	
		}
		Spinner spinner = new Spinner(parent, style);
		spinner.setData(KEY_WIDGET_DATA, property);
		spinner.setMinimum(0);
		spinner.setMaximum(100);
		spinner.setIncrement(1);
		IObservableValue modelObservable = isBean ? BeanProperties.value(property).observe(model) : PojoProperties.value(property).observe(model);
		bindingCtx.bindValue(WidgetProperties.selection().observe(spinner), modelObservable);
		spinner.addSelectionListener(this);
		return spinner;
	}
	
	/**
	 * Binds to this helpers {@link AbstractExpense} model object. If you need to bind to a different model object,
	 * use {@link #bind(Class, StructuredViewer, Object, String, boolean)} instead.
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
	 * @param model
	 * @param property
	 * @param addSelectionListener
	 */
	protected <T> void bind(Class<T> contentTypeClass, StructuredViewer viewer, Object model, String property, boolean addSelectionListener) {
		UpdateValueStrategy from = new UpdateValueStrategy();
		from.setConverter(new FromStructuredSelectionConverter(contentTypeClass));
		UpdateValueStrategy to = new UpdateValueStrategy();
		to.setConverter(new ToStructuredSelectionConverter(contentTypeClass));
		
		bindingCtx.bindValue(
				ViewersObservables.observeSinglePostSelection(viewer), 
				PojoProperties.value(property).observe(model), 
				from, to);
		
		if (addSelectionListener) {
			viewer.addPostSelectionChangedListener(this);
		}		
	}
	
	/**
	 * 
	 * @param text
	 * @param model
	 * @param property
	 * @param isBean
	 */
	protected void bind(Text text, Object model, String property, boolean isBean) {
		IObservableValue swtObservable = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue modelObservable = isBean ? BeanProperties.value(property).observe(model) : PojoProperties.value(property).observe(model);
		bindingCtx.bindValue(swtObservable, modelObservable);
		text.addKeyListener(this);
		//modelObservable.addValueChangeListener(this);
	}
	
	/**
	 * 
	 * @param button
	 * @param model
	 * @param property
	 * @param isBean
	 */
	protected void bind(Button button, Object model, String property, boolean isBean) {
		bindingCtx.bindValue(WidgetProperties.selection().observe(button),PojoProperties.value(property).observe(model));
		button.addSelectionListener(this);
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
	 * @param model
	 * @param property
	 * @param isBean
	 */
	protected void bindMonetaryText(Text text, Object model, String property, boolean isBean) {
		IObservableValue swtObservable = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue modelObservable = isBean ? BeanProperties.value(property).observe(model) : PojoProperties.value(property).observe(model);
		bindingCtx.bindValue(swtObservable, modelObservable, toPrice, fromPrice);
		text.addKeyListener(this);
		//modelObservable.addValueChangeListener(this);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		String origin;
		if (event.getSource() instanceof StructuredViewer) {
			origin = ((StructuredViewer) event.getSource()).getData(KEY_WIDGET_DATA).toString();			
			Object selection = ((StructuredSelection) event.getSelection()).getFirstElement();
			LOG.debug(String.format("SELECTION for property [%s] is [%s]", origin, selection));
			notifyModelChange(origin);			
		} else if (event.getSource() instanceof Widget) {
			extractOriginFromWidget((Widget) event.getSource());
		}
	}
	
	/**
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
	}
	
	/**
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() instanceof Widget) {
			extractOriginFromWidget((Widget) e.getSource());
		}
		
	}
	
	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() instanceof Widget) {
			extractOriginFromWidget((Widget) e.getSource());
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	/**
	 * 
	 * @param widget
	 */
	private void extractOriginFromWidget(Widget widget) {
		Object data = widget.getData(KEY_WIDGET_DATA);
		if (data == null || !(data instanceof String)) {
			LOG.warn(String.format("Illegal data stored in widget [%s] under key [%s] : [%s]", widget.toString(), KEY_WIDGET_DATA, data));
		} else {
			notifyModelChange((String) data);
		}
	}
//
//	/**
//	 * @see org.eclipse.core.databinding.observable.value.IValueChangeListener#handleValueChange(org.eclipse.core.databinding.observable.value.ValueChangeEvent)
//	 */
//	@Override
//	public void handleValueChange(ValueChangeEvent event) {
//		if (event.getSource() instanceof ISWTObservable) {
//			notifyModelChange(getOriginFromWidget(((ISWTObservableValue) event.getSource()).getWidget()));
//		} else {
//			LOG.warn(String.format("Unknown value change source: [%s]", event.getSource().toString()));
//		}
//		
//	}
}
