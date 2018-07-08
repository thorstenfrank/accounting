/**
 * 
 */
package de.tfsw.accounting.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * Base class for editor parts using JFace forms.
 * 
 * @author thorsten
 */
public abstract class AbstractFormBasedEditor {

	private final Logger log = LogManager.getLogger(getClass());
	
	private FormToolkit toolkit;
	private ScrolledForm form;
	private DataBindingContext bindingContext;
	
	@Inject
	private MDirtyable dirtyable;
	
	@PostConstruct
	public void initControl(Composite parent) {
		log.debug("PostCreate from superclass!");
		
		this.toolkit = new FormToolkit(parent.getDisplay());
		this.form = toolkit.createScrolledForm(parent);
		this.bindingContext = new DataBindingContext();
		
		createControl(form.getBody());
		
		toolkit.decorateFormHeading(form.getForm());
		form.reflow(true);
	}
	
	@Focus
	public void onFocus() {
		log.debug("Focus gained");
		form.getBody().setFocus();
	}
	
	/**
	 * Implementations need to create their UI in this method.
	 * 
	 * @param parent the equivalent of {@link Form#getBody()}
	 */
	protected abstract void createControl(Composite parent);
	
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
	 * 
	 * @param parent
	 * @param label
	 * @param text
	 * @param modelObject
	 * @param propertyName
	 * @return
	 */
	protected Text createTextWithLabel(Composite parent, String label, String text, Object modelObject, String propertyName) {
		toolkit.createLabel(parent, label);
		return createText(parent, text, modelObject, propertyName);
	}
	
	/**
	 * Creates JFace bindings for the supplied widget and also adds this instance as a focus and key listener.
	 * @param text
	 * @param modelObject
	 * @param propertyName
	 */
	@SuppressWarnings("unchecked")
	protected Binding createBindings(Text text, Object modelObject, String propertyName) {
//		text.addFocusListener(this);
//		text.addKeyListener(this);
		text.addKeyListener(KeyListener.keyReleasedAdapter(e -> setDirty(true)));
		return bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(text), 
				PojoProperties.value(propertyName).observe(modelObject));
	}
	
	/**
	 * 
	 * @param text
	 * @param modelObject
	 * @param propertyName
	 * @param toModel
	 * @param fromModel
	 */
	@SuppressWarnings("unchecked")
	protected Binding createBindings(
			Text text, Object modelObject, String propertyName, UpdateValueStrategy toModel, UpdateValueStrategy fromModel) {
		return bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(text), 
				PojoProperties.value(propertyName).observe(modelObject), 
				toModel, 
				fromModel);
//		text.addFocusListener(this);
//		text.addKeyListener(this);
	}
	
	/**
	 * @return the toolkit
	 */
	protected FormToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * @return the form
	 */
	protected ScrolledForm getForm() {
		return form;
	}

	/**
	 * @return
	 * @see org.eclipse.e4.ui.model.application.ui.MDirtyable#isDirty()
	 */
	protected boolean isDirty() {
		return dirtyable.isDirty();
	}

	/**
	 * @param value
	 * @see org.eclipse.e4.ui.model.application.ui.MDirtyable#setDirty(boolean)
	 */
	protected void setDirty(boolean value) {
		dirtyable.setDirty(value);
	}
	
}
