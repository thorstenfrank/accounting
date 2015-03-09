/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.tfsw.accounting.ui.util.MonthAndYearSelector;
import de.tfsw.accounting.ui.util.MonthAndYearSelectorCallback;
import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.TimeFrame;
import de.tfsw.accounting.util.TimeFrameType;

/**
 * Abstract base class for command handlers that offer timeframe-based selection dialogs.
 * 
 * @author Thorsten Frank - thorsten.frank@tfsw.de
 *
 */
public abstract class AbstractTimeFrameSelectionHandler extends AbstractAccountingHandler implements MonthAndYearSelectorCallback {
		
	private TimeFrame currentTimeFrame;
	private boolean timeFrameActive = true;
	private DateTime from;
	private DateTime to;
	private Combo months;
	private Combo years;
	private List<Button> typeButtons;
	
	/**
	 * @see de.tfsw.accounting.ui.util.MonthAndYearSelectorCallback#getTimeFrame()
	 */
	@Override
	public TimeFrame getTimeFrame() {
		return timeFrameActive ? currentTimeFrame : null;
	}

	/**
	 * @see de.tfsw.accounting.ui.util.MonthAndYearSelectorCallback#timeFrameChanged()
	 */
	@Override
	public void timeFrameChanged() {
		WidgetHelper.dateToWidget(currentTimeFrame.getFrom(), from);
		WidgetHelper.dateToWidget(currentTimeFrame.getUntil(), to);
	}

	/**
	 * @param currentTimeFrame the currentTimeFrame to set
	 */
	protected void setCurrentTimeFrame(TimeFrame currentTimeFrame) {
		this.currentTimeFrame = currentTimeFrame;
	}
	
	/**
	 *  
	 * @param parent
	 * @return
	 */
	protected Composite buildTimeFrameSelectionComposite(Composite parent, boolean enabledByDefault) {
		timeFrameActive = enabledByDefault;
		
		// SHADOW_ETCHED_IN, SHADOW_ETCHED_OUT, SHADOW_IN, SHADOW_OUT, SHADOW_NONE 
		Group group = new Group(parent, SWT.SHADOW_NONE);
		group.setText(Messages.labelTimeFrame);
		group.setLayout(new GridLayout(4, false));
		WidgetHelper.grabBoth(group);
		WidgetHelper.grabHorizontal(group);
		
		// USE TIME TOGGLE
		final Button useTime = new Button(group, SWT.CHECK);
		GridDataFactory.fillDefaults().span(4, 1).applyTo(useTime);
		useTime.setText(Messages.AbstractTimeFrameSelectionHandler_useTimeFrame);
		useTime.setSelection(enabledByDefault);
		useTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				timeFrameActive = useTime.getSelection();
				handleUseTimeSelection();
			}
		});
		
		// MONTH COMBO
		WidgetHelper.createLabel(group, Messages.labelMonth);
		months = new Combo(group, SWT.READ_ONLY);
		months.setEnabled(enabledByDefault);

		// YEAR COMBO
		WidgetHelper.createLabel(group, Messages.labelYear);		
		years = new Combo(group, SWT.READ_ONLY);
		years.setEnabled(enabledByDefault);
		WidgetHelper.grabHorizontal(years);
		
		GridDataFactory gdf = GridDataFactory.fillDefaults().span(4, 1).grab(true, false);
		
		Composite customComp = new Composite(group, SWT.NONE);
		customComp.setLayout(new GridLayout(4, false));
		gdf.applyTo(customComp);
		
		// CUSTOM DATE SELECTION
		Label customLabel = WidgetHelper.createLabel(customComp, Messages.labelCustom);
		gdf.applyTo(customLabel);
		
		WidgetHelper.createLabel(customComp, Messages.labelFrom);
		from = new DateTime(customComp, SWT.DROP_DOWN);
		from.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setFrom(WidgetHelper.widgetToDate(from));
			}
		});
		
		WidgetHelper.createLabel(customComp, Messages.labelUntil);
		to = new DateTime(customComp, SWT.DROP_DOWN);
		to.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setUntil(WidgetHelper.widgetToDate(to));
			}
		});
		
		MonthAndYearSelector selector = new MonthAndYearSelector(this, months, years);
		
		// PRESETS
		Composite presetsCombo = new Composite(group, SWT.NONE);
		presetsCombo.setLayout(new GridLayout(4, true));
		gdf.applyTo(presetsCombo);
		Label presetsLabel = WidgetHelper.createLabel(presetsCombo, Messages.labelPresets);
		gdf.applyTo(presetsLabel);
		typeButtons = new ArrayList<Button>();
		buildTimeFrameTypeButton(selector, TimeFrameType.CURRENT_MONTH, presetsCombo);
		buildTimeFrameTypeButton(selector, TimeFrameType.LAST_MONTH, presetsCombo);
		buildTimeFrameTypeButton(selector, TimeFrameType.CURRENT_YEAR, presetsCombo);
		buildTimeFrameTypeButton(selector, TimeFrameType.LAST_YEAR, presetsCombo);
		
		timeFrameChanged();
		
		return group;
	}
	
	/**
	 * 
	 * @param type
	 * @param parent
	 * @return
	 */
	private Button buildTimeFrameTypeButton(MonthAndYearSelector selector, TimeFrameType type, Composite parent) {
		Button b = new Button(parent, SWT.PUSH);
		b.setData(TIME_FRAME_TYPE_KEY, type);
		WidgetHelper.grabHorizontal(b);
		typeButtons.add(b);
		b.setText(type.getTranslatedName());
		selector.adaptTimeFrameTypeSelector(b);
		b.setSelection(type == currentTimeFrame.getType());
		return b;
	}
		
	/**
	 * 
	 */
	private void handleUseTimeSelection() {
		months.setEnabled(timeFrameActive);
		years.setEnabled(timeFrameActive);
		from.setEnabled(timeFrameActive);
		to.setEnabled(timeFrameActive);
		for (Button b : typeButtons) {
			b.setEnabled(timeFrameActive);
		}
	}
}
