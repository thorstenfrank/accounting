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
package de.tfsw.accounting.ui;

/**
 * @author tfrank1
 *
 */
public interface IDs {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// VIEWS / EDITORS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String EDIT_USER_ID = "de.tfsw.accounting.ui.user.UserEditor"; //$NON-NLS-1$
	public static final String EDIT_CLIENT_ID = "de.tfsw.accounting.ui.client.ClientEditor"; //$NON-NLS-1$
	public static final String VIEW_CLIENTS_ID = "de.tfsw.accounting.ui.client.ClientView"; //$NON-NLS-1$
	public static final String VIEW_INVOICES_ID = "de.tfsw.accounting.ui.invoice.InvoiceView"; //$NON-NLS-1$
	public static final String EDIT_INVOIDCE_ID = "de.tfsw.accounting.ui.invoice.InvoiceEditor"; //$NON-NLS-1$
	public static final String VIEW_EXPENSES_ID = "de.tfsw.accounting.ui.expense.ExpensesView"; //$NON-NLS-1$
	public static final String EDIT_EXPENSE_ID = "de.tfsw.accounting.ui.expense.ExpenseEditor"; //$NON-NLS-1$
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// COMMANDS / ACTIONS / EVENTS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// User
	public static final String CMD_OPEN_USER_EDITOR = "de.tfsw.accounting.ui.user.OpenUserEditor"; //$NON-NLS-1$
	
	// Clients
	public static final String CMD_NEW_CLIENT_WIZARD = "de.tfsw.accounting.ui.client.NewClientCommand"; //$NON-NLS-1$
	public static final String CMD_EDIT_CLIENT = "de.tfsw.accounting.ui.client.EditClientCommand"; //$NON-NLS-1$
	public static final String CMD_DELETE_CLIENT = "de.tfsw.accounting.ui.client.DeleteClientCommand"; //$NON-NLS-1$
	
	// Invoices
	public static final String CMD_NEW_INVOICE = "de.tfsw.accounting.ui.invoice.NewInvoiceCommand"; //$NON-NLS-1$
	public static final String CMD_EDIT_INVOICE = "de.tfsw.accounting.ui.invoice.EditInvoiceCommand"; //$NON-NLS-1$
	public static final String CMD_DELETE_INVOICE = "de.tfsw.accounting.ui.invoice.DeleteInvoiceCommand"; //$NON-NLS-1$
	public static final String CMD_INVOICE_TO_PDF = "de.tfsw.accounting.ui.invoice.ExportInvoiceCommand"; //$NON-NLS-1$
	public static final String CMD_SEND_INVOICE = "de.tfsw.accounting.ui.invoice.SendInvoiceCommand"; //$NON-NLS-1$
	public static final String CMD_COPY_INVOICE = "de.tfsw.accounting.ui.invoice.CopyInvoiceCommand"; //$NON-NLS-1$
	public static final String CMD_MARK_INVOICE_AS_PAID = "de.tfsw.accounting.ui.invoice.MarkInvoiceAsPaidCommand"; //$NON-NLS-1$
	public static final String CMD_CANCEL_INVOICE = "de.tfsw.accounting.ui.invoice.CancelInvoiceCommand"; //$NON-NLS-1$
	public static final String CMD_CHANGE_INVOICES_FILTER = "de.tfsw.accounting.ui.invoice.ChangeInvoicesFilterCommand"; //$NON-NLS-1$
	public static final String CMD_FIND_INVOICE = "de.tfsw.accounting.ui.invoice.FindInvoiceCommand"; //$NON-NLS-1$
	
	// Expenses
	public static final String CMD_EDIT_EXPENSE = "de.tfsw.accounting.ui.expense.EditExpenseCommand"; //$NON-NLS-1$
	public static final String CMD_CHANGE_EXPENSES_VIEW_TIME_FRAME = "de.tfsw.accounting.ui.expense.ChangeExpensesViewTimeFrameCommand"; //$NON-NLS-1$
	public static final String CMD_NEW_EXPENSE = "de.tfsw.accounting.ui.expense.NewExpenseCommand"; //$NON-NLS-1$
	
	// Reporting
	public static final String CMD_OPEN_REVENUE_DIALOG = "de.tfsw.accounting.ui.revenue.OpenRevenueCommand"; //$NON-NLS-1$
	public static final String CMD_OPEN_REVENUE_BY_YEAR_DIALOG = "de.tfsw.accounting.ui.revenue.RevenueByYearCommand"; //$NON-NLS-1$
	public static final String CMD_OPEN_EXPENSES_DIALOG = "de.tfsw.accounting.ui.expense.OpenExpenseDialog"; //$NON-NLS-1$
	public static final String CMD_OPEN_CASH_FLOW_STATEMENT = "de.tfsw.accounting.reports.OpenCashFlowStatementDialog"; //$NON-NLS-1$
	public static final String CMD_OPEN_INCOME_STATEMENT = "de.tfsw.accounting.reports.OpenIncomeStatementDialog"; //$NON-NLS-1$
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// EXTENSION POINTS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String EXTENSION_POINT_REPORT_HANDLERS = "de.tfsw.accounting.ui.reportHandlers"; //$NON-NLS-1$
}
