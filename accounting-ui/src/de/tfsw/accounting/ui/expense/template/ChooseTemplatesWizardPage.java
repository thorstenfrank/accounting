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
package de.tfsw.accounting.ui.expense.template;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.util.FormatUtil;

/**
 * @author Thorsten Frank
 *
 */
class ChooseTemplatesWizardPage extends WizardPage {
	
	private static final Logger LOG = Logger.getLogger(ChooseTemplatesWizardPage.class);
	
	private static final String ROOT_ELEMENT = ChooseTemplatesWizardPage.class.getName();
		
	private Map<ExpenseTemplate, Set<ConcreteTemplateInstance>> allTemplates;
	private Collection<ConcreteTemplateInstance> selectedTemplates;
	
	/**
	 * 
	 * @param templates
	 */
	ChooseTemplatesWizardPage(Collection<ExpenseTemplate> templates) {
		super(ChooseTemplatesWizardPage.class.getName());
		setTitle(Messages.ChooseTemplatesWizardPage_title);
		setDescription(Messages.ChooseTemplatesWizardPage_desc);
		allTemplates = new HashMap<ExpenseTemplate, Set<ConcreteTemplateInstance>>();
		selectedTemplates = new HashSet<ConcreteTemplateInstance>();
		
		for (ExpenseTemplate template : templates) {
			SortedSet<ConcreteTemplateInstance> concretes = new TreeSet<ChooseTemplatesWizardPage.ConcreteTemplateInstance>();
			for (LocalDate date : template.getOutstandingApplications()) {
				ConcreteTemplateInstance instance = new ConcreteTemplateInstance(template, date);
				concretes.add(instance);
				selectedTemplates.add(instance);
			}
			allTemplates.put(template, concretes);
		}
	}
	
	/**
	 * 
	 */
	protected void applySelectedTemplates() {
		Map<ExpenseTemplate, Set<LocalDate>> map = new HashMap<ExpenseTemplate, Set<LocalDate>>();
		for (ConcreteTemplateInstance instance : selectedTemplates) {
			if (!map.containsKey(instance.template)) {
				map.put(instance.template, new HashSet<LocalDate>());
			}
			map.get(instance.template).add(instance.date);
		}
				
		AccountingService service = AccountingUI.getAccountingService();
		
		for (ExpenseTemplate template : map.keySet()) {
			LOG.debug("Now applying template: " + template.getDescription() + " / " + template.getNumberOfOutstandingApplications()); //$NON-NLS-1$ //$NON-NLS-2$			
			for (LocalDate selectedDate : map.get(template)) {
				Expense expense = template.apply(selectedDate);
				LOG.info(String.format("Expense [%s] created for [%s]", expense.getDescription(), expense.getPaymentDate().toString())); //$NON-NLS-1$
				service.saveExpense(expense);
			}
			
			LOG.debug("Done saving expenses, now saving template..."); //$NON-NLS-1$
			service.saveExpenseTemplate(template);
		}
		
		LOG.debug("Done!"); //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        
        buildTree(composite);
        
        setControl(composite);
	}
	
	/**
	 * 
	 * @param composite
	 */
	private void buildTree(Composite parent) {
		CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		GridDataFactory.fillDefaults().grab(true,  true).span(2, 1).applyTo(tree);
		
		createTreeColumn(tree, SWT.CENTER, String.format("%s / %s", Messages.labelDescription, Messages.labelDate), 200); //$NON-NLS-1$
		createTreeColumn(tree, SWT.CENTER, Messages.labelExpenseType, 150);
		createTreeColumn(tree, SWT.CENTER, Messages.labelCategory, 150);
		createTreeColumn(tree, SWT.RIGHT, Messages.labelNet, 100);
		createTreeColumn(tree, SWT.CENTER, Messages.labelTaxRate, 100);
		
		treeViewer.setContentProvider(new TemplateContentProvider());
		treeViewer.setLabelProvider(new TemplateLabelProvider());
		treeViewer.setInput(ROOT_ELEMENT);
		
		treeViewer.expandAll();
		for (ExpenseTemplate template : allTemplates.keySet()) {
			treeViewer.setSubtreeChecked(template, true);
		}
		
		treeViewer.addCheckStateListener(e -> {
			if (e.getElement() instanceof ExpenseTemplate) {
				treeViewer.setSubtreeChecked(e.getElement(), e.getChecked());
				for (ConcreteTemplateInstance instance : allTemplates.get((ExpenseTemplate) e.getElement())) {
					addOrRemoveFromSelection(instance, e.getChecked());
				}
			} else if (e.getElement() instanceof ConcreteTemplateInstance){
				addOrRemoveFromSelection((ConcreteTemplateInstance)e.getElement(), e.getChecked());
			}
		});
	}
	
	/**
	 * 
	 * @param instance
	 * @param selected
	 */
	private void addOrRemoveFromSelection(ConcreteTemplateInstance instance, boolean selected) {
		if (selected) {
			selectedTemplates.add(instance);
		} else {
			selectedTemplates.remove(instance);
		}
		
		setPageComplete(selectedTemplates.size() > 0);
	}
	
	/**
	 * 
	 * @param tree
	 * @param style
	 * @param label
	 * @param width
	 */
	private void createTreeColumn(Tree tree, int style, String label, int width) {
		TreeColumn col = new TreeColumn(tree, style);
		col.setText(label);
		col.setWidth(width); 
	}
	
	/**
	 * 
	 *
	 */
	private class TemplateContentProvider implements ITreeContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			//LOG.debug(String.format("ContentProvider.getElements([%s])", inputElement));
			
			if (ROOT_ELEMENT.equals(inputElement)) {
				return allTemplates.keySet().toArray();
			}
			
			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			//LOG.debug(String.format("ContentProvider.getChildren([%s])", parentElement));
			if (parentElement instanceof ExpenseTemplate) {
				return allTemplates.get((ExpenseTemplate) parentElement).toArray();
			}
			return new Object[0];
		}

		@Override
		public Object getParent(Object element) {
			//LOG.debug(String.format("ContentProvider.getParent([%s])", element));
			if (element instanceof ConcreteTemplateInstance) {
				return ((ConcreteTemplateInstance) element).template;
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			//LOG.debug(String.format("ContentProvider.hasChildren([%s])", element));
			return (element instanceof ExpenseTemplate);
		}
		@Override
		public void dispose() {
			//LOG.debug("ContentProvider.dispose()");
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			//LOG.debug(String.format("ContentProvider.inputChanged([%s], [%s])", oldInput, newInput));
		}
	}
	
	/**
	 *
	 */
	private class TemplateLabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object element, int columnIndex) {
			switch(columnIndex) {
			case 0:
				if (element instanceof ExpenseTemplate) {
					return ((ExpenseTemplate) element).getDescription();
				} else if (element instanceof ConcreteTemplateInstance) {
					return FormatUtil.formatDate(((ConcreteTemplateInstance) element).date);
				}
				break;
			case 1:
				if (element instanceof ExpenseTemplate) {
					return ((ExpenseTemplate) element).getExpenseType().getTranslatedString();
				}
				break;
			case 2:
				if (element instanceof ExpenseTemplate) {
					String cat = ((ExpenseTemplate) element).getCategory();
					return cat != null ? cat : Constants.HYPHEN;
				}
				break;
			case 3:
				if (element instanceof ExpenseTemplate) {
					return FormatUtil.formatCurrency(((ExpenseTemplate) element).getNetAmount());
				}
				break;
			case 4:
				if (element instanceof ExpenseTemplate) {
					TaxRate rate = ((ExpenseTemplate) element).getTaxRate();
					return rate != null ? rate.toShortString() : Constants.HYPHEN;
				}
				break;
			}
			
			return null;
		}
		@Override
		public void addListener(ILabelProviderListener listener) {			
		}

		@Override
		public void dispose() {
		}
		
		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		
		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}
	
	/**
	 * 
	 */
	private class ConcreteTemplateInstance implements Comparable<ConcreteTemplateInstance> {
		private ExpenseTemplate template;
		private LocalDate date;
		
		ConcreteTemplateInstance(ExpenseTemplate template, LocalDate date) {
			this.template = template;
			this.date = date;
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(ConcreteTemplateInstance o) {
			int val = template.getDescription().compareTo(o.template.getDescription());
			if (val == 0) {
				val = date.compareTo(o.date);
			}
			return val;
		}
	}
}
