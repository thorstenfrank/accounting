package de.tfsw.accounting.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;

/**
 * This is a stub implementation containing e4 LifeCycle annotated methods.<br />
 * There is a corresponding entry in <em>plugin.xml</em> (under the
 * <em>org.eclipse.core.runtime.products' extension point</em>) that references
 * this class.
 **/
@SuppressWarnings("restriction")
public class AccountingE4UILifeCycle {

	private static final Logger LOG = LogManager.getLogger(AccountingE4UILifeCycle.class);
	
	@PostContextCreate
	void postContextCreate(IEclipseContext workbenchContext) {
		LOG.debug("PostContextCreate");
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
