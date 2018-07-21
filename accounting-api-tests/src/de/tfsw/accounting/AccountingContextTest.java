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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author thorsten
 *
 */
public class AccountingContextTest {

	@Test
	public void testHappyPath() {
		AccountingContext context = new AccountingContext("some name", "/foo/bar/baz.xyz");
		assertEquals("some name", context.getUserName());
		assertEquals("/foo/bar/baz.xyz", context.getDbLocation());
	}
	
	@Test(expected = AccountingException.class)
	public void testUserNameNull() {
		new AccountingContext(null, "some_file");
	}
	
	@Test(expected = AccountingException.class)
	public void testUserNameEmpty() {
		new AccountingContext("", "some_file");
	}
	
	@Test(expected = AccountingException.class)
	public void testDbFileNameNull() {
		new AccountingContext("some name", null);
	}
	
	@Test(expected = AccountingException.class)
	public void testDbFileNameEmpty() {
		new AccountingContext("some name", "");
	}
}
