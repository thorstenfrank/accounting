/**
 * 
 */
package de.tfsw.accounting.service.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.UserProfileService;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.BankAccount;
import de.tfsw.accounting.model.UserProfile;

/**
 * @author tfrank1
 *
 */
public class UserProfileServiceTest extends AbstractPersistenceTest {
	
	private UserProfileService service;
	
	@Before
	public void setUp() {
		if (service == null) {
			service = ServiceLocator.getService(UserProfileService.class);
		}
		
		assertNotNull("No UserProfileservice instance found, cannot run test", service);
	}
	
	@Test
	public void testCreateAndUpdate() {
		assertNull("Should not have found user profile in DB", service.getUserProfile(TEST_USER_NAME));
		
		UserProfile profile = new UserProfile();
		profile.setName(TEST_USER_NAME);
		profile.setDescription("Some test description");
		
		BankAccount ba = new BankAccount();
		ba.setAccountNumber("123345");
		ba.setBankCode("AB346/jkdfi-seventyfour");
		ba.setBankName("Moneygone Bank Inc.");
		profile.setBankAccount(ba);
		
		profile.setPrimaryAddress(new Address());
		
		service.saveUserProfile(profile);
		
		UserProfile fromDB = service.getUserProfile(TEST_USER_NAME);
		assertNotNull("Saved instance should not be null!", fromDB);
		assertEquals(TEST_USER_NAME, fromDB.getName());
		assertEquals(profile.getDescription(), fromDB.getDescription());
		assertEquals(profile.getBankAccount().getAccountNumber(), fromDB.getBankAccount().getAccountNumber());
		
		// update
		profile.setDescription("Some updated description");
		service.saveUserProfile(profile);
		
		assertEquals(profile.getDescription(), service.getUserProfile(TEST_USER_NAME).getDescription());
	}
	
	@Test(expected = AccountingException.class)
	public void testNullInsert() {
		service.saveUserProfile(new UserProfile());
	}
	
	@Test(expected = AccountingException.class)
	public void testNameUpdate() {
		UserProfile up = new UserProfile();
		up.setName("testNameUpdate");
		service.saveUserProfile(up);
		
		up.setName("testNameUpdate_update");
		service.saveUserProfile(up);
	}
}
