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

	UserProfile getUserProfile(String name);
	
	void saveUserProfile(UserProfile profile);
}
