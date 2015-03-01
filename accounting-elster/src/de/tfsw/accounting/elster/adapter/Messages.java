/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.elster.adapter;

import org.eclipse.osgi.util.NLS;

/**
 * Message resources for the adapter package.
 * 
 * @author Thorsten Frank
 *
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "de.tfsw.accounting.elster.adapter.messages"; //$NON-NLS-1$
	
	public static String AbstractElsterAdapter_errorCreatingXML;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}