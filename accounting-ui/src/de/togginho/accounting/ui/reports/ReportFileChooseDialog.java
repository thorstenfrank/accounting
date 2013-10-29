/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.ui.AbstractModalDialog;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
public class ReportFileChooseDialog extends AbstractModalDialog {
	
	private Text filePath;
	
	private String targetFile;
	
	private boolean openAfterExport;
	
	/**
	 * 
	 * @param parentShell
	 * @param targetFile
	 */
	public ReportFileChooseDialog(Shell parentShell, String targetFile) {
		this(parentShell, targetFile, false);
	}
	
	/**
	 * 
	 * @param parentShell
	 * @param targetFile
	 * @param openAfterExport
	 */
	public ReportFileChooseDialog(Shell parentShell, String targetFile, boolean openAfterExport) {
		super(parentShell, "This is the title", "This is the message");
		this.targetFile = targetFile;
		this.openAfterExport = openAfterExport;
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractModalDialog#createMainContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createMainContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		WidgetHelper.grabBoth(composite);
		
		
		WidgetHelper.createLabel(composite, "Export to file:");
		
		filePath = WidgetHelper.createSingleBorderText(composite, targetFile);
		
		Button openFileDialog = new Button(composite, SWT.PUSH);
		openFileDialog.setText("...");
		
		openFileDialog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getParentShell(), SWT.SAVE);
				fd.setFileName(filePath.getText());
				fd.setFilterExtensions(new String[]{"*.pdf"}); //$NON-NLS-1$
				fd.setFilterNames(new String[]{Messages.ReportGenerationUtil_labelPdfFiles});
				
				String selected = fd.open();
				if (selected != null) {
					filePath.setText(selected);
				}
			}
		});
		
		Button openAfter = new Button(composite, SWT.CHECK);
		openAfter.setSelection(openAfterExport);
		openAfter.setText("Open file after exporting");
		GridDataFactory.fillDefaults().span(3, 1).applyTo(openAfter);
		
	}

	/**
	 * @return the targetFile
	 */
	protected String getTargetFile() {
		return filePath.getText();
	}

	/**
	 * @return the openAfterExport
	 */
	protected boolean isOpenAfterExport() {
		return openAfterExport;
	}
}
