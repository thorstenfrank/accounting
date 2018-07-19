/**
 * 
 */
package de.tfsw.accounting.service.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.model.BankAccount;
import de.tfsw.accounting.model.UserProfile;
import de.tfsw.accounting.service.spi.UserProfileDao;

/**
 * @author tfrank1
 *
 */
public class UserProfileDaoTest {

	private UserProfileDao dao;
	
	@Before
	public void setUp() {
		if (dao == null) {
			dao = ServiceLocator.getService(UserProfileDao.class);
		}
	}
	
	@Test
	public void testCreateAndUpdate() {
		final String name = "JUnit UserProfile Name";
		assertNull("Should not have found user profile in DB", dao.get(name));
		
		UserProfile profile = new UserProfile();
		profile.setName(name);
		profile.setDescription("Some test description");
		
		BankAccount ba = new BankAccount();
		ba.setAccountNumber("123345");
		ba.setBankCode("AB346/jkdfi-seventyfour");
		ba.setBankName("Moneygone Bank Inc.");
		profile.setBankAccount(ba);
		
		dao.save(profile);
		
		UserProfile fromDB = dao.get(name);
		assertNotNull("Saved instance should not be null!", fromDB);
		assertEquals(name, fromDB.getName());
		assertEquals(profile.getDescription(), fromDB.getDescription());
		assertEquals(profile.getBankAccount().getAccountNumber(), fromDB.getBankAccount().getAccountNumber());
		
		// update
		profile.setDescription("Some updated description");
		dao.save(profile);
		
		assertEquals(profile.getDescription(), dao.get(name).getDescription());
	}
}
