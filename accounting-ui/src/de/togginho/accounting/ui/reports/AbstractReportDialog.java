/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.reports;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;
import de.togginho.accounting.util.TimeFrame;
import de.togginho.accounting.util.TimeFrameType;

/**
 * @author thorsten
 *
 */
public abstract class AbstractReportDialog extends TrayDialog {

	private DateTime fromDate;
	private DateTime untilDate;
	
	/**
	 * 
	 * @param shell
	 */
	public AbstractReportDialog(Shell shell) {
		super(shell);
		setHelpAvailable(true);
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
		
		formToolkit.createLabel(sectionClient, Messages.labelFrom);
		fromDate = new DateTime(sectionClient, SWT.BORDER | SWT.DROP_DOWN);
		formToolkit.adapt(fromDate);
		formToolkit.paintBordersFor(fromDate);
		
		formToolkit.createLabel(sectionClient, Messages.labelUntil);
		untilDate = new DateTime(sectionClient, SWT.BORDER | SWT.DROP_DOWN);
		formToolkit.adapt(untilDate);
		formToolkit.paintBordersFor(untilDate);
				
		final Button btnSearch = formToolkit.createButton(sectionClient, Messages.labelSearch, SWT.PUSH);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(btnSearch);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateModel();
			}
		});
		
		Composite timeFrameShortcuts = new Composite(sectionClient, SWT.NONE);
		formToolkit.adapt(timeFrameShortcuts);
		timeFrameShortcuts.setLayout(new GridLayout(4, false));
		GridDataFactory.fillDefaults().span(5, 1).applyTo(timeFrameShortcuts);
		
		createTimeFrameRadioButton(timeFrameShortcuts, TimeFrameType.CURRENT_YEAR);
		createTimeFrameRadioButton(timeFrameShortcuts, TimeFrameType.LAST_YEAR);
		createTimeFrameRadioButton(timeFrameShortcuts, TimeFrameType.CURRENT_MONTH);
		createTimeFrameRadioButton(timeFrameShortcuts, TimeFrameType.LAST_MONTH);
		
		if (needsCustomQueryParameters()) {
			Composite customParams = new Composite(sectionClient, SWT.NONE);
			GridDataFactory.fillDefaults().span(5, 1).applyTo(customParams);
			addCustomQueryParameters(customParams);			
		}
		
		return querySection;		
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
	 * @param parent
	 * @param label
	 * @param timeFrame
	 * @param initallySelected
	 */
	private void createTimeFrameRadioButton(Composite parent, TimeFrameType timeFrameType) {
		final TimeFrame timeFrame;
		boolean selected = timeFrameType.equals(getDefaultTimeFrameType());
		
		switch (timeFrameType) {
		case CURRENT_MONTH:
			timeFrame = TimeFrame.currentMonth();
			break;
		case LAST_MONTH:
			timeFrame = TimeFrame.lastMonth();
			break;
		case CURRENT_YEAR:
			timeFrame = TimeFrame.currentYear();
			break;
		case LAST_YEAR:
			timeFrame = TimeFrame.lastYear();
			break;
		default:
			timeFrame = TimeFrame.currentMonth();
			break;
		}
		
		if (selected) {
			WidgetHelper.dateToWidget(timeFrame.getFrom(), fromDate);
			WidgetHelper.dateToWidget(timeFrame.getUntil(), untilDate);
		}
		
		final Button button = getToolkit().createButton(parent, timeFrame.getType().getTranslatedName(), SWT.RADIO);
		button.setSelection(selected);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (button.getSelection()) {
					WidgetHelper.dateToWidget(timeFrame.getFrom(), fromDate);
					WidgetHelper.dateToWidget(timeFrame.getUntil(), untilDate);
					updateModel(timeFrame);					
				}
			}
		});
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract FormToolkit getToolkit();
	
	/**
	 * 
	 * @return
	 */
	protected abstract TimeFrameType getDefaultTimeFrameType();
	
	/**
	 * 
	 */
	protected void updateModel() {
		updateModel(new TimeFrame(WidgetHelper.widgetToDate(fromDate), WidgetHelper.widgetToDate(untilDate)));
	}
	
	/**
	 * 
	 * @param timeFrame
	 */
	protected abstract void updateModel(TimeFrame timeFrame);
	
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
	 */
	protected abstract void handleExport();
}
