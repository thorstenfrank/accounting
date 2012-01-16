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

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.User;

/**
 * @author thorsten
 *
 */
public abstract class BaseTestFixture {

	protected static final String TEST_USER_NAME = "JUnitTestUser";
	
	protected static final String TEST_DB_FILE = "JUnitTestDbFile";
	
	private static User testUser;
	
	private static Client testClient;
	
	private static AccountingContext testContext;

	/**
	 * @return the testContext
	 */
	@SuppressWarnings("serial")
	protected static AccountingContext getTestContext() {
		if (testContext == null) {
			testContext = new AccountingContext() {
				@Override
				public String getUserName() { return TEST_USER_NAME;}
				
				@Override
				public String getDbFileName() { return TEST_DB_FILE;}
			};			
		}
		return testContext;
	}
	
	/**
	 * 
	 * @return test user instance
	 */
	protected static User getTestUser() {
		if (testUser == null) {
			testUser = new User();
			testUser.setName(TEST_USER_NAME);
		}
		return testUser;
	}
	
	/**
	 * 
	 * @return the test client instance
	 */
	protected static Client getTestClient() {
		if (testClient == null) {
			testClient = new Client();
			testClient.setName("JUnitTestClientName");
		}
		return testClient;
	}
}
