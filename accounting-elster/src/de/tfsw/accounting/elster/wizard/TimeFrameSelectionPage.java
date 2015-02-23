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
package de.tfsw.accounting.elster.wizard;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.tfsw.accounting.elster.AccountingElsterPlugin;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
class TimeFrameSelectionPage extends AbstractElsterExportWizardPage implements SelectionListener {
	
	private Combo yearCombo;
	private Combo monthCombo;
	private int[] availableYears;
	private Month[] months;
	private YearMonth selectedTimeFrame;
	
	/**
	 * 
	 * @param dataProvider
	 */
	TimeFrameSelectionPage() {
		super(TimeFrameSelectionPage.class.getName(), Messages.TimeFrameSelectionPage_Title, Messages.TimeFrameSelectionPage_Description);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		setControl(control);
		
		control.setLayout(new GridLayout(4, false));
		
		Label lblYear = new Label(control, SWT.NONE);
		lblYear.setText(Messages.TimeFrameSelectionPage_Year);
		
		yearCombo = new Combo(control, SWT.READ_ONLY);
		yearCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true, false));
		yearCombo.addSelectionListener(this);
		
		Label lblTimeFrame = new Label(control, SWT.NONE);
		lblTimeFrame.setText(Messages.TimeFrameSelectionPage_Period);
		
		monthCombo = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
		monthCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true, false));
		monthCombo.addSelectionListener(this);
		
		// default time frame is last month
		selectedTimeFrame = YearMonth.now().minusMonths(1);
		
		this.availableYears = AccountingElsterPlugin.getDefault().getElsterAdapterFactory().getAvailableYears();
		for (int i = 0; i < availableYears.length; i++) {
			yearCombo.add(Integer.toString(availableYears[i]));
			if (availableYears[i] == selectedTimeFrame.getYear()) {
				yearCombo.select(i);
			}
		}
		
		this.months = Month.values();
		int counter = 0;
		for (Month month : months) {
			monthCombo.add(month.getDisplayName(TextStyle.FULL, Locale.getDefault()));
			if (selectedTimeFrame.getMonth().equals(month)) {
				monthCombo.select(counter);
			}
			counter++;
		}
		
		// this page is complete by default, because we already have a timeframe pre-selected
		setPageComplete(true);
		reportTimeFrameChange();
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		try {
			selectedTimeFrame = YearMonth.of(availableYears[yearCombo.getSelectionIndex()], months[monthCombo.getSelectionIndex()]);
			LOG.debug("Selected time frame changed to " + selectedTimeFrame.toString()); //$NON-NLS-1$
			reportTimeFrameChange();
		} catch (Exception ex) {
			LOG.error("Error parsing date", ex); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// nothing to do here...
	}
	
	/**
	 * 
	 */
	private void reportTimeFrameChange() {
		getWizard().timeFrameChanged(selectedTimeFrame);
	}
	

}
