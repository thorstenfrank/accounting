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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

import de.togginho.accounting.ui.util.WidgetHelper;
import de.togginho.accounting.util.TimeFrame;
import de.togginho.accounting.util.TimeFrameType;

/**
 * @author thorsten
 *
 */
public abstract class AbstractTimeFrameSelectionHandler extends AbstractAccountingHandler {
	
	/**
	 * 
	 */
	private static final int INDEX_WHOLE_YEAR = Calendar.DECEMBER + 1;
	
	private TimeFrame currentTimeFrame;
	private boolean timeFrameActive = true;
	
	private DateTime from;
	private DateTime to;
	private Combo months;
	private Combo years;
	private List<Integer> yearIndexMap;
	private List<Button> typeButtons;
	
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
		Calendar cal = Calendar.getInstance();
		for (int i = 0;i <= Calendar.DECEMBER; i++) {
			cal.set(Calendar.MONTH, i);
			months.add(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
		}
		months.add(TimeFrameType.WHOLE_YEAR.getTranslatedName());
		createMonthsSelectionListener();
		
		// YEAR COMBO
		WidgetHelper.createLabel(group, Messages.labelYear);		
		years = new Combo(group, SWT.READ_ONLY);
		years.setEnabled(enabledByDefault);
		WidgetHelper.grabHorizontal(years);
		yearIndexMap = new ArrayList<Integer>();
		for (int i = getStartDateForYearSelector().get(Calendar.YEAR); i <= cal.get(Calendar.YEAR); i++) {
			years.add(Integer.toString(i));
			yearIndexMap.add(i);
		}
		createYearsSelectionListener();
		
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
		
		// PRESETS
		Composite presetsCombo = new Composite(group, SWT.NONE);
		presetsCombo.setLayout(new GridLayout(4, true));
		gdf.applyTo(presetsCombo);
		Label presetsLabel = WidgetHelper.createLabel(presetsCombo, Messages.labelPresets);
		gdf.applyTo(presetsLabel);
		typeButtons = new ArrayList<Button>();
		buildTimeFrameTypeButton(TimeFrameType.CURRENT_MONTH, presetsCombo);
		buildTimeFrameTypeButton(TimeFrameType.LAST_MONTH, presetsCombo);
		buildTimeFrameTypeButton(TimeFrameType.CURRENT_YEAR, presetsCombo);
		buildTimeFrameTypeButton(TimeFrameType.LAST_YEAR, presetsCombo);
		
		currentTimeFrameChanged();
		
		return group;
	}
	
	private Button buildTimeFrameTypeButton(TimeFrameType type, Composite parent) {
		final Button b = new Button(parent, SWT.PUSH);
		WidgetHelper.grabHorizontal(b);
		typeButtons.add(b);
		b.setText(type.getTranslatedName());
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
		
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame = timeFrame;
				currentTimeFrameChanged();
			}
		});
		return b;
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract Calendar getStartDateForYearSelector();
	
	/**
	 * @return the currentTimeFrame
	 */
	protected TimeFrame getCurrentTimeFrame() {
		return timeFrameActive ? currentTimeFrame : null;
	}

	/**
	 * @param currentTimeFrame the currentTimeFrame to set
	 */
	protected void setCurrentTimeFrame(TimeFrame currentTimeFrame) {
		this.currentTimeFrame = currentTimeFrame;
	}
	
	/**
	 * 
	 */
	private void currentTimeFrameChanged() {
		WidgetHelper.dateToWidget(currentTimeFrame.getFrom(), from);
		WidgetHelper.dateToWidget(currentTimeFrame.getUntil(), to);
		getLogger().debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
		
		updateComboSelections();
	}
	
	/**
	 * 
	 */
	private void updateComboSelections() {
		getLogger().debug("Current time frame type is now: " + currentTimeFrame.getType().name());
		int monthIndex = -1;
		int yearIndex = -1;
		
		yearIndex = yearIndexMap.indexOf(currentTimeFrame.getFromYear());
		monthIndex = currentTimeFrame.getFromMonth();

		switch (currentTimeFrame.getType()) {
		case CURRENT_YEAR:
		case LAST_YEAR:
		case WHOLE_YEAR:
			monthIndex = INDEX_WHOLE_YEAR;
			break;
		case CUSTOM:
			monthIndex = -1;
			yearIndex = -1;
			break;
		}
		
		months.select(monthIndex);
		years.select(yearIndex);
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
	
	/**
	 * 
	 */
	private void createYearsSelectionListener() {
		years.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setYear(yearIndexMap.get(years.getSelectionIndex()), false);
				if (months.getSelectionIndex() < 0) {
					months.select(INDEX_WHOLE_YEAR);
				}
				currentTimeFrameChanged();
			}
		});
	}

	/**
	 * 
	 */
	private void createMonthsSelectionListener() {
		months.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (months.getSelectionIndex() <= Calendar.DECEMBER) {
					currentTimeFrame.setMonth(months.getSelectionIndex());
				} else {
					if (years.getSelectionIndex() < 0) {
						years.select(years.getItemCount() - 1);
					}
					currentTimeFrame.setYear(yearIndexMap.get(years.getSelectionIndex()), true);
				}
				currentTimeFrameChanged();
			}
		});
	}
}
