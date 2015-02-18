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
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class AccountingServiceConsumer {

	private AccountingService accountingService;

	/**
	 * @return the accountingService
	 */
	public AccountingService getAccountingService() {
		return accountingService;
	}

	/**
	 * @param accountingService the accountingService to set
	 */
	public synchronized void setAccountingService(AccountingService accountingService) {
		this.accountingService = accountingService;
		AccountingElsterPlugin.registerAccountingServiceConsumer(this);
	}
	
	/**
	 * @param accountingService the accountingService to unset
	 */
	public synchronized void unsetAccountingService(AccountingService accountingService) {
		AccountingElsterPlugin.unregisterAccountingServiceConsumer(this);
		this.accountingService = null;
	}
}
