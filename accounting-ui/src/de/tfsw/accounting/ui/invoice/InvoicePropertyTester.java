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
package de.tfsw.accounting.ui.invoice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.expressions.PropertyTester;

import de.tfsw.accounting.model.Invoice;

/**
 * @author thorsten
 *
 */
public class InvoicePropertyTester extends PropertyTester {

	private static final Logger LOG = LogManager.getLogger(InvoicePropertyTester.class);
	
	private static final String CAN_BE_DELETED = "canBeDeleted"; //$NON-NLS-1$
	
	private static final String CAN_BE_CANCELLED = "canBeCancelled"; //$NON-NLS-1$
	
	private static final String CAN_BE_PAID = "canBePaid"; //$NON-NLS-1$
	
	private static final String CAN_BE_EXPORTED = "canBeExported"; //$NON-NLS-1$
	
	private static final String CAN_BE_SENT = "canBeSent"; //$NON-NLS-1$
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		Invoice invoice = null;
		//LOG.debug("PROPERTY: " + property); //$NON-NLS-1$
		if (receiver instanceof Invoice) {
			invoice = (Invoice) receiver;
		} else if (receiver instanceof InvoiceEditorInput) {
			//LOG.debug("testing from editor input"); //$NON-NLS-1$
			invoice = ((InvoiceEditorInput) receiver).getInvoice();
		} else {
			//LOG.warn("Unknown receiver type: " + receiver.getClass().getName()); //$NON-NLS-1$
			return false;
		}
		
		return doTest(invoice, property);
	}

	/**
	 * 
	 * @param invoice
	 * @param property
	 * @return
	 */
	private boolean doTest(Invoice invoice, String property) {
		boolean result = false;
		
		if (invoice == null) {
			LOG.warn("Invoice was [null]!"); //$NON-NLS-1$
			return result;
		}
		
		if (CAN_BE_DELETED.equals(property)) {
			result = invoice.canBeDeleted();
		} else if (CAN_BE_CANCELLED.equals(property)) {
			result = invoice.canBeCancelled();
		} else if (CAN_BE_PAID.equals(property)) {
			result = invoice.canBePaid();
		} else if (CAN_BE_EXPORTED.equals(property)) {
			result = invoice.canBeExported();
		} else if (CAN_BE_SENT.equals(property)) {
			result = invoice.canBeSent();
		}
		//LOG.debug(String.format("Property [%s] on Invoice [%s] was tested [%s]",  //$NON-NLS-1$
		//		property, invoice.getNumber(), result));
		return result;
	}
}
