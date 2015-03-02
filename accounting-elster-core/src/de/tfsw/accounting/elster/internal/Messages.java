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
package de.tfsw.accounting.elster.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Message resources for the internal ELSTER package.
 * 
 * @author Thorsten Frank
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "de.tfsw.accounting.elster.internal.messages"; //$NON-NLS-1$
	
	public static String ElsterDTOBuilder_ErrorAccountingServiceNull;

	public static String ElsterServiceImpl_ErrorFilingPeriodIsNull;
	public static String ElsterServiceImpl_ErrorGeneratingXML;
	public static String ElsterServiceImpl_ErrorInitMarshaller;
	public static String ElsterServiceImpl_ErrorNoAdapterForFilingPeriod;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
