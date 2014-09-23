/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui;

import java.util.EventObject;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import de.togginho.accounting.model.Address;
import de.togginho.accounting.ui.util.WidgetHelper;

/**
 * @author tfrank1
 *
 */
public abstract class AbstractAccountingEditor extends EditorPart implements FocusListener, KeyListener {
	
	private static Logger LOG;
	private boolean isDirty = false;
	private String focusedElementContents = null;
	private DataBindingContext bindingContext;
	
	/**
	 * 
	 */
	public AbstractAccountingEditor() {
		super();
		if (LOG == null) {
			LOG = Logger.getLogger(getClass());
		}
		LOG.debug("Creating new editor of type: " + getClass().getName()); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		bindingContext = new DataBindingContext();
	}
	
	/**
	 * Returns the toolkit for this editor.
	 * @return the {@link FormToolkit}
	 */
	protected abstract FormToolkit getToolkit();
	
	/**
	 * Returns the form for this editor.
	 * @return the {@link Form}
	 */
	protected abstract Form getForm();
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// nothing to do here...
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		updateFocusedElementContents(e);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof Text) {
			focusedElementContents = ((Text) e.getSource()).getText();
			LOG.debug("Focused element value: " + focusedElementContents); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		updateFocusedElementContents(e);
		focusedElementContents = null;
	}

	/**
	 * 
	 * @param e
	 */
	private void updateFocusedElementContents(EventObject e) {
		if (e.getSource() instanceof Text) {
			final String contents = ((Text) e.getSource()).getText();
			if (!contents.equals(focusedElementContents)) {
				LOG.debug(String.format(
						"Value changed from [%s] to [%s], editor is now dirty", focusedElementContents, contents)); //$NON-NLS-1$
				// only have to do this once... 
				if (!isDirty) {
					setIsDirty(true);
				}
			}
		}
	}
		
	/**
	 * 
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		FormToolkit toolkit = getToolkit();
		if (toolkit != null) {
			toolkit.dispose();
		}
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return isDirty;
	}
	
	/**
	 * 
	 * @param dirty
	 */
	protected void setIsDirty(boolean dirty) {
		isDirty = dirty;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * Always returns false.
	 * @return <code>false</code>
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		getForm().getBody().setFocus();
	}

	/**
	 * Creates a {@link Text} widget for a form using {@link SWT#SINGLE} and {@link SWT#BORDER}.
	 * 
	 * <p>The created text is configured to grab all available horizontal space using 
	 * {@link WidgetHelper#FILL_GRID_DATA}.</p>
	 * 
	 * @param parent the containing composite
	 * @param text the {@link Text#setText(String)}
	 * @return the {@link Text}
	 */
	protected Text createText(Composite parent, String text) {
		Text textField = getToolkit().createText(parent, text, SWT.SINGLE | SWT.BORDER);
		WidgetHelper.grabHorizontal(textField);
		WidgetHelper.selectAllOnEntry(textField);
		return textField;
	}
	
	/**
	 * Creates a {@link Text} widget for a form using {@link SWT#SINGLE} and {@link SWT#BORDER}. Additionally, a
	 * data binding is created using the supplied model object and property name via 
	 * {@link #createBindings(Text, Object, String)}. The created text is configured to grab all available horizontal
	 * space using {@link WidgetHelper#FILL_GRID_DATA}.
	 * 
	 * @param parent the parent composite
	 * @param text the text for the new text field
	 * @param modelObject the model object to bind the new text field to
	 * @param propertyName the property name of the modelObject to bind the new text field to
	 * @return the newly created and bound {@link Text}
	 */
	protected Text createText(Composite parent, String text, Object modelObject, String propertyName) {
		Text textField = createText(parent, text);	
		createBindings(textField, modelObject, propertyName);
		return textField;
	}
	
	/**
	 * Creates JFace bindings for the supplied widget and also adds this instance as a focus and key listener.
	 * @param text
	 * @param modelObject
	 * @param propertyName
	 */
	protected void createBindings(Text text, Object modelObject, String propertyName) {
		IObservableValue widgetObservable = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue pojoObservable = PojoObservables.observeValue(modelObject, propertyName);
		bindingContext.bindValue(widgetObservable, pojoObservable);
		text.addFocusListener(this);
		text.addKeyListener(this);
	}
	
	/**
	 * 
	 * @param text
	 * @param modelObject
	 * @param propertyName
	 * @param toModel
	 * @param fromModel
	 */
	protected void createBindings(Text text, Object modelObject, String propertyName, UpdateValueStrategy toModel, 
			UpdateValueStrategy fromModel) {
		
		IObservableValue widgetObservable = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue pojoObservable = PojoObservables.observeValue(modelObject, propertyName);
		bindingContext.bindValue(widgetObservable, pojoObservable, toModel, fromModel);
		text.addFocusListener(this);
		text.addKeyListener(this);
	}
	
	/**
	 * @return the bindingContext
	 */
	protected DataBindingContext getBindingContext() {
		return bindingContext;
	}

	/**
	 * Creates an entire {@link Section} representing the data of the supplied {@link Address}.
	 * The widgets and address data is automatically bound.
	 * 
	 * @param address the address to create a visual representation for
	 */
	protected void createAddressSection(Address address) {
		FormToolkit toolkit = getToolkit();
		Section addressSection = toolkit.createSection(getForm().getBody(), Section.TITLE_BAR);
		addressSection.setText(Messages.labelAddress);
		addressSection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Composite addressSectionClient = getToolkit().createComposite(addressSection);
		addressSectionClient.setLayout(new GridLayout(2, false));
		
		toolkit.createLabel(addressSectionClient, Messages.labelRecipientDetail);
		createText(addressSectionClient, address.getRecipientDetail(), address, Address.FIELD_RECIPIENT_DETAIL);
		toolkit.createLabel(addressSectionClient, Messages.labelStreet);
		createText(addressSectionClient, address.getStreet(), address, Address.FIELD_STREET);
		toolkit.createLabel(addressSectionClient, Messages.labelPostalCode);
		createText(addressSectionClient, address.getPostalCode(), address, Address.FIELD_POSTAL_CODE);
		toolkit.createLabel(addressSectionClient, Messages.labelCity);
		createText(addressSectionClient, address.getCity(), address, Address.FIELD_CITY);
		toolkit.createLabel(addressSectionClient, Messages.labelEmail);
		createText(addressSectionClient, address.getEmail(), address, Address.FIELD_EMAIL);
		toolkit.createLabel(addressSectionClient, Messages.labelPhone);
		createText(addressSectionClient, address.getPhoneNumber(), address, Address.FIELD_PHONE_NUMBER);
		toolkit.createLabel(addressSectionClient, Messages.labelMobile);
		createText(addressSectionClient, address.getMobileNumber(), address, Address.FIELD_MOBILE_NUMBER);
		toolkit.createLabel(addressSectionClient, Messages.labelFax);
		createText(addressSectionClient, address.getFaxNumber(), address, Address.FIELD_FAX_NUMBER);
		
		addressSection.setClient(addressSectionClient);
	}
	
	/**
	 * 
	 * @param e
	 */
	protected void showError(Exception e) {
		MessageBox msgBox = new MessageBox(getEditorSite().getShell(), SWT.ICON_ERROR | SWT.OK);
		msgBox.setMessage(e.getLocalizedMessage());
		msgBox.setText(Messages.labelError);
		msgBox.open();
	}
}