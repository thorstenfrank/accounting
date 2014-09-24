/*
 *  Copyright 2011 thorsten frank (thorsten.frank@tfsw.de).
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
package de.tfsw.accounting.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.tfsw.accounting.AccountingException;

/**
 * Base implementation for all command handlers within the accounting application.
 * 
 * <p>
 * Apart from wrapping the actual handler execution into a try-catch block, this class offers basic convenience access
 * to often-used resources, such as getting the display shell.   
 * </p>
 * 
 * @author Thorsten Frank
 *
 */
public abstract class AbstractAccountingHandler extends AbstractHandler {

	/**
	 * All handler execution logic goes here - {@link #execute(ExecutionEvent)} wraps a call to this method into
	 * administrative and overhead such as error handling, so implementations can concentrate on their actual task.
	 * 
	 * <p>
	 * This method should not be called directly!
	 * </p>
	 * 
	 * @param event	An event containing all the information about the current state of the application
	 * 
	 * @throws ExecutionException if an exception occurs during execution. 
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	protected abstract void doExecute(ExecutionEvent event) throws ExecutionException;
	
	/**
	 * All centralized logging statements will be made to the {@link Logger} returned by this method.
	 * 
	 * <p>
	 * For log traceability reasons, implementations are encouraged to provide their own instinctive logging 
	 * instance instead of using a general one.
	 * </p>
	 * 
	 * @return	the {@link Logger} for this handler implementation
	 */
	protected abstract Logger getLogger();
	
	/**
	 * Basic handler execution - subclasses should <b>not</b> override this method, but place all logic inside their 
	 * {@link #doExecute(ExecutionEvent)} implementation.
	 * 
	 * <p>
	 * This method will call {@link #doExecute(ExecutionEvent)} and display any exception in an error 
	 * {@link MessageBox}.
	 * </p>
	 * 
	 * @return	always <code>null</code>, as per definition in {@link IHandler#execute(ExecutionEvent)}
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try {
			doExecute(event);
		} catch (Exception e) {
			getLogger().error("Error executing command", e); //$NON-NLS-1$
			MessageBox msgBox = new MessageBox(getShell(event), SWT.ICON_ERROR | SWT.OK);
			msgBox.setText(Messages.labelError);
			msgBox.setMessage(e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.toString());
			msgBox.open();
		}
		
		return null;
	}
	
	/**
	 * Returns the active {@link Shell}. 
	 * 
	 * @param event	the {@link ExecutionEvent} provided through {@link #doExecute(ExecutionEvent)}
	 * 
	 * @return	the current {@link Shell}
	 * 
	 * @see HandlerUtil#getActiveShell(ExecutionEvent)
	 */
	protected Shell getShell(ExecutionEvent event) {
		Shell shell = HandlerUtil.getActiveShell(event);
		if (shell == null) {
			shell = getActivePage(event).getWorkbenchWindow().getShell();
		}
		return shell;
	}
	
	/**
	 * Returns the active workbench window's page.
	 * 
	 * @param event	the {@link ExecutionEvent} provided through {@link #doExecute(ExecutionEvent)}
	 * 
	 * @return	the current active workbench window/page
	 * 
	 * @see HandlerUtil#getActiveWorkbenchWindow(ExecutionEvent)
	 * @see IWorkbenchWindow#getActivePage()
	 */
	protected IWorkbenchPage getActivePage(ExecutionEvent event) {
		IWorkbenchPage page = null;
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		if (window != null) {
			page = window.getActivePage();
		}
		
		if (page == null) {
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		}
		
		return page;
	}
	
	/**
	 * Returns the selection provider of the currently active page.
	 * 
	 * @param event the {@link ExecutionEvent} provided through {@link #doExecute(ExecutionEvent)}
	 * 
	 * @return	the current selection provider
	 */
	protected ISelectionProvider getSelectionProvider(ExecutionEvent event) {
		return getActivePage(event).getActivePart().getSite().getSelectionProvider();
	}
	
	/**
	 * 
	 * @param event
	 * @param targetClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getCurrentSelection(ExecutionEvent event, Class<T> targetClass) {
		ISelectionProvider selectionProvider = getSelectionProvider(event);
		
		if (selectionProvider == null) {
			getLogger().error("Selection Provider is NULL! Target class is " + targetClass.getName()); //$NON-NLS-1$
			throw new AccountingException(Messages.AbstractAccountingHandler_errorGettingSelection);
		}
		
		ISelection selection = selectionProvider.getSelection();
		if (selection.isEmpty()) {
			getLogger().error("Selection is empty! Target class is " + targetClass.getName()); //$NON-NLS-1$
			throw new AccountingException(Messages.AbstractAccountingHandler_errorGettingSelection);
		}

		
		Object element = ((IStructuredSelection) selection).getFirstElement();
		
		if (targetClass.isAssignableFrom(element.getClass())) {
			return (T) element;
		}
		
		getLogger().error(String.format("Current selection is of type [%s] and canont be cast to target class [%s]", //$NON-NLS-1$ 
				element.getClass().getName(), targetClass.getName()));
		throw new AccountingException(Messages.AbstractAccountingHandler_errorGettingSelection);
	}
		
		
	
	/**
	 * Displays a warning message, i.e. a simple modal dialog with a message and a warning icon.
	 * 
	 * @param event	the {@link ExecutionEvent} provided through {@link #doExecute(ExecutionEvent)}
	 * @param message the (localized) text to be displayed by the dialog
	 * @param title the (localized) title string of the dialog
	 * @param includeCancelButton whether or not to include a localized <code>Cancel</code> button for the dialog 
	 * 		  instead of just an <code>OK</code> one
	 * 
	 * @return <code>true</code> if {@link MessageBox#open()} returns {@link SWT#OK}, <code>false</code> otherwise
	 */
	protected boolean showWarningMessage(
			ExecutionEvent event, final String message, final String title, boolean includeCancelButton) {
		
		int style = SWT.ICON_WARNING | SWT.OK;		
		if (includeCancelButton) {
			style = style | SWT.CANCEL;
		}
		
		MessageBox msgBox = new MessageBox(getShell(event), style);
		msgBox.setMessage(message);
		msgBox.setText(title);
		
		return (msgBox.open() == SWT.OK);
	}
}
