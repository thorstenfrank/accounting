/**
 * 
 */
package de.tfsw.accounting.ui.util;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import de.tfsw.accounting.model.AbstractBaseEntity;

/**
 * Either provide the necessary fields manually or use 
 * {@link ContextInjectionFactory#make(Class, org.eclipse.e4.core.contexts.IEclipseContext)}.
 *
 */
public final class ApplicationModelHelper {

	public static final String EDITOR_PARTSTACK_ID = "de.tfsw.accounting.ui.partstack.editors";
	
	public static final String KEY_ELEMENT_ID = "editedElementId";
	
	@Inject
	private EModelService modelService;
	
	@Inject
	private EPartService partService;
	
	@Inject
	private MApplication app;

	/**
	 * 
	 * @param targetEditorId
	 */
	public void openNewEditor(final String targetEditorId) {
		partService.showPart(createEditor(targetEditorId), PartState.ACTIVATE);
	}
	
	/**
	 * 
	 * @param targetEditorId
	 * @param elementId
	 * @param editedElement
	 */
	public <E extends AbstractBaseEntity> void openExistingOrCreateNewEditor(
			final String targetEditorId, final String elementId, final E editedElement) {
		final MPart part = partService.getParts().stream()
				.filter(p -> isExistingPart(p, targetEditorId, elementId))
				.findFirst()
				.orElseGet(() -> createEditor(targetEditorId, elementId, editedElement));
		
		partService.showPart(part, PartState.ACTIVATE);
	}
	
	private boolean isExistingPart(final MPart part, String targetEditorId, final String elementId) {
		boolean result = false;
		
		if (targetEditorId.equals(part.getElementId())) {
			result = elementId.equals(part.getProperties().get(KEY_ELEMENT_ID));
		}
		
		return result;
	}
	
	private <E extends AbstractBaseEntity> MPart createEditor(
			final String targetEditorId, final String elementId, final E editedElement) {
		final MPart part = createEditor(targetEditorId);
		part.getProperties().put(KEY_ELEMENT_ID, elementId);
		part.setObject(editedElement);
		return part;
	}
	
	/**
	 * Creates a part and adds it to the editor partstack.
	 * 
	 */
	private MPart createEditor(final String targetEditorId) {
		final MPart part = partService.createPart(targetEditorId);
		part.setVisible(true);
		MPartStack partStack = (MPartStack) modelService.find(EDITOR_PARTSTACK_ID, app);
		partStack.getChildren().add(part);
		return part;
	}
}
