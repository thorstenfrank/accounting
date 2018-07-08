package de.tfsw.accounting.service;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.Event;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.ContextInitialisedEvent;

class ContextInitialisedEventImpl extends Event implements ContextInitialisedEvent {

	private static final String CONTEXT_KEY = "accountingContext";
	
	private ContextInitialisedEventImpl(Map<String, ?> properties) {
		super(EVENT_ID, properties);
	}

	@Override
	public AccountingContext getContext() {
		return (AccountingContext) getProperty(CONTEXT_KEY);
	}

	static ContextInitialisedEventImpl create(AccountingContext context) {
		Map<String, Object> properties = new HashMap<>();
		properties.put(CONTEXT_KEY, context);
		return new ContextInitialisedEventImpl(properties);
	}
}
