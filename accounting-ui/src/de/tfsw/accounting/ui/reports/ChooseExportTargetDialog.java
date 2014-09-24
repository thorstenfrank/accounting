/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.ui.AbstractModalDialog;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
public class ChooseExportTargetDialog extends AbstractModalDialog {
	
	private static final String[] EXPORT_EXTENSIONS = {"*.pdf", "*.doc;*.docx", "*.odt", "*.xml"};
	
	private static final String[] EXPORT_FORMAT_DESC = {
		"Adobe PDF (" + EXPORT_EXTENSIONS[0] + ")",
		"MS Word (" + EXPORT_EXTENSIONS[1] + ")",
		"Open Document Format (" + EXPORT_EXTENSIONS[2] + ")",
		"XML (" + EXPORT_EXTENSIONS[3] + ")"
	};
	
	private String targetFile;
	
	private boolean openAfterExport;
	
	/**
	 * 
	 * @param parentShell
	 * @param targetFile
	 */
	public ChooseExportTargetDialog(Shell parentShell, String targetFile) {
		this(parentShell, targetFile, false);
	}
	
	/**
	 * 
	 * @param parentShell
	 * @param targetFile
	 * @param openAfterExport
	 */
	public ChooseExportTargetDialog(Shell parentShell, String targetFile, boolean openAfterExport) {
		super(parentShell, Messages.ChooseExportTargetDialog_title, Messages.ChooseExportTargetDialog_message);
		this.targetFile = targetFile;
		this.openAfterExport = openAfterExport;
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractModalDialog#createMainContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createMainContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		WidgetHelper.grabBoth(composite);
		
		
		WidgetHelper.createLabel(composite, Messages.ChooseExportTargetDialog_targetFile);
		
		final Text filePath = WidgetHelper.createSingleBorderText(composite, targetFile);
		filePath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				targetFile = filePath.getText();
			}
		});
		
		Button openFileDialog = new Button(composite, SWT.PUSH);
		openFileDialog.setText("..."); //$NON-NLS-1$
		
		openFileDialog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getParentShell(), SWT.SAVE);
				fd.setFileName(filePath.getText());
				fd.setFilterExtensions(EXPORT_EXTENSIONS); //$NON-NLS-1$
				fd.setFilterNames(EXPORT_FORMAT_DESC);
				
				String selected = fd.open();
				if (selected != null) {
					filePath.setText(selected);
					targetFile = selected;
				}
			}
		});
		
		final Button openAfter = new Button(composite, SWT.CHECK);
		openAfter.setSelection(openAfterExport);
		openAfter.setText(Messages.ChooseExportTargetDialog_openAfterExport);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(openAfter);
		
		openAfter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openAfterExport = openAfter.getSelection();
			}
		});
	}

	/**
	 * @return the targetFile
	 */
	protected String getTargetFile() {
		return targetFile;
	}

	/**
	 * @return the openAfterExport
	 */
	protected boolean isOpenAfterExport() {
		return openAfterExport;
	}
}
