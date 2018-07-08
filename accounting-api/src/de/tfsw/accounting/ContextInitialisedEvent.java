/**
 * 
 */
package de.tfsw.accounting;

/**
 * @author thorsten
 *
 */
public interface ContextInitialisedEvent {

	static final String EVENT_ID = "de/tfsw/accounting/service/init/context";
	
	AccountingContext getContext();
}
