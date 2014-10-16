/*
 *  Copyright 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.user;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.menus.MenuUtil;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.CVEntry;
import de.tfsw.accounting.model.CurriculumVitae;
import de.tfsw.accounting.ui.AbstractAccountingEditor;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.IDs;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.ModelChangeListener;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
class CVEditor extends AbstractAccountingEditor implements ModelChangeListener {

	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".CVEditor"; //$NON-NLS-1$
	
	private static final Logger LOG = Logger.getLogger(CVEditor.class);
	
	/**
	 * Used for the "new entry" popup dialog
	 */
	private static final IInputValidator INPUT_VALIDATOR = new IInputValidator() {
		
		@Override
		public String isValid(String newText) {
			if (newText == null || newText.length() < 1) {
				return Messages.CVEditor_addEntryDialogValidatorMessage;
			}
			return null;
		}
	};
	
	// UI Elements
	private FormToolkit toolkit;
	private ScrolledForm form;
	private List cvEntriesList;
	private DateTime from;
	private Button isStillActive;
	private DateTime until;
	private Text customer;
	private Text title;
	private Text tasks;
	private Text description;
	
	// model elements
	private CurriculumVitae cv;
	
	// Model-to-UI Bindings
	// This is re-used whenever a different CV entry is selected
	private Set<Binding> currentBindings;

	// currently selected CV entry, i.e. displayed in the details section - may be null!
	private CVEntry currentSelection;
		
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		LOG.debug("Creating editor"); //$NON-NLS-1$
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		
		form.setText(Messages.CVEditor_header);
		
		GridLayout layout = new GridLayout(2, true);
		form.getBody().setLayout(layout);
		
		createListSection();
		createDetailsSection();
		
		AccountingUI.addModelChangeListener(this);
		
		currentBindings = new HashSet<Binding>();
		
		IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
		menuService.populateContributionManager((ToolBarManager) form.getToolBarManager(), MenuUtil.toolbarUri(IDs.EDIT_USER_ID));
		
		modelChanged();
		
		toolkit.decorateFormHeading(form.getForm());
		form.reflow(true);
	}
	
	/**
	 * 
	 */
	private void createListSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText(Messages.CVEditor_entryListText);
		section.setDescription(Messages.CVEditor_entryListDescrition);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(2, false));
		WidgetHelper.grabBoth(client);
		
		cvEntriesList = new List(client, SWT.BORDER | SWT.SINGLE);
		cvEntriesList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDetailsSection(cvEntriesList.getSelectionIndex());
			}
		});
		
		WidgetHelper.grabBoth(cvEntriesList);
				
		Composite buttons = toolkit.createComposite(client);
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		buttons.setLayout(new FillLayout(SWT.VERTICAL));
		final Button add = toolkit.createButton(buttons, Messages.labelAdd, SWT.PUSH);
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog id = new InputDialog(
						getSite().getShell(), 
						Messages.CVEditor_addEntryDialogTitle, 
						Messages.CVEditor_addEntryDialogMessage, 
						Messages.CVEditor_addEntryDialogSuggestion, 
						INPUT_VALIDATOR);
				
				if (InputDialog.OK == id.open()) {
					CVEntry entry = new CVEntry();
					
					entry.setFrom(LocalDate.now());
					entry.setUntil(LocalDate.now());
					entry.setTitle(id.getValue());
					cv.getReferences().add(entry);
					cvEntriesList.add(entry.getTitle());
					
					int newIndex = cvEntriesList.getItemCount() - 1;
					cvEntriesList.setSelection(newIndex);
					updateDetailsSection(newIndex);
					setIsDirty(true);
				}
			}
		});
			
		final Button remove = toolkit.createButton(buttons, Messages.labelRemove, SWT.PUSH);
		remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = cvEntriesList.getSelectionIndex();
				if (index < 0) {
					return;
				}
				cv.getReferences().remove(index);
				cvEntriesList.remove(index);
				if (index >= cvEntriesList.getItemCount()) {
					index--;
				}
				cvEntriesList.setSelection(index);
				updateDetailsSection(index);
				setIsDirty(true);
			}
		});
		
		section.setClient(client);
	}
	
	/**
	 * 
	 */
	private void createDetailsSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText(Messages.CVEditor_entryDetailsText);
		section.setDescription(Messages.CVEditor_entryDetailsDescription);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(2, false));
		
		toolkit.createLabel(client, Messages.labelFrom);
		from = new DateTime(client, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		from.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LocalDate start = WidgetHelper.widgetToLocalDate(from);
				LOG.debug("PARSED: " + start.toString());
				if (start.isAfter(LocalDate.now())) {
					showWarningMessage(String.format("\"%s\" cannot be in the future", Messages.labelFrom), "Validation Error", false);
					WidgetHelper.dateToWidget(currentSelection.getFrom(), from);
				} else if (currentSelection.getUntil() != null && start.isAfter(currentSelection.getUntil())) {
					showWarningMessage(String.format("\"%s\" must be before \"%s\"", Messages.labelFrom, Messages.labelUntil), "Validation Error", false);
					WidgetHelper.dateToWidget(currentSelection.getFrom(), from);
				} else {
					currentSelection.setFrom(start);
					setIsDirty(true);					
				}
			}
		});
		
		isStillActive = toolkit.createButton(client, "Is still active?", SWT.CHECK);
		GridDataFactory.fillDefaults().grab(true,  false).span(2, 1).applyTo(isStillActive);
		isStillActive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isStillActive.getSelection()) {
					until.setEnabled(false);
					currentSelection.setUntil(null);
				} else {
					until.setEnabled(true);
					currentSelection.setUntil(WidgetHelper.widgetToLocalDate(until));
				}
			}
		});
		
		toolkit.createLabel(client, Messages.labelUntil);
		until = new DateTime(client, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		until.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CVEntry entry = cv.getReferences().get(cvEntriesList.getSelectionIndex());
				entry.setUntil(WidgetHelper.widgetToLocalDate(until));
				setIsDirty(true);
			}
		});
		
		toolkit.createLabel(client, Messages.CVEditor_entryCustomer);
		customer = createText(client, Constants.EMPTY_STRING);
		
		toolkit.createLabel(client, Messages.CVEditor_entryTitle);
		title = createText(client, Constants.EMPTY_STRING);
		
		toolkit.createLabel(client, Messages.CVEditor_entryTasks);
		tasks = createText(client, Constants.EMPTY_STRING);
		
		Label descriptionLabel = toolkit.createLabel(client, Messages.CVEditor_entryTasks);
		descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		description = new Text(client, SWT.MULTI | SWT.BORDER);
		WidgetHelper.grabBoth(description);
		
		enableOrDisableDetailFields(false);
		
		section.setClient(client);		
	}
	
	/**
	 * 
	 * @param index
	 */
	private void updateDetailsSection(int index) {
		// remove any previous bindings
		LOG.debug("Removing bindings");
		if (currentBindings.size() > 0) {
			for (Binding binding : currentBindings) {
				getBindingContext().removeBinding(binding);
				binding.dispose();
			}
		}
		
		LOG.debug("Resetting current selection");
		currentSelection = null;
		
		if (index < 0) {
			LOG.debug("List is empty, disabling fields");
			enableOrDisableDetailFields(false);
		} else {
			currentSelection = cv.getReferences().get(index);
			LOG.debug("Current Selection changed: " + currentSelection.getTitle());
			enableOrDisableDetailFields(true);
			currentBindings.add(createBindings(title, currentSelection, CVEntry.FIELD_TITLE));
			currentBindings.add(createBindings(customer, currentSelection, CVEntry.FIELD_CUSTOMER));
			currentBindings.add(createBindings(description, currentSelection, CVEntry.FIELD_DESCRIPTION));
			currentBindings.add(createBindings(tasks, currentSelection, CVEntry.FIELD_TASKS));
			WidgetHelper.dateToWidget(currentSelection.getFrom(), from);
			
			if (currentSelection.getUntil() != null) {
				isStillActive.setSelection(false);
				until.setEnabled(true);
				WidgetHelper.dateToWidget(currentSelection.getUntil(), until);
			} else {
				WidgetHelper.dateToWidget(LocalDate.now(), until);
				isStillActive.setSelection(true);
				until.setEnabled(false);
			}
		}
	}
	
	/**
	 * 
	 * @param enabled
	 */
	private void enableOrDisableDetailFields(boolean enabled) {
		isStillActive.setEnabled(enabled);
		from.setEnabled(enabled);
		until.setEnabled(enabled);
		customer.setEnabled(enabled);
		title.setEnabled(enabled);
		tasks.setEnabled(enabled);
		description.setEnabled(enabled);
		
		if (!enabled) {
			customer.setText(Constants.BLANK_STRING);
			title.setText(Constants.BLANK_STRING);
			tasks.setText(Constants.BLANK_STRING);
			description.setText(Constants.BLANK_STRING);
		}
	}
	
	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingEditor#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingEditor#getForm()
	 */
	@Override
	protected Form getForm() {
		return form.getForm();
	}

	/**
	 * @see de.tfsw.accounting.ui.AbstractAccountingEditor#dispose()
	 */
	@Override
	public void dispose() {
		AccountingUI.removeModelChangeListener(this);
		super.dispose();
	}

	/**
	 * @see de.tfsw.accounting.ui.ModelChangeListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		// clear the UI list
		cvEntriesList.removeAll();
		
		// read entries from the service
		cv = AccountingUI.getAccountingService().getCurriculumVitae();
		
		// read all entries from persistence and add all entries to the UI list		
		for (CVEntry entry : cv.getReferences()) {
			cvEntriesList.add(entry.getTitle());
		}
	}
	
	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		LOG.debug("doSave");
		
		try {
			AccountingUI.getAccountingService().saveCurriculumVitae(cv);
			setIsDirty(false);
		} catch (Exception e) {
			showError(e);
		}
	}
}
