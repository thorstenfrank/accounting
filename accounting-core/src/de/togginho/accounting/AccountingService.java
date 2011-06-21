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

import java.util.Set;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.model.User;

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
 */
public interface AccountingService {

	/**
	 * Initialises this service with the supplied context, which contains the necessary minimal information needed to
	 * use this service.
	 * 
	 * <p>This method must be the very first call by a client. Subsequent calls to this method will have no effect.
	 * Until this method finishes processing successfully, all calls to other methods of this service will fail with an
	 * exception.</p>
	 * 
	 * @param context the context to use for init of this service - must <b>NOT</b> be <code>null</code>, and all
	 * 		  properties of the context need to be non-empty
	 * 
	 * @throws AccountingException if this service cannot be properly initialised or if the context or one of it's
	 * 		   properties is <code>null</code>
	 */
	void init(AccountingContext context);
	
	/**
	 * 
	 * @return
	 */
	User getCurrentUser();
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	User saveCurrentUser(User user);
	
	/**
	 * 
	 * @param invoice
	 * @return
	 */
	Invoice saveInvoice(Invoice invoice);
	
	/**
	 * 
	 * @param invoice
	 * @return
	 */
	Invoice sendInvoice(Invoice invoice);
	
	/**
	 * 
	 * @param invoice
	 */
	void deleteInvoice(Invoice invoice);
	
	/**
	 * 
	 * @param invoiceNumber
	 * @return
	 */
	Invoice getInvoice(String invoiceNumber);
	
	/**
	 * 
	 * @param user
	 * @param states
	 * @return
	 */
	Set<Invoice> findInvoices(User user, InvoiceState... states);
}
