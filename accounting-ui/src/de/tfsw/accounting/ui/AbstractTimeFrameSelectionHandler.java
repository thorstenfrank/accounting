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

import java.time.DateTimeException;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
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

import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.TimeFrame;
import de.tfsw.accounting.util.TimeFrameType;

/**
 * Abstract base class for command handlers that offer timeframe-based selection dialogs.
 * 
 * @author Thorsten Frank - thorsten.frank@tfsw.de
 *
 */
public abstract class AbstractTimeFrameSelectionHandler extends AbstractAccountingHandler {
	
	private static final int SELECTION_WHOLE_YEAR = 0;
	
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
	 * @return
	 */
	protected abstract int getStartDateForYearSelector();
	
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
		months.add(TimeFrameType.WHOLE_YEAR.getTranslatedName(), SELECTION_WHOLE_YEAR);
		
		for (Month month : Month.values()) {
			months.add(month.getDisplayName(TextStyle.FULL, Locale.getDefault()), month.getValue());
		}
		createMonthsSelectionListener();
		
		// YEAR COMBO
		WidgetHelper.createLabel(group, Messages.labelYear);		
		years = new Combo(group, SWT.READ_ONLY);
		years.setEnabled(enabledByDefault);
		WidgetHelper.grabHorizontal(years);
		yearIndexMap = new ArrayList<Integer>();
		int index = 0;
		for (int year = getStartDateForYearSelector(); year <= Year.now().getValue(); year++) {
			years.add(Integer.toString(year), index);
			yearIndexMap.add(index, year);
			index++;
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
		
		currentTimeFrameChanged(true);
		
		return group;
	}
	
	/**
	 * 
	 * @param type
	 * @param parent
	 * @return
	 */
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
				currentTimeFrameChanged(true);
			}
		});
		return b;
	}
	
	/**
	 * 
	 */
	private void currentTimeFrameChanged(boolean updateCombos) {
		WidgetHelper.dateToWidget(currentTimeFrame.getFrom(), from);
		WidgetHelper.dateToWidget(currentTimeFrame.getUntil(), to);
		getLogger().debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
		
		if (updateCombos) {
			updateComboSelections();
		}
	}
	
	/**
	 * 
	 */
	private void updateComboSelections() {
		getLogger().debug("Current time frame type is now: " + currentTimeFrame.getType().name());
		switch (currentTimeFrame.getType()) {
		case CURRENT_YEAR:
		case LAST_YEAR:
		case WHOLE_YEAR:
			months.select(SELECTION_WHOLE_YEAR);
			years.select(yearIndexMap.indexOf(currentTimeFrame.getFromYear()));
			break;
		case CURRENT_MONTH:
		case LAST_MONTH:
		case SINGLE_MONTH:
			months.select(currentTimeFrame.getFromMonth());
			years.select(yearIndexMap.indexOf(currentTimeFrame.getFromYear()));
			break;
		case CUSTOM:
			months.select(-1);
			years.select(-1);
			break;
		}
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
				currentTimeFrame.setYear(Integer.parseInt(years.getItem(years.getSelectionIndex())), false);
				if (months.getSelectionIndex() < SELECTION_WHOLE_YEAR) {
					months.select(SELECTION_WHOLE_YEAR);
				}
				currentTimeFrameChanged(false);
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
				
				try {
					currentTimeFrame.setMonth(months.getSelectionIndex());
				} catch (DateTimeException ex) {
					// this happens when the user selects WHOLE_YEAR (index = 0)
					final int selectionIndex = years.getSelectionIndex();
					if (selectionIndex < 0) {
						// nothing selected at the moment - we'll choose the most recent year
						years.select(years.getItemCount() - 1);
					}
					currentTimeFrame.setYear(Integer.parseInt(years.getItem(selectionIndex)), true);
				}				
				currentTimeFrameChanged(false);
			}
		});
	}
}
