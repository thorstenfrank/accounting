/**
 * 
 */
package de.tfsw.accounting.service.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.model.UserProfile;
import de.tfsw.accounting.service.spi.UserProfileDao;

/**
 * @author tfrank1
 *
 */
public class UserDaoTest {

	private UserProfileDao dao;
	
	@Before
	public void setUp() {
		if (dao == null) {
			dao = ServiceLocator.getService(UserProfileDao.class);
		}
	}
	
	@Test
	public void test() {
		final String name = "JUnit UserProfile Name";
		assertNull("Should not have found user profile in DB", dao.get(name));
		
		UserProfile profile = new UserProfile();
		profile.setName(name);
		profile.setDescription("Some test description");
		
		dao.save(profile);
		
		UserProfile fromDB = dao.get(name);
		assertNotNull("Saved instance should not be null!", fromDB);
		assertEquals(name, fromDB.getName());
		assertEquals(profile.getDescription(), fromDB.getDescription());
	}
}
