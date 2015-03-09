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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
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
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.ModelMetaInformation;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.util.MonthAndYearSelector;
import de.tfsw.accounting.ui.util.MonthAndYearSelectorCallback;
import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.FormatUtil;
import de.tfsw.accounting.util.TimeFrame;
import de.tfsw.accounting.util.TimeFrameType;

/**
 * @author thorsten
 *
 */
public abstract class AbstractReportDialog extends TrayDialog implements MonthAndYearSelectorCallback {
	
	private TimeFrame timeFrame;
	
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
	 * Returns the year of either the oldest {@link Expense} or {@link Invoice} known to the system, whichever is older.
	 * If subclasses need to restrict this in a different way, they must override this method.
	 * 
	 * @see de.tfsw.accounting.ui.util.MonthAndYearSelectorCallback#getOldestYear()
	 */
	@Override
	public int getOldestYear() {
		ModelMetaInformation mmi = AccountingUI.getAccountingService().getModelMetaInformation();
		
		int oldestExpenseYear = mmi.getOldestKnownExpenseDate().getYear();
		int oldestInvoiceYear = mmi.getOldestKnownInvoiceDate().getYear();
		
		return oldestExpenseYear < oldestInvoiceYear ? oldestInvoiceYear : oldestInvoiceYear;
	}

	/**
	 * @see de.tfsw.accounting.ui.util.MonthAndYearSelectorCallback#getTimeFrame()
	 */
	@Override
	public TimeFrame getTimeFrame() {
		return timeFrame;
	}

	/**
	 * @see de.tfsw.accounting.ui.util.MonthAndYearSelectorCallback#timeFrameChanged()
	 */
	@Override
	public void timeFrameChanged() {
		updateModel();
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
		getToolkit().createLabel(sectionClient, Messages.labelMonth);
		Combo months = new Combo(sectionClient, SWT.READ_ONLY);
		getToolkit().adapt(months);
		getToolkit().paintBordersFor(months);
		
		// YEAR COMBO
		getToolkit().createLabel(sectionClient, Messages.labelYear);		
		Combo years = new Combo(sectionClient, SWT.READ_ONLY);
		getToolkit().adapt(years);
		getToolkit().paintBordersFor(years);
		
		MonthAndYearSelector selector = new MonthAndYearSelector(this, months, years);
		
		// PRESETS
		Composite presets = new Composite(parent, SWT.NONE);
		presets.setLayout(new GridLayout(4, true));
		GridDataFactory.fillDefaults().span(5, 1).grab(true, false).applyTo(presets);
		
		Label presetsLabel = WidgetHelper.createLabel(presets, Messages.labelPresets);
		GridDataFactory.fillDefaults().span(4, 1).applyTo(presetsLabel);
		buildTimeFrameTypeButton(selector, TimeFrameType.CURRENT_MONTH, presets);
		buildTimeFrameTypeButton(selector, TimeFrameType.LAST_MONTH, presets);
		buildTimeFrameTypeButton(selector, TimeFrameType.CURRENT_YEAR, presets);
		buildTimeFrameTypeButton(selector, TimeFrameType.LAST_YEAR, presets);
				
		if (needsCustomQueryParameters()) {
			Composite customParams = new Composite(sectionClient, SWT.NONE);
			GridDataFactory.fillDefaults().span(5, 1).applyTo(customParams);
			addCustomQueryParameters(customParams);			
		}
		
		return querySection;		
	}
		
	/**
	 * 
	 * @param selector
	 * @param type
	 * @param parent
	 */
	private void buildTimeFrameTypeButton(MonthAndYearSelector selector, TimeFrameType type, Composite parent) {
		final Button b = getToolkit().createButton(parent, type.getTranslatedName(), SWT.PUSH);
		b.setData(TIME_FRAME_TYPE_KEY, type);
		WidgetHelper.grabHorizontal(b);
		selector.adaptTimeFrameTypeSelector(b);
	}
	
	/**
	 * Subclasses must overwrite this method if they want to use {@link #addCustomQueryParameters(Composite)}.
	 * Default always returns <code>false</code>.
	 * 
	 * <p>If subclasses override and return <code>true</code>, they must also override 
	 * {@link #addCustomQueryParameters(Composite)}.<p>
	 * 
	 * @return	<code>true</code> if custom additions to the query param section need to be made, 
	 * 			default always returns <code>false</code>
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
}
