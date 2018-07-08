/*
 *  Copyright 2018 Thorsten Frank (accounting@tfsw.de).
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

import java.util.Set;

import de.tfsw.accounting.model.Client;

/**
 * 
 * @author thorsten
 */
public interface ClientService {
	
	Set<String> getClientNames();
	
	/**
	 * Returns all {@link Client} instances.
	 * 
	 * @return all clients known to the system
	 */
	Set<Client> getClients();
	
	/**
	 * Saves the supplied {@link Client} to persistence.
	 * 
	 * <p>This method applies to both new and changed existing instances.</p>
	 * 
	 * @param client the {@link Client} to save
	 * 
	 * @return the saved instance - clients should use this for any further work
	 */
	Client saveClient(Client client);
	
	/**
	 * Removes the supplied client permanently from persistence.
	 * 
	 * <p>This method will throw an {@link AccountingException} if there are saved invoices connected to this cient.</p>
	 * 
	 * @param client the {@link Client} instance to delete
	 */
	void deleteClient(Client client);
}
