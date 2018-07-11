/**
 * 
 */
package de.tfsw.accounting.ui;

import java.text.Normalizer.Form;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.ui.util.CssStyleClass;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * This used to be a base class for editors using the Forms toolkit. This no longer seems to be supported
 * in e4, because... well, you know - it was a good featue. So now we have to do everything manually and also
 * style the editors using CSS.
 * 
 * @author thorsten
 */
public abstract class AbstractFormBasedEditor {

	private final Logger log = LogManager.getLogger(getClass());
	
	private DataBindingContext bindingContext;
	
	@Inject
	private MPart part;
	
	@Inject
	private MDirtyable dirtyable;
	
	private ScrolledComposite scrollable;
	
	@PostConstruct
	public void initControl(Composite parent) {
		log.debug("PostCreate from superclass!");
		
		this.bindingContext = new DataBindingContext();
		
		parent.setLayout(new FillLayout());
		
		this.scrollable = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		
		scrollable.setExpandHorizontal(true);
		scrollable.setExpandVertical(true);
		
		Composite client = new Composite(scrollable, SWT.NULL);
		client.setLayout(new GridLayout());
		WidgetHelper.grabBoth(client);
		WidgetHelper.applyStyle(client, CssStyleClass.editorWindow);
		
		createHeader(client);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(new Label(client, SWT.SEPARATOR | SWT.HORIZONTAL));
		
		Composite content = new Composite(client, SWT.NULL);
		WidgetHelper.grabBoth(content);
		WidgetHelper.applyStyle(content, CssStyleClass.editorWindow);
		createControl(content);
		
		scrollable.setContent(client);
		scrollable.setMinSize(client.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	/**
	 * This is pretty much just stolen from {@link TitleAreaDialog}'s createHeader() method.
	 * @param parent
	 */
	private void createHeader(Composite parent) {
		Composite header = new Composite(parent, SWT.NONE);
		header.setLayout(new FormLayout());
		WidgetHelper.applyStyle(header, CssStyleClass.editorHeader);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(header);
		
		final int horizontalSpacing = 5;
		final int verticalSpacing = 5;
		
		Label titleImageLabel = new Label(header, SWT.CENTER);
		WidgetHelper.applyStyle(titleImageLabel, CssStyleClass.editorHeader);
		titleImageLabel.setImage(WidgetHelper.createImageFromFile("icons/logo_50.png"));
		FormData imageData = new FormData();
		imageData.top = new FormAttachment(0, 0);
		imageData.right = new FormAttachment(100, 0);
		titleImageLabel.setLayoutData(imageData);
		
		Label titleLabel = new Label(header, SWT.LEFT);
		titleLabel.setText(getEditorHeader());
		WidgetHelper.applyStyle(titleLabel, CssStyleClass.editorTitle);
		FormData titleData = new FormData();
		titleData.top = new FormAttachment(0, 5);
		titleData.right = new FormAttachment(titleImageLabel);
		titleData.left = new FormAttachment(0, 5);
		titleLabel.setLayoutData(titleData);
		
		Label messageImageLabel = new Label(header, SWT.CENTER);
		WidgetHelper.applyStyle(messageImageLabel, CssStyleClass.editorDesc);
		FormData messageImageData = new FormData();
		messageImageData.top = new FormAttachment(titleLabel, verticalSpacing);
		messageImageData.left = new FormAttachment(0, horizontalSpacing);
		messageImageLabel.setLayoutData(messageImageData);
		
		// Message label @ bottom, center
		Label messageLabel = new Label(header, SWT.NONE);
		messageLabel.setText("Lorem Ipsum Dolerat Compendianum \n ad infinitum rhabarberis");
		WidgetHelper.applyStyle(messageLabel, CssStyleClass.editorDesc);
		FormData messageLabelData = new FormData();
		messageLabelData.top = new FormAttachment(titleLabel, verticalSpacing);
		messageLabelData.right = new FormAttachment(titleImageLabel);
		messageLabelData.left = new FormAttachment(messageImageLabel, horizontalSpacing);
		messageLabelData.height = messageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		messageLabel.setLayoutData(messageLabelData);
		
		// Filler labels
//		Label leftFillerLabel = new Label(header, SWT.CENTER);
//		WidgetHelper.applyStyle(leftFillerLabel, CssStyleClass.editorDesc);
//		FormData leftFillerData = new FormData();
//		leftFillerData.top = new FormAttachment(messageImageLabel, 0, SWT.TOP);
//		leftFillerData.left = new FormAttachment(0, 0);
//		leftFillerData.bottom = new FormAttachment(messageImageLabel, 0, SWT.BOTTOM);
//		leftFillerData.right = new FormAttachment(messageImageLabel, 0);
//		leftFillerLabel.setLayoutData(leftFillerData);
//		
//		Label bottomFillerLabel =new Label(header, SWT.CENTER);
//		FormData bottomFillerData = new FormData();
//		bottomFillerData.left = new FormAttachment(0, horizontalSpacing);
//		bottomFillerData.top = new FormAttachment(messageImageLabel, 0);
//		bottomFillerData.bottom = new FormAttachment(messageLabel, 0, SWT.BOTTOM);
//		bottomFillerLabel.setLayoutData(bottomFillerData);
	}
	
	@Focus
	public void onFocus() {
		log.debug("Focus gained");
		this.scrollable.setFocus();
	}
	
	/**
	 * Implementations need to create their UI in this method.
	 * 
	 * @param parent the equivalent of {@link Form#getBody()}
	 */
	protected abstract void createControl(Composite parent);
	
	/**
	 * 
	 * @return
	 */
	protected abstract String getEditorHeader();
	
	/**
	 * {@link MPart#setLabel(String)}
	 */
	protected void setPartLabel(final String label) {
		this.part.setLabel(label);
	}
	
	/**
	 * {@link MPart#getProperties()}
	 */
	protected String getPartProperty(final String key) {
		return part.getProperties().get(key);
	}
	
	/**
	 * 
	 * @param parent
	 * @param text
	 * @return
	 */
	protected Group createGroup(Composite parent, String text) {
		final Group group = new Group(parent, SWT.SHADOW_OUT);
		group.setText(text);
		group.setLayout(new GridLayout(2, false));
		WidgetHelper.grabBoth(group);
		return group;
	}
	
	/**
	 * 
	 * @param parent
	 * @param text
	 * @return
	 */
	protected Label createLabel(Composite parent, String text) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
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
		final Text textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
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
		final Text textField = createText(parent, text);	
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
		createLabel(parent, label);
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
		text.addModifyListener(e -> setDirty(true));
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
	protected Binding createBindings(Text text, Object modelObject, String propertyName, UpdateValueStrategy toModel, UpdateValueStrategy fromModel) {
		return bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(text), 
				PojoProperties.value(propertyName).observe(modelObject), 
				toModel, 
				fromModel);
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
		if (value != isDirty()) {
			log.debug("Dirty status changing to {}", value);
			dirtyable.setDirty(value);			
		}
	}

	@PreDestroy
	public void disposeComposite() {
		this.scrollable.dispose();
	}
}
