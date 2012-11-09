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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.ui.IViewPart;

import de.togginho.accounting.ui.AbstractAccountingHandler;
import de.togginho.accounting.ui.AbstractModalDialog;
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
	private DateTime from;
	private DateTime to;
	
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
		
		AbstractModalDialog dialog = new AbstractModalDialog(
				getShell(event), 
				Messages.ChangeExpensesViewTimeFrameCommand_title, 
				Messages.ChangeExpensesViewTimeFrameCommand_message) {
			
			@Override
			protected void createMainContents(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(2, false));
				WidgetHelper.grabHorizontal(composite);
				
				buildButtonForTimeFrame(composite, Messages.labelCurrentMonth, TimeFrameType.CURRENT_MONTH);
				buildButtonForTimeFrame(composite, Messages.labelLastMonth, TimeFrameType.LAST_MONTH);
				buildButtonForTimeFrame(composite, Messages.labelCurrentYear, TimeFrameType.CURRENT_YEAR);
				buildButtonForTimeFrame(composite, Messages.labelLastYear, TimeFrameType.LAST_YEAR);
        		
        		final Button custom = new Button(composite, SWT.RADIO);
        		custom.setSelection(currentTimeFrame.getType() == TimeFrameType.CUSTOM);
        		
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
        		to = new DateTime(dates, SWT.DROP_DOWN);
        		to.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				currentTimeFrame.setFrom(WidgetHelper.widgetToDate(to));
        				custom.setSelection(true);
        			}
				});
        		
        		currentTimeFrameChanged();
			}
		};

		if (dialog.show()) {
			LOG.info("Changing expenses view"); //$NON-NLS-1$
			expensesView.setCurrentTimeFrame(currentTimeFrame);
			expensesView.modelChanged();			
		} else {
			LOG.info("Changing expenses timeframe was cancelled by user"); //$NON-NLS-1$
		}
		
	}

	/**
	 * 
	 * @param parent
	 * @param label
	 * @param type
	 */
	private void buildButtonForTimeFrame(Composite parent, String label, TimeFrameType type) {
		Button button = new Button(parent, SWT.RADIO);
		button.setText(label);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(button);
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
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTimeFrame = timeFrame;
				currentTimeFrameChanged();
			}
		});
		
		button.setSelection(currentTimeFrame.getType() == type);
	}

	/**
	 * 
	 */
	private void currentTimeFrameChanged() {
		WidgetHelper.dateToWidget(currentTimeFrame.getFrom(), from);
		WidgetHelper.dateToWidget(currentTimeFrame.getUntil(), to);
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
