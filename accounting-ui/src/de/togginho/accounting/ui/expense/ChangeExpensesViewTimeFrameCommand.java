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
package de.togginho.accounting.ui.expense;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;

import de.togginho.accounting.Constants;
import de.togginho.accounting.ui.AbstractAccountingHandler;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;
import de.togginho.accounting.util.TimeFrame;
import de.togginho.accounting.util.TimeFrameType;

/**
 * @author thorsten
 *
 */
public class ChangeExpensesViewTimeFrameCommand extends AbstractAccountingHandler {

	private static final Logger LOG = Logger.getLogger(ChangeExpensesViewTimeFrameCommand.class);
		
	private TimeFrame currentTimeFrame;
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		LOG.debug("ChangeExpensesViewTimeFrameCommand"); //$NON-NLS-1$
		
		IViewPart viewPart = getActivePage(event).findView(IDs.VIEW_EXPENSES_ID);
		ExpensesView expensesView = null;
		if (viewPart != null && viewPart instanceof ExpensesView) {
			expensesView = (ExpensesView) viewPart;
		} else {
			LOG.warn("No active invoice view found! This command should not have been fired..."); //$NON-NLS-1$
			return;
		}
	
		currentTimeFrame = expensesView.getCurrentTimeFrame();
		
		TitleAreaDialog tad = new TitleAreaDialog(getShell(event)) {
			private DateTime from;
			private DateTime until;
			
			@Override
			public void create() {
			    super.create();
			    setTitle(Messages.ChangeExpensesViewTimeFrameCommand_title);
			    setMessage(Messages.ChangeExpensesViewTimeFrameCommand_message);
			}
			
			@Override
			protected Control createDialogArea(Composite parent) {
            	Composite composite = new Composite(parent, SWT.NONE);
            	GridLayout layout = new GridLayout(2, false);
            	layout.marginHeight = 0;
            	layout.marginWidth = 0;
            	
            	composite.setLayout(layout);
            	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
            	
        		final Label topSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(topSeparator);
        		
        		GridDataFactory gdf = GridDataFactory.fillDefaults().indent(5, 0);
        		
        		Button currentMonth = new Button(composite, SWT.RADIO);
        		gdf.applyTo(currentMonth);
        		currentMonth.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						currentTimeFrame = TimeFrame.currentMonth();
						currentTimeFrameChanged();
					}
				});
        		gdf.applyTo(WidgetHelper.createLabel(composite, Messages.labelCurrentMonth));
        		
        		if (currentTimeFrame.getType() == TimeFrameType.CURRENT_MONTH) {
        			currentMonth.setSelection(true);
        		} else {
        			currentMonth.setSelection(false);
        		}
        		
        		Button lastMonth = new Button(composite, SWT.RADIO);
        		gdf.applyTo(lastMonth);
        		lastMonth.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						currentTimeFrame = TimeFrame.lastMonth();
						currentTimeFrameChanged();
					}
				});
        		gdf.applyTo(WidgetHelper.createLabel(composite, Messages.labelLastMonth));
        		
        		if (currentTimeFrame.getType() == TimeFrameType.LAST_MONTH) {
        			lastMonth.setSelection(true);
        		} else {
        			lastMonth.setSelection(false);
        		}
        		
        		Button currentYear = new Button(composite, SWT.RADIO);
        		gdf.applyTo(currentYear);
        		currentYear.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						currentTimeFrame = TimeFrame.currentYear();
						currentTimeFrameChanged();
					}
				});
        		gdf.applyTo(WidgetHelper.createLabel(composite, Messages.labelCurrentYear));
        		
        		if (currentTimeFrame.getType() == TimeFrameType.CURRENT_YEAR) {
        			currentYear.setSelection(true);
        		} else {
        			currentYear.setSelection(false);
        		}
        		
        		Button lastYear = new Button(composite, SWT.RADIO);
        		gdf.applyTo(lastYear);
        		lastYear.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						currentTimeFrame = TimeFrame.lastYear();
						currentTimeFrameChanged();
					}
				});
        		gdf.applyTo(WidgetHelper.createLabel(composite, Messages.labelLastYear));
        		
        		if (currentTimeFrame.getType() == TimeFrameType.LAST_YEAR) {
        			lastYear.setSelection(true);
        		} else {
        			lastYear.setSelection(false);
        		}
        		
        		final Button custom = new Button(composite, SWT.RADIO);
        		gdf.applyTo(custom);
        		if (currentTimeFrame.getType() == TimeFrameType.CUSTOM) {
        			custom.setSelection(true);
        		} else {
        			custom.setSelection(false);
        		}
        		
        		Composite dates = new Composite(composite, SWT.NONE);
        		dates.setLayout(new RowLayout());
        		WidgetHelper.createLabel(dates, Messages.labelFrom);
        		from = new DateTime(dates, SWT.DROP_DOWN);
        		from.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				currentTimeFrame.setFrom(WidgetHelper.widgetToDate(from));
        				custom.setSelection(true);
        			}
				});
        		
        		WidgetHelper.createLabel(dates, Messages.labelUntil);
        		until = new DateTime(dates, SWT.DROP_DOWN);
        		until.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				currentTimeFrame.setFrom(WidgetHelper.widgetToDate(until));
        				custom.setSelection(true);
        			}
				});
        		gdf.applyTo(dates);
        		
        		currentTimeFrameChanged();
        		
        		final Label fillToBottom = WidgetHelper.createLabel(composite, Constants.EMPTY_STRING);
        		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(fillToBottom);
        		
        		final Label bottomSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(bottomSeparator);
        		
        		return composite;
			}
			
			/**
			 * 
			 */
			private void currentTimeFrameChanged() {
				WidgetHelper.dateToWidget(currentTimeFrame.getFrom(), from);
				WidgetHelper.dateToWidget(currentTimeFrame.getUntil(), until);
			}
		};
		
		if (tad.open() == IDialogConstants.OK_ID) {
			LOG.info("Changing expenses view"); //$NON-NLS-1$
			expensesView.setCurrentTimeFrame(currentTimeFrame);
			expensesView.modelChanged();
		} else {
			LOG.info("Changing expenses timeframe was cancelled by user"); //$NON-NLS-1$
		}
		
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

}
