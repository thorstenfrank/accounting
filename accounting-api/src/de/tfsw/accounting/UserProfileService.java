/**
 * 
 */
package de.tfsw.accounting;

import java.util.Set;

import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.UserProfile;

/**
 * @author thorsten
 *
 */
public interface UserProfileService {

	/**
	 * 
	 * @param name
	 * @return
	 */
	UserProfile getUserProfile(String name);
	
	/**
	 * 
	 * @param profile
	 */
	void saveUserProfile(UserProfile profile);
	
	/**
	 * 
	 * @return
	 */
	Set<TaxRate> getTaxRates();
	
	/**
	 * 
	 * @param rate
	 */
	void saveTaxRate(TaxRate rate);
}
