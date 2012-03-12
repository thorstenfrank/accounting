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
	
	public static String AbstractInvoiceHandler_errorNoInvoiceInCurrentEditor;
	public static String AbstractInvoiceHandler_errorNoInvoiceInSelection;

	public static String AccountingSplashHandler_errorUserNotFoundInDbFile;
	public static String AccountingSplashHandler_setupDialogCancelledMsg;
	public static String AccountingSplashHandler_welcomeDialogTitle;
	public static String AccountingSplashHandler_welcomeDialogMessage;
	public static String AccountingSplashHandler_welcomeDialogCreateNew;
	public static String AccountingSplashHandler_welcomeDialogUseExisting;
	public static String AccountingSplashHandler_welcomeDialogImportXml;
	
	public static String AddressWizardPage_desc;
	public static String AddressWizardPage_title;

	public static String CancelInvoiceCommand_confirmMessage;
	public static String CancelInvoiceCommand_confirmText;
	
	public static String CashFlowDialog_grossProfit;

	public static String CashFlowDialog_inputTax;

	public static String CashFlowDialog_operatingExpenses;

	public static String CashFlowDialog_outputTax;

	public static String CashFlowDialog_Result;

	public static String CashFlowDialog_sum;

	public static String CashFlowDialog_title;

	public static String CashFlowDialog_totalExpenses;

	public static String ChangeExpensesViewTimeFrameCommand_message;
	public static String ChangeExpensesViewTimeFrameCommand_title;
	
	public static String ChangeInvoicesFilterCommand_message;
	public static String ChangeInvoicesFilterCommand_title;

	public static String ChooseReportCommand_message;
	public static String ChooseReportCommand_title;
	
	public static String ClientEditorInput_tooltip;
	
	public static String ClientNameWizardPage_title;
	public static String ClientNameWizardPage_desc;
	
	public static String CopyInvoiceCommand_dialogMessage;
	public static String CopyInvoiceCommand_dialogTitle;
	public static String CopyInvoiceCommand_errorEmptyInvoiceNumber;
	public static String CopyInvoiceCommand_errorExistingInvoice;
	public static String CopyInvoiceCommand_errorOpeningEditor;

	public static String DeleteClientCommand_confirmMessage;
	public static String DeleteClientCommand_confirmText;
	
	public static String DeleteInvoiceCommand_confirmMessage;
	public static String DeleteInvoiceCommand_confirmText;
	
	public static String EditClientCommand_errorOpeningEditor;
	
	public static String EditExpenseWizard_newTitle;
	public static String EditExpenseWizard_editTitle;
	public static String EditExpenseWizard_newDesc;
	public static String EditExpenseWizard_editDesc;
	
	public static String EditInvoiceCommand_errorOpeningEditor;
	public static String EditInvoiceCommand_uneditableMessage;
	public static String EditInvoiceCommand_uneditableText;
	
	public static String ExportInvoiceCommand_addingReportParameters;
	public static String ExportInvoiceCommand_exportingReportToFile;
	public static String ExportInvoiceCommand_fillingReport;
	public static String ExportInvoiceCommand_loadingTemplate;
	public static String ExportInvoiceCommand_startingReportGeneration;
	
	public static String ExportModelToXmlHandler_exportSuccessfull_title;
	public static String ExportModelToXmlHandler_exportSuccessfull_text;
	
	public static String FindInvoiceCommand_inputMessage;
	public static String FindInvoiceCommand_inputTitle;
	public static String FindInvoiceCommand_noresultMessage;
	public static String FindInvoiceCommand_noresultTitle;
	
	public static String ImportFromXmlWizard_dataFileLabel;
	public static String ImportFromXmlWizard_errorDataFileExists;
	public static String ImportFromXmlWizard_title;
	public static String ImportFromXmlWizard_message;
	public static String ImportFromXmlWizard_xmlFileLabel;
	public static String ImportFromXmlWizard_xmlFileSelectText;
	
	public static String InvoiceEditor_details;
	public static String InvoiceEditor_header;
	public static String InvoiceEditor_overview;

	public static String InvoicePositionWizard_editTitle;
	public static String InvoicePositionWizard_editDesc;
	public static String InvoicePositionWizard_newTitle;
	public static String InvoicePositionWizard_newDesc;
	
	public static String InvoiceView_number;
	public static String InvoiceView_state;
	public static String InvoiceView_dueDate;
	
	public static String MarkInvoiceAsPaidCommand_errorPaymentDateBeforeSentDate;
	public static String MarkInvoiceAsPaidCommand_errorPaymentDateInTheFuture;
	public static String MarkInvoiceAsPaidCommand_message;
	public static String MarkInvoiceAsPaidCommand_paymentDateLabel;
	public static String MarkInvoiceAsPaidCommand_title;
	
	public static String NewClientWizard_windowTitle;
	public static String NewClientWizard_unknownErrorMsg;
	public static String NewClientWizard_errorSavingTitle;
	
	public static String NewInvoiceWizard_windowTitle;
	public static String NewInvoiceWizardPage_chooseClient;
	public static String NewInvoiceWizardPage_desc;
	public static String NewInvoiceWizardPage_title;
	
	public static String NewTaxRateWizard_abbreviation;
	public static String NewTaxRateWizard_description;
	public static String NewTaxRateWizard_errorMessageTitle;
	public static String NewTaxRateWizard_pageTitle;
	public static String NewTaxRateWizard_rate;
	public static String NewTaxRateWizard_windowTitle;
	
	public static String ReportGenerationUtil_errorGeneratingInvoice;
	public static String ReportGenerationUtil_errorNoReportingService;
	public static String ReportGenerationUtil_labelPdfFiles;
	public static String ReportGenerationUtil_successMsg;
	public static String ReportGenerationUtil_successText;
	
	public static String RevenueDialog_client;
	public static String RevenueDialog_defaultFilename;
	public static String RevenueDialog_invoiceDate;
	public static String RevenueDialog_invoiceNo;
	public static String RevenueDialog_invoices;
	public static String RevenueDialog_paymentDate;
	public static String RevenueDialog_title;
	
	public static String SendInvoiceCommand_message;
	public static String SendInvoiceCommand_title;
	public static String SendInvoiceCommand_sentDateLabel;
	
	public static String SetupWizard_windowTitle;
	
	public static String SetupBankAccountWizardPage_desc;
	
	public static String SetupExistingDataWizard_windowTitle;
	
	public static String TaxIdAndDescriptionWizardPage_desc;
	
	public static String UserEditor_header;
	public static String UserEditor_taxeRateDescription;
	public static String UserEditor_taxRateAbbreviation;
	public static String UserEditor_taxRateName;
	public static String UserEditor_taxRate;
	public static String UserEditor_taxRates;
	
	public static String UserNameAndDbFileWizardPage_dataFile;
	public static String UserNameAndDbFileWizardPage_desc;
	public static String UserNameAndDbFileWizardPage_descNewFile;
	public static String UserNameAndDbFileWizardPage_descOldFile;
	public static String UserNameAndDbFileWizardPage_errorExistingFile;
	public static String UserNameAndDbFileWizardPage_errorNonExistingFile;
	public static String UserNameAndDbFileWizardPage_selectFile;
	public static String UserNameAndDbFileWizardPage_title;
	public static String UserNameAndDbFileWizardPage_yourName;
	
	// COMMON LABELS
	public static String labelAccountNumber;
	public static String labelAddress;
	public static String labelBankAccount;
	public static String labelBankCode;
	public static String labelBankName;
	public static String labelBasicInformation;
	public static String labelBIC;
	public static String labelCity;
	public static String labelClient;
	public static String labelClientDetails;
	public static String labelClientName;
	public static String labelClientNumber;
	public static String labelDate;
	public static String labelDepreciationMethod;
	public static String labelDepreciationPeriodInYears;
	public static String labelDescription;
	public static String labelEmail;
	public static String labelExpense;
	public static String labelExpenses;
	public static String labelExpenseType;
	public static String labelExport;
	public static String labelFax;
	public static String labelGross;
	public static String labelIBAN;
	public static String labelInvoiceDate;
	public static String labelInvoiceDueDate;
	public static String labelInvoiceNo;
	public static String labelMobile;
	public static String labelName;
	public static String labelNet;
	public static String labelPaymentTerms;
	public static String labelPaymentType;
	public static String labelPaymentTarget;
	public static String labelPhone;
	public static String labelPostalCode;
	public static String labelPricePerUnit;
	public static String labelQuantity;
	public static String labelRevenue;
	public static String labelRevenueRelevant;
	public static String labelScrapValue;
	public static String labelSearch;
	public static String labelStreet;
	public static String labelSubtotal;
	public static String labelTaxes;
	public static String labelTaxId;
	public static String labelTaxRate;
	public static String labelTotalGross;
	public static String labelTotalNet;
	public static String labelTotalTax;
	public static String labelTotals;
	public static String labelUnit;
	
	//
	public static String labelTimeFrame;
	public static String labelFrom;
	public static String labelUntil;
	public static String labelLastMonth;
	public static String labelCurrentMonth;
	public static String labelLastYear;
	public static String labelCurrentYear;
	
	public static String labelAdd;
	public static String labelRemove;
	
	public static String labelYes;
	public static String labelNo;
	
	public static String labelError;
	public static String labelUnknownError;
	
	// ICONS
	public static String iconsGroupEdit;
	public static String iconsUserEdit;
	public static String iconsExportToPdf;
	public static String iconsInvoiceCancelled;
	public static String iconsInvoiceCreated;
	public static String iconsInvoiceEdit;
	public static String iconsInvoiceOverdue;
	public static String iconsInvoicePaid;
	public static String iconsInvoiceSend;
	public static String iconsRevenue;
	public static String iconsExpenses;
	public static String iconsReports;
	public static String iconsCashFlowStatement;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
