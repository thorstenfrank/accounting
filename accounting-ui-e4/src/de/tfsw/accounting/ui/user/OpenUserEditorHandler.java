/**
 * 
 */
package de.tfsw.accounting.ui.user;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * @author tfrank1
 *
 */
public class OpenUserEditorHandler {

	private static final Logger LOG = LogManager.getLogger(OpenUserEditorHandler.class);
	
	@Execute
	public void openEditor(EModelService modelService, EPartService partService, MApplication app) {
		MPart part = Optional.ofNullable(findPart(partService))
				.orElseGet(() -> createPart(modelService, partService, app));
		partService.showPart(part, PartState.ACTIVATE);
	}
	
	private MPart findPart(EPartService partService) {
		MPart part = partService.findPart(UserEditor.PART_ID);
		LOG.debug("Attempted to find existing user editor: {}", part != null);
		return part;
	}
	
	private MPart createPart(EModelService modelService, EPartService partService, MApplication app) {
		LOG.debug("Creating user editor");
		MPart part = partService.createPart(UserEditor.PART_ID);
		part.setVisible(true);
		return part;
	} 
}
