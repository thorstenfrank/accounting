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

import org.eclipse.osgi.util.NLS;

/**
 * Message resources for the accounting RCP application.
 * @author thorsten
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "de.togginho.accounting.ui.messages"; //$NON-NLS-1$
	
	public static String AbstractClientHandler_message;
	public static String AbstractClientHandler_text;
	
	public static String AccountingSplashHandler_reuseDataMsg;
	public static String AccountingSplashHandler_reuseDataText;
	public static String AccountingSplashHandler_setupDialogCancelledMsg;

	public static String AddressWizardPage_desc;
	public static String AddressWizardPage_title;

	public static String ClientNameWizardPage_title;
	
	public static String ClientEditorInput_tooltip;
	public static String ClientNameWizardPage_desc;
	
	public static String DeleteClientCommand_confirmMessage;
	public static String DeleteClientCommand_confirmText;
	
	public static String DeleteInvoiceCommand_confirmMessage;
	public static String DeleteInvoiceCommand_confirmText;
	
	public static String EditClientCommand_errorOpeningEditor;
	
	public static String EditInvoiceCommand_errorOpeningEditor;
	
	public static String InvoiceEditor_details;
	public static String InvoiceEditor_header;
	public static String InvoiceEditor_overview;

	public static String InvoicePositionWizard_editTitle;
	public static String InvoicePositionWizard_editDesc;
	public static String InvoicePositionWizard_newTitle;
	public static String InvoicePositionWizard_newDesc;
	
	public static String InvoiceToPdfCommand_errorGeneratingInvoice;
	public static String InvoiceToPdfCommand_errorNoReportingService;
	public static String InvoiceToPdfCommand_labelPdfFiles;
	public static String InvoiceToPdfCommand_successMsg;
	public static String InvoiceToPdfCommand_successText;
	
	public static String InvoiceView_number;
	public static String InvoiceView_state;
	
	public static String MarkInvoiceAsPaidCommand_errorPaymentDateBeforeSentDate;
	public static String MarkInvoiceAsPaidCommand_errorPaymentDateInTheFuture;
	public static String MarkInvoiceAsPaidCommand_message;
	public static String MarkInvoiceAsPaidCommand_paymentDateLabel;
	public static String MarkInvoiceAsPaidCommand_title;
	
	public static String NewClientWizard_windowTitle;
	public static String NewClientWizard_alreadyExistsMsg;
	public static String NewClientWizard_alreadyExistsText;
	
	public static String NewInvoiceWizard_windowTitle;
	public static String NewInvoiceWizardPage_chooseClient;
	public static String NewInvoiceWizardPage_desc;
	public static String NewInvoiceWizardPage_generate;
	public static String NewInvoiceWizardPage_title;
	public static String NewInvoiceWizardPage_alreadyExistsMsg;
	public static String NewInvoiceWizardPage_alreadyExistsText;
	
	public static String NewTaxRateWizard_abbreviation;
	public static String NewTaxRateWizard_description;
	public static String NewTaxRateWizard_errorMessageTitle;
	public static String NewTaxRateWizard_pageTitle;
	public static String NewTaxRateWizard_rate;
	public static String NewTaxRateWizard_windowTitle;
	
	public static String SetupWizard_windowTitle;
	public static String SetupBankAccountWizardPage_desc;
	public static String SetupBasicInfoWizardPage_dataFile;
	public static String SetupBasicInfoWizardPage_desc;
	public static String SetupBasicInfoWizardPage_selectFile;
	public static String SetupBasicInfoWizardPage_yourName;

	public static String UserEditor_header;
	public static String UserEditor_taxeRateDescription;
	public static String UserEditor_taxRateAbbreviation;
	public static String UserEditor_taxRateName;
	public static String UserEditor_taxRate;
	public static String UserEditor_taxRates;
	
	// COMMON LABELS
	public static String labelAccountNumber;
	public static String labelAddress;
	public static String labelBankAccount;
	public static String labelBankCode;
	public static String labelBankName;
	public static String labelBasicInformation;
	public static String labelCity;
	public static String labelClient;
	public static String labelClientDetails;
	public static String labelClientName;
	public static String labelDescription;
	public static String labelEmail;
	public static String labelError;
	public static String labelFax;
	public static String labelInvoiceDate;
	public static String labelInvoiceDueDate;
	public static String labelInvoiceNo;
	public static String labelMobile;
	public static String labelName;
	public static String labelPhone;
	public static String labelPostalCode;
	public static String labelPricePerUnit;
	public static String labelQuantity;
	public static String labelStreet;
	public static String labelSubtotal;
	public static String labelTaxId;
	public static String labelTaxRate;
	public static String labelTotalGross;
	public static String labelTotalNet;
	public static String labelTotalTax;
	public static String labelUnit;
	
	public static String labelAdd;
	public static String labelRemove;
	
	// ICONS
	public static String iconsGroupEdit;
	public static String iconsUserEdit;
	public static String iconsInvoiceEdit;
	public static String iconsDeleteInvoice;
	public static String iconsInvoiceToPdf;
	public static String iconsMarkInvoiceAsPaid;
	public static String iconsSendInvoice;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
