/**
 * 
 */
package de.tfsw.accounting.service.derby;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.tfsw.accounting.UserProfileService;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.UserProfile;

/**
 * @author thorsten
 *
 */
@Component(service = UserProfileService.class, enabled = true)
public class UserProfileServiceImpl implements UserProfileService {

	private static final Logger LOG = LogManager.getLogger(UserProfileServiceImpl.class);
	
	@Reference
	private PersistenceAccess persistence;
	
	@Override
	public UserProfile getUserProfile(String name) {
		LOG.debug("Retrieving user profile for name {}", name);
		return persistence.findById(UserProfile.class, name);
	}

	@Override
	public void saveUserProfile(UserProfile profile) {
		LOG.debug("Saving user profile: {}", profile.getName());
		persistence.save(profile);
	}

	@Override
	public Set<TaxRate> getTaxRates() {
		return persistence.findAll(TaxRate.class);
	}

	@Override
	public void saveTaxRate(TaxRate rate) {
		LOG.trace("Saving tax rate: {}", rate);
		persistence.save(rate);
	}
}
