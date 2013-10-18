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
package de.togginho.accounting;

import org.eclipse.osgi.util.NLS;

/**
 * Localized message resources for the accounting core.
 * 
 * @author thorsten
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "de.togginho.accounting.messages"; //$NON-NLS-1$
	
	public static String FormatUtil_errorParsingDate;
	public static String FormatUtil_errorParsingPercentage;
	public static String FormatUtil_errorParsingCurrency;
	public static String FormatUtil_errorParsingDecimal;
	
	public static String AccountingContextFactory_errorMissingContextProperty;
	
	public static String AccountingService_errorNoContext;
	public static String AccountingService_errorServiceNotInitialised;
	public static String AccountingService_errorFileLocked;
	public static String AccountingService_errorIllegalFileFormat;
	public static String AccountingService_errorDbFileNotFound;
	public static String AccountingService_errorServiceInit;
	public static String AccountingService_errorIO;
	public static String AccountingService_errorDatabaseClosed;
	public static String AccountingService_errorDatabaseReadOnly;
	public static String AccountingService_errorMissingInvoiceNumber;
	public static String AccountingService_errorUnknownUser;
	public static String AccountingService_errorInvoiceCannotBeDeleted;
	public static String AccountingService_errorCannotMarkInvoiceAsPaid;
	public static String AccountingService_errorCannotSaveInvoice;
	public static String AccountingService_errorCannotSendCancelledInvoice;
	public static String AccountingService_errorCannotSendInvoiceWithoutDueDate;
	public static String AccountingService_errorCannotSendInvoiceWithDueDateInPast;
	public static String AccountingService_errorMissingClient;
	public static String AccountingService_errorInvoiceNumberExists;
	public static String AccountingService_errorClientExists;
	public static String AccountingService_errorClientNameExists;
	public static String AccountingService_errorClientNumberExists;
	
	public static String ModelMapper_errorDuringExport;
	public static String ModelMapper_errorDuringImport;
		
	public static String InvoiceState_UNSAVED;
	public static String InvoiceState_CREATED;
	public static String InvoiceState_SENT;
	public static String InvoiceState_OVERDUE;
	public static String InvoiceState_PAID;
	public static String InvoiceState_CANCELLED;
		
	public static String PaymentType_TRADE_CREDIT;
	
	public static String ExpenseType_OPEX;
	public static String ExpenseType_CAPEX;
	public static String ExpenseType_OTHER;
	
	public static String DepreciationMethod_STRAIGHTLINE;
	public static String DepreciationMethod_FULL;
	
	public static String TimeFrameType_currentMonth;
	public static String TimeFrameType_lastMonth;
	public static String TimeFrameType_currentYear;
	public static String TimeFrameType_lastYear;
	public static String TimeFrameType_custom;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
