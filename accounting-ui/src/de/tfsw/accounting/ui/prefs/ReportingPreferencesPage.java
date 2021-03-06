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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;

/**
 * @author thorsten
 *
 */
public class ReportingPreferencesPage extends AbstractAccountingPreferencesPage implements ReportingPreferencesConstants {

	/**
	 * 
	 */
	private static final Logger LOG = LogManager.getLogger(ReportingPreferencesPage.class);
	
	/**
	 * 
	 */
	public ReportingPreferencesPage() {
		super(GRID);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(LAST_SAVE_DIR, Messages.ReportingPrefs_DefaultExportDir, getFieldEditorParent()));
		addField(new BooleanFieldEditor(OPEN_AFTER_EXPORT, Messages.ReportingPrefs_OpenAfterExport, getFieldEditorParent()));
	}

	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.prefs.AbstractAccountingPreferencesPage#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
