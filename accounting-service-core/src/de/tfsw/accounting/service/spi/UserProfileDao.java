package de.tfsw.accounting.service.spi;

import de.tfsw.accounting.model.UserProfile;

public interface UserProfileDao {

	/**
	 * 
	 * @param name
	 * @return
	 */
	UserProfile get(String name);
	
	/**
	 * 
	 * @param profile
	 */
	void save(UserProfile profile);
}
