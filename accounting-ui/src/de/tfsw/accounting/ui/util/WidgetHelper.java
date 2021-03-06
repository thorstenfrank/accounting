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
package de.tfsw.accounting.ui.util;

import java.time.LocalDate;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.conversion.FromStructuredSelectionConverter;
import de.tfsw.accounting.ui.conversion.ToStructuredSelectionConverter;

/**
 * Simple helper class containing often used widget creation code.
 * 
 * @author Thorsten Frank
 */
public class WidgetHelper {
	
	private static final GridDataFactory GRAB_HORIZONTAL = GridDataFactory.fillDefaults().grab(true, false);
	
	private static final GridDataFactory GRAB_BOTH = GridDataFactory.fillDefaults().grab(true, true);
	
	private static final int DATE_TIME_MONTH_OFFSET = 1;
	
	/**
	 * Private since this is only a utility class.
	 */
	private WidgetHelper() {
		
	}
		
	/**
	 * Creates a new label with the specified parent and text.
	 * 
	 * @param parent parent
	 * @param text text to display, may be <code>null</code>
	 * @return the {@link Label}
	 */
	public static Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NULL);
		if (text != null) {
			label.setText(text);	
		}
		return label;
	}
		
	/**
	 * Creates a simple text field with styles {@link SWT#SINGLE} and {@link SWT#BORDER}.
	 * 
	 * @param parent parent composite
	 * @param text text for the widget, may be <code>null</code>
	 * @return the {@link Text}
	 */
	public static Text createSingleBorderText(Composite parent, String text) {
		Text theText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		if (text != null) {
			theText.setText(text);
		}
		GRAB_HORIZONTAL.applyTo(theText);
		selectAllOnEntry(theText);
		return theText;
	}
	
	/**
	 * Applies the date currently represented by the supplied widget to a new {@link LocalDate}.
	 * 
	 * @param dateTime	source
	 * @return the {@link LocalDate} that represents the date as contained in the supplied widget
	 */
	public static LocalDate widgetToDate(DateTime dateTime) {
		return LocalDate.of(dateTime.getYear(), dateTime.getMonth() + DATE_TIME_MONTH_OFFSET, dateTime.getDay());
	}
	
	/**
	 * Applies the day, month and year of the supplied {@link LocalDate} to the supplied {@link DateTime} widget.
	 * 
	 * @param date		source
	 * @param dateTime	target
	 */
	public static void dateToWidget(LocalDate date, DateTime dateTime) {
		dateTime.setDate(date.getYear(), date.getMonthValue() - DATE_TIME_MONTH_OFFSET, date.getDayOfMonth());
	}
	
	/**
	 * The same as <code>GridDataFactory.fillDefaults().grab(true, false).applyTo(control)</code>.
	 * 
	 * @param control the {@link Control} to apply the grid layout data to
	 */
	public static void grabHorizontal(Control control) {
		GRAB_HORIZONTAL.applyTo(control);
	}
	
	/**
	 * The same as <code>GridDataFactory.fillDefaults().grab(true, true).applyTo(control)</code>.
	 * 
	 * @param control the {@link Control} to apply the grid layout data to
	 */
	public static void grabBoth(Control control) {
		GRAB_BOTH.applyTo(control);
	}
	
	/**
	 * 
	 * @param text
	 */
	public static void selectAllOnEntry(final Text text) {
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});
		
		text.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				text.selectAll();
			}
		});
	}
	
	/**
	 * 
	 * @param parent
	 * @param bindingContext
	 * @param pojo
	 * @param property
	 * @return
	 */
	public static ComboViewer createTaxRateCombo(
			Composite parent, DataBindingContext bindingContext, Object pojo, String property) {
		
		ComboViewer taxRateCombo = new ComboViewer(parent, SWT.READ_ONLY);
		grabHorizontal(taxRateCombo.getCombo());
		taxRateCombo.setContentProvider(CollectionContentProvider.WITH_EMPTY);
		taxRateCombo.setInput(AccountingUI.getAccountingService().getCurrentUser().getTaxRates());
		taxRateCombo.setLabelProvider(new GenericLabelProvider(TaxRate.class, "toShortString"));
		UpdateValueStrategy from = new UpdateValueStrategy();
		from.setConverter(new FromStructuredSelectionConverter(TaxRate.class));
		UpdateValueStrategy to = new UpdateValueStrategy();
		to.setConverter(new ToStructuredSelectionConverter(TaxRate.class));
		bindingContext.bindValue(
				ViewersObservables.observeSinglePostSelection(taxRateCombo), 
				PojoProperties.value(property).observe(pojo),
				from, to);
		
		return taxRateCombo;
	}
}
