package de.tfsw.accounting.ui;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import de.tfsw.accounting.model.AbstractBaseEntity;

public abstract class AbstractEditorOpeningView {

	public static final String EDITOR_PARTSTACK_ID = "de.tfsw.accounting.ui.partstack.editors";
	public static final String KEY_ELEMENT_ID = "editedElementId";
	public static final String KEY_ELEMENT = "editedElement";
	
	protected final Logger log = LogManager.getLogger(getClass().getName());
	
	@Inject
	private EModelService modelService;
	
	@Inject
	private EPartService partService;
	
	@Inject
	private MApplication app;
	
	/**
	 * 
	 * @param name
	 */
	protected <E extends AbstractBaseEntity> void openExistingOrCreateNew(String elementName, String targetEditorId, E edited) {
		MPart part = partService.getParts().stream()
				.filter(p -> isThisThePart(p, elementName, targetEditorId))
				.findFirst()
				.orElseGet(() -> createPart(elementName, targetEditorId, edited));
		
		partService.showPart(part, PartState.ACTIVATE);
	}
	
	private boolean isThisThePart(MPart part, String name, String targetEditorId) {
		boolean result = false;
		if (targetEditorId.equals(part.getElementId())) {
			String editedElementId = part.getProperties().get(KEY_ELEMENT_ID);
			result = name.equals(editedElementId);
		}
		
		if (result) {
			log.trace("Re-using existing open part found for editor id [{}] and name [{}]", targetEditorId, name);
		}
		
		return result;
	}
	
	private <E extends AbstractBaseEntity> MPart createPart(String name, String targetEditorId, E edited) {
		log.trace("Creating new editor with id [{}]for element [{}]", targetEditorId, name);
		MPart part = partService.createPart(targetEditorId);
		part.setVisible(true);
		part.getProperties().put(KEY_ELEMENT_ID, name);
		part.setObject(edited);
		MPartStack partStack = (MPartStack) modelService.find(EDITOR_PARTSTACK_ID, app);
		partStack.getChildren().add(part);
		return part;
	}
}
