/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.setup;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author tfrank1
 *
 */
public class ImportFromXmlWizard extends Wizard {

	private ImportFromXmlWizardPage page;
	
	private String xmlFile = null;
	private String dbFile = null;
	
	public ImportFromXmlWizard() {
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.ImportFromXmlWizard_title);
    }

	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
	    page = new ImportFromXmlWizardPage();
	    addPage(page);
	}
	
	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return true;
	}
	
	/**
     * @return the xmlFile
     */
    public String getXmlFile() {
    	return xmlFile;
    }

	/**
     * @return the dbFile
     */
    public String getDbFile() {
    	return dbFile;
    }

	/**
	 *
	 */
	private class ImportFromXmlWizardPage extends WizardPage {
				
		/**
		 * 
		 */
		private ImportFromXmlWizardPage() {
			super(ImportFromXmlWizardPage.class.getName());
			setTitle(Messages.ImportFromXmlWizard_title);
			setMessage(Messages.ImportFromXmlWizard_message);
		}
		
		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
         */
        @Override
        public void createControl(Composite parent) {
    		final Composite composite = new Composite(parent, SWT.NULL);
    		GridLayout layout = new GridLayout(3, false);
    		composite.setLayout(layout);
    	
    		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
    		
    		WidgetHelper.createLabel(composite, Messages.ImportFromXmlWizard_xmlFileLabel);
    		final Text xmlFileText = WidgetHelper.createSingleBorderText(composite, Constants.EMPTY_STRING);
    		grabHorizontal.applyTo(xmlFileText);
    		xmlFileText.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                	xmlFile = xmlFileText.getText();
	                updateIsPageComplete();
                }
			});
    		
    		Button browseXml = new Button(composite, SWT.PUSH);
    		browseXml.setText("..."); //$NON-NLS-1$
    		browseXml.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				FileDialog fd = new FileDialog(composite.getShell(), SWT.OPEN);
    				fd.setFilterExtensions(new String[]{"*.xml"}); //$NON-NLS-1$
    				fd.setText(Messages.ImportFromXmlWizard_xmlFileSelectText);
    				fd.setFileName(xmlFileText.getText());
    				String selected = fd.open();
    				if (selected != null) {
    					xmlFileText.setText(selected);
    					xmlFile = xmlFileText.getText();
    				}
    				
    				updateIsPageComplete();
    			}
			});
    		
    		WidgetHelper.createLabel(composite, Messages.ImportFromXmlWizard_dataFileLabel);
    		final Text dbFileText = WidgetHelper.createSingleBorderText(
    				composite, AccountingUI.buildDefaultDbFileLocation());
    		dbFile = dbFileText.getText();
    		grabHorizontal.applyTo(dbFileText);
    		dbFileText.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                	dbFile = dbFileText.getText();
	                updateIsPageComplete();
                }
			});
    		
    		Button browseDbFile = new Button(composite, SWT.PUSH);
    		browseDbFile.setText("..."); //$NON-NLS-1$
    		browseDbFile.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				FileDialog fd = new FileDialog(composite.getShell(), SWT.SAVE);
    				fd.setText(Messages.UserNameAndDbFileWizardPage_selectFile);
    				fd.setFileName(dbFileText.getText());
    				String selected = fd.open();
    				if (selected != null) {
    					dbFileText.setText(selected);
    					dbFile = dbFileText.getText();
    				}
    				updateIsPageComplete();
    			}
			});
    		setControl(composite);
    		setPageComplete(false);
        }
		
        /**
         * 
         */
		private void updateIsPageComplete() {
			boolean complete = dbFile != null && xmlFile != null;
			if (complete) {
				if (new File(dbFile).exists()) {
					setErrorMessage(Messages.ImportFromXmlWizard_errorDataFileExists);
					complete = false;
				} else {
					setErrorMessage(null);
				}
			}
			setPageComplete(complete);
		}
	}
}
