/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.prefs;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import de.togginho.accounting.AccountingContext;
import de.togginho.accounting.AccountingContextFactory;
import de.togginho.accounting.AccountingException;
import de.togginho.accounting.ui.AccountingUI;

/**
 * @author thorsten
 *
 */
public final class AccountingPreferences implements AccountingPreferencesConstants {

	/**
	 * 
	 * @return
	 */
	public static IEclipsePreferences getAccountingPreferences() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(AccountingUI.PLUGIN_ID);
		
		if (prefs == null) {
			throw new AccountingException(Messages.Preferences_ErrorUnavailable);
		}
		
		return prefs;
	}
	
	/**
	 * 
	 * @return
	 */
	public static AccountingContext readContextFromPreferences() {
		IEclipsePreferences prefs = getAccountingPreferences();
		
		return AccountingContextFactory.buildContext(
				prefs.get(ACCOUNTING_USER_NAME, null), prefs.get(ACCOUNTING_DB_FILE, null));
	}
}
