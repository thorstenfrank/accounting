package de.tfsw.accounting.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.equinox.app.IApplicationContext;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.ui.setup.ApplicationInit;

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
	void postContextCreate(IEclipseContext eclipseContext) {
		LOG.debug("PostContextCreate");
		
		// make sure the splash screen comes down		
		eclipseContext.get(IApplicationContext.class).applicationRunning();
		
		if (!ApplicationInit.runApplicationSetup(eclipseContext)) {
			LOG.error("Forced exit, application setup/init not possible");
			System.exit(-1);
		}
	}
	
	@PreSave
	void preSave(IEclipseContext workbenchContext) {
		LOG.debug("PreSave");
		AccountingContext ctx = workbenchContext.get(AccountingContext.class);
		LOG.debug("Context from context: {}", ctx);
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
