/**
 * 
 */
package de.tfsw.accounting.service.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.service.spi.ClientDao;

/**
 * @author thorsten
 *
 */
public class ClientDaoTest extends AbstractDaoTest {

	private static Client testClient;
	
	private ClientDao dao;
		
	@Before
	public void setUp() {
		if (dao == null) {
			dao = ServiceLocator.getService(ClientDao.class);
		}
		
		assertNotNull("No ClientDao instance found, cannot run test", dao);
		
		if (testClient == null) {
			testClient = new Client("JUnit_Test_Client");
			testClient.setClientNumber("JUNIT_Client_Number_1");
			dao.save(testClient);
		}
	}
	
	@Test
	public void testCrud() {
		final int initialSize = dao.getClients().size();
		Client client = new Client("crudTestClient");
		client.setClientNumber("" + System.currentTimeMillis());
		client.setPrimaryAddress(new Address());
		client.getPrimaryAddress().setCity("Notthatimportant");
		dao.save(client);
		assertEquals(initialSize + 1, dao.getClients().size());
		
		client.getPrimaryAddress().setEmail("moved.on@newhome.com");
		dao.save(client);
		assertEquals(initialSize + 1, dao.getClients().size());
	}
	
	@Test(expected = AccountingException.class)
	public void testNameUpdateFails() {
		Client client = dao.getClient(testClient.getName());
		client.setName("nameUpdateFailsTest_updated");
		dao.save(client);
	}
	
	@Test(expected = AccountingException.class)
	public void testNameUnique() {
		dao.save(new Client(testClient.getName()));
	}
	
	@Test(expected = AccountingException.class)
	public void testNumberUnique() {
		Client client = new Client("numberUniqueTestClient");
		client.setClientNumber(testClient.getClientNumber());
		dao.save(client);
	}
}
