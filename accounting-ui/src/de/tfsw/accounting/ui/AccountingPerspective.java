/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * 
 * @author thorsten frank
 *
 */
public class AccountingPerspective implements IPerspectiveFactory {
	
	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(AccountingPerspective.class);
	
	/**
	 * ID of this perspective.
	 */
	public static final String ID = "de.tfsw.accounting.ui.perspective"; //$NON-NLS-1$
	
	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		LOG.debug("Creating initial layout"); //$NON-NLS-1$
		layout.setEditorAreaVisible(true);
		
		layout.addView(IDs.VIEW_CLIENTS_ID, IPageLayout.LEFT, 0.5f, IPageLayout.ID_EDITOR_AREA);
		layout.addView(IDs.VIEW_INVOICES_ID, IPageLayout.BOTTOM, 0.5f, IPageLayout.ID_EDITOR_AREA);
		layout.addView(IDs.VIEW_EXPENSES_ID, IPageLayout.LEFT, 0.5f, IDs.VIEW_INVOICES_ID);
	}
}
