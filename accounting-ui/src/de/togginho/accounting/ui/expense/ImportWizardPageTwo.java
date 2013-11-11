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
package de.togginho.accounting.ui.expense;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseImportResult;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
class ImportWizardPageTwo extends WizardPage {

	private static final String PAGE_NAME = "ImportWizardPageTwo"; //$NON-NLS-1$
	
	private Collection<Expense> importedExpenses;
	private Collection<Expense> selectedExpenses;
	private CheckboxTableViewer expensesViewer;
	private Label summaryLabel;
	private Button selectAll;
	private Button selectNone;
	
	/**
	 * 
	 */
	ImportWizardPageTwo() {
		super(PAGE_NAME, Messages.ImportExpensesWizard_title, AccountingUI.getImageDescriptor(Messages.iconsExpenseAdd));
		setDefaultMessage();
	}
	
	/**
     * {@inheritDoc}.
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
    	if (visible) {
    		ExpenseImportResult result = getImportResult();
    		if (result == null) {
    			setErrorMessage(Messages.ImportExpensesWizard_errorNoImportResult);
    		} else if (result.getError() != null) {
    			setErrorMessage(result.getError());
    		} else {
    			importedExpenses = result.getExpenses();
    			selectedExpenses = new ArrayList<Expense>();
    			expensesViewer.setInput(importedExpenses);
    			
    			updatePage();
    			setErrorMessage(null);
    			if (result.hasWarnings()) {
    				setWarningMessage(Messages.ImportExpensesWizard_warningsExist);
    			} else {
    				setDefaultMessage();
    			}
    		}
    		
    	}
	    super.setVisible(visible);
    }
    
    /**
     * 
     * @return
     */
    protected Collection<Expense> getSelectedExpenses() {
    	return selectedExpenses;
    }
    
    /**
     * 
     */
    private void setDefaultMessage() {
    	setMessage(Messages.ImportExpensesWizard_pageTwo);
    }
    
    /**
     * 
     * @param msg
     */
    private void setWarningMessage(String msg) {
    	setMessage(msg, IMessageProvider.WARNING);
    }
    
	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        
		Composite expensesComposite = new Composite(composite, SWT.NONE);
		expensesComposite.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(expensesComposite);
		
		Table expensesTable = new Table(expensesComposite, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.SINGLE | SWT.CHECK | SWT.BORDER);
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
		expensesViewer.setLabelProvider(new ExpensesLabelProvider() {
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == 0) {
					Expense exp = (Expense) element;
					if (getImportResult().hasWarningFor(exp)) {
						return AccountingUI.getImageDescriptor(Messages.iconsError).createImage();
					}
				}
				return super.getColumnImage(element, columnIndex);
			}
		});
		expensesViewer.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Expense expense = (Expense) event.getElement();
				if (event.getChecked()) {
					selectedExpenses.add(expense);
				} else {
					selectedExpenses.remove(expense);
				}
				updatePage();
			}
		});
		
		expensesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Expense selected = (Expense) ((StructuredSelection)event.getSelection()).getFirstElement();
				if (getImportResult().hasWarningFor(selected)) {
					setWarningMessage(getImportResult().getWarnings().get(selected));
				} else {
					setDefaultMessage();
				}
			}
		});
		
		Composite summaryComposite = new Composite(composite, SWT.NONE);
		summaryComposite.setLayout(new GridLayout(1, false));
		WidgetHelper.grabHorizontal(summaryComposite);
		summaryLabel = WidgetHelper.createLabel(summaryComposite, Constants.BLANK_STRING);
		WidgetHelper.grabHorizontal(summaryLabel);
		
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(buttonComposite);
		
        selectAll = new Button(buttonComposite, SWT.PUSH);
        selectAll.setEnabled(false);
        selectAll.setText(Messages.labelSelectAll);
        selectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	expensesViewer.setAllChecked(true);
            	selectedExpenses = new ArrayList<Expense>(importedExpenses);
            	updatePage();
            }
        	
		});
        
        selectNone = new Button(buttonComposite, SWT.PUSH);
        selectNone.setEnabled(false);
        selectNone.setText(Messages.labelSelectNone);
        selectNone.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	expensesViewer.setAllChecked(false);
            	selectedExpenses.clear();
            	updatePage();
            }
        	
		});
		
        setPageComplete(false);
        setControl(composite);
	}

	/**
	 * 
	 * @return
	 */
	private ExpenseImportResult getImportResult() {
		return ((ImportExpensesFromCsvWizard) getWizard()).getImportResult();
	}
	
    /**
     * 
     */
    private void updatePage() {
    	int noSelected = 0;
    	int noAvailable = 0;
    	
    	if (importedExpenses != null && importedExpenses.size() > 0) {
        	selectAll.setEnabled(true);
        	selectNone.setEnabled(true);
        	noSelected = selectedExpenses.size();
        	noAvailable = importedExpenses.size();
    	} else {
        	selectAll.setEnabled(false);
        	selectNone.setEnabled(false);
    	}
    	
    	summaryLabel.setText(Messages.bind(Messages.ImportExpensesWizard_summary, noSelected, noAvailable));
    	
    	if (selectedExpenses.isEmpty()) {
    		setErrorMessage(Messages.MultiEditExpensesHandler_errorNoExpensesSelected);
    		setPageComplete(false);
    	} else {
    		setErrorMessage(null);
    		setPageComplete(true);
    	}
    }
}
