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
package de.togginho.accounting.ui;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
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

/**
 * Simple helper class containing often used widget creation code.
 * 
 * @author tfrank1
 */
public class WidgetHelper {
	
	private static final GridDataFactory GDF = GridDataFactory.fillDefaults().grab(true, false);
	
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
		GDF.applyTo(theText);
		selectAllOnEntry(theText);
		return theText;
	}

	/**
	 * Transforms the contents of the supplied {@link DateTime} widget to a {@link Date}.
	 * All fields except for the day, month and year are nullified.
	 * 
	 * @param dateTime source
	 * @return the {@link Date} that 
	 */
	public static Date widgetToDate(DateTime dateTime) {
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
		cal.set(Calendar.MONTH, dateTime.getMonth());
		cal.set(Calendar.YEAR, dateTime.getYear());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	/**
	 * Applies the day, month and year of the supplied {@link Date} to the supplied {@link DateTime} widget.
	 * 
	 * @param date		source
	 * @param dateTime  target
	 */
	public static void dateToWidget(Date date, DateTime dateTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		dateToWidget(cal, dateTime);
	}
	
	/**
	 * Applies the day, month and year of the supplied {@link Calendar} to the supplied {@link DateTime} widget.
	 * 
	 * @param cal		source
	 * @param dateTime  target
	 */
	public static void dateToWidget(Calendar cal, DateTime dateTime) {
		dateTime.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * The same as <code>GridDataFactory.fillDefaults().grab(true, false).applyTo(control)</code>.
	 */
	public static void grabHorizontal(Control control) {
		GDF.applyTo(control);
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
}
