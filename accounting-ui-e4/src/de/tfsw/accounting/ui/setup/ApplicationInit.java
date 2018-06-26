/*
 *  Copyright 2018 Thorsten Frank (accounting@tfsw.de).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.tfsw.accounting.ui.setup;

import java.io.File;
import java.util.Locale;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.nls.IMessageFactoryService;
import org.eclipse.e4.core.services.translation.ResourceBundleProvider;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingInitService;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.ui.util.AccountingPreferences;

/**
 * Controls application startup/init by attempting to read necessary config values from preferences. If not
 * available, a "first app run" is assumed and the user is presented with options of either creating a new
 * database or importing a previously saved set of data.
 * 
 * <p>Unless explicitly cancelled by the user or running into an error, this class will schedule init of the
 * {@link AccountingService} as soon as it comes online.</p> 
 * 
 * @author thorsten
 */
public final class ApplicationInit {

	/** */
	private static final Logger LOG = LogManager.getLogger(ApplicationInit.class);
	
	/**
	 * 
	 */
	private ApplicationInit() {
		// nothing to do here...
	}
	
	/**
	 * 
	 * @param eclipseContext
	 * @return
	 */
	public static Consumer<AccountingInitService> runApplicationSetup(final IEclipseContext eclipseContext) {
		return new ApplicationInit().initContext(eclipseContext);
	}
	
	/**
	 * 
	 * @param eclipseContext
	 * @return
	 */
	private Consumer<AccountingInitService> initContext(final IEclipseContext eclipseContext) {
		Consumer<AccountingInitService> consumer = initFromPreferences();
		if (consumer == null) {
			consumer = initFromUserInput(eclipseContext.get(Display.class), getMessageInstance(eclipseContext));
		}
		return consumer;
	}
	
	private Messages getMessageInstance(final IEclipseContext eclipseContext) {
		final IMessageFactoryService mfs = eclipseContext.get(IMessageFactoryService.class);
		final ResourceBundleProvider rbp = eclipseContext.get(ResourceBundleProvider.class);
		
		Locale locale = eclipseContext.get(Locale.class);
		if (locale == null) {
			LOG.info("No locale found by class");
			locale = (Locale) eclipseContext.get(TranslationService.LOCALE);
			if (locale == null) {
				LOG.info("No locale found by name, using default");
				locale = Locale.getDefault();
			}
		}
		
		final Messages messages = mfs.getMessageInstance(locale, Messages.class, rbp);
		
		LOG.debug("Using locale: {} / {}", locale, messages.wuppdi);
		return messages;
	}
	
	/**
	 * 
	 * @return
	 */
	private Consumer<AccountingInitService> initFromPreferences() {
		Consumer<AccountingInitService> result = null;
		
		try {
			final AccountingContext accountingContext = AccountingPreferences.readContext();
			result = (service) -> {
				service.init(accountingContext);
			};
		} catch (AccountingException e) {
			LOG.warn("Couldn't instantiate context from preferences, assuming none exist", e);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param display
	 * @return
	 */
	private Consumer<AccountingInitService> initFromUserInput(final Display display, final Messages messages) {
		final Shell shell = createShell(display);
		final WelcomeDialog welcomeDialog = new WelcomeDialog(shell, messages);
		Consumer<AccountingInitService> result = null;
		if (welcomeDialog.open() == WelcomeDialog.OK) {
			try {
				final AbstractSetupWizard setupWizard = welcomeDialog.getSelectedWizardType().newInstance();
				setupWizard.setMessages(messages);
				final WizardDialog wizardDialog = new WizardDialog(shell, setupWizard);
				if (wizardDialog.open() == WizardDialog.OK) {
					result = setupWizard.buildFunctionForWhenServiceComesOnline();
				}
			} catch (InstantiationException | IllegalAccessException e) {
				// don't re-throw, just return null - this will cause the application to exit as if the user
				// had pressed "cancel"
				LOG.error("Error creating wizard instance", e);
			}
		} else {
			LOG.warn("WelcomeDialog cancelled by user");
		}
			
		return result;
	}
	
    private Shell createShell(Display display) {
    	final Shell shell = new Shell(SWT.SHELL_TRIM);
        Monitor monitor = display.getPrimaryMonitor();
        Rectangle monitorRect = monitor.getBounds();
        Rectangle shellRect = shell.getBounds();
        int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
        int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
        shell.setLocation(x, y);
        return shell;
    }
    
	static String buildDefaultDbFileLocation() {
		final StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.home")); //$NON-NLS-1$
		sb.append(File.separatorChar);
		sb.append("accounting.data"); //$NON-NLS-1$
		return sb.toString();
	}
}
