/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.elster.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
class ExportTargetSelectionPage extends AbstractElsterExportWizardPage {

	private Text txtTarget;
	private Text txtPreview;

	/**
	 * 
	 */
	ExportTargetSelectionPage() {
		super(ExportTargetSelectionPage.class.getName(), "Ziel wählen", "Wählen Sie eine Zieldatei für den Export aus.\nDrücken Sie \'Finish\' zum exportieren des XML.");
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		setPageComplete(false);
		
		Composite control = new Composite(parent, SWT.NULL);
		setControl(control);
		
		control.setLayout(new GridLayout(3, false));
		
		Label lblTarget = new Label(control, SWT.NONE);
		lblTarget.setText("Zieldatei:");
		
		txtTarget = new Text(control, SWT.BORDER | SWT.READ_ONLY);
		txtTarget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Button btnSearch = new Button(control, SWT.PUSH);
		btnSearch.setText("...");
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				fd.setFilterExtensions(new String[]{"*.xml"});
				
				final String selection = fd.open();
				
				if (selection != null) {
					txtTarget.setText(selection);
					getWizard().setTargetFile(selection);
					setPageComplete(true);
				}
			}
		});
		
		Label horiSep1 = new Label(control, SWT.SEPARATOR | SWT.HORIZONTAL);
		horiSep1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		txtPreview = new Text(control, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
		txtPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	}
	
	/**
	 * @see de.tfsw.accounting.elster.wizard.AbstractElsterExportWizardPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			txtPreview.setText(getWizard().getAdapter().writeDataToXML());
		}
		super.setVisible(visible);
	}
}