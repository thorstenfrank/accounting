/**
 * 
 */
package de.tfsw.accounting.ui.clients;

import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.tfsw.accounting.ClientService;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.ui.AbstractFormBasedEditor;
import de.tfsw.accounting.ui.util.ApplicationModelHelper;

/**
 * @author tfrank1
 *
 */
public class ClientEditor extends AbstractFormBasedEditor {

	public static final String PART_ID = "de.tfsw.accounting.ui.part.clienteditor";
	
	private static final Logger LOG = LogManager.getLogger(ClientEditor.class);

	@Inject
	@Translation
	private Messages messages;
	
	@Inject
	private ClientService clientService;

	private Client client;
	
	@Override
	protected boolean createControl(Composite parent) {
		final String name = Optional
				.ofNullable(getPartProperty(ApplicationModelHelper.KEY_ELEMENT_ID))
				.orElse("New Client");
		
		LOG.trace("Creating editor for client {}", name);
		
		setPartLabel(name);
		
		parent.setLayout(new GridLayout(2, false));
		
		boolean dirty = assignEditedClient();
		
		createBasicInfoSection(parent);
		
		if (client.getPrimaryAddress() == null) {
			client.setPrimaryAddress(new Address());
			// should we set the editor to dirty here too?
		}
		createAddressSection(parent, client.getPrimaryAddress());
		
		return dirty;
	}
	
	@Override
	protected boolean doSave() {
		clientService.saveClient(client);
		this.setPartLabel(client.getName()); // update the part label (tab label) just in case
		return true;
	}
	
	@PreDestroy
	public void dispose() {
		setDirty(false);
		client = null;
		LOG.trace("PreDestroy too");
	}
	
	@Override
	protected String getEditorHeader() {
		return messages.clientEditorHeader;
	}
	
	private boolean assignEditedClient() {
		final Optional<Client> opt = Optional.ofNullable(getPartObject(Client.class));
		if (opt.isPresent()) {
			this.client = opt.get();
			return false;
		} else {
			this.client = clientService.newClient();
			return true;
		}
	}
	
	private void createBasicInfoSection(Composite parent) {
		final Composite group = createGroup(parent, messages.labelBasicInformation);
		
		if (client.getName() == null) {
			createTextWithLabelNotNullable(group, messages.labelClientName, client, Client.FIELD_NAME);
		} else {
			createTextWithLabel(group, messages.labelClientName, client, Client.FIELD_NAME).setEditable(false);
		}
		
		createTextWithLabel(group, messages.labelClientNumber, client, Client.FIELD_CLIENT_NUMBER);
	}
}
