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
package de.togginho.accounting.ui.invoice;

import org.apache.log4j.Logger;
import org.eclipse.core.expressions.PropertyTester;

import de.togginho.accounting.model.Invoice;

/**
 * @author thorsten
 *
 */
public class InvoicePropertyTester extends PropertyTester {

	private static final Logger LOG = Logger.getLogger(InvoicePropertyTester.class);
	
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
		Invoice invoice = (Invoice) receiver;
		boolean result = false;
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
	
		LOG.debug(String.format("Property [%s] on Invoice [%s] was tested [%s]", property, invoice.getNumber(), result)); //$NON-NLS-1$
		return result;
	}

}
