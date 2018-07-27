package de.tfsw.accounting.service.derby;

import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.ClientService;
import de.tfsw.accounting.EventIds;
import de.tfsw.accounting.model.Client;

/**
 * @author thorsten
 *
 */
@Component(service = ClientService.class, enabled = true)
public class ClientServiceImpl implements ClientService {
	
	private static final Logger LOG = LogManager.getLogger(ClientServiceImpl.class);
	
	@Reference
	private PersistenceAccess persistence;
	
	@Reference
	private EventAdmin eventAdmin;
	
	@Override
	public Client newClient() {
		// TODO add automatic client number generation here
		final String clientNumber = "007";
		Client client = new Client();
		LOG.trace("Creating new client with number {}", clientNumber);
		client.setClientNumber(clientNumber);
		return client;
	}

	@Override
	public Set<Client> getClients() {
		LOG.trace("Finding all clients");
		return persistence.findAll(Client.class);
	}

	@Override
	public Client getClient(String name) {
		return persistence.findById(Client.class, name);
	}

	@Override
	public void saveClient(Client client) {
		LOG.trace("Saving client {} / {}", client.getName(), client.getClientNumber());
		persistence.save(client);
		eventAdmin.postEvent(new Event(EventIds.modelChangeTopicFor(Client.class), (Map<String, ?>) null));
	}

	@Override
	public void deleteClient(Client client) {
		throw new AccountingException("Not implemented yet!");	
	}
}
