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
package de.tfsw.accounting.elster.ui.wizard;

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
 * ELSTER export wizard page supplying the user with a preview of the XML document to be exported  along with the 
 * choice of the actual file where to export data to.
 * 
 * @author Thorsten Frank
 *
 */
class ExportTargetSelectionPage extends AbstractElsterWizardPage {

	/** Target filename. */
	private Text txtTarget;
	
	/** Preview text area. */
	private Text txtPreview;
	
	/**
	 * Creates a new wizard page.
	 */
	ExportTargetSelectionPage() {
		super(ExportTargetSelectionPage.class.getName(), Messages.ExportTargetSelectionPage_Title, Messages.ExportTargetSelectionPage_Description);
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
		lblTarget.setText(Messages.ExportTargetSelectionPage_TargetFile);
		
		txtTarget = new Text(control, SWT.BORDER | SWT.READ_ONLY);
		txtTarget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Button btnSearch = new Button(control, SWT.PUSH);
		btnSearch.setText(Messages.ExportTargetSelectionPage_FileSelectionLabel);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				fd.setFilterExtensions(new String[]{"*.xml"});
				fd.setFileName(getWizard().getTargetFileName());
				final String selection = fd.open();
				
				if (selection != null) {
					txtTarget.setText(selection);
					getWizard().setTargetFileName(selection);
					setPageComplete(true);
				}
			}
		});
		
		Label horiSep1 = new Label(control, SWT.SEPARATOR | SWT.HORIZONTAL);
		horiSep1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblPreview = new Label(control, SWT.NONE);
		lblPreview.setText(Messages.ExportTargetSelectionPage_Preview);
		lblPreview.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
				
		txtPreview = new Text(control, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd.heightHint = 350;
		txtPreview.setLayoutData(gd);
	}
	
	/**
	 * Upon the page becoming visible, the preview XML is newly generated. This method also controls whether or not
	 * the wizard can finish properly.
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			txtPreview.setText(getWizard().generatePreview());
			final String fileName = getWizard().getTargetFileName();
			if (fileName != null && !fileName.isEmpty()) {
				txtTarget.setText(fileName);
				setPageComplete(true);
			} else {
				txtTarget.setText("");
				setPageComplete(false);
			}
						
		}
		
		super.setVisible(visible);
	}
	
}
