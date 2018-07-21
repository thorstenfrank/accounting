/**
 * 
 */
package de.tfsw.accounting.service.core;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.tfsw.accounting.UserProfileService;
import de.tfsw.accounting.model.UserProfile;
import de.tfsw.accounting.service.spi.UserProfileDao;

/**
 * @author thorsten
 *
 */
@Component(service = UserProfileService.class, enabled = true)
public class UserProfileServiceImpl implements UserProfileService {

	@Reference
	private UserProfileDao dao;

	@Override
	public UserProfile getCurrentUserProfile() {
		dao.get(null);
		return null;
	}
	
	@Override
	public void saveUserProfile(UserProfile profile) {
		dao.save(profile);
	}
}
