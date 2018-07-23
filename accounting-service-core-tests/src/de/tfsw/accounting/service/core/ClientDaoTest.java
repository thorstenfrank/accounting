/**
 * 
 */
package de.tfsw.accounting.service.core;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.service.spi.ClientDao;

/**
 * @author thorsten
 *
 */
public class ClientDaoTest extends AbstractDaoTest {

	private ClientDao dao;
	
	@Before
	public void setUp() {
		if (dao == null) {
			dao = ServiceLocator.getService(ClientDao.class);
		}
		
		assertNotNull("No ClientDao instance found, cannot run test", dao);
	}
	
	@Test
	public void test() {
		dao.getClients();
	}
}
