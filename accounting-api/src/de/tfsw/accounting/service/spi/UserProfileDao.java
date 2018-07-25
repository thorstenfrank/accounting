package de.tfsw.accounting.service.spi;

import java.util.Set;

import de.tfsw.accounting.model.TaxRate;
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
