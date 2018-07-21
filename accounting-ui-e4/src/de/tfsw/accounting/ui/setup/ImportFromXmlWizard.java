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
import java.util.function.Function;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingInitService;
import de.tfsw.accounting.Constants;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * A wizard for initialising the application with data exported from a previous installation/version.
 * 
 * @author thorsten
 *
 */
final class ImportFromXmlWizard extends AbstractSetupWizard {

	private ImportFromXmlWizardPage page;
	
	private String xmlFile = null;
	private String dbFile = null;
	
	@Override
	void messagesAvailable() {
		setWindowTitle(getMessages().importFromXmlWizard_windowTitle);
	}

	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
	    page = new ImportFromXmlWizardPage(getMessages());
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

	@Override
	Function<AccountingInitService, AccountingContext> buildFunctionForWhenServiceComesOnline() {
		return service -> service.importModelFromXml(xmlFile, dbFile);
	}
	
	/**
	 *
	 */
	private class ImportFromXmlWizardPage extends WizardPage {
		
		private Messages messages;
		
		/**
		 * 
		 */
		private ImportFromXmlWizardPage(Messages messages) {
			super(ImportFromXmlWizardPage.class.getName());
			this.messages = messages;
			setTitle(messages.importFromXmlWizard_pageTitle);
			setMessage(messages.importFromXmlWizard_pageMessage);
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
    		
    		WidgetHelper.createLabel(composite, messages.importFromXmlWizard_xmlFileLabel);
    		final Text xmlFileText = WidgetHelper.createSingleBorderText(composite, Constants.EMPTY_STRING);
    		xmlFileText.setEditable(false);
    		xmlFileText.addKeyListener(KeyListener.keyReleasedAdapter(e -> {
            	xmlFile = xmlFileText.getText();
                updateIsPageComplete();    			
    		}));
    		
    		Button browseXml = new Button(composite, SWT.PUSH);
    		browseXml.setText("..."); //$NON-NLS-1$
    		browseXml.addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> {
				FileDialog fd = new FileDialog(composite.getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[]{Constants.XML_FILES});
				fd.setText(messages.importFromXmlWizard_xmlFileSelectText);
				fd.setFileName(xmlFileText.getText());
				String selected = fd.open();
				if (selected != null) {
					xmlFileText.setText(selected);
					xmlFile = xmlFileText.getText();
				}
				
				updateIsPageComplete();    			
    		}));
    		
    		WidgetHelper.createLabel(composite, messages.setupWizards_dataFileLabelNew);
    		final Text dbFileText = WidgetHelper.createSingleBorderText(
    				composite,
    				ApplicationInit.buildDefaultDbFileLocation());
    		dbFileText.setEditable(false);
    		dbFile = dbFileText.getText();
    		dbFileText.addKeyListener(KeyListener.keyReleasedAdapter(event -> {
            	dbFile = dbFileText.getText();
                updateIsPageComplete();    			
    		}));

    		final Button browseDbFile = new Button(composite, SWT.PUSH);
    		browseDbFile.setText("..."); //$NON-NLS-1$
    		browseDbFile.addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> {
				FileDialog fd = new FileDialog(composite.getShell(), SWT.SAVE);
				fd.setText(messages.setupWizards_dataFileSelectText);
				fd.setFileName(dbFileText.getText());
				String selected = fd.open();
				if (selected != null) {
					dbFileText.setText(selected);
					dbFile = dbFileText.getText();
				}
				updateIsPageComplete();
    		}));

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
					setErrorMessage(messages.setupWizards_errorDataFileExists);
					complete = false;
				} else {
					setErrorMessage(null);
				}
			}
			setPageComplete(complete);
			((ImportFromXmlWizard) getWizard()).setCanFinish(complete);
		}
	}
}
