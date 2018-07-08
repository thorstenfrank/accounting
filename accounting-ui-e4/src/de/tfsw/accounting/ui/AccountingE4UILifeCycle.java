package de.tfsw.accounting.ui;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.equinox.app.IApplicationContext;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingInitService;
import de.tfsw.accounting.ContextInitialisedEvent;
import de.tfsw.accounting.ui.setup.ApplicationInit;
import de.tfsw.accounting.ui.util.AccountingPreferences;

/**
 * Accounting UI application life cycle listener. Primarily responsible for
 * ensuring the application can start up properly.
 * 
 * @author Thorsten Frank
 * @since 2.0
 **/
@SuppressWarnings("restriction")
public class AccountingE4UILifeCycle {

	private static final Logger LOG = LogManager.getLogger(AccountingE4UILifeCycle.class);

	@PostContextCreate
	void postContextCreate(IEclipseContext eclipseContext, @Optional AccountingInitService initService) {
		LOG.debug("PostContextCreate");

		eclipseContext.get(IEventBroker.class).subscribe(ContextInitialisedEvent.EVENT_ID, event -> {
			LOG.debug("Received context init event");
			if (event instanceof ContextInitialisedEvent) {
				LOG.debug("Context init finished, now saving to preferences");
				AccountingContext ac = ((ContextInitialisedEvent) event).getContext();
				AccountingPreferences.storeContext(ac);
				eclipseContext.set(AccountingContext.class, ac);
			} else {
				LOG.warn("Meh!");
			}
		});
		
		// make sure the splash screen comes down		
		eclipseContext.get(IApplicationContext.class).applicationRunning();
		
		final Consumer<AccountingInitService> consumer = ApplicationInit.runApplicationSetup(eclipseContext);
		if (consumer == null) {
			LOG.warn("Application setup failed, aborting startup");
			System.exit(-1);
		} else if (initService != null) {
			LOG.debug("Init service available directly, calling now...");
			consumer.accept(initService);
		} else {
			LOG.debug("Scheduling init for later...");
			eclipseContext.get(IEventBroker.class).subscribe(AccountingInitService.EVENT_TOPIC_SERVICE_INIT, event -> {
				LOG.debug("Received event from service, now running init");
				final AccountingInitService service = (AccountingInitService) event
						.getProperty(AccountingInitService.EVENT_PROPERTY_INIT_SERVICE);
				if (service == null) {
					LOG.warn("No AccountingService instance available from event properties!");
					// TODO no service, no work. Throw something and exit or show a dialog and exit!
				} else {
					LOG.info("AccountingService online, now initializing context...");

					// TODO in case of an error while running app init, show a dialog and exit...
					consumer.accept(service);
				}
			});
		}
	}
	
	@PreSave
	void preSave(IEclipseContext workbenchContext) {
		LOG.debug("PreSave");
	}

	@ProcessAdditions
	void processAdditions(IEclipseContext workbenchContext) {
		LOG.debug("ProcessAdditions");
	}

	@ProcessRemovals
	void processRemovals(IEclipseContext workbenchContext) {
		LOG.debug("ProcessRemovals");
	}
}
