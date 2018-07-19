/**
 * 
 */
package de.tfsw.accounting.service.derby;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import de.tfsw.accounting.model.UserProfile;
import de.tfsw.accounting.service.spi.UserProfileDao;

/**
 * @author tfrank1
 *
 */
@Component(service = UserProfileDao.class, enabled = true)
public class UserProfileDaoImpl implements UserProfileDao {

	private static final Logger LOG = LogManager.getLogger(UserProfileDaoImpl.class);
	
	private EntityManager entityManager;
	
	@Activate
	protected void activate() {
		this.entityManager = PersistenceHelper.init();
	}
	
	@Override
	public UserProfile get(String name) {
		LOG.debug("Retrieving user profile for name {}", name);
		return entityManager.find(UserProfile.class, name);
	}

	@Override
	public void save(UserProfile profile) {
		LOG.debug("Saving user profile: {}", profile.getName());
		PersistenceHelper.save(profile);
	}
}
