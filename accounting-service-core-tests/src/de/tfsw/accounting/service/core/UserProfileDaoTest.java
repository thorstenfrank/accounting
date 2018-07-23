/**
 * 
 */
package de.tfsw.accounting.service.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.BankAccount;
import de.tfsw.accounting.model.UserProfile;
import de.tfsw.accounting.service.spi.UserProfileDao;

/**
 * @author tfrank1
 *
 */
public class UserProfileDaoTest extends AbstractDaoTest {
	
	private UserProfileDao dao;
	
	@Before
	public void setUp() {
		if (dao == null) {
			dao = ServiceLocator.getService(UserProfileDao.class);
		}
		
		assertNotNull("No UserProfileDao instance found, cannot run test", dao);
	}
	
	@Test
	public void testCreateAndUpdate() {
		assertNull("Should not have found user profile in DB", dao.get(TEST_USER_NAME));
		
		UserProfile profile = new UserProfile();
		profile.setName(TEST_USER_NAME);
		profile.setDescription("Some test description");
		
		BankAccount ba = new BankAccount();
		ba.setAccountNumber("123345");
		ba.setBankCode("AB346/jkdfi-seventyfour");
		ba.setBankName("Moneygone Bank Inc.");
		profile.setBankAccount(ba);
		
		profile.setPrimaryAddress(new Address());
		
		dao.save(profile);
		
		UserProfile fromDB = dao.get(TEST_USER_NAME);
		assertNotNull("Saved instance should not be null!", fromDB);
		assertEquals(TEST_USER_NAME, fromDB.getName());
		assertEquals(profile.getDescription(), fromDB.getDescription());
		assertEquals(profile.getBankAccount().getAccountNumber(), fromDB.getBankAccount().getAccountNumber());
		
		// update
		profile.setDescription("Some updated description");
		dao.save(profile);
		
		assertEquals(profile.getDescription(), dao.get(TEST_USER_NAME).getDescription());
	}
	
	@Test(expected = AccountingException.class)
	public void testNullInsert() {
		dao.save(new UserProfile());
	}
}
