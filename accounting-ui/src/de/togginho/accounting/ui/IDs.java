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

/**
 * @author tfrank1
 *
 */
public interface IDs {

	/** The main perspective. */
	public static final String PERSPECTIVE_ID = "de.togginho.accounting.ui.rcp.perspective"; //$NON-NLS-1$
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// VIEWS / EDITORS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String EDIT_USER_ID = "de.togginho.accounting.ui.user.UserEditor"; //$NON-NLS-1$
	
	public static final String EDIT_CLIENT_ID = "de.togginho.accounting.ui.client.ClientEditor"; //$NON-NLS-1$
	
	public static final String VIEW_CLIENTS_ID = "de.togginho.accounting.ui.client.ClientView"; //$NON-NLS-1$
	
	public static final String VIEW_INVOICES_ID = "de.togginho.accounting.ui.invoice.InvoiceView"; //$NON-NLS-1$
	
	public static final String EDIT_INVOIDCE_ID = "de.togginho.accounting.ui.invoice.InvoiceEditor"; //$NON-NLS-1$
	
	public static final String VIEW_EXPENSES_ID = "de.togginho.accounting.ui.expense.ExpensesView"; //$NON-NLS-1$
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// COMMANDS / ACTIONS / EVENTS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String CMD_OPEN_USER_EDITOR = "de.togginho.accounting.ui.user.OpenUserEditor"; //$NON-NLS-1$
	
	public static final String CMD_NEW_CLIENT_WIZARD = "de.togginho.accounting.ui.client.NewClientCommand"; //$NON-NLS-1$
	
	public static final String CMD_EDIT_CLIENT = "de.togginho.accounting.ui.client.EditClientCommand"; //$NON-NLS-1$
		
	public static final String CMD_DELETE_CLIENT = "de.togginho.accounting.ui.client.DeleteClientCommand"; //$NON-NLS-1$
	
	public static final String CMD_NEW_INVOICE = "de.togginho.accounting.ui.invoice.NewInvoiceCommand"; //$NON-NLS-1$
	
	public static final String CMD_EDIT_INVOICE = "de.togginho.accounting.ui.invoice.EditInvoiceCommand"; //$NON-NLS-1$
		
	public static final String CMD_DELETE_INVOICE = "de.togginho.accounting.ui.invoice.DeleteInvoiceCommand"; //$NON-NLS-1$
	
	public static final String CMD_INVOICE_TO_PDF = "de.togginho.accounting.ui.invoice.ExportInvoiceCommand"; //$NON-NLS-1$
	
	public static final String CMD_SEND_INVOICE = "de.togginho.accounting.ui.invoice.SendInvoiceCommand"; //$NON-NLS-1$
	
	public static final String CMD_COPY_INVOICE = "de.togginho.accounting.ui.invoice.CopyInvoiceCommand"; //$NON-NLS-1$
	
	public static final String CMD_MARK_INVOICE_AS_PAID = "de.togginho.accounting.ui.invoice.MarkInvoiceAsPaidCommand"; //$NON-NLS-1$
	
	public static final String CMD_CANCEL_INVOICE = "de.togginho.accounting.ui.invoice.CancelInvoiceCommand"; //$NON-NLS-1$
	
	public static final String CMD_CHANGE_INVOICES_FILTER = "de.togginho.accounting.ui.invoice.ChangeInvoicesFilterCommand"; //$NON-NLS-1$

	public static final String CMD_FIND_INVOICE = "de.togginho.accounting.ui.invoice.FindInvoiceCommand"; //$NON-NLS-1$
	
	public static final String CMD_EDIT_EXPENSE = "de.togginho.accounting.ui.expense.EditExpenseCommand"; //$NON-NLS-1$
}
