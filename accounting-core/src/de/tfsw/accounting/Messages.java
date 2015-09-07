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
package de.tfsw.accounting;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.eclipse.osgi.util.NLS;

/**
 * Localized message resources for the accounting core.
 * 
 * @author thorsten
 *
 */
public class Messages extends NLS {

	private static final Logger LOG = Logger.getLogger(Messages.class);
	
	private static final String BUNDLE_NAME = "de.tfsw.accounting.messages"; //$NON-NLS-1$
	
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
	
	public static String ExpenseImporter_WarningUnparseableAmount;
	public static String ExpenseImporter_WarningUnparseableDate;
	public static String ExpenseImporter_WarningUnparseableTaxRate;
	public static String ExpenseImporter_WarningUnknownTaxRate;
	public static String ExpenseImporter_WarningInvalidType;
	public static String ExpenseImporter_ErrorReadingFile;
	
	public static String Frequency_DAILY;
	public static String Frequency_WEEKLY;
	public static String Frequency_MONTHLY;
	public static String Frequency_YEARLY;
	
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
	public static String TimeFrameType_month;
	public static String TimeFrameType_wholeYear;
	
	public static String DateFormatPattern_DMY;
	public static String DateFormatPattern_MDY;
	public static String DateFormatPattern_YMD;
	public static String DateFormatPattern_MYD;
	public static String DateFormatPattern_DYM;
	public static String DateFormatPattern_YDM;
		
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages() {
	}
	
	private static final String FORMAT_KEY_NOT_FOUND = "!%s!";
	
	/**
	 * Translates an enum value. This method works only if there is a static variable by the name of
	 * <code>[e.getClass().getSimpleName()]_[e.name()]</code> defined in this class.
	 * 
	 * <p>
	 * Example: <code>SomeEnum_ENUMVALUE</code>
	 * </p>
	 * 
	 * <p>
	 * If there is no field with the defined name, the name of the enum is returned with preceding and trailing
	 * exlamation marks.
	 * </p>
	 * 
	 * @param e the enumeration value to translate
	 * 
	 * @return the translation of the supplied enum value, or an error string if no translation could be found
	 */
	public static String translate(Enum<?> e) {
		final String name = new StringBuilder(e.getClass().getSimpleName())
				.append(Constants.UNDERSCORE)
				.append(e.name())
				.toString();
		try {
			Field field = Messages.class.getDeclaredField(name);
			return (String)field.get(null);
		} catch (Exception ex) {
			LOG.error(String.format("Error trying to get translation for key [%s]", name), ex);
		}
		return String.format(FORMAT_KEY_NOT_FOUND, name);
	}
}
