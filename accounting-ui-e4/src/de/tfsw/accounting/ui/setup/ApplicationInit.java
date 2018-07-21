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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.function.Function;

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
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingInitService;
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
	 */
	public static void runApplicationSetup(final IEclipseContext eclipseContext) {
		final AccountingContext accountingContext = new ApplicationInit()
				.initContext(eclipseContext)
				.apply(getInitService(eclipseContext));
		
		AccountingPreferences.storeContext(accountingContext);
	}
	
	/**
	 * 
	 * @param eclipseContext
	 * @return
	 */
	private Function<AccountingInitService, AccountingContext> initContext(final IEclipseContext eclipseContext) {
		Function<AccountingInitService, AccountingContext> initFunction = initFromPreferences();
		if (initFunction == null) {
			initFunction = initFromUserInput(eclipseContext.get(Display.class), getMessageInstance(eclipseContext));
		}
		return initFunction;
	}
	
	private Messages getMessageInstance(final IEclipseContext eclipseContext) {
		final IMessageFactoryService mfs = eclipseContext.get(IMessageFactoryService.class);
		final ResourceBundleProvider rbp = eclipseContext.get(ResourceBundleProvider.class);
		
		Locale locale = (Locale) eclipseContext.get(TranslationService.LOCALE);
		if (locale == null) {
			LOG.debug("No locale found in context, using default");
			locale = Locale.getDefault();
		}
		
		final Messages messages = mfs.getMessageInstance(locale, Messages.class, rbp);
		
		LOG.debug("Using locale: {} / {}", locale, messages.wuppdi);
		return messages;
	}
	
	/**
	 * 
	 * @return
	 */
	private Function<AccountingInitService, AccountingContext> initFromPreferences() {
		Function<AccountingInitService, AccountingContext> result = null;
		
		try {
			final AccountingContext accountingContext = AccountingPreferences.readContext();
			result = (service) -> {
				service.init(accountingContext);
				return accountingContext;
			};
		} catch (AccountingException e) {
			LOG.warn("Couldn't instantiate context from preferences, assuming none exist");
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param display
	 * @return
	 */
	private Function<AccountingInitService, AccountingContext> initFromUserInput(final Display display, final Messages messages) {
		LOG.debug("Creating setup wizard");
		
		final Shell shell = createShell(display);
		final WelcomeDialog welcomeDialog = new WelcomeDialog(shell, messages);
		Function<AccountingInitService, AccountingContext> result = null;
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
		String result = System.getProperty("user.home"); // the fallback
		final String osgiInstanceArea = System.getProperty("osgi.instance.area");
		try {
			final File instanceArea = new File(new URI(osgiInstanceArea));
			final File dbLoc = new File(instanceArea, "data");
			result = dbLoc.getAbsolutePath();
		} catch (URISyntaxException e) {
			LOG.warn("Error converting to URI: " + osgiInstanceArea, e);
		}
		return result;
	}
	
	private static AccountingInitService getInitService(IEclipseContext eclipseContext) {
		AccountingInitService initService = eclipseContext.get(AccountingInitService.class);
		if (initService == null) {
			LOG.debug("No UserProfileService found, now checking service tracker");
			Bundle bundle = FrameworkUtil.getBundle(ApplicationInit.class);
			if (bundle != null) {
				ServiceTracker<AccountingInitService, AccountingInitService> tracker = 
						new ServiceTracker<>(bundle.getBundleContext(), AccountingInitService.class, null);
				tracker.open();
				try {
					LOG.debug("Waiting for service to come online...");
					initService = tracker.waitForService(1000);
				} catch (InterruptedException e) {
					LOG.error("Immer der gleiche Dreck!", e);
				}
			} else {
				LOG.warn("No bundle found, dafuq is going on?");
			}
		} else {
			LOG.debug("Init service was available directly from context");
		}
		
		if (initService == null) {
			throw new AccountingException("No service implementation found!");
		}
		
		LOG.info("Service implementation: {}", initService.getDescription());
		return initService;
	}
}
