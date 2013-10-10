/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

import de.togginho.accounting.ui.util.WidgetHelper;
import de.togginho.accounting.util.TimeFrame;
import de.togginho.accounting.util.TimeFrameType;

/**
 * @author thorsten
 *
 */
public abstract class AbstractTimeFrameSelectionHandler extends AbstractAccountingHandler {
	private TimeFrame currentTimeFrame;
	private DateTime from;
	private DateTime to;
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	protected Composite buildTimeFrameSelectionComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		WidgetHelper.grabHorizontal(composite);
		
		buildButtonForTimeFrame(composite, TimeFrameType.CURRENT_MONTH);
		buildButtonForTimeFrame(composite, TimeFrameType.LAST_MONTH);
		buildButtonForTimeFrame(composite, TimeFrameType.CURRENT_YEAR);
		buildButtonForTimeFrame(composite, TimeFrameType.LAST_YEAR);
		
		final Button custom = new Button(composite, SWT.RADIO);
		custom.setSelection(currentTimeFrame.getType() == TimeFrameType.CUSTOM);
		
		Composite dates = new Composite(composite, SWT.NONE);
		dates.setLayout(new RowLayout());
		
		WidgetHelper.createLabel(dates, Messages.labelFrom);
		from = new DateTime(dates, SWT.DROP_DOWN);
		from.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setFrom(WidgetHelper.widgetToDate(from));
				getLogger().debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
				custom.setSelection(true);
			}
		});
		
		WidgetHelper.createLabel(dates, Messages.labelUntil);
		to = new DateTime(dates, SWT.DROP_DOWN);
		to.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setUntil(WidgetHelper.widgetToDate(to));
				getLogger().debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
				custom.setSelection(true);
			}
		});
		
		currentTimeFrameChanged();
		
		return composite;
	}
		
	/**
	 * @return the currentTimeFrame
	 */
	public TimeFrame getCurrentTimeFrame() {
		return currentTimeFrame;
	}

	/**
	 * @param currentTimeFrame the currentTimeFrame to set
	 */
	public void setCurrentTimeFrame(TimeFrame currentTimeFrame) {
		this.currentTimeFrame = currentTimeFrame;
	}
	
	/**
	 * 
	 * @param parent
	 * @param type
	 */
	private void buildButtonForTimeFrame(Composite parent, TimeFrameType type) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(type.getTranslatedName());
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(button);
		final TimeFrame timeFrame;
		switch (type) {
		case CURRENT_YEAR:
			timeFrame = TimeFrame.currentYear();
			break;
		case LAST_MONTH:
			timeFrame = TimeFrame.lastMonth();
			break;
		case LAST_YEAR:
			timeFrame = TimeFrame.lastYear();
			break;
		default:
			timeFrame = TimeFrame.currentMonth();
			break;
		}
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (button.getSelection()) { // only need to react if the button was actually selected (ignore deselect)
					currentTimeFrame = timeFrame;
					currentTimeFrameChanged();					
				}
			}
		});
		
		button.setSelection(currentTimeFrame.getType() == type);
	}
	
	/**
	 * 
	 */
	private void currentTimeFrameChanged() {
		WidgetHelper.dateToWidget(currentTimeFrame.getFrom(), from);
		WidgetHelper.dateToWidget(currentTimeFrame.getUntil(), to);
		getLogger().debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
	}
}
