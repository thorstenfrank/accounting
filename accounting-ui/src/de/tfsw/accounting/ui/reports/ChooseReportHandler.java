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

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.tfsw.accounting.ui.AbstractAccountingHandler;
import de.tfsw.accounting.ui.AbstractModalDialog;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.reports.DynamicReportUtil.DynamicReportHandler;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
public class ChooseReportHandler extends AbstractAccountingHandler {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(ChooseReportHandler.class);
	
	private Composite mainComposite;
	
	/**
	 * The dialog opening command to run.
	 */
	private Command commandToRun = null;
	
	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
				
		AbstractModalDialog dialog = new AbstractModalDialog(
				getShell(event), 
				Messages.ChooseReportCommand_title, 
				Messages.ChooseReportCommand_message, 
				Messages.iconsReports) {
			
			@Override
			protected void createMainContents(Composite parent) {
				mainComposite = new Composite(parent, SWT.NONE);
				mainComposite.setLayout(new GridLayout(2, false));
				WidgetHelper.grabHorizontal(mainComposite);
				
				DynamicReportUtil.parseReportHandlers(new DynamicReportHandler() {
					
					@Override
					public void handleDynamicReport(IConfigurationElement configElement, final Command command, int index) {
						final Button button = new Button(mainComposite, SWT.RADIO);
						String imgDesc = configElement.getAttribute(DynamicReportUtil.ICON);
						if (imgDesc != null) {
							button.setImage(AccountingUI.getImageDescriptor(imgDesc).createImage());
						}
						String name = configElement.getAttribute(DynamicReportUtil.LABEL);
						if (name == null) {
							try {
					            name = command.getName();
				            } catch (NotDefinedException e) {
				            	LOG.warn("Error retrieving name from command", e); //$NON-NLS-1$
				            	name = command.getId();
				            }
						}
						button.setText(name);
						GridDataFactory.fillDefaults().span(2, 1).grab(true, true).indent(5, 0).applyTo(button);
						button.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if (button.getSelection()) {
									commandToRun = command;
								}
							}
						});
						
						if (index == 0) {
							button.setSelection(true);
							commandToRun = command;
						}
					}
				});
			}
		};
		
		if (dialog.show() && commandToRun != null) {
			LOG.debug("Running selected command: " + commandToRun.getId()); //$NON-NLS-1$
			try {
                commandToRun.executeWithChecks(event);
            } catch (Exception e) {
            	LOG.error(e);
            }
		} else {
			LOG.debug("Report selection was cancelled by user"); //$NON-NLS-1$
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}