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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.ui.AbstractModalDialog;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class MultiEditExpensesHandler extends AbstractExpenseHandler {

	private static final Logger LOG = Logger.getLogger(MultiEditExpensesHandler.class);
	
	private ExpenseType defaultType;
	private boolean applyNewType = false;
	private String defaultDesc;
	private boolean applyNewDesc = false;
	private String defaultCategory;
	private boolean applyNewCategory = false;
	private List<Expense> expensesIncludedInSave;
		
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		
		final List<Expense> expensesToBeEdited = initListOfExpensesToBeEdited(event);
		if (expensesToBeEdited.isEmpty()) {
			LOG.warn("There were no expenses selected, this command should not have been fired!"); //$NON-NLS-1$
			return;
		}
		
		// The list of expenses actually selected - initially all of them
		expensesIncludedInSave = new ArrayList<Expense>(expensesToBeEdited);
		
		AbstractModalDialog dialog = new AbstractModalDialog(
				getShell(event), 
				Messages.MultiEditExpensesHandler_title, 
				Messages.MultiEditExpensesHandler_message, 
				Messages.iconsExpenseEdit) {
			
			@Override
			protected void createMainContents(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, true));
				WidgetHelper.grabBoth(composite);
				
				Composite editingFields = new Composite(composite, SWT.NONE);
				editingFields.setLayout(new GridLayout(3, false));
				WidgetHelper.grabHorizontal(editingFields);
				
				// TYPE
				WidgetHelper.createLabel(editingFields, Messages.labelExpenseType);
				final Combo typeCombo = new Combo(editingFields, SWT.READ_ONLY | SWT.DROP_DOWN);
				WidgetHelper.grabHorizontal(typeCombo);
				final ExpenseType[] allTypes = new ExpenseType[ExpenseType.values().length + 1];
				allTypes[0] = null;
				int selected = 0;
				int index = 1;
				typeCombo.add(Constants.HYPHEN);
				for (ExpenseType type : ExpenseType.values()) {
					typeCombo.add(type.getTranslatedString());
					allTypes[index] = type;
					if (defaultType == type) {
						selected = index;
					}
					index++;
				}
				typeCombo.select(selected);
				
				final Button changeType = new Button(editingFields, SWT.CHECK);
				changeType.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (changeType.getSelection()) {
							setErrorMessage(null);
						}
					}
				});
				
				typeCombo.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						applyNewType = true;
						defaultType = allTypes[typeCombo.getSelectionIndex()];
						changeType.setSelection(true);
					}
				});
				
				// DESCRIPTION
				WidgetHelper.createLabel(editingFields, Messages.labelDescription);
				final Text description = WidgetHelper.createSingleBorderText(editingFields, defaultDesc);
				final Button changeDescription = new Button(editingFields, SWT.CHECK);
				changeDescription.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (changeDescription.getSelection()) {
							setErrorMessage(null);
						}
					}
				});
				description.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						applyNewDesc = true;
						defaultDesc = description.getText();
						changeDescription.setSelection(true);
					}
				});
						
				// CATEGORY
				WidgetHelper.createLabel(editingFields, Messages.labelCategory);
				final Text category = WidgetHelper.createSingleBorderText(editingFields, defaultCategory);
				final Button changeCategory = new Button(editingFields, SWT.CHECK);
				changeCategory.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (changeType.getSelection()) {
							setErrorMessage(null);
						}
					}
				});
				category.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						applyNewCategory = true;
						defaultCategory = category.getText();
						changeCategory.setSelection(true);
					}
				});
				
				Composite expensesComposite = new Composite(composite, SWT.NONE);
				expensesComposite.setLayout(new GridLayout(1, false));
				expensesComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 1).create());
				
				Table expensesTable = new Table(expensesComposite, SWT.H_SCROLL | SWT.V_SCROLL
						| SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK | SWT.BORDER);
				expensesTable.setHeaderVisible(true);
				WidgetHelper.grabHorizontal(expensesTable);
				
				TableColumn selectCol = new TableColumn(expensesTable, SWT.LEFT);
				selectCol.setText(Messages.MultiEditExpensesHandler_apply);
				selectCol.setWidth(50);
				
				TableColumn dateCol = new TableColumn(expensesTable, SWT.LEFT);
				dateCol.setText(Messages.labelDate);
				dateCol.setWidth(100);
				
				TableColumn descCol = new TableColumn(expensesTable, SWT.LEFT);
				descCol.setText(Messages.labelDescription);
				descCol.setWidth(300);
				
				CheckboxTableViewer expensesViewer = new CheckboxTableViewer(expensesTable);
				expensesViewer.setContentProvider(ArrayContentProvider.getInstance());
				expensesViewer.setLabelProvider(new ExpensesLabelProvider());
				expensesViewer.setInput(expensesToBeEdited);
				expensesViewer.setAllChecked(true);
				expensesViewer.addCheckStateListener(new ICheckStateListener() {
					
					@Override
					public void checkStateChanged(CheckStateChangedEvent event) {
						Expense expense = (Expense) event.getElement();
						if (event.getChecked()) {
							expensesIncludedInSave.add(expense);
						} else {
							expensesIncludedInSave.remove(expense);
						}
						if (expensesIncludedInSave.isEmpty()) {
							setErrorMessage(Messages.MultiEditExpensesHandler_errorNoExpensesSelected);
						} else {
							setErrorMessage(null);
						}
					}
				});
			}
			
			@Override
			protected void buttonPressed(int buttonId) {
				if (buttonId == IDialogConstants.OK_ID) {
					if (expensesIncludedInSave.isEmpty()) {
						setErrorMessage(Messages.MultiEditExpensesHandler_errorNoExpensesSelected);
						return;
					}
					
					if (!applyNewCategory && !applyNewDesc && !applyNewType) {
						setErrorMessage(Messages.MultiEditExpensesHandler_errorNoChanges);
						return;
					}
				}
			    super.buttonPressed(buttonId);
			}
		};
				
		if (dialog.show()) {			
			for (Expense expense : expensesIncludedInSave) {
				LOG.debug("About to save: " + expense.getDescription());
				if (applyNewCategory) {
					LOG.debug(String.format("Setting new category [%s] in expense [%s]", defaultCategory, expense.getDescription())); //$NON-NLS-1$
					expense.setCategory(defaultCategory);
				}
				if (applyNewDesc) {
					LOG.debug(String.format("Setting new description [%s] in expense [%s]", defaultDesc, expense.getDescription())); //$NON-NLS-1$
					expense.setDescription(defaultDesc);
				}
				if (applyNewType) {
					LOG.debug(String.format("Setting new type [%s] in expense [%s]", defaultType, expense.getDescription())); //$NON-NLS-1$
					expense.setExpenseType(defaultType);
				}
			}
			
			AccountingUI.getAccountingService().saveExpenses(expensesIncludedInSave);
		}
	}
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	/**
	 * 
	 * @param event
	 * @return
	 */
	private List<Expense> initListOfExpensesToBeEdited(ExecutionEvent event) {
		final List<Expense> expensesToBeEdited = new ArrayList<Expense>();
		
		ISelectionProvider selectionProvider = getSelectionProvider(event);
		if (selectionProvider != null && !selectionProvider.getSelection().isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
			Object[] allElements = structuredSelection.toArray();
			for (int i = 0; i < allElements.length; i++) {
				if (allElements[i] instanceof ExpenseWrapper) {
					Expense expense = ((ExpenseWrapper) allElements[i]).getExpense();
					LOG.debug("MULTI_EDITING: " + expense.getDescription()); //$NON-NLS-1$
					expensesToBeEdited.add(expense);
					
					// use the values of the first element as a reference point
					if (i == 0) {
						defaultType = expense.getExpenseType();
						defaultDesc = expense.getDescription() != null ? expense.getDescription() : Constants.EMPTY_STRING;
						defaultCategory = expense.getCategory() != null ? expense.getCategory() : Constants.EMPTY_STRING;
					} else {
						if (defaultType != expense.getExpenseType()) {
							defaultType = null;
						}
						if (!defaultDesc.equals(expense.getDescription())) {
							defaultDesc = Constants.EMPTY_STRING;
						}
						if (!defaultCategory.equals(expense.getCategory())) {
							defaultCategory = Constants.EMPTY_STRING;
						}
					}
				}
			}
		}
		
		return expensesToBeEdited;
	}
	
	/**
	 * 
	 * @author thorsten
	 *
	 */
	private class ExpensesLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
	        return null;
        }

		/**
         * {@inheritDoc}.
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
        	Expense expense = (Expense) element;
        	
        	switch (columnIndex) {
			case 1:
				return FormatUtil.formatDate(expense.getPaymentDate());
			case 2:
				return expense.getDescription();
			default:
				return null;
			}
        }
		
	}
}
