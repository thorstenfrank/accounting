/**
 * 
 */
package de.tfsw.accounting.ui.clients;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;

import de.tfsw.accounting.ui.util.ApplicationModelHelper;

/**
 * @author tfrank1
 *
 */
public class NewClientHandler {

	private static final Logger LOG = LogManager.getLogger(NewClientHandler.class);
	
	@Execute
	public void execute(IEclipseContext context) {
		LOG.trace("New client handler");
		ContextInjectionFactory.make(ApplicationModelHelper.class, context).openNewEditor(ClientEditor.PART_ID);
	}
}
