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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.ui.AbstractModalDialog;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.CollectionContentProvider;
import de.togginho.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
public class MultiEditExpensesHandler extends AbstractExpenseHandler {

	private static final Logger LOG = Logger.getLogger(MultiEditExpensesHandler.class);
	
	
	private ExpenseType defaultType;
	private String defaultDesc;
	private String defaultCategory;
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		ISelectionProvider selectionProvider = getSelectionProvider(event);
		
		List<Expense> expensesToBeEdited = new ArrayList<Expense>();
		

		
		if (selectionProvider != null && !selectionProvider.getSelection().isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
			Object[] allElements = structuredSelection.toArray();
			for (int i = 0; i < allElements.length; i++) {
				if (allElements[i] instanceof ExpenseWrapper) {
					Expense expense = ((ExpenseWrapper) allElements[i]).getExpense();
					LOG.debug("MULTI_EDITING: " + expense.getDescription());
					expensesToBeEdited.add(expense);
					
					// use the values of the first element as a reference point
					if (i == 0) {
						defaultType = expense.getExpenseType();
						defaultDesc = expense.getDescription() != null ? expense.getDescription() : Constants.EMPTY_STRING;
						defaultCategory = expense.getCategory() != null ? expense.getCategory() : Constants.EMPTY_STRING;
					} else {
						if (expense.getExpenseType() != defaultType) {
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
		
		AbstractModalDialog dialog = new AbstractModalDialog(getShell(event), "MultiEditTitle", "MultiEditMessage", "icons/tag_blue_edit.png") {
			
			@Override
			protected void createMainContents(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(3, false));
				WidgetHelper.grabBoth(composite);
				
				// TYPE
				WidgetHelper.createLabel(composite, Messages.labelExpenseType);
				ComboViewer type = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
				type.add(StructuredSelection.EMPTY);
				WidgetHelper.grabHorizontal(type.getCombo());
				type.setContentProvider(new CollectionContentProvider(true));
				type.setInput(ExpenseType.values());
				type.setLabelProvider(new LabelProvider() {
					@Override
					public String getText(Object element) {
						if (element instanceof ExpenseType) {
							return ((ExpenseType) element).getTranslatedString();
						}
						return Constants.HYPHEN;
					}
				});
				if (defaultType != null) {
					type.setSelection(new StructuredSelection(defaultType));
				} else {
					type.setSelection(StructuredSelection.EMPTY);
				}
				
				final Button changeType = new Button(composite, SWT.CHECK);
				type.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						changeType.setSelection(true);
					}
				});
				
				
				
				
				// DESCRIPTION
				WidgetHelper.createLabel(composite, Messages.labelDescription);
				final Text description = WidgetHelper.createSingleBorderText(composite, defaultDesc);
				final Button changeDescription = new Button(composite, SWT.CHECK);
				description.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						changeDescription.setSelection(true);
					}
				});
						
				// CATEGORY
				WidgetHelper.createLabel(composite, Messages.labelCategory);
				final Text category = WidgetHelper.createSingleBorderText(composite, defaultCategory);
				final Button changeCategory = new Button(composite, SWT.CHECK);
				category.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						changeCategory.setSelection(true);
					}
				});
			}
		};
		
		//dialog.setMessage("MultiEditMessageLater", IMessageProvider.WARNING);
		
		if (dialog.show()) {
//			if (changeType.getSelection()) {
//				IStructuredSelection selection = (IStructuredSelection) type.getSelection();
//				LOG.debug("CHANGING TYPE TO " + (ExpenseType) selection.getFirstElement());
//			}
//			if (changeDescription.getSelection() && !description.getText().isEmpty()) {
//				LOG.debug("CHANGING DESCRIPTION TO " + description.getText());
//			}
//			if (changeCategory.getSelection()) {
//				LOG.debug("CHANGING CATEGORY TO " + category.getText());
//			}
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

}
