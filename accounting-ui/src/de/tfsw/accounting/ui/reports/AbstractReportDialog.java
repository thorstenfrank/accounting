/*
 *  Copyright 2012 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.model.ModelMetaInformation;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.FormatUtil;
import de.tfsw.accounting.util.TimeFrame;
import de.tfsw.accounting.util.TimeFrameType;

/**
 * @author thorsten
 *
 */
public abstract class AbstractReportDialog extends TrayDialog {

	/**
	 * 
	 */
	private static final int INDEX_WHOLE_YEAR = Calendar.DECEMBER + 1;
	
	private TimeFrame timeFrame;
	private Combo months;
	private Combo years;
	private List<Integer> yearIndexMap;
	private List<Button> typeButtons;
	
	/**
	 * 
	 * @param shell
	 */
	public AbstractReportDialog(Shell shell, TimeFrame timeFrame) {
		super(shell);
		setHelpAvailable(true);
		this.timeFrame = timeFrame;
	}
	
	/**
	 * 
	 * @param parent
	 */
	protected Section createQuerySection(Composite parent) {
		return createQuerySection(parent, new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
	}
	
	/**
	 * 
	 * @param parent
	 * @param gridData
	 * @return
	 */
	protected Section createQuerySection(Composite parent, GridData gridData) {
		final FormToolkit formToolkit = getToolkit();
		Section querySection = formToolkit.createSection(parent, Section.TITLE_BAR);
		querySection.setLayoutData(gridData);
		formToolkit.paintBordersFor(querySection);
		querySection.setText(Messages.labelTimeFrame);
		
		Composite sectionClient = new Composite(querySection, SWT.NONE);
		formToolkit.adapt(sectionClient);
		formToolkit.paintBordersFor(sectionClient);
		querySection.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(5, false));
		
		// MONTH COMBO
		buildMonthSelector(sectionClient);
		
		// YEAR COMBO
		buildYearSelector(sectionClient);
		
		// PRESETS
		createPresetButtons(sectionClient);
				
		if (needsCustomQueryParameters()) {
			Composite customParams = new Composite(sectionClient, SWT.NONE);
			GridDataFactory.fillDefaults().span(5, 1).applyTo(customParams);
			addCustomQueryParameters(customParams);			
		}

		updateComboSelections(false);
		
		return querySection;		
	}

	/**
	 * @param sectionClient
	 */
	private void buildMonthSelector(Composite sectionClient) {
		Calendar cal = Calendar.getInstance();
		
		getToolkit().createLabel(sectionClient, Messages.labelMonth);
		months = new Combo(sectionClient, SWT.READ_ONLY);
		getToolkit().adapt(months);
		getToolkit().paintBordersFor(months);
		for (int i = 0;i <= Calendar.DECEMBER; i++) {
			cal.set(Calendar.MONTH, i);
			months.add(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
		}
		months.add(TimeFrameType.WHOLE_YEAR.getTranslatedName());
		months.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (months.getSelectionIndex() <= Calendar.DECEMBER) {
					timeFrame.setMonth(months.getSelectionIndex());
				} else {
					if (years.getSelectionIndex() < 0) {
						years.select(years.getItemCount() - 1);
					}
					timeFrame.setYear(yearIndexMap.get(years.getSelectionIndex()), true);
				}
				updateComboSelections(true);
			}
		});
	}

	/**
	 * @param parent
	 */
	private void buildYearSelector(Composite parent) {
		final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		getToolkit().createLabel(parent, Messages.labelYear);		
		years = new Combo(parent, SWT.READ_ONLY);
		getToolkit().adapt(years);
		getToolkit().paintBordersFor(years);
		WidgetHelper.grabHorizontal(years);
		yearIndexMap = new ArrayList<Integer>();
		ModelMetaInformation meta = AccountingUI.getAccountingService().getModelMetaInformation();
		int oldest = meta.getOldestKnownExpenseDate().isAfter(meta.getOldestKnownInvoiceDate()) 
				? meta.getOldestKnownInvoiceDate().getYear() 
				: meta.getOldestKnownExpenseDate().getYear();
		for (int i = oldest; i <= currentYear; i++) {
			years.add(Integer.toString(i));
			yearIndexMap.add(i);
		}
		years.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				timeFrame.setYear(yearIndexMap.get(years.getSelectionIndex()), false);
				if (months.getSelectionIndex() < 0) {
					months.select(INDEX_WHOLE_YEAR);
				}
				updateComboSelections(true);
			}
		});
	}
	
	/**
	 * @param parent
	 */
	private void createPresetButtons(Composite parent) {
		Composite presetsCombo = new Composite(parent, SWT.NONE);
		presetsCombo.setLayout(new GridLayout(4, true));
		GridDataFactory.fillDefaults().span(5, 1).grab(true, false).applyTo(presetsCombo);
		
		Label presetsLabel = WidgetHelper.createLabel(presetsCombo, Messages.labelPresets);
		GridDataFactory.fillDefaults().span(4, 1).applyTo(presetsLabel);
		typeButtons = new ArrayList<Button>();
		buildTimeFrameTypeButton(TimeFrameType.CURRENT_MONTH, presetsCombo);
		buildTimeFrameTypeButton(TimeFrameType.LAST_MONTH, presetsCombo);
		buildTimeFrameTypeButton(TimeFrameType.CURRENT_YEAR, presetsCombo);
		buildTimeFrameTypeButton(TimeFrameType.LAST_YEAR, presetsCombo);
	}
	
	/**
	 * 
	 * @param type
	 * @param parent
	 * @return
	 */
	private Button buildTimeFrameTypeButton(TimeFrameType type, Composite parent) {
		final Button b = getToolkit().createButton(parent, type.getTranslatedName(), SWT.PUSH);
		WidgetHelper.grabHorizontal(b);
		typeButtons.add(b);
		final TimeFrame tf;
		switch (type) {
		case CURRENT_YEAR:
			tf = TimeFrame.currentYear();
			break;
		case LAST_MONTH:
			tf = TimeFrame.lastMonth();
			break;
		case LAST_YEAR:
			tf = TimeFrame.lastYear();
			break;
		default:
			tf = TimeFrame.currentMonth();
			break;
		}
		
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				timeFrame = tf;
				updateComboSelections(true);
			}
		});
		return b;
	}
	
	/**
	 * Subclasses must overwrite this method if they want to use {@link #addCustomQueryParameters(Composite)}.
	 * @return	<code>true</code> if custom additions to the query param section need to be made
	 */
	protected boolean needsCustomQueryParameters() {
		return false;
	}
	
	/**
	 * 
	 * @param customParams
	 */
	protected void addCustomQueryParameters(Composite customParams) {
		// nothing to do in the default implementation
	}
		
	/**
	 * 
	 * @return
	 */
	protected abstract FormToolkit getToolkit();

	/**
	 * 
	 */
	protected abstract void updateModel();
	
	/**
	 * 
	 */
	protected abstract void handleExport();
	
	/**
	 * 
	 * @return
	 */
	protected TimeFrame getTimeFrame() {
		return timeFrame;
	}
		
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		final Button exportButton = createButton(parent, IDialogConstants.PROCEED_ID, Messages.labelExport, false);
		exportButton.setImage(AccountingUI.getImageDescriptor(Messages.iconsExportToPdf).createImage());
		
		// the ID is OK, because IDialogConstants.CLOSE_ID won't cause the dialog to close - for whatever reason
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}
	
	/**
	 * The super() implementation for some reason cut the size of the button too small - hence the override.
	 * {@inheritDoc}
	 * @see Dialog#setButtonLayoutData(Button)
	 */
	@Override
	protected void setButtonLayoutData(Button button) {
		WidgetHelper.grabHorizontal(button);
	}
	
	/**
	 * Always returns <code>true</code>.
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	@Override
	protected boolean isResizable() {
	    return true;
	}
	
	/**
	 * 
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.PROCEED_ID) {
			try {
				handleExport();
			} catch (AccountingException e) {
				MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
				msgBox.setText(Messages.labelError);
				msgBox.setMessage(e.getLocalizedMessage());
				msgBox.open();
			}
		} else {
			super.buttonPressed(buttonId);
		}
	}
	


	/**
     * 
     * @param text
     * @param value
     */
    protected void setCurrencyValue(Text text, BigDecimal value) {
    	if (value == null) {
    		value = BigDecimal.ZERO;
    	}
    	text.setText(FormatUtil.formatCurrency(value));
    }
    
	/**
	 * 
	 */
	private void updateComboSelections(boolean updateModel) {
		int monthIndex = -1;
		int yearIndex = -1;
		
		yearIndex = yearIndexMap.indexOf(timeFrame.getFromYear());
		monthIndex = timeFrame.getFromMonth();

		switch (timeFrame.getType()) {
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
		
		if (updateModel) {
			updateModel();
		}
	}
}
