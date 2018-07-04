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

public abstract class AbstractEditorOpeningView {

	protected final Logger log = LogManager.getLogger(getClass().getName());
	
	@Inject
	private EModelService modelService;
	
	@Inject
	private EPartService partService;
	
	@Inject
	private MApplication app;
	
	protected void openExistingOrCreateNew(String name) {
		MPart part = partService.getParts().stream()
				.filter(p -> isThisThePart(p, name))
				.findFirst()
				.orElseGet(() -> createPart(name));
		
		partService.showPart(part, PartState.ACTIVATE);
	}
	
	private boolean isThisThePart(MPart part, String name) {
		boolean result = false;
		if ("de.tfsw.accounting.ui.part.clienteditor".equals(part.getElementId())) {
			log.debug("Checking part with correct ID");
			String value = part.getProperties().get("wuppdi");
			log.debug("Property wuppdi: {}", value);
			result = name.equals(value);
		} else {
			log.debug("Different id: {}", part.getElementId());
		}
		
		return result;
	}
	
	private MPart createPart(String name) {
		log.debug("Part not found, creating new: {}", name);
		MPart part = partService.createPart("de.tfsw.accounting.ui.part.clienteditor");
		part.setVisible(true);
		part.getProperties().put("wuppdi", name);
		MPartStack partStack = (MPartStack) modelService.find("de.tfsw.accounting.ui.partstack.editors", app);
		partStack.getChildren().add(part);
		return part;
	}
}
