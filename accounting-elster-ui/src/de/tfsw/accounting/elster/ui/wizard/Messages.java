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
package de.tfsw.accounting.elster.ui.wizard;

import org.eclipse.osgi.util.NLS;

/**
 * Localisation Messages for the ELSTER export wizard package.
 * 
 * @author Thorsten Frank
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "de.tfsw.accounting.elster.ui.wizard.messages"; //$NON-NLS-1$
	
	public static String AbstractElsterWizardPage_icon;
	
	public static String AddressPage_City;
	public static String AddressPage_Country;
	public static String AddressPage_Description;
	public static String AddressPage_HouseAdd;
	public static String AddressPage_HouseNo;
	public static String AddressPage_Street;
	public static String AddressPage_Title;
	public static String AddressPage_ZIPCode;
	
	public static String AmountsPage_Description;
	public static String AmountsPage_InputTax;
	public static String AmountsPage_Kz66;
	public static String AmountsPage_Kz81;
	public static String AmountsPage_Kz83;
	public static String AmountsPage_Kz86;
	public static String AmountsPage_Revenue;
	public static String AmountsPage_Sum;
	public static String AmountsPage_TaxSum;
	public static String AmountsPage_Title;
	
	public static String AmountsPage_VAT19;
	public static String AmountsPage_VAT7;
	
	public static String CompanyNamePage_CompanyName;
	public static String CompanyNamePage_Description;
	public static String CompanyNamePage_Email;
	public static String CompanyNamePage_FirstName;
	public static String CompanyNamePage_LastName;
	public static String CompanyNamePage_GroupName;
	public static String CompanyNamePage_GroupContactInfo;
	public static String CompanyNamePage_GroupTax;
	public static String CompanyNamePage_Phone;
	public static String CompanyNamePage_State;
	public static String CompanyNamePage_TaxNoGenerated;
	public static String CompanyNamePage_TaxNoOrig;
	public static String CompanyNamePage_Title;
	
	public static String ElsterExportWizard_Title;
	
	public static String ExportTargetSelectionPage_Description;
	public static String ExportTargetSelectionPage_FileSelectionLabel;
	public static String ExportTargetSelectionPage_Preview;
	public static String ExportTargetSelectionPage_TargetFile;
	public static String ExportTargetSelectionPage_Title;
	
	public static String TimeFrameSelectionPage_Description;
	public static String TimeFrameSelectionPage_Period;
	public static String TimeFrameSelectionPage_Title;
	public static String TimeFrameSelectionPage_Year;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
