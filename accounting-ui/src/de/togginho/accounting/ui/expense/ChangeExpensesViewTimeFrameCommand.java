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
 * A popup dialog to let users choose the timeframe of expenses in detail.
 * Provides for shortcuts (last month, current month, etc.) as well as detailed from-until date choosers.
 * 
 * @author thorsten
 *
 */
public class ChangeExpensesViewTimeFrameCommand extends AbstractAccountingHandler {

	private static final Logger LOG = Logger.getLogger(ChangeExpensesViewTimeFrameCommand.class);
	
	private static final String PARAMETER_NAME = "timeFrame"; //$NON-NLS-1$
	
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
		
		if (!extractTimeFrameFromParameter(event)) {
			LOG.debug("No time frame supplied as parameter, now showing chooser dialog"); //$NON-NLS-1$
			
			currentTimeFrame = expensesView.getCurrentTimeFrame();
			if (!extractTimeFrameFromPopup(event)) {
				LOG.debug("Choosing time frame was cancelled by user"); //$NON-NLS-1$
				return;
			}
		}
		
		if (currentTimeFrame != null) {
			LOG.info("Changing expenses view"); //$NON-NLS-1$
			expensesView.setCurrentTimeFrame(currentTimeFrame);
			expensesView.modelChanged();			
		} else {
			LOG.warn("No time frame provided, cancelling execution of this command!"); //$NON-NLS-1$
		}
	}

	/**
     * @param event
     */
    private boolean extractTimeFrameFromParameter(ExecutionEvent event) {
	    final String param = event.getParameter(PARAMETER_NAME);
		if (param != null) {
			LOG.debug(String.format("Found command param [%s]: %s", PARAMETER_NAME, param)); //$NON-NLS-1$
            try {
            	TimeFrameType type = TimeFrameType.valueOf(param);
            	switch (type) {
				case CURRENT_MONTH:
					currentTimeFrame = TimeFrame.currentMonth();
					break;
				case CURRENT_YEAR:
					currentTimeFrame = TimeFrame.currentYear();
					break;
				case LAST_MONTH:
					currentTimeFrame = TimeFrame.lastMonth();
					break;
				case LAST_YEAR:
					currentTimeFrame = TimeFrame.lastYear();
					break;
				default:
					currentTimeFrame = null;
					break;
				}
            } catch (Exception e) {
            	LOG.error("Error trying to retrieve TimeFrameType for parameter " + param, e); //$NON-NLS-1$
            	currentTimeFrame = null;
            }
		} else {
			currentTimeFrame = null;
		}
		
		return currentTimeFrame != null;
    }

	/**
     * @param event
     */
    private boolean extractTimeFrameFromPopup(ExecutionEvent event) {
	    AbstractModalDialog dialog = new AbstractModalDialog(
				getShell(event), 
				Messages.ChangeExpensesViewTimeFrameCommand_title, 
				Messages.ChangeExpensesViewTimeFrameCommand_message) {
			
			@Override
			protected void createMainContents(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(2, false));
				WidgetHelper.grabHorizontal(composite);
				
				buildButtonForTimeFrame(composite, TimeFrameType.CURRENT_MONTH);
				buildButtonForTimeFrame(composite, TimeFrameType.LAST_MONTH);
				buildButtonForTimeFrame(composite, TimeFrameType.CURRENT_YEAR);
				buildButtonForTimeFrame(composite, TimeFrameType.LAST_YEAR);
        		
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
        				LOG.debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
        				custom.setSelection(true);
        			}
				});
        		
        		WidgetHelper.createLabel(dates, Messages.labelUntil);
        		to = new DateTime(dates, SWT.DROP_DOWN);
        		to.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				currentTimeFrame.setUntil(WidgetHelper.widgetToDate(to));
        				LOG.debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
        				custom.setSelection(true);
        			}
				});
        		
        		currentTimeFrameChanged();
			}
		};
		
		return dialog.show();
    }
    
	/**
	 * 
	 * @param parent
	 * @param type
	 */
	private void buildButtonForTimeFrame(Composite parent, TimeFrameType type) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(type.getTranslatedName());
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
				if (button.getSelection()) { // only need to react if the button was actually selected (ignore deselect)
					currentTimeFrame = timeFrame;
					currentTimeFrameChanged();					
				}
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
		LOG.debug(String.format("Chosen timeframe is [%s]", currentTimeFrame.toString())); //$NON-NLS-1$
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
