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
package de.tfsw.accounting.elster.ui.wizard;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.tfsw.accounting.elster.ui.ElsterUI;

/**
 * ELSTER export wizard page letting the user choose the filing period of the VAT report.
 * 
 * <p>
 * The initially selected period is based on both availability of available XML adapters as well as the current date.
 * </p>
 * 
 * @author Thorsten Frank
 *
 */
class TimeFrameSelectionPage extends AbstractElsterWizardPage implements ISelectionChangedListener {
	
	/**
	 * Creates a new page.
	 */
	TimeFrameSelectionPage() {
		super(TimeFrameSelectionPage.class.getName(), 
			  Messages.TimeFrameSelectionPage_Title, 
			  Messages.TimeFrameSelectionPage_Description);
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
		
		final ComboViewer years = new ComboViewer(control, SWT.READ_ONLY);
		years.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true, false));
		years.setContentProvider(ArrayContentProvider.getInstance());
		years.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Year) {
					return ((Year) element).toString();
				}
				return super.getText(element);
			}
		});
		years.setInput(ElsterUI.getDefault().getElsterService().getAvailableYears());
		years.addSelectionChangedListener(this);
		
		Label lblTimeFrame = new Label(control, SWT.NONE);
		lblTimeFrame.setText(Messages.TimeFrameSelectionPage_Period);
		
		final ComboViewer months = new ComboViewer(control, SWT.READ_ONLY);
		months.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true, false));
		months.setContentProvider(ArrayContentProvider.getInstance());
		months.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Month) {
					return ((Month)element).getDisplayName(TextStyle.FULL, Locale.getDefault());
				}
				return super.getText(element);
			}
		});
		months.setInput(Month.values());
		months.addSelectionChangedListener(this);
		
		// pre-select values as defined by the DTO
		YearMonth filingPeriod = getWizard().getElsterDTO().getFilingPeriod();
		
		years.setSelection(new StructuredSelection(Year.of(filingPeriod.getYear())));
		months.setSelection(new StructuredSelection(filingPeriod.getMonth()));
	}

	/**
	 * Handles selections of the two dropdown elements controlling the year and month of the filing period.
	 * 
	 * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelection() instanceof IStructuredSelection) {
			Object selection = ((IStructuredSelection) event.getSelection()).getFirstElement();
			
			if (selection instanceof Year) {
				getWizard().adaptDtoToNewPeriod(getDTO().getFilingPeriod().withYear(((Year)selection).getValue()));
			} else if (selection instanceof Month) {
				getWizard().adaptDtoToNewPeriod(getDTO().getFilingPeriod().withMonth(((Month)selection).getValue()));
			}
		} 
	}
}
