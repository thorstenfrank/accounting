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
package de.togginho.accounting.ui.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
public class ExportModelToXmlWizard extends Wizard implements IExportWizard {

	private static final String[] VALID_EXTENSIONS = {"*.xml"};
	
	private String targetFileName;
	private WizardPage page;
	
	/**
     * {@inheritDoc}.
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    	setWindowTitle(Messages.ExportModelToXmlWizard_title);
    	
    }
    
    /**
     * 
     * {@inheritDoc}.
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
    	page = new ChooseFilePage();
    	addPage(page);
    }
    
	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
	        getContainer().run(true, true, new IRunnableWithProgress() {
	        	
	        	@Override
	        	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
	        		monitor.beginTask(Messages.ExportModelToXmlWizard_progressMessage, 2);
	        		monitor.worked(1);
	        		AccountingUI.getAccountingService().exportModelToXml(targetFileName);
	        		monitor.worked(2);
	        		monitor.done();
	        		
	        	}
	        });
	        
	        MessageDialog.openInformation(
	        		getShell(), 
	        		Messages.ExportModelToXmlWizard_finishedTitle, 
	        		Messages.bind(Messages.ExportModelToXmlWizard_finishedMsg, targetFileName));
	        
	        return true;
        } catch (AccountingException e) {
        	page.setErrorMessage(e.getCause().getLocalizedMessage());
        } catch (Exception e) {
        	if (e.getCause() instanceof AccountingException) {
        		page.setErrorMessage(e.getCause().getLocalizedMessage());
        	} else {
        		page.setErrorMessage(Messages.bind(Messages.labelUnknownError, e.toString()));
        	}
        	
        	
        }
		
		return false;
	}

	private class ChooseFilePage extends WizardPage {
		/**
		 * 
		 */
		private ChooseFilePage() {
			super("ChooseFilePage", //$NON-NLS-1$
					Messages.ExportModelToXmlWizard_title, 
				   null);
			setMessage(Messages.ExportModelToXmlWizard_message);
		}
		
		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
         */
        @Override
        public void createControl(Composite parent) {
	        final Composite composite = new Composite(parent, SWT.NONE);
	        composite.setLayout(new GridLayout(3, false));
	        
	        Label label = new Label(composite, SWT.NONE);
	        label.setText(Messages.ExportModelToXmlWizard_targetFile);
	        
	        final Text fileText = new Text(composite, SWT.BORDER);
	        fileText.setEnabled(false);
	        WidgetHelper.grabHorizontal(fileText);
	        
	        Button browse = new Button(composite, SWT.PUSH);
	        browse.setText(Messages.labelBrowse);
	        browse.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				FileDialog fd = new FileDialog(composite.getShell(), SWT.SAVE);
    				fd.setFilterExtensions(VALID_EXTENSIONS);
    				fd.setText(Messages.ExportModelToXmlWizard_fileDialogTitle);
    				fd.setFileName(fileText.getText());
    				String selected = fd.open();
    				if (selected != null) {
    					targetFileName = selected;
    					fileText.setText(selected);
    					setPageComplete(true);
    					
    					// make sure the user knows that the existing file will be overwritten
    					File file = new File(selected);
    					if (file.exists()) {
    						setMessage(Messages.ExportModelToXmlWizard_warningTargetFileExists, WARNING);
    					}
    				}
    			}
			});
	        
	        setPageComplete(false);
	        setControl(composite);
        }
	}
}
