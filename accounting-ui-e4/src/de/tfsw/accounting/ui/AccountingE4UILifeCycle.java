package de.tfsw.accounting.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.EventHandler;

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
	void postContextCreate(IApplicationContext context, IEventBroker eventBroker, Display display) {
		LOG.debug("PostContextCreate");
		
		EventHandler handler = event -> {LOG.debug("Event: {}", event);};
		
		eventBroker.subscribe(UIEvents.UILifeCycle.ACTIVATE, handler);
		eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, handler);
		
		final Shell shell = new Shell(SWT.SHELL_TRIM);
	    MessageBox msgBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK | SWT.CANCEL);
	    msgBox.setText("Some text");
	    msgBox.setMessage("Some message");
	    
	    context.applicationRunning();
	    
	    setLocation(display, shell);
	    
	    if (SWT.OK == msgBox.open()) {
	    	LOG.info("Now starting the application...");
	    	
	    } else {
	    	LOG.warn("This isn't working for me...");
	    	System.exit(-1);
	    }

	}

    private void setLocation(Display display, Shell shell) {
        Monitor monitor = display.getPrimaryMonitor();
        Rectangle monitorRect = monitor.getBounds();
        Rectangle shellRect = shell.getBounds();
        int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
        int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
        shell.setLocation(x, y);
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
