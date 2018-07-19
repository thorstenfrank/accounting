/**
 * 
 */
package de.tfsw.accounting;

import de.tfsw.accounting.model.UserProfile;

/**
 * @author thorsten
 *
 */
public interface UserProfileService {

	UserProfile getCurrentUserProfile();
	
	void saveUserProfile(UserProfile profile);
}
