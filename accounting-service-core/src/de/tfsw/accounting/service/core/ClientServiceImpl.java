/**
 * 
 */
package de.tfsw.accounting.service.core;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.ClientService;
import de.tfsw.accounting.EventIds;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.service.spi.ClientDao;

/**
 * @author thorsten
 *
 */
@Component(service = ClientService.class, enabled = true)
public class ClientServiceImpl implements ClientService {

	@Reference
	private ClientDao dao;
	
	@Reference
	private EventAdmin eventAdmin;
	
	@Override
	public Client newClient() {
		// TODO add automatic client number generation here
		Client client = new Client();
		client.setClientNumber("007");
		return client;
	}

	@Override
	public Set<Client> getClients() {
		return dao.getClients();
	}

	@Override
	public Client getClient(String name) {
		return dao.getClient(name);
	}

	@Override
	public void saveClient(Client client) {
		dao.save(client);
		eventAdmin.postEvent(new Event(EventIds.modelChangeTopicFor(Client.class), (Map<String, ?>) null));
	}

	@Override
	public void deleteClient(Client client) {
		throw new AccountingException("Not implemented yet!");
	}

}
