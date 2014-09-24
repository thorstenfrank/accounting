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
package de.tfsw.accounting.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A basic modal dialog with classic Ok and Cancel buttons. It also provides a basic layout and simple way to customize
 * the title, message and icon of the dialog. The actual content creation is then delegated to subclasses.
 * 
 * @author thorsten
 *
 */
public abstract class AbstractModalDialog extends TitleAreaDialog {
	
	private String title;
	private String message;
	private String iconPath;
	
	/**
	 * Creates a modal dialog without a title, message or icon.
	 * 
	 * @param parentShell the parent shell to use
	 */
	public AbstractModalDialog(Shell parentShell) {
		this(parentShell, null, null, null);
	}
	
	/**
	 * Creates a modal dialog with a title and message, but no specific icon.
	 * 
	 * @param parentShell the parent shell to use
	 * @param title the dialog's title
	 * @param message the dialog's message
	 */
	public AbstractModalDialog(Shell parentShell, String title, String message) {
		super(parentShell);
		this.title = title;
		this.message = message;
	}



	/**
	 * Creates a modal dialog with a unique title, message and icon.
	 * 
	 * @param parentShell the parent shell to use
	 * @param title the dialog's title
	 * @param message the dialog's message
	 * @param iconPath the dialog's icon
	 */
	public AbstractModalDialog(Shell parentShell, String title, String message, String iconPath) {
		super(parentShell);
		this.title = title;
		this.message = message;
		this.iconPath = iconPath;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create() {
		super.create();
		
		if (title != null) {
			setTitle(title);
		}
		if (message != null) {
			setMessage(message);
		}
		if (iconPath != null) {
			getShell().setImage(AccountingUI.getImageDescriptor(iconPath).createImage());
		}
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 * @see TitleAreaDialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
    	Composite composite = (Composite) super.createDialogArea(parent); //new Composite(parent, SWT.NONE);
    	GridLayout layout = new GridLayout(1, false);
    	layout.marginHeight = 0;
    	layout.marginWidth = 0;
    	
    	composite.setLayout(layout);
    	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
		
		// Let subclasses build the main contents				
		createMainContents(composite);
		
		// BOTTOM SEPARATOR
		final Label bottomSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).align(SWT.FILL, SWT.END).applyTo(bottomSeparator);
		
		return composite;
	}
	
	/**
	 * Opens this dialog and returns whether or not it was closed again using the "OK" button.
	 * This is a shortcut to
	 * <code>if (dialog.open() == IDialogConstants.OK_ID)</code>
	 * 
	 * @return <code>true</code> if this dialog was closed using the OK button
	 */
	public boolean show() {
		return (this.open() == IDialogConstants.OK_ID);
	}
	
	/**
	 * Called when the main part of the dialog is built. Subclasses must use this method to create their main dialog 
	 * layout.
	 * 
	 * @param parent the {@link Composite} to use as a parent container
	 */
	protected abstract void createMainContents(Composite parent);
}
