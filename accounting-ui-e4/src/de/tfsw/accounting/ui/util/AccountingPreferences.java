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
package de.tfsw.accounting.ui.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingException;

/**
 * @author thorsten
 *
 */
public final class AccountingPreferences {
	
	private static final Logger LOG = LogManager.getLogger(AccountingPreferences.class);
	
	private static final String PREFERENCE_NODE = "de.tfsw.accounting.ui";
	
	/** */
	private static final String ACCOUNTING_USER_NAME = "context.user.name"; //$NON-NLS-1$
	
	/** */
	private static final String ACCOUNTING_DB_FILE = "context.db.location"; //$NON-NLS-1$
	
	/**
	 * 
	 * @return
	 */
	public static AccountingContext readContext() {
		LOG.debug("Reading context from preferences");
		final IEclipsePreferences prefs = getPreferenceNode();
		return new AccountingContext(
				prefs.get(ACCOUNTING_USER_NAME, null), prefs.get(ACCOUNTING_DB_FILE, null));
	}
	
	/**
	 * 
	 * @param context
	 */
	public static void storeContext(final AccountingContext context) {
		LOG.debug("Saving context to preferences");
		final IEclipsePreferences prefs = getPreferenceNode();
		prefs.put(ACCOUNTING_USER_NAME, context.getUserName());
		prefs.put(ACCOUNTING_DB_FILE, context.getDbLocation());
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			LOG.error("Error writing preferences", e);
			throw new AccountingException("Error writing context to preference store", e);
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws AccountingException if the node is not available
	 */
	private static IEclipsePreferences getPreferenceNode() {
		final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(PREFERENCE_NODE);
		if (prefs == null) {
			throw new AccountingException(
					String.format("Preference node [%s] unavailable", PREFERENCE_NODE));
		}
		return prefs;
	}
}
