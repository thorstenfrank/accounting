/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.elster.adapter;

import org.eclipse.core.runtime.IExtensionRegistry;

import de.tfsw.accounting.AccountingService;

/**
 * Implementations of this interface provide necessary services for the {@link ElsterAdapterFactory}.
 * 
 * @author Thorsten Frank
 *
 * @see ElsterAdapterFactory
 */
public interface ServiceProvider {

	/**
	 * Supplies access to the eclipse extension mechanism.
	 * 
	 * @return an {@link IExtensionRegistry} for lookup of defined extensions
	 */
	IExtensionRegistry getExtensionRegistry();
	
	/**
	 * Supplies an active instance of the {@link AccountingService}.
	 * 
	 * @return an {@link AccountingService} instance
	 */
	AccountingService getAccountingService();
}
