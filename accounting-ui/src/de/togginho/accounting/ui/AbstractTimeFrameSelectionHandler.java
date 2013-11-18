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

import java.util.Calendar;
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
	private TimeFrame currentTimeFrame;
	private TimeFrame previousTimeFrame;
	private DateTime from;
	private DateTime to;
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	protected Composite buildTimeFrameSelectionComposite(Composite parent, boolean enabledByDefault) {
		// SHADOW_ETCHED_IN, SHADOW_ETCHED_OUT, SHADOW_IN, SHADOW_OUT, SHADOW_NONE 
		Group group = new Group(parent, SWT.SHADOW_NONE);
		group.setText("Time Frame");
		group.setLayout(new GridLayout(4, false));
		WidgetHelper.grabBoth(group);
		WidgetHelper.grabHorizontal(group);
		
		final Button useTime = new Button(group, SWT.CHECK);
		GridDataFactory.fillDefaults().span(4, 1).applyTo(useTime);
		useTime.setText("Constrict to time frame:");
		useTime.setSelection(enabledByDefault);
		
		WidgetHelper.createLabel(group, "Month/Quarter:");
		final Combo months = new Combo(group, SWT.READ_ONLY);
		months.setEnabled(enabledByDefault);
		Calendar cal = Calendar.getInstance();
		for (int i = 0;i <= Calendar.DECEMBER; i++) {
			cal.set(Calendar.MONTH, i);
			months.add(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
		}
		months.add("Whole Year");
		
		WidgetHelper.createLabel(group, "Year:");		
		final Combo years = new Combo(group, SWT.READ_ONLY);
		years.setEnabled(enabledByDefault);
		WidgetHelper.grabHorizontal(years);
		for (int i = 2006;i <= cal.get(Calendar.YEAR); i++) {
			years.add(Integer.toString(i));
		}
		years.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setYear(Integer.valueOf(years.getItems()[years.getSelectionIndex()]), false);
				currentTimeFrameChanged();
			}
		});
		
		months.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int sel = months.getSelectionIndex();
				if (sel <= Calendar.DECEMBER) {
					currentTimeFrame.setMonth(sel, true);
				} else {
					if (years.getSelectionIndex() < 0) {
						years.select(years.getItemCount() - 1);
					}
					currentTimeFrame.setYear(Integer.valueOf(years.getItems()[years.getSelectionIndex()]), true);
				}
				currentTimeFrameChanged();
			}
		});
		
		WidgetHelper.createLabel(group, "Presets:");
		final Combo presets = new Combo(group, SWT.READ_ONLY);
		presets.setEnabled(enabledByDefault);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(presets);
		presets.add("None (Custom Date)");
		presets.add(TimeFrameType.CURRENT_MONTH.getTranslatedName());
		presets.add(TimeFrameType.LAST_MONTH.getTranslatedName());
		presets.add(TimeFrameType.CURRENT_YEAR.getTranslatedName());
		presets.add(TimeFrameType.LAST_YEAR.getTranslatedName());
					
		WidgetHelper.createLabel(group, "Custom/Detailed:");
		from = new DateTime(group, SWT.DROP_DOWN);
		from.setEnabled(false);
		from.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setFrom(WidgetHelper.widgetToDate(from));
				getLogger().debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
			}
		});
		
		WidgetHelper.createLabel(group, Messages.labelUntil);
		to = new DateTime(group, SWT.DROP_DOWN);
		to.setEnabled(false);
		to.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame.setUntil(WidgetHelper.widgetToDate(to));
				getLogger().debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
			}
		});
		
		presets.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (presets.getSelectionIndex()) {
				case 0:
					from.setEnabled(true);
					to.setEnabled(true);
					return; // do nothing
				case 1:
					currentTimeFrame = TimeFrame.currentMonth();
					break;
				case 2:
					currentTimeFrame = TimeFrame.lastMonth();
					break;
				case 3:
					currentTimeFrame = TimeFrame.currentYear();
					break;
				case 4:
					currentTimeFrame = TimeFrame.lastYear();
					break;
				}
				System.out.println("DISABLING");
				from.setEnabled(false);
				to.setEnabled(false);
				currentTimeFrameChanged();
			}
		});
		
		currentTimeFrameChanged();
		
		if (!enabledByDefault) {
			previousTimeFrame = currentTimeFrame;
			currentTimeFrame = null;
		}
		
		useTime.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				months.setEnabled(useTime.getSelection());
				years.setEnabled(useTime.getSelection());
				presets.setEnabled(useTime.getSelection());
				if (useTime.getSelection() && presets.getSelectionIndex() == 0) {
					to.setEnabled(true);
					from.setEnabled(true);					
				} else {
					to.setEnabled(false);
					from.setEnabled(false);
				}
				
				if (useTime.getSelection()) {
					currentTimeFrame = previousTimeFrame;
				} else {
					previousTimeFrame = currentTimeFrame;
					currentTimeFrame = null;
				}
			}
		});
		
		return group;
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
	 */
	private void currentTimeFrameChanged() {
		WidgetHelper.dateToWidget(currentTimeFrame.getFrom(), from);
		WidgetHelper.dateToWidget(currentTimeFrame.getUntil(), to);
		getLogger().debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
	}
}
