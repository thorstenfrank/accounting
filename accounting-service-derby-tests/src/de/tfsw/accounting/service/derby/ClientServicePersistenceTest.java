/**
 * 
 */
package de.tfsw.accounting.service.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.ClientService;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.Client;

/**
 * @author thorsten
 *
 */
public class ClientServicePersistenceTest extends AbstractPersistenceTest {

	private static Client testClient;
	
	private ClientService service;
		
	@Before
	public void setUp() {
		if (service == null) {
			service = ServiceLocator.getService(ClientService.class);
		}
		
		assertNotNull("No ClientService instance found, cannot run test", service);
		
		if (testClient == null) {
			testClient = new Client("JUnit_Test_Client");
			testClient.setClientNumber("JUNIT_Client_Number_1");
			service.saveClient(testClient);
		}
	}
	
	// from the old mocked test - maybe use the real event admin in this test or move the mocked stuff
	// to its own test class
//	ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
//	verify(eventMock).postEvent(eventCaptor.capture());
//	assertEquals(EventIds.modelChangeTopicFor(Client.class), eventCaptor.getValue().getTopic());
	
	@Test
	public void testCrud() {
		final int initialSize = service.getClients().size();
		Client client = new Client("crudTestClient");
		client.setClientNumber("" + System.currentTimeMillis());
		client.setPrimaryAddress(new Address());
		client.getPrimaryAddress().setCity("Notthatimportant");
		service.saveClient(client);
		assertEquals(initialSize + 1, service.getClients().size());
		
		client.getPrimaryAddress().setEmail("moved.on@newhome.com");
		service.saveClient(client);
		assertEquals(initialSize + 1, service.getClients().size());
	}
	
	@Test(expected = AccountingException.class)
	public void testNameUpdateFails() {
		Client client = service.getClient(testClient.getName());
		client.setName("nameUpdateFailsTest_updated");
		service.saveClient(client);
	}
	
	@Test(expected = AccountingException.class)
	public void testNameUnique() {
		service.saveClient(new Client(testClient.getName()));
	}
	
	@Test(expected = AccountingException.class)
	public void testNumberUnique() {
		Client client = new Client("numberUniqueTestClient");
		client.setClientNumber(testClient.getClientNumber());
		service.saveClient(client);
	}
}
