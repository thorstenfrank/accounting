package de.tfsw.accounting.service.derby;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.service.spi.ClientDao;

@Component(service = ClientDao.class, enabled = true)
public class ClientDaoImpl implements ClientDao {

	private static final Logger LOG = LogManager.getLogger(ClientDaoImpl.class);
	
	@Reference
	private PersistenceAccess persistence;
	
	@Activate
	protected void activate() {
		LOG.trace("DAO activated");
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
	public void save(Client client) {
		LOG.trace("Saving client {} / {}", client.getName(), client.getClientNumber());
		persistence.save(client);
	}
}
