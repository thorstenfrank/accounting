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
package de.togginho.accounting.ui.expense;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
public class ImportExpensesFromCsvWizard extends Wizard implements IImportWizard {

	private String sourceFile;
	private Collection<Expense> selectedExpenses;
	
	/**
     * {@inheritDoc}.
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    	setWindowTitle("Import from CSV Wizard Window Title");
    	setNeedsProgressMonitor(true);
    }

	/**
     * {@inheritDoc}.
     * @see Wizard#addPages()
     */
    @Override
    public void addPages() {
	    addPage(new ChooseFilePage());
    }
    
	/**
	 * {@inheritDoc}.
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		AccountingUI.getAccountingService().saveExpenses(selectedExpenses);
		return true;
	}

	/**
	 * 
	 * @author thorsten
	 *
	 */
	private class ChooseFilePage extends WizardPage {
		private CheckboxTableViewer expensesViewer;

		/**
		 * 
		 */
		private ChooseFilePage() {
			super("ChooseFilePage", "ChooseFilePage", null);
		}
		
		/**
         * {@inheritDoc}.
         * @see IDialogPage#createControl(Composite)
         */
        @Override
        public void createControl(Composite parent) {
	        final Composite composite = new Composite(parent, SWT.NONE);
	        composite.setLayout(new GridLayout(3, false));
	        
	        Label label = new Label(composite, SWT.NONE);
	        label.setText("From File:");
	        
	        final Text fileText = new Text(composite, SWT.BORDER);
	        WidgetHelper.grabHorizontal(fileText);
	        fileText.addKeyListener(new KeyAdapter() {

				/**
                 * {@inheritDoc}.
                 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
                 */
                @Override
                public void keyReleased(KeyEvent e) {
                	sourceFile = fileText.getText();
                	checkSourceFile();
                }
	        	
			});
	        
	        Button browse = new Button(composite, SWT.PUSH);
	        browse.setText("Browse...");
	        browse.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				FileDialog fd = new FileDialog(composite.getShell(), SWT.OPEN);
    				fd.setFilterExtensions(new String[]{"*.csv", "*.txt"}); //$NON-NLS-1$
    				fd.setText("Select Text");
    				fd.setFileName(fileText.getText());
    				String selected = fd.open();
    				if (selected != null) {
    					fileText.setText(selected);
    					sourceFile = fileText.getText();
    					checkSourceFile();
    				}
    			}
			});
	        
	        Button selectAll = new Button(composite, SWT.PUSH);
	        selectAll.setText(Messages.labelSelectAll);
	        selectAll.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                	expensesViewer.setAllChecked(true);
                	selectedExpenses = new ArrayList<Expense>((Collection<Expense>)expensesViewer.getInput());
                	checkIfPageComplete();
                }
	        	
			});
	        
	        Button selectNone = new Button(composite, SWT.PUSH);
	        selectNone.setText(Messages.labelSelectNone);
	        selectNone.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                	expensesViewer.setAllChecked(false);
                	selectedExpenses.clear();
                	checkIfPageComplete();
                }
	        	
			});
	        
	        // spacer label
	        WidgetHelper.createLabel(composite, Constants.BLANK_STRING);
	        
			Composite expensesComposite = new Composite(composite, SWT.NONE);
			expensesComposite.setLayout(new GridLayout(1, false));
			expensesComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 1).create());
			
			Table expensesTable = new Table(expensesComposite, SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK | SWT.BORDER);
			expensesTable.setHeaderVisible(true);
			WidgetHelper.grabBoth(expensesTable);
			
			TableColumn selectCol = new TableColumn(expensesTable, SWT.LEFT);
			selectCol.setText(Messages.MultiEditExpensesHandler_apply);
			selectCol.setWidth(50);
			
			TableColumn dateCol = new TableColumn(expensesTable, SWT.LEFT);
			dateCol.setText(Messages.labelDate);
			dateCol.setWidth(75);
			
			TableColumn descCol = new TableColumn(expensesTable, SWT.LEFT);
			descCol.setText(Messages.labelDescription);
			descCol.setWidth(150);
			
			TableColumn catCol = new TableColumn(expensesTable, SWT.LEFT);
			catCol.setText(Messages.labelCategory);
			catCol.setWidth(150);
			
			TableColumn typeCol = new TableColumn(expensesTable, SWT.LEFT);
			typeCol.setText(Messages.labelExpenseType);
			typeCol.setWidth(100);
			
			TableColumn netCol = new TableColumn(expensesTable, SWT.RIGHT);
			netCol.setText(Messages.labelNet);
			netCol.setWidth(100);
			
			TableColumn taxCol = new TableColumn(expensesTable, SWT.CENTER);
			taxCol.setText(Messages.labelTaxes);
			taxCol.setWidth(75);
			
			expensesViewer = new CheckboxTableViewer(expensesTable);
			expensesViewer.setContentProvider(ArrayContentProvider.getInstance());
			expensesViewer.setLabelProvider(new ExpensesLabelProvider());
			expensesViewer.addCheckStateListener(new ICheckStateListener() {
				
				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					Expense expense = (Expense) event.getElement();
					if (event.getChecked()) {
						selectedExpenses.add(expense);
					} else {
						selectedExpenses.remove(expense);
					}
					checkIfPageComplete();
				}
			});
	        
	        setPageComplete(false);
	        setControl(composite);
        }
		
        /**
         * 
         */
        private void checkSourceFile() {
        	File file = new File(sourceFile);
        	if (!file.exists() || file.isDirectory()) {
        		setErrorMessage("File does not exist or is a directory!");
        	} else {
        		try {
	                Collection<Expense> expenses = AccountingUI.getAccountingService().importExpenses(sourceFile);
	                expensesViewer.setInput(expenses);
	                expensesViewer.setAllChecked(true);
	                selectedExpenses = new ArrayList<Expense>(expenses);
	                checkIfPageComplete();
                } catch (AccountingException e) {
                	setErrorMessage(e.getMessage());
                }
        	}
        }
        
        private void checkIfPageComplete() {
        	if (selectedExpenses.isEmpty()) {
        		setErrorMessage(Messages.MultiEditExpensesHandler_errorNoExpensesSelected);
        		setPageComplete(false);
        	} else {
        		setErrorMessage(null);
        		setPageComplete(true);
        	}
        }
	}
	
}
