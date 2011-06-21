/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * 
 * @author thorsten frank
 *
 */
public class Perspective implements IPerspectiveFactory {

	public static final String PERSPECTIVE_ID = "de.togginho.accounting.ui.perspective"; //$NON-NLS-1$
	
	/** Logger. */
	private static final Logger LOG = Logger.getLogger(Perspective.class);
		
	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		LOG.debug("Creating initial layout"); //$NON-NLS-1$
		layout.setEditorAreaVisible(true);
		
		layout.addView(IDs.VIEW_CLIENTS_ID, IPageLayout.LEFT, 0.33f, IPageLayout.ID_EDITOR_AREA);
		
		layout.addView(IDs.VIEW_INVOICES_ID, IPageLayout.BOTTOM, 0.5f, IDs.VIEW_CLIENTS_ID);
	}
}
