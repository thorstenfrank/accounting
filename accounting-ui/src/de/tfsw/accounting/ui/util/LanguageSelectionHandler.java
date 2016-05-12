/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.tfsw.accounting.ui.AbstractAccountingHandler;
import de.tfsw.accounting.ui.AbstractModalDialog;
import de.tfsw.accounting.ui.Messages;

/**
 * Very simple handler that allows users to change the language and locale of the application by writing it to the
 * OSGI config file under key <pre>osgi.nl</pre>.
 * 
 * @author thorsten
 *
 */
public class LanguageSelectionHandler extends AbstractAccountingHandler {

	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(LanguageSelectionHandler.class);
	
	/**
	 * 
	 */
	private static final String OSGI_NL = "osgi.nl"; //$NON-NLS-1$

	/**
	 * 
	 */
	private static final String CONFIG_INI = "config.ini"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final String[] SUPPORTED_LANGUAGES = {
		new Locale("de").getLanguage(), //$NON-NLS-1$
		new Locale("en").getLanguage() //$NON-NLS-1$
	};
	
	/**
	 * 
	 */
	private Locale newLocale;

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
				
		SelectionDialog dialog = new SelectionDialog(getShell(event));
		
		if (dialog.show()) {
			
			if (changeLocale(event)) {
				MessageDialog md = new MessageDialog(
						getShell(event), 
						Messages.LanguageSelectionHandler_RestartTitle, 
						null, 
						Messages.LanguageSelectionHandler_RestartMessage, 
						MessageDialog.INFORMATION, 
						new String[]{Messages.LanguageSelectionHandler_RestartNow, Messages.LanguageSelectionHandler_RestartLater}, 
						0);
				
				if (md.open() == 0) {
					PlatformUI.getWorkbench().restart();
				}
			}
		}
	}

	/**
	 * 
	 * @param event
	 * @return
	 */
	private boolean changeLocale(ExecutionEvent event) {
		if (newLocale == null) {
			LOG.warn("No locale was selected! Skipping writing to config"); //$NON-NLS-1$
			return false;
		}
		
		File configFile = getConfigFile();
		if (configFile == null || !configFile.exists() || configFile.isDirectory()) {
			MessageDialog.openError(getShell(event), 
					Messages.LanguageSelectionHandler_ErrorTitle, Messages.LanguageSelectionHandler_ErrorReading);
			return false;
		}
		
		Properties props = readPropertiesFromConfig(configFile);
		if (props == null) {
			MessageDialog.openError(getShell(event), 
					Messages.LanguageSelectionHandler_ErrorTitle, Messages.LanguageSelectionHandler_ErrorReading);
			return false;
		}
		
		if (props.containsKey(OSGI_NL)) {
			LOG.debug("Configured locale is " + props.getProperty(OSGI_NL)); //$NON-NLS-1$
		} else {
			LOG.debug("No locale configured yet..."); //$NON-NLS-1$
		}
		
		props.put(OSGI_NL, newLocale.toString());
		
		if (!writePropertiesToConfig(props, configFile)) {
			MessageDialog.openError(getShell(event), 
					Messages.LanguageSelectionHandler_ErrorTitle, Messages.LanguageSelectionHandler_ErrorWriting);
			return false;
		}
		
		return true;
	}
		
	/**
	 * 
	 * @return
	 */
	private File getConfigFile() {
		try {
			URL configURL = Platform.getConfigurationLocation().getDataArea(CONFIG_INI);
			if (configURL != null) {
				return new File(configURL.toURI());
			} else {
				LOG.warn("No URL could be found for config.ini file..."); //$NON-NLS-1$
			}
		} catch (Exception e) {
			LOG.error("Error getting config file", e); //$NON-NLS-1$
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param configFile
	 * @return
	 */
	private Properties readPropertiesFromConfig(File configFile) {
		Properties props = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream(configFile);
			props.load(in);
			in.close();
		} catch (Exception e) {
			LOG.error("Error reading configuration", e); //$NON-NLS-1$
			props = null;
		}
		return props;
	}
	
	/**
	 * 
	 * @param props
	 * @param configFile
	 * @return
	 */
	private boolean writePropertiesToConfig(Properties props, File configFile) {
		try {
			FileOutputStream out = new FileOutputStream(configFile);
			props.store(out, "Generated by AccountingUI"); //$NON-NLS-1$
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			LOG.error("Error writing config to file", e); //$NON-NLS-1$
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	private class SelectionDialog extends AbstractModalDialog {
		
		private SelectionDialog(Shell shell) {
			super(shell, Messages.LanguageSelectionHandler_Title, Messages.LanguageSelectionHandler_Message);
		}
		
		@Override
		protected void createMainContents(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			WidgetHelper.grabHorizontal(composite);
    		
			new Label(composite, SWT.NONE).setText(Messages.LanguageSelectionHandler_Language);
			
			final Combo language = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			//WidgetHelper.grabHorizontal(language);
			
			final Locale defaultLocale = Locale.getDefault();
			
			List<String> supported = new ArrayList<String>();
			for (String s : SUPPORTED_LANGUAGES) {
				supported.add(s);
			}
			
			final List<Locale> available = new ArrayList<Locale>();
			
			int selected = 0;
			int index = 0;
			for (Locale locale : Locale.getAvailableLocales()) {
				if (!locale.getCountry().isEmpty() && supported.contains(locale.getLanguage())) {
					final String display = String.format("%s (%s)", //$NON-NLS-1$
							locale.getDisplayLanguage(locale), locale.getDisplayCountry(locale));
					language.add(display);
					available.add(locale);
					if (defaultLocale.equals(locale)) {
						selected = index;
					}
					index++;
				}
			}
			language.select(selected);
			
			language.addSelectionListener(new SelectionAdapter() {

				/**
				 * {@inheritDoc}
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Locale selectedLocale = available.get(language.getSelectionIndex());
					LOG.debug("Selected new locale: " + selectedLocale.toString()); //$NON-NLS-1$
					newLocale = selectedLocale;
				}
			});
		}
	}
}
