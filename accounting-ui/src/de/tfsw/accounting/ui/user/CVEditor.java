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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.model.CVEntry;
import de.tfsw.accounting.ui.AbstractAccountingEditor;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.ModelChangeListener;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class CVEditor extends AbstractAccountingEditor implements ModelChangeListener {

	private static final Logger LOG = Logger.getLogger(CVEditor.class);
	
	private static final IInputValidator INPUT_VALIDATOR = new IInputValidator() {
		
		@Override
		public String isValid(String newText) {
			if (newText == null || newText.length() < 1) {
				return "Please enter some text";
			}
			return null;
		}
	};
	
	// UI Elements
	private FormToolkit toolkit;
	private ScrolledForm form;
	private List cvEntriesList;
	private DateTime from;
	private DateTime until;
	private Text customer;
	private Text title;
	private Text tasks;
	private Text description;
	
	// model elements
	private java.util.List<CVEntryWrapper> cvEntries;
	private java.util.List<CVEntry> deletedEntries;
	private Set<Binding> currentBindings;
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		
		form.setText("CV Editor Form Text");
		
		GridLayout layout = new GridLayout(2, true);
		form.getBody().setLayout(layout);
		
		createListSection();
		createDetailsSection();
		
		AccountingUI.addModelChangeListener(this);
		
		modelChanged();
		
		toolkit.decorateFormHeading(form.getForm());
		form.reflow(true);
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
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public CVEditorInput getEditorInput() {
		return (CVEditorInput) super.getEditorInput();
	}
	
	/**
	 * @see de.tfsw.accounting.ui.ModelChangeListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		// clear the UI list
		cvEntriesList.removeAll();
		
		// create a new wrapper list if necessary
		if (cvEntries == null || cvEntries.size() > 0) {
			cvEntries = new ArrayList<CVEditor.CVEntryWrapper>();
		}
		
		// create a new deleted list if necessary
		if (deletedEntries == null || deletedEntries.size() > 0) {
			deletedEntries = new ArrayList<CVEntry>();
		}
		
		// read all entries from persistence and add all entries to the UI list		
		for (CVEntry entry : AccountingUI.getAccountingService().getCvEntries()) {
			cvEntries.add(new CVEntryWrapper(entry));
			cvEntriesList.add(entry.getTitle());
		}
	}
	
	/**
	 * 
	 */
	private void createListSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText("List Section Text");
		section.setDescription("List Section Description");
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
				InputDialog id = new InputDialog(getSite().getShell(), "Title", "Message", "Initial Value", INPUT_VALIDATOR);
				if (InputDialog.OK == id.open()) {
					CVEntry entry = new CVEntry();
					
					entry.setFrom(LocalDate.now());
					entry.setUntil(LocalDate.now());
					entry.setTitle(id.getValue());
					cvEntries.add(new CVEntryWrapper(entry, EntryState.NEW));
					cvEntriesList.add(entry.getTitle());
					
					int newIndex = cvEntriesList.getItemCount() - 1;
					cvEntriesList.setSelection(newIndex);
					updateDetailsSection(newIndex);
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
				CVEntryWrapper wrapper = cvEntries.remove(index);
				cvEntriesList.remove(index);
				deletedEntries.add(wrapper.entry);
				if (index >= cvEntriesList.getItemCount()) {
					index--;
				}
				cvEntriesList.setSelection(index);
				updateDetailsSection(index);
			}
		});
		
		section.setClient(client);
	}
	
	/**
	 * 
	 * @param index
	 */
	private void updateDetailsSection(int index) {
		LOG.debug("Updating details, index: " + index);
		if (index < 0) {
			enableOrDisableDetailFields(false);
		} else {
			enableOrDisableDetailFields(true);
		}
		
		if (currentBindings == null) {
			currentBindings = new HashSet<Binding>();
		}
		
		// remove any previous bindings
		if (currentBindings.size() > 0) {
			for (Binding binding : currentBindings) {
				getBindingContext().removeBinding(binding);
				binding.dispose();
			}
		}
		
		CVEntry entry = cvEntries.get(index).entry;
		
		// and bind the detail components to the newly selected entry
		currentBindings.add(createBindings(title, entry, CVEntry.FIELD_TITLE));
		currentBindings.add(createBindings(customer, entry, CVEntry.FIELD_CUSTOMER));
		currentBindings.add(createBindings(description, entry, CVEntry.FIELD_DESCRIPTION));
		currentBindings.add(createBindings(tasks, entry, CVEntry.FIELD_TASKS));
		WidgetHelper.dateToWidget(entry.getFrom(), from);
		WidgetHelper.dateToWidget(entry.getUntil(), until);
	}
	
	/**
	 * 
	 */
	private void createDetailsSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText("Details Section Text");
		section.setDescription("Details Section Description");
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(2, false));
		
		toolkit.createLabel(client, "From:");
		from = new DateTime(client, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		from.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CVEntry entry = cvEntries.get(cvEntriesList.getSelectionIndex()).entry;
				entry.setFrom(WidgetHelper.widgetToLocalDate(from));
				setIsDirty(true);
			}
		});
		
		
		toolkit.createLabel(client, "Until:");
		until = new DateTime(client, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		until.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CVEntry entry = cvEntries.get(cvEntriesList.getSelectionIndex()).entry;
				entry.setUntil(WidgetHelper.widgetToLocalDate(until));
				setIsDirty(true);
			}
		});
		
		toolkit.createLabel(client, "Customer:");
		customer = createText(client, Constants.EMPTY_STRING);
		
		toolkit.createLabel(client, "Title:");
		title = createText(client, Constants.EMPTY_STRING);
		
		toolkit.createLabel(client, "Tasks:");
		tasks = createText(client, Constants.EMPTY_STRING);
		
		Label descriptionLabel = toolkit.createLabel(client, "Description:");
		descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		description = new Text(client, SWT.MULTI | SWT.BORDER);
		WidgetHelper.grabBoth(description);
		
		enableOrDisableDetailFields(false);
		
		section.setClient(client);		
	}
	
	/**
	 * 
	 * @param enabled
	 */
	private void enableOrDisableDetailFields(boolean enabled) {
		from.setEnabled(enabled);
		until.setEnabled(enabled);
		customer.setEnabled(enabled);
		title.setEnabled(enabled);
		tasks.setEnabled(enabled);
		description.setEnabled(enabled);
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
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/**
	 * 
	 * @author Thorsten Frank
	 *
	 * @since 1.2
	 */
	private enum EntryState {
		SAVED, NEW, MODIFIED;
	}
	
	/**
	 * 
	 * @author Thorsten Frank
	 *
	 * @since 1.2
	 */
	private class CVEntryWrapper {
		private CVEntry entry;
		private EntryState state;
		
		private CVEntryWrapper(CVEntry entry) {
			this(entry, EntryState.SAVED);
		}
		
		private CVEntryWrapper(CVEntry entry, EntryState state) {
			this.entry = entry;
			this.state = state;
		}
	}
}
