/**
 * 
 */
package de.tfsw.accounting.service.derby;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.tfsw.accounting.model.UserProfile;
import de.tfsw.accounting.service.spi.UserProfileDao;

/**
 * @author tfrank1
 *
 */
@Component(service = UserProfileDao.class, enabled = true)
public class UserProfileDaoImpl implements UserProfileDao {

	private static final Logger LOG = LogManager.getLogger(UserProfileDaoImpl.class);
	
	@Reference
	private PersistenceAccess persistence;
	
	@Activate
	protected void activate() {
		LOG.trace("DAO activated");
	}
	
	@Override
	public UserProfile get(String name) {
		LOG.debug("Retrieving user profile for name {}", name);
		return persistence.findById(UserProfile.class, name);
	}

	@Override
	public void save(UserProfile profile) {
		LOG.debug("Saving user profile: {}", profile.getName());
		persistence.save(profile);
	}
}
