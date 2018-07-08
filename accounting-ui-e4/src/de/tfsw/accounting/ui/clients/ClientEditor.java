/**
 * 
 */
package de.tfsw.accounting.ui.clients;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.tfsw.accounting.ui.AbstractEditorOpeningView;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author tfrank1
 *
 */
public class ClientEditor {

	public static final String PART_ID = "de.tfsw.accounting.ui.part.clienteditor";
	
	private static final Logger LOG = LogManager.getLogger(ClientEditor.class);
	
	@Inject
	private MPart part;
	
	private String name;
	
	@PostConstruct
	public void createComposite(Composite parent) {
		this.name = part == null ? "NULL!" : part.getProperties().get(AbstractEditorOpeningView.KEY_ELEMENT_ID);
		LOG.debug("Creating composite for {}", name);
		LOG.debug("Part details: {}", part.getElementId());
		parent.setLayout(new GridLayout());
		WidgetHelper.createLabel(parent, "Client editor sample label: " + name);
		part.setLabel(name);
	}
	
	@PreDestroy
	public void cleanup() {
		LOG.debug("Cleaning up for {}", name);
	}
	
}
