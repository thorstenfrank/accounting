/**
 * 
 */
package de.tfsw.accounting.ui.handlers;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

/**
 * @author thorsten
 *
 */
public class NewWizardHandler {

	private static final Logger LOG = LogManager.getLogger(NewWizardHandler.class);
	
	@Execute
	public void execute(Shell shell, @Named("de.tfsw.accounting.ui.command.newWizard.id") String id) {
		LOG.debug("Opening newWizard: {}", id);
	}
}
