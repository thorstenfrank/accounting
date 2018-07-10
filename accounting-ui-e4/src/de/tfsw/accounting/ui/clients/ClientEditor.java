/**
 * 
 */
package de.tfsw.accounting.ui.clients;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.tfsw.accounting.ClientService;
import de.tfsw.accounting.ui.AbstractEditorOpeningView;
import de.tfsw.accounting.ui.AbstractFormBasedEditor;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author tfrank1
 *
 */
public class ClientEditor extends AbstractFormBasedEditor {

	public static final String PART_ID = "de.tfsw.accounting.ui.part.clienteditor";
	
	private static final Logger LOG = LogManager.getLogger(ClientEditor.class);
	
	@Inject
	private ClientService clientService;

	@Override
	protected void createControl(Composite parent) {
		final String name = getPartProperty(AbstractEditorOpeningView.KEY_ELEMENT_ID);
		LOG.debug("Creating editor for client {}", name);
		parent.setLayout(new GridLayout());
		WidgetHelper.createLabel(parent, "Client editor sample label: " + name);
		setPartLabel(name);
//		final Client client = clientService.
		parent.setLayout(new GridLayout(2, false));
	}
	
	@Override
	protected String getEditorHeader() {
		return "Client Editor";
	}
	
//	private void createBasicInfoSection(Client client) {
//		final FormToolkit toolkit = getToolkit();
//		Section basicSection = toolkit.createSection(getForm().getBody(), Section.TITLE_BAR);
//		basicSection.setText("Messages.labelBasicInformation");
//		basicSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//		basicSection.setLayout(new GridLayout(2, false));
//		
//		Composite sectionClient = toolkit.createComposite(basicSection);
//		sectionClient.setLayout(new GridLayout(2, false));
//		
//		createTextWithLabel(sectionClient, "Messages.labelClientName", client.getName(), client, Client.FIELD_NAME);
//		createTextWithLabel(sectionClient, "Messages.labelClientNumber", client.getClientNumber(), client, Client.FIELD_CLIENT_NUMBER);
//		
//		basicSection.setClient(sectionClient);
//	}
}
