/**
 * 
 */
package de.tfsw.accounting;

import de.tfsw.accounting.model.User;

/**
 * @author thorsten
 *
 */
public interface UserService {
	
	/**
	 * Returns the {@link User} denoted by the name contained in the {@link AccountingContext} that was used to
	 * initialize this service.
	 * 
	 * @return the current session's user, or <code>null</code> if none exists in persistence
	 * @throws AccountingException if a technical error occurred while accessing persistence
	 */
	User getCurrentUser();
	
	/**
	 * Saves the current user to persistence. 
	 * 
	 * <p>Saving includes the user's configured tax rates, bank account and address.</p>
	 * 
	 * <p>When saving is successful, an event is issued (using the OSGi <code>EventAdmin</code>) using 
	 * {@link EventIds#MODEL_CHANGE_TOPIC_PREFIX}.</p>
	 * 
	 * @param user the {@link User} to save
	 * 
	 * @return the saved instance - clients should use this for any further work 
	 */
	User saveCurrentUser(User user);
}
