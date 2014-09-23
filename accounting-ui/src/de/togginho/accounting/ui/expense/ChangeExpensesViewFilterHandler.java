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

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IViewPart;

import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.ui.AbstractModalDialog;
import de.togginho.accounting.ui.AbstractTimeFrameSelectionHandler;
import de.togginho.accounting.ui.AccountingUI;
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
public class ChangeExpensesViewFilterHandler extends AbstractTimeFrameSelectionHandler {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(ChangeExpensesViewFilterHandler.class);
	
	/**
	 * 
	 */
	private static final String PARAMETER_NAME = "timeFrame"; //$NON-NLS-1$
	
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
			
			setCurrentTimeFrame(expensesView.getCurrentTimeFrame());
			if (!getQueryParamsFromPopup(event, expensesView)) {
				LOG.debug("Choosing time frame was cancelled by user"); //$NON-NLS-1$
				return;
			}
		}
		
		LOG.info("Changing expenses view"); //$NON-NLS-1$
		expensesView.updateExpenses(getCurrentTimeFrame());
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
					setCurrentTimeFrame(TimeFrame.currentMonth());
					break;
				case CURRENT_YEAR:
					setCurrentTimeFrame(TimeFrame.currentYear());
					break;
				case LAST_MONTH:
					setCurrentTimeFrame(TimeFrame.lastMonth());
					break;
				case LAST_YEAR:
					setCurrentTimeFrame(TimeFrame.lastYear());
					break;
				default:
					setCurrentTimeFrame(null);
					break;
				}
            } catch (Exception e) {
            	LOG.error("Error trying to retrieve TimeFrameType for parameter " + param, e); //$NON-NLS-1$
            	setCurrentTimeFrame(null);
            }
		} else {
			setCurrentTimeFrame(null);
		}
		
		return getCurrentTimeFrame() != null;
    }

	/**
     * @param event
     */
    private boolean getQueryParamsFromPopup(ExecutionEvent event, final ExpensesView view) {
	    AbstractModalDialog dialog = new AbstractModalDialog(
				getShell(event), 
				Messages.ChangeExpensesViewTimeFrameCommand_title, 
				Messages.ChangeExpensesViewTimeFrameCommand_message) {
			
			@Override
			protected void createMainContents(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));
				WidgetHelper.grabBoth(composite);
				
				buildTimeFrameSelectionComposite(composite, true);
				
				Group typeSelector = new Group(composite, SWT.SHADOW_ETCHED_IN);
				typeSelector.setText(Messages.ChangeExpensesViewTimeFrameCommand_types);
				typeSelector.setLayout(new GridLayout(3, false));
				WidgetHelper.grabBoth(typeSelector);
				
				for (final ExpenseType type : ExpenseType.values()) {
					final Button b = new Button(typeSelector, SWT.CHECK);
					b.setText(type.getTranslatedString());
					b.setSelection(view.getSelectedTypes().contains(type));
					
					b.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if(b.getSelection()) {
								view.getSelectedTypes().add(type);
							} else {
								view.getSelectedTypes().remove(type);
							}
						}
					});
				}
			}
		};
		
		return dialog.show();
    }
    	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractTimeFrameSelectionHandler#getStartDateForYearSelector()
	 */
	@Override
	protected Calendar getStartDateForYearSelector() {
		return AccountingUI.getAccountingService().getModelMetaInformation().getOldestKnownExpenseDate();
	}

}
