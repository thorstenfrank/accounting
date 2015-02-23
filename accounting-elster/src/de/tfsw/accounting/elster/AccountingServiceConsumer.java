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
package de.tfsw.accounting.elster;

import de.tfsw.accounting.AccountingService;

/**
 * Simplistic service consumer intended for the OSGi declarative services framework as a recipient of an
 * {@link AccountingService} instance. Upon receiving said instance, the consumer will register with the 
 * {@link AccountingElsterPlugin}.
 * 
 * <p>Note that instances of this class will <b>not</b> keep a reference to the {@link AccountingService} it receives
 * through {@link #setAccountingService(AccountingService)}, but instead will simply pass on the service instance to
 * the plugin.
 * </p> 
 *  
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class AccountingServiceConsumer {
	
	/**
	 * OSGi declarative services bind method.
	 * 
	 * @param accountingService the accountingService to set
	 */
	public synchronized void setAccountingService(AccountingService accountingService) {
		AccountingElsterPlugin.getDefault().registerAccountingService(accountingService);
	}
	
	/**
	 * OSGi declarative services unbind method.
	 * 
	 * @param accountingService the accountingService to unset
	 */
	public synchronized void unsetAccountingService(AccountingService accountingService) {
		AccountingElsterPlugin.getDefault().unregisterAccountingService(accountingService);
	}
}
