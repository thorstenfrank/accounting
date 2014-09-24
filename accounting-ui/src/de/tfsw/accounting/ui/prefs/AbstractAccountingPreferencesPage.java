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
package de.tfsw.accounting.ui.prefs;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.tfsw.accounting.ui.AccountingUI;

/**
 * @author thorsten
 *
 */
abstract class AbstractAccountingPreferencesPage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public AbstractAccountingPreferencesPage() {
		
	}

	/**
	 * @param style
	 */
	public AbstractAccountingPreferencesPage(int style) {
		super(style);
	}

	/**
	 * @param title
	 * @param style
	 */
	public AbstractAccountingPreferencesPage(String title, int style) {
		super(title, style);
	}

	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public AbstractAccountingPreferencesPage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		getLogger().debug("init - setting preference store"); //$NON-NLS-1$
		
		setPreferenceStore(new ScopedPreferenceStore(
				InstanceScope.INSTANCE, AccountingUI.getDefault().getBundle().getSymbolicName()));
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract Logger getLogger();

}
