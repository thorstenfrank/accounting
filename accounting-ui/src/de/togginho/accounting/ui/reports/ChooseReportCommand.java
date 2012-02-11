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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import de.togginho.accounting.Constants;
import de.togginho.accounting.ui.AbstractAccountingHandler;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;

/**
 * @author thorsten
 *
 */
public class ChooseReportCommand extends AbstractAccountingHandler {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(ChooseReportCommand.class);
	
	/**
	 * The dialog opening command to run - default is the revenue dialog.
	 */
	private String commandIdToRun = IDs.CMD_OPEN_REVENUE_DIALOG;
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		TitleAreaDialog tad = new TitleAreaDialog(getShell(event)) {
			@Override
			public void create() {
			    super.create();
			    setTitle(Messages.ChooseReportCommand_title);
			    setMessage(Messages.ChooseReportCommand_message);
			    getShell().setImage(AccountingUI.getImageDescriptor(Messages.iconsReports).createImage());
			    getShell().setText(Messages.ChooseReportCommand_title);
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
        		
        		GridDataFactory gdf = GridDataFactory.fillDefaults().span(2, 1).grab(true, true).indent(5, 0);
        		
        		final Button profitButton = new Button(composite, SWT.RADIO);
        		profitButton.setImage(AccountingUI.getImageDescriptor(Messages.iconsCashFlowStatement).createImage());
        		profitButton.setText(Messages.CashFlowDialog_title);
        		gdf.applyTo(profitButton);
        		profitButton.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				if (profitButton.getSelection()) {
        					commandIdToRun = IDs.CMD_OPEN_CASH_FLOW_DIALOG;
        				}
        			}
				});
        		
        		final Button revenueButton = new Button(composite, SWT.RADIO);
        		revenueButton.setImage(AccountingUI.getImageDescriptor(Messages.iconsRevenue).createImage());
        		revenueButton.setText(Messages.RevenueDialog_title);
        		gdf.applyTo(revenueButton);
        		revenueButton.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				if (revenueButton.getSelection()) {
        					commandIdToRun = IDs.CMD_OPEN_REVENUE_DIALOG;
        				}
        			}
				});
        		
        		final Button expensesButton = new Button(composite, SWT.RADIO);
        		expensesButton.setImage(AccountingUI.getImageDescriptor(Messages.iconsExpenses).createImage());
        		expensesButton.setText(Messages.labelExpenses);
        		gdf.applyTo(expensesButton);
        		expensesButton.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				if (expensesButton.getSelection()) {
        					commandIdToRun = IDs.CMD_OPEN_EXPENSES_DIALOG;
        				}
        			}
				});
        		
        		final Label fillToBottom = WidgetHelper.createLabel(composite, Constants.EMPTY_STRING);
        		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(fillToBottom);
        		
        		final Label bottomSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(bottomSeparator);
        		
        		return composite;
			}
		};

		if (tad.open() == IDialogConstants.OK_ID) {
			LOG.debug("Opening dialog: " + commandIdToRun); //$NON-NLS-1$
			IHandlerService handlerService = 
					(IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
			try {
				handlerService.executeCommand(commandIdToRun, new Event());
			} catch (Exception e) {
				LOG.error("Error opening command " + commandIdToRun, e); //$NON-NLS-1$
			}
		} else {
			LOG.debug("Command was cancelled by user"); //$NON-NLS-1$
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
