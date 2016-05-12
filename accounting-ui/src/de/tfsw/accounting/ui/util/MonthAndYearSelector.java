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
package de.tfsw.accounting.ui.util;

import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

import de.tfsw.accounting.util.TimeFrame;
import de.tfsw.accounting.util.TimeFrameType;

/**
 * UI helper utility that handles choices of year/month combinations based on two {@link Combo} instances.
 * 
 * <p>
 * Any selection by users to either of these two combos will cause the supplied callbacks time frame to be adjusted
 * as requested, and will result in a call to {@link MonthAndYearSelectorCallback#timeFrameChanged()}, for actual 
 * processing.
 * </p> 
 * <p>
 * In addition, shortcut or preset buttons can be adapted to reflect on these two combo selectors. Typically, these
 * represent one of the shortcut {@link TimeFrame} instances like {@link TimeFrame#currentMonth()}. 
 * See {@link #adaptTimeFrameTypeSelector(Button)} for details.
 * </p>
 * 
 * @author Thorsten Frank
 * 
 * @see MonthAndYearSelectorCallback
 */
public class MonthAndYearSelector {
	
	private static final Logger LOG = LogManager.getLogger(MonthAndYearSelector.class);
	
	private static final int SELECTION_WHOLE_YEAR = 0;
	
	private MonthAndYearSelectorCallback callback;
	private Combo months;
	private Combo years;
	private List<Integer> yearsIndex;
	
	/**
	 * Creates a new utility based on the supplied data. None of the parameters must be <code>null</code>.
	 * 
	 * <p>
	 * Both of the supplied combos should be empty - clients only need to create the widgets and adapt their layouts,
	 * all data and event handling is being processed by this instance. 
	 * </p>
	 * 
	 * @param callback client/callback interface
	 * @param months the {@link Combo} representing month selections
	 * @param years the {@link Combo} representing year selections
	 */
	public MonthAndYearSelector(MonthAndYearSelectorCallback callback, Combo months, Combo years) {
		this.callback = callback;
		this.months = months;
		this.years = years;
		initCombos();
	}
	
	/**
	 * The button must supply the required {@link TimeFrameType} using {@link Button#getData(String)} with 
	 * {@link MonthAndYearSelectorCallback#TIME_FRAME_TYPE_KEY} as the key.
	 *  
	 * @param selector
	 */
	public void adaptTimeFrameTypeSelector(final Button selector) {
		Object value = selector.getData(MonthAndYearSelectorCallback.TIME_FRAME_TYPE_KEY);
		if (value != null && (value instanceof TimeFrameType)) {
			final TimeFrameType type = (TimeFrameType)value;
			selector.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					callback.getTimeFrame().adjustInto(type);
					currentTimeFrameChanged(true);
				};
			});
		} else {
			LOG.warn("Requested button doesn't supply proper timeframetype!"); //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 */
	private void initCombos() {
		months.add(TimeFrameType.WHOLE_YEAR.getTranslatedName(), SELECTION_WHOLE_YEAR);
		for (Month month : Month.values()) {
			months.add(month.getDisplayName(TextStyle.FULL, Locale.getDefault()), month.getValue());
		}
		createMonthsSelectionListener();
		
		yearsIndex = new ArrayList<Integer>();
		int index = 0;
		for (int year = callback.getOldestYear(); year <= Year.now().getValue(); year++) {
			years.add(Integer.toString(year), index);
			yearsIndex.add(index, year);
			index++;
		}
		createYearsSelectionListener();
		
		updateComboSelections();
	}
	
	/**
	 * 
	 */
	private void createMonthsSelectionListener() {
		months.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final int selectionIndex = months.getSelectionIndex();
				if (selectionIndex == SELECTION_WHOLE_YEAR) {
					if (years.getSelectionIndex() < 0) {
						// no year selected at the moment - we'll choose the most recent year
						years.select(years.getItemCount() - 1);
					}
					callback.getTimeFrame().setYear(yearsIndex.get(years.getSelectionIndex()), true);
				} else if (selectionIndex >= Month.JANUARY.getValue() && selectionIndex <= Month.DECEMBER.getValue()) {
					callback.getTimeFrame().setMonth(selectionIndex);
				}
				
				currentTimeFrameChanged(false);
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
				callback.getTimeFrame().setYear(yearsIndex.get(years.getSelectionIndex()), false);
				if (months.getSelectionIndex() < SELECTION_WHOLE_YEAR) {
					// if nothing was previously selected, choose the entire year
					months.select(SELECTION_WHOLE_YEAR);
				}
				currentTimeFrameChanged(false);
			}
		});
	}
	
	/**
	 * 
	 */
	private void currentTimeFrameChanged(boolean updateCombos) {
		LOG.debug(String.format("Chosen timeframe is [%s]", callback.getTimeFrame().toString())); //$NON-NLS-1$
		
		if (updateCombos) {
			updateComboSelections();
		}
		
		callback.timeFrameChanged();
	}
	
	/**
	 * 
	 */
	private void updateComboSelections() {
		TimeFrame timeFrame = callback.getTimeFrame();
		if (timeFrame == null) {
			LOG.warn("Current time frame is NULL"); //$NON-NLS-1$
			return;
		}
		LOG.debug("Current time frame type is now: " + timeFrame.getType().name());
		switch (timeFrame.getType()) {
		case CURRENT_YEAR:
		case LAST_YEAR:
		case WHOLE_YEAR:
			months.select(SELECTION_WHOLE_YEAR);
			years.select(yearsIndex.indexOf(timeFrame.getFromYear()));
			break;
		case CURRENT_MONTH:
		case LAST_MONTH:
		case SINGLE_MONTH:
			months.select(timeFrame.getFromMonth());
			years.select(yearsIndex.indexOf(timeFrame.getFromYear()));
			break;
		case CUSTOM:
			months.select(-1);
			years.select(-1);
			break;
		}
	}
}
