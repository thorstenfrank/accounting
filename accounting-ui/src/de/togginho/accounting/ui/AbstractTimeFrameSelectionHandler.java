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
	
	private static final int PRESET_INDEX_CUSTOM = 0;
	private static final int PRESET_INDEX_CM = 1;
	private static final int PRESET_INDEX_LM = 2;
	private static final int PRESET_INDEX_CY = 3;
	private static final int PRESET_INDEX_LY = 4;
	
	private TimeFrame currentTimeFrame;
	private boolean timeFrameActive = true;
	
	private DateTime from;
	private DateTime to;
	private Combo months;
	private Combo years;
	private Combo presets;
	
	private List<Integer> yearIndexMap;
	
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
		
		// PRESETS
		WidgetHelper.createLabel(group, Messages.labelPresets);
		presets = new Combo(group, SWT.READ_ONLY);
		presets.setEnabled(enabledByDefault);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(presets);
		presets.add(Messages.labelPresetsNone, PRESET_INDEX_CUSTOM);
		presets.add(TimeFrameType.CURRENT_MONTH.getTranslatedName(), PRESET_INDEX_CM);
		presets.add(TimeFrameType.LAST_MONTH.getTranslatedName(), PRESET_INDEX_LM);
		presets.add(TimeFrameType.CURRENT_YEAR.getTranslatedName(), PRESET_INDEX_CY);
		presets.add(TimeFrameType.LAST_YEAR.getTranslatedName(), PRESET_INDEX_LY);
		createPresetsSelectionListener();
		
		// CUSTOM DATE SELECTION
		WidgetHelper.createLabel(group, Messages.labelCustom);
		from = new DateTime(group, SWT.DROP_DOWN);
		from.setEnabled(false);
		from.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setFrom(WidgetHelper.widgetToDate(from));
			}
		});
		
		WidgetHelper.createLabel(group, Messages.labelUntil);
		to = new DateTime(group, SWT.DROP_DOWN);
		to.setEnabled(false);
		to.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setUntil(WidgetHelper.widgetToDate(to));
			}
		});
		
		currentTimeFrameChanged();
		
		return group;
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
		int monthIndex = -1;
		int yearIndex = -1;
		int presetIndex = -1;
		
		yearIndex = yearIndexMap.indexOf(currentTimeFrame.getFromYear());
		monthIndex = currentTimeFrame.getFromMonth();

		switch (currentTimeFrame.getType()) {
		case CURRENT_MONTH:
			presetIndex = PRESET_INDEX_CM;
			break;
		case LAST_MONTH:
			presetIndex = PRESET_INDEX_LM;
			break;
		case CURRENT_YEAR:
			monthIndex = months.getItemCount() - 1;
			presetIndex = PRESET_INDEX_CY;
			break;
		case LAST_YEAR:
			monthIndex = months.getItemCount() - 1;
			presetIndex = PRESET_INDEX_LY;
			break;
		case WHOLE_YEAR:
			monthIndex = months.getItemCount() - 1;
			break;
		default:
			monthIndex = -1;
			yearIndex = -1;
			presetIndex = 0;
		}
		
		months.select(monthIndex);
		years.select(yearIndex);
		presets.select(presetIndex);
	}
	
	/**
	 * 
	 */
	private void handleUseTimeSelection() {
		months.setEnabled(timeFrameActive);
		years.setEnabled(timeFrameActive);
		presets.setEnabled(timeFrameActive);
		if (timeFrameActive && presets.getSelectionIndex() == PRESET_INDEX_CUSTOM) {
			to.setEnabled(true);
			from.setEnabled(true);					
		} else {
			to.setEnabled(false);
			from.setEnabled(false);
		}
	}
	
	/**
	 * 
	 */
	private void createPresetsSelectionListener() {
		presets.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (presets.getSelectionIndex()) {
				case PRESET_INDEX_CUSTOM:
					from.setEnabled(true);
					to.setEnabled(true);
					return; // do nothing
				case PRESET_INDEX_CM:
					currentTimeFrame = TimeFrame.currentMonth();
					break;
				case PRESET_INDEX_LM:
					currentTimeFrame = TimeFrame.lastMonth();
					break;
				case PRESET_INDEX_CY:
					currentTimeFrame = TimeFrame.currentYear();
					break;
				case PRESET_INDEX_LY:
					currentTimeFrame = TimeFrame.lastYear();
					break;
				}
				from.setEnabled(false);
				to.setEnabled(false);
				currentTimeFrameChanged();
			}
		});
	}

	/**
	 * 
	 */
	private void createYearsSelectionListener() {
		years.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setYear(yearIndexMap.get(years.getSelectionIndex()), false);
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
				int sel = months.getSelectionIndex();
				if (sel <= Calendar.DECEMBER) {
					currentTimeFrame.setMonth(sel, true);
				} else {
					if (years.getSelectionIndex() < 0) {
						years.select(INDEX_WHOLE_YEAR);
					}
					currentTimeFrame.setYear(yearIndexMap.get(years.getSelectionIndex()), true);
				}
				currentTimeFrameChanged();
			}
		});
	}
}
