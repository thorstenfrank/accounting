/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * @author tfrank1
 *
 */
public class SimpleCommandCallingAction extends Action implements SelectionListener {

	/** Log. */
	private static final Logger LOG = Logger.getLogger(SimpleCommandCallingAction.class);
	
	/** Command ID to call when {@link #widgetSelected(SelectionEvent)} is called. */
	private String commandId;
	
	private Command command;
	
	private String imagePath;
	
	/**
	 * 
	 * @param commandId command ID to call when {@link #widgetSelected(SelectionEvent)} is called.
	 */
	public SimpleCommandCallingAction(String commandId) {
		this(commandId, null);
	}
	
	/**
	 * @param commandId
	 * @param imagePath
	 */
	public SimpleCommandCallingAction(String commandId, String imagePath) {
		super();
		this.commandId = commandId;
		this.imagePath = imagePath;
		ICommandService cmdService = 
			(ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		this.command = cmdService.getCommand(commandId);
	}

	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return command.isEnabled();
	}

	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		try {
			return command.getName();
		} catch (NotDefinedException e) {
			LOG.warn("Name not defined", e); //$NON-NLS-1$
			return commandId;
		}
	}

	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.action.Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		if (imagePath != null) {
			return AccountingUI.getImageDescriptor(imagePath);
		}
		return super.getImageDescriptor();
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		LOG.debug("Now executing command " + commandId); //$NON-NLS-1$
		ICommandService cmdService = 
			(ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		
		try {
			cmdService.getCommand(commandId).executeWithChecks(new ExecutionEvent());
		} catch (Exception ex) {
			LOG.error("Error executing command: " + commandId, ex); //$NON-NLS-1$
		}
	}

	@Override
	public void runWithEvent(Event event) {
		run();
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		run();
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		LOG.debug("Widget selected: " + e.getSource().getClass().getName()); //$NON-NLS-1$
		run();
	}
}