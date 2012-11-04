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
package de.togginho.accounting.ui;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.togginho.accounting.Constants;

/**
 * @author thorsten
 *
 */
public abstract class AbstractModalDialog extends TitleAreaDialog {
	
	private String title;
	private String message;
	private String iconPath;
	
	/**
	 * 
	 * @param parentShell
	 */
	public AbstractModalDialog(Shell parentShell) {
		this(parentShell, null, null, null);
	}
	
	/**
	 * 
	 * @param parentShell
	 * @param title
	 * @param message
	 * @param iconPath
	 */
	public AbstractModalDialog(Shell parentShell, String title, String message, String iconPath) {
		super(parentShell);
		this.title = title;
		this.message = message;
		this.iconPath = iconPath;
	}

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
	
	@Override
	protected Control createDialogArea(Composite parent) {
    	Composite composite = new Composite(parent, SWT.NONE);
    	GridLayout layout = new GridLayout(1, false);
    	layout.marginHeight = 0;
    	layout.marginWidth = 0;
    	
    	composite.setLayout(layout);
    	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(composite);
    	
    	// TOP SEPARATOR
		final Label topSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(topSeparator);
		
		// Let subclasses build the main contents				
		createMainContents(composite);
		
		// SPACER TO BOTTOM SEPARATOR
		final Label fillToBottom = WidgetHelper.createLabel(composite, Constants.EMPTY_STRING);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(fillToBottom);
		
		// BOTTOM SEPARATOR
		final Label bottomSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(bottomSeparator);
		
		return composite;
	}
	
	protected abstract void createMainContents(Composite parent);
}
