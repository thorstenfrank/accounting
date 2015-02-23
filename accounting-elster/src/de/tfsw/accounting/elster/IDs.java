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
package de.tfsw.accounting.elster;

/**
 * ID constants.
 * 
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public final class IDs {

	/**
	 * ID of the Elster plugin.
	 */
	public static final String PLUGIN_ID = "de.tfsw.accounting.elster"; //$NON-NLS-1$
	
	/**
	 * ID of the plugin extension for adapter implementations.
	 */
	public static final String ADAPTER_EXTENSION_ID = "de.tfsw.accounting.elster.elsterApdapter"; //$NON-NLS-1$
	
	/**
	 * Help Context ID of the Elster Export Wizard.
	 */
	public static final String ELSTER_EXPORT_WIZARD_HELP_ID = "de.tfsw.accounting.elster.ElsterExportWizard"; //$NON-NLS-1$
	
	/**
	 * No need to instantiate this class.
	 */
	private IDs() {
		// 
	}
}
