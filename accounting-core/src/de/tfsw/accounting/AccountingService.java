/*
 *  Copyright 2011 ,2014 Thorsten Frank (accounting@tfsw.de).
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

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import de.tfsw.accounting.model.AnnualDepreciation;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.CurriculumVitae;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.model.ExpenseImportParams;
import de.tfsw.accounting.model.ExpenseImportResult;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.IncomeStatement;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoicePosition;
import de.tfsw.accounting.model.InvoiceState;
import de.tfsw.accounting.model.ModelMetaInformation;
import de.tfsw.accounting.model.PaymentTerms;
import de.tfsw.accounting.model.Revenue;
import de.tfsw.accounting.util.TimeFrame;

/**
 * Service interface for the <code>accounting-core</code> plugin.
 * 
 * <p>Clients must make sure that the very first method call is to {@link #init(AccountingContext)} and that this
 * methods returns successfully, i.e. without an {@link AccountingException} being thrown. Otherwise, all subsequent
 * calls to methods of this service will result in an exception.</p>
 * 
 * <p>The lifecycle of this service is controlled by it's bundle ({@link AccountingCore}), especially the shut down
 * procedure, so no attempt should be made to access a service implementation outside of the bundle.</p>
 * 
 * <p>This interface is not meant to be implemented by clients.</p>
 * 
 * @author thorsten frank
 *
 * @deprecated to be removed once the migration to separate services is complete
 */
@Deprecated
public interface AccountingService extends AccountingInitService, UserService, ClientService {
	
	/**
	 * Returns the next (sequential) invoice number.
	 * 
	 * @return the next valid invoice number
	 */
	String getNextInvoiceNumber();
	
	/**
	 * Creates a new invoice whose state will be {@link InvoiceState#UNSAVED}.
	 * <p> 
	 * The returned instance will be assigned both the supplied invoice number and
	 * client, along with the current user (see {@link #getCurrentUser()}). Also, the invoice date will be set to today,
	 * and the payment terms will either be taken from the supplied Client's default value, or if there is none, the
	 * global default ( {@link PaymentTerms#DEFAULT}.  
	 * </p>
	 * 
	 * @param invoiceNumber a unique number for the invoice to create. If an invoice with the same number already 
	 * 		  exists, an {@link AccountingException} is thrown
	 * 
	 * @param client the {@link Client} that the newly created invoice will be billed to, must not be <code>null</code>
	 * 
	 * @return a newly created {@link Invoice}
	 * 
	 * @throws AccountingException if the supplied invoice number is either empty or another invoice with that number
	 * 	  	   already exists, or if the supplied client is <code>null</code>
	 */
	Invoice createNewInvoice(String invoiceNumber, Client client);
	
	/**
	 * Saves an {@link Invoice} to persistence.
	 * 
	 * <p>Note that invoices may only be saved through this method until they have been sent, i.e. only while they are
	 * either in state {@link InvoiceState#UNSAVED} or {@link InvoiceState#CREATED}. Any attempt to save an invoice
	 * afterwards will result in an {@link AccountingException}.</p>
	 * 
	 * @param invoice the {@link Invoice} to save
	 * @return the saved invoice
	 * @throws AccountingException if this invoice cannot be saved due to being in an invalid state, or if a technical
	 * 		   error happens during persisting operations
	 * @see #sendInvoice(Invoice)
	 * @see #markAsPaid(Invoice, Date)
	 */
	@ModelChanging
	Invoice saveInvoice(Invoice invoice);
	
	/**
	 * Flags the supplied invoice as having been sent to its {@link Client}. After an invoice has been sent, it cannot
	 * be edited or saved, only marked as paid or cancelled.
	 * 
	 * <p>An invoice cannot be re-sent if is has been cancelled, does not have a due date, or if the due date is in the
	 * past.</p>
	 * 
	 * @param invoice the invoice sent to its client
	 * @return the sent invoice
	 * @throws AccountingException if the invoice cannot be sent due to business rules, or if a technical error happens
	 * @see InvoiceState#CANCELLED
	 * @see Invoice#getDueDate() 
	 */
	@ModelChanging
	Invoice sendInvoice(Invoice invoice, LocalDate sentDate);
	
	/**
	 * Assigns the payment date to the invoice and saves it. Afterwards, the invoice will be in state
	 * {@link InvoiceState#PAID}.
	 * 
	 * <p>An invoice can only be marked as paid while in state {@link InvoiceState#SENT} or
	 * {@link InvoiceState#OVERDUE}.</p>
	 * 
	 * @param invoice the {@link Invoice} that was paid
	 * @param paymentDate the {@link Date} the invoice was paid
	 * @return the saved invoice, now in state {@link InvoiceState#PAID}
	 * @throws AccountingException if the invoice cannot be marked as paid
	 * @see Invoice#getPaymentDate()
	 * @see Invoice#canBePaid()
	 */
	@ModelChanging
	Invoice markAsPaid(Invoice invoice, LocalDate paymentDate);
	
	/**
	 * Cancels the supplied invoice and saves it. The returned invoice will be in state {@link InvoiceState#CANCELLED}.
	 * Note that no further life-cycle actions are possible after cancelling an invoice.
	 * 
	 * <p>An invoice can only be cancelled while in one of the following states:
	 * <ul>
	 * <li>{@link InvoiceState#SENT}</li>
	 * <li>{@link InvoiceState#OVERDUE}</li>
	 * <li>{@link InvoiceState#PAID}</li>
	 * </ul>
	 * </p> 
	 * 
	 * @param invoice the invoice to cancel
	 * @return the cancelled and saved invoice
	 * @see Invoice#canBeCancelled()
	 */
	@ModelChanging
	Invoice cancelInvoice(Invoice invoice);
	
	/**
	 * Deletes the invoice permanently. An invoice may only be deleted while it hasn't been sent yet, i.e. while it is
	 * in state {@link InvoiceState#CREATED}. All attempts to delete an invoice in a state beyond that will result in
	 * an {@link AccountingException} being thrown. 
	 * 
	 * <p>Requests to delete invoices in state {@link InvoiceState#UNSAVED} will be ignored.</p>
	 * 
	 * @param invoice the {@link Invoice} to delete
	 * @throws AccountingException if the invoice cannot be deleted or if an error occurred during deletion
	 */
	@ModelChanging
	void deleteInvoice(Invoice invoice);
	
	/**
	 * Attempts to retrieve a specific {@link Invoice} from persistence.
	 * 
	 * @param invoiceNumber the {@link Invoice#getNumber()} to search for
	 * @return the {@link Invoice} with the supplied number, or <code>null</code> if there is no such invoice
	 * @throws AccountingException if a technical error occurred while accessing persistence
	 */
	Invoice getInvoice(String invoiceNumber);
	
	/**
	 * Creates a copy of the supplied {@link Invoice}.
	 * 
	 * <p>The copied invoice will contain most of the properties and references of the original with some exceptions:
	 * the copy will be in state {@link InvoiceState#UNSAVED}, and all state-related dates will be set to 
	 * <code>null</code>, namely:
	 * <ul>
	 * <li>{@link Invoice#getCreationDate()}</li>
	 * <li>{@link Invoice#getCancelledDate()}</li>
	 * <li>{@link Invoice#getPaymentDate()}</li>
	 * <li>{@link Invoice#getSentDate()}</li>
	 * </ul>
	 * 
	 * The copied invoice's number will be the supplied one.
	 * </p>
	 * 
	 * <p>Both {@link Invoice#getInvoiceDate()} and {@link Invoice#getDueDate()} will be reset to reflect the current
	 * date and the configured default payment terms, respectively.</p>
	 * 
	 * <p>The copy will contain unique new instances of any {@link InvoicePosition}s the original may contain, but it
	 * will only contain references to the user and client.</p>
	 * 
	 * <p>The aforementioned changes to the new copy will be made regardless of the state of the original, meaning that
	 * the mentioned fields will be reset no matter if the original contains this information, or if it is valid. As
	 * such, any invoice in any kind of state may be copied during any time of its life cycle.</p>
	 * 
	 * <p>Copying an invoice does not change the original in any way.</p>
	 * 
	 * @param invoice		the {@link Invoice} to copy
	 * @param invoiceNumber the {@link Invoice#getNumber()} of the copy, may not be <code>null</code>
	 * @return the copied invoice
	 */
	Invoice copyInvoice(Invoice invoice, String invoiceNumber);
	
	/**
	 * This is a shortcut to calling {@link #findInvoices(<code>null</code>, InvoiceState...)}
	 */
	Set<Invoice> findInvoices(InvoiceState... states);

	/**
	 * Finds all invoices for the current user that match the specified states.
	 * 
	 * <p>The current user is the user denoted by the user name in the {@link AccountingContext} that this service was
	 * initialized with.</p>
	 * 
	 * <p>No states have to be specified, in which case all invoices for the current user will be returned.</p>
	 * 
	 * <p>This method will never return <code>null</code>, if there were no invoices found for the specified arguments,
	 * an empty {@link Set} is returned.</p>
	 * 
	 * @param timeFrame an optional time frame in which an invoice's {@link Invoice#getInvoiceDate()} must be. 
	 * 				    May be <code>null</code>
	 * @param states an optional and arbitrary number of {@link InvoiceState}s that the returned invoices must match
	 * 			     May be <code>null</code>, which is the same as supplying all available states
	 * 
	 * @return a {@link Set} of {@link Invoice} instances for the current user matching the supplied states
	 * 
	 * @throws AccountingException if a technical error occurred while accessing persistence
	 */
	Set<Invoice> findInvoices(TimeFrame timeFrame, InvoiceState... states);
	
	/**
	 * Finds and returns revenue for the given timeframe.
	 * 
	 * <p>Revenue considers all invoices that have a payment date within the given timeframe and are not cancelled.</p>
	 * 
	 * @param timeFrame the time frame for which to collect the {@link Revenue}
	 * 
	 * @return a {@link Revenue} instance that contains all paid invoices within the given timeframe
	 */
	Revenue getRevenue(TimeFrame timeFrame);
	
	/**
	 * Returns a complete list of all known paid invoices sorted by year. For every year, a {@link Revenue} object is
	 * compiled.
	 * 
	 * @return a {@link List} of {@link Revenue} objects representing individual yearly revenue
	 */
	List<Revenue> getRevenueByYears();
	
	/**
	 * Saves an {@link Expense}.
	 * 
	 * @param expense the {@link Expense} to save
	 * 
	 * @return the saved instance - this should be used by clients for any further work
	 */
	@ModelChanging
	Expense saveExpense(Expense expense);
	
	/**
	 * Saves a collection of expenses.
	 * 
	 * @param expenses the expenses to save
	 * 
	 * @return the saved instances
	 */
	@ModelChanging
	Collection<Expense> saveExpenses(Collection<Expense> expenses);
	
	/**
	 * Returns a {@link Set} of {@link Expense}s that were paid for within the supplied {@link TimeFrame}.
	 *  
	 * @param timeFrame	the time frame within which the {@link Expense#getPaymentDate()} needs to be in order to be
	 * 		  part of the returned {@link Set}
	 * @param types		optional parameter to further narrow down the result set to include only those expenses with a
	 * 	      matching type
	 * 
	 * @return	a collection of expenses from within the supplied {@link TimeFrame}
	 */
	ExpenseCollection findExpenses(TimeFrame timeFrame, ExpenseType...types);
		
	/**
	 * Permanently remove the supplied {@link Expense} from persistence.
	 * 
	 * @param expense the {@link Expense} to delete
	 */
	@ModelChanging
	void deleteExpense(Expense expense);
	
	/**
	 * Returns all single {@link AnnualDepreciation} entries for the supplied year
	 * 
	 * @param year the year to search for as a four-digit number, e.g. 2010, 1999, etc.
	 * 
	 * @return a list of all annual depreciation entries for the supplied year
	 */
	List<AnnualDepreciation> getDepreciationForYear(int year);
	
	/**
	 * 
	 * @param timeFrame
	 * @return
	 */
	IncomeStatement getIncomeStatement(TimeFrame timeFrame);
	
	/**
	 * Exports the entire database to the specified XML file. This file can later be used to re-create the DB.
	 * 
	 * @param targetFileName the fully qualified path and filename of the XML file to create
	 * 
	 * @see #importModelFromXml(String, String)
	 */
	void exportModelToXml(String targetFileName);
	
	/**
	 * Imports expenses from a comma-separated-values file.
	 * 
	 * @param sourceFile the file to import from
	 * 
	 * @return the imported expenses
	 */
	ExpenseImportResult importExpenses(String sourceFile, ExpenseImportParams params);
	
	/**
	 * 
	 * @return
	 */
	ModelMetaInformation getModelMetaInformation();
	
	/**
	 * 
	 * @param cv
	 * @return
	 */
	@ModelChanging
	CurriculumVitae saveCurriculumVitae(CurriculumVitae cv);
	
	/**
	 * 
	 * @return
	 */
	CurriculumVitae getCurriculumVitae();
	
	/**
	 * 
	 * @return
	 */
	Set<ExpenseTemplate> getExpenseTemplates();
	
	/**
	 * 
	 * @return
	 */
	Set<ExpenseTemplate> findApplicableExpenseTemplates();
	
	/**
	 * 
	 * @param expenseTemplate
	 * @return
	 */
	@ModelChanging
	ExpenseTemplate saveExpenseTemplate(ExpenseTemplate expenseTemplate);
	
	/**
	 * 
	 * @param expenseTemplate
	 */
	@ModelChanging
	void deleteExpenseTemplate(ExpenseTemplate expenseTemplate);
}

