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

import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author thorsten
 *
 */
public class AccountingPreferencesPage extends AbstractAccountingPreferencesPage implements AccountingPreferencesConstants {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(AccountingPreferencesPage.class);
	
	/**
	 * 
	 */
	public AccountingPreferencesPage() {
		super(GRID);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		LOG.debug("Creating Field Editors"); //$NON-NLS-1$
		
		Group group = new Group(getFieldEditorParent(), SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText(Messages.AccountingPrefs_GroupTitle);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(group);

		Label label = new Label(group, SWT.WRAP);
		label.setText(Messages.AccountingPrefs_Explanation);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		gd.widthHint= convertWidthInCharsToPixels(30);
		label.setLayoutData(gd);
		
		StringFieldEditor userName = new StringFieldEditor(ACCOUNTING_USER_NAME, Messages.AccountingPrefs_UserName, group);
		userName.setEnabled(false, group);
		addField(userName);
		
		StringFieldEditor dbPath = new StringFieldEditor(ACCOUNTING_DB_FILE, Messages.AccountingPrefs_DBfile, group);
		dbPath.setEnabled(false, group);
		addField(dbPath);
	}

	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.prefs.AbstractAccountingPreferencesPage#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
