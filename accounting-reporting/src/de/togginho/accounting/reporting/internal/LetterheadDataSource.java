/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.reporting.internal;

import java.util.Map;

import de.togginho.accounting.reporting.model.LetterheadWrapper;

/**
 * @author thorsten
 *
 */
public class LetterheadDataSource extends AbstractReportDataSource {

	private LetterheadWrapper wrapper;
	
	/**
	 * 
	 */
	protected LetterheadDataSource(LetterheadWrapper wrapper) {
		this.wrapper = wrapper;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.internal.AbstractReportDataSource#addFieldsToMap(java.util.Map)
	 */
	@Override
	protected void addFieldsToMap(Map<String, Object> fieldMap) {
		fieldMap.put("wrapper", wrapper); //$NON-NLS-1$
		fieldMap.put("user.bank.title", Messages.InvoiceDataSource_userBankTitle); //$NON-NLS-1$
		fieldMap.put("user.bank.account.title", Messages.InvoiceDataSource_userBankAccountTitle); //$NON-NLS-1$
		fieldMap.put("user.bank.code.title", Messages.InvoiceDataSource_userBankCodeTitle); //$NON-NLS-1$
		
		// only add BIC and IBAN labels if present in bank account
		if (wrapper.getUserBankBIC() != null && !wrapper.getUserBankBIC().isEmpty()) {
			fieldMap.put("user.bank.bic.title", Messages.InvoiceDataSource_userBankBicTitle); //$NON-NLS-1$
		}
		if (wrapper.getUserBankIBAN() != null && !wrapper.getUserBankIBAN().isEmpty()) {
			fieldMap.put("user.bank.iban.title", Messages.InvoiceDataSource_userBankIbanTitle); //$NON-NLS-1$
		}
	}

}
