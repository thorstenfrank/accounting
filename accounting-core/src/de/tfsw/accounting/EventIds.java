/**
 * 
 */
package de.tfsw.accounting;

import de.tfsw.accounting.model.AbstractBaseEntity;

/**
 * Event IDs / topic names for events published by the services.
 */
public final class EventIds {

	public static final String MODEL_CHANGE_TOPIC_PREFIX = "de/tfsw/accounting/service/change/";
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static String modelChangeTopicFor(final Class<? extends AbstractBaseEntity> type) {
		return MODEL_CHANGE_TOPIC_PREFIX + type.getSimpleName();
	}
}
