package de.tfsw.accounting.ui;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.equinox.app.IApplicationContext;

import de.tfsw.accounting.AccountingInitService;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.ui.setup.ApplicationInit;

/**
 * Accounting UI application life cycle listener. Primarily responsible for ensuring the application can
 * start up properly.
 * 
 * @author Thorsten Frank
 * @since 2.0
 **/
@SuppressWarnings("restriction")
public class AccountingE4UILifeCycle {

	private static final Logger LOG = LogManager.getLogger(AccountingE4UILifeCycle.class);
	
	@PostContextCreate
	void postContextCreate(IEclipseContext eclipseContext) {
		LOG.debug("PostContextCreate");

		// make sure the splash screen comes down
	    eclipseContext.get(IApplicationContext.class).applicationRunning();

	    final Consumer<AccountingInitService> consumer = ApplicationInit.runApplicationSetup(eclipseContext);
	    if (consumer == null) {
	    	LOG.warn("Application setup failed, aborting startup");
	    	System.exit(-1);
	    } else {
	    	LOG.debug("Scheduling init for later...");
		    eclipseContext.get(IEventBroker.class).subscribe(AccountingService.EVENT_TOPIC_SERVICE_INIT, event -> {
		    	LOG.debug("Received event from service, now running init");
				final AccountingInitService service = (AccountingInitService) event.
						getProperty(AccountingService.EVENT_PROPERTY_INIT_SERVICE);
				if (service == null) {
					LOG.warn("No AccountingService instance available from event properties!");
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
