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
package de.tfsw.accounting.ui.expense.template;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Spinner;

import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.Frequency;
import de.tfsw.accounting.model.RecurrenceRule;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.expense.editing.BaseExpenseEditHelper;
import de.tfsw.accounting.ui.expense.editing.ExpenseEditingHelperClient;
import de.tfsw.accounting.ui.util.GenericLabelProvider;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author Thorsten Frank
 *
 */
class ExpenseTemplateEditHelper extends BaseExpenseEditHelper {
	
	private static final String FOREVER = "Button.Forever"; //$NON-NLS-1$
	private static final String UNTIL = "Button.Until"; //$NON-NLS-1$
	private static final String OCCURRENCE = "Button.Occurrence"; //$NON-NLS-1$
	private static final int MIN_INTERVAL = 1;
	private static final int MIN_COUNT = 1;
	
	
	private ExpenseTemplate expense;
	private DateTime first;
	private DateTime untilDate;
	private Spinner count;

	/**
	 * @param expense
	 * @param client
	 */
	ExpenseTemplateEditHelper(ExpenseTemplate expense, ExpenseEditingHelperClient client) {
		super(expense, client);
		this.expense = expense;
	}

	protected void createRecurrenceSection(Composite container) {
		RecurrenceRule rule = expense.getRule();
		
		GridDataFactory span2 = GridDataFactory.fillDefaults().span(2, 1);
		
		// ACTIVE
		span2.applyTo(createButton(
				container, Messages.ExpenseTemplateEditHelper_active, SWT.CHECK, expense, ExpenseTemplate.FIELD_ACTIVE, false));
		
		// FIRST APPLICATION
		getClient().createLabel(container, Messages.ExpenseTemplateEditHelper_validFrom);
		first = new DateTime(container, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		first.setData(KEY_WIDGET_DATA, ExpenseTemplate.FIELD_FIRST);
		WidgetHelper.dateToWidget(expense.getFirstApplication(), first);
		first.addSelectionListener(this);
		
		// INTERVAL
		createSpinner(container, SWT.WRAP, Messages.ExpenseTemplateEditHelper_interval, rule,
				RecurrenceRule.FIELD_INTERVAL, false).setMinimum(MIN_INTERVAL);
		
		// FREQUENCY
		createComboViewer(container, SWT.READ_ONLY, Messages.ExpenseTemplateEditHelper_frequency, rule, 
				RecurrenceRule.FIELD_FREQUENCY, Frequency.class, false, Frequency.values(),
				new GenericLabelProvider(Frequency.class, "getTranslatedString")); //$NON-NLS-1$
		
		// RESTRICTIONS
		Composite buttons = new Composite(container, SWT.NULL);
		buttons.setLayout(new GridLayout(8, false));
		span2.applyTo(buttons);
		
		// FOREVER
		Button forever = new Button(buttons, SWT.RADIO);
		forever.setText(Messages.ExpenseTemplateEditHelper_forever);
		forever.setData(KEY_WIDGET_DATA, FOREVER);
		forever.addSelectionListener(this);
		
		// UNTIL
		Button until = new Button(buttons, SWT.RADIO);
		until.setText(Messages.labelUntil);
		until.setData(KEY_WIDGET_DATA, UNTIL);
		until.addSelectionListener(this);
		untilDate = new DateTime(buttons, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		untilDate.setData(KEY_WIDGET_DATA, RecurrenceRule.FIELD_UNTIL);
		
		// COUNT
		Button occurrence = new Button(buttons, SWT.RADIO);
		occurrence.setData(KEY_WIDGET_DATA, OCCURRENCE);
		occurrence.addSelectionListener(this);
		count = createSpinner(buttons, SWT.WRAP, Messages.ExpenseTemplateEditHelper_count, rule, RecurrenceRule.FIELD_COUNT, false);
		count.setMinimum(0);
		count.setMaximum(1000);
		
		// Initial radio button selections
		if (rule.getCount() != null) {
			occurrence.setSelection(true);
			count.setEnabled(true);
			untilDate.setEnabled(false);
		} else if (rule.getUntil() != null) {
			until.setSelection(true);
			WidgetHelper.dateToWidget(rule.getUntil(), untilDate);
			untilDate.setEnabled(true);
			count.setEnabled(false);
		} else {
			forever.setSelection(true);
			untilDate.setEnabled(false);
			count.setEnabled(false);
		}
	}
	
	@Override
	protected void notifiyModelChangeInternal(String origin) {
		if (RecurrenceRule.FIELD_UNTIL.equals(origin)) {
			expense.getRule().setUntil(WidgetHelper.widgetToDate(untilDate));
		} else if (ExpenseTemplate.FIELD_FIRST.equals(origin)) {
			expense.setFirstApplication(WidgetHelper.widgetToDate(first));
		} else if (FOREVER.equals(origin)) {
			expense.getRule().setCount(null);
			expense.getRule().setUntil(null);
			untilDate.setEnabled(false);
			count.setEnabled(false);
		} else if (UNTIL.equals(origin)) {
			expense.getRule().setUntil(WidgetHelper.widgetToDate(untilDate));
			untilDate.setEnabled(true);
			count.setEnabled(false);
		} else if (OCCURRENCE.equals(origin)) {
			// the first time this radio button is selected, the spinner's value is 0, which is an illegal argument for rule.setCount()
			if (count.getSelection() < MIN_COUNT) {
				count.setSelection(MIN_COUNT);
			}
			expense.getRule().setCount(count.getSelection());
			count.setEnabled(true);
			untilDate.setEnabled(false);
		}
	}
}