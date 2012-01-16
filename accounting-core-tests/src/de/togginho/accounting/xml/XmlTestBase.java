/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import de.togginho.accounting.BaseTestFixture;
import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.BankAccount;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.PaymentTerms;
import de.togginho.accounting.xml.generated.XmlAddress;
import de.togginho.accounting.xml.generated.XmlBankAccount;
import de.togginho.accounting.xml.generated.XmlClient;
import de.togginho.accounting.xml.generated.XmlPaymentTerms;
import de.togginho.accounting.xml.generated.XmlPaymentType;

/**
 * @author thorsten
 *
 */
class XmlTestBase extends BaseTestFixture {
	
	/**
	 * 
	 * @param address
	 * @param xmlAddress
	 */
	protected void assertAddressesSame(Address address, XmlAddress xmlAddress) {
		if (address == null) {
			assertNull(xmlAddress);
		} else {
			assertNotNull(xmlAddress);
		}
		assertEquals(address.getCity(), xmlAddress.getCity());
		assertEquals(address.getEmail(), xmlAddress.getEmail());
		assertEquals(address.getFaxNumber(), xmlAddress.getFax());
		assertEquals(address.getMobileNumber(), xmlAddress.getMobile());
		assertEquals(address.getPhoneNumber(), xmlAddress.getPhone());
		assertEquals(address.getPostalCode(), xmlAddress.getPostalCode());
		assertEquals(address.getStreet(), xmlAddress.getStreet());
	}
	
	/**
	 * 
	 * @param account
	 * @param xmlAccount
	 */
	protected void assertBankAccountSame(BankAccount account, XmlBankAccount xmlAccount) {
		if (account == null) {
			assertNull(xmlAccount);
		} else {
			assertNotNull(xmlAccount);
		}
		assertEquals(account.getAccountNumber(), xmlAccount.getAccountNumber());
		assertEquals(account.getBankCode(), xmlAccount.getBankCode());
		assertEquals(account.getBankName(), xmlAccount.getBankName());
		assertEquals(account.getBic(), xmlAccount.getBic());
		assertEquals(account.getIban(), xmlAccount.getIban());
	}
	
	/**
	 * 
	 * @param client
	 * @param xmlClient
	 */
	protected void assertClientsSame(Client client, XmlClient xmlClient) {
		if (client == null) {
			assertNull(xmlClient);
		} else {
			assertNotNull(xmlClient);
		}
		
		assertEquals(client.getClientNumber(), xmlClient.getClientNumber());
		assertEquals(client.getName(), xmlClient.getName());
		assertAddressesSame(client.getAddress(), xmlClient.getAddress());
		assertPaymentTermsSame(client.getDefaultPaymentTerms(), xmlClient.getDefaultPaymentTerms());
	}
	
	/**
	 * 
	 * @param terms
	 * @param xmlTerms
	 */
	protected void assertPaymentTermsSame(PaymentTerms terms, XmlPaymentTerms xmlTerms) {
		if (terms == null) {
			assertNull(xmlTerms);
		} else {
			assertNotNull(xmlTerms);
		}
		
		switch (terms.getPaymentType()) {
		case TRADE_CREDIT:
			assertEquals(XmlPaymentType.NET, xmlTerms.getType());
			break;
		default:
			break;
		}
		
		assertEquals(terms.getFullPaymentTargetInDays(), xmlTerms.getFullPaymentTargetInDays());
	}
}
