/*
 *  Copyright 2010 thorsten frank (thorsten.frank@gmx.de).
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link AccountingContextFactory}.
 * 
 * @author thorsten frank
 *
 */
public class AccountingContextFactoryTest {
	
	private static final String TEST_USER_NAME = "Some User";

	private static final String TEST_DB_FILE_NAME = "SomeFileName";
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link de.togginho.accounting.AccountingContextFactory#buildContext(java.util.Properties)}.
	 */
	@Test
	public void testBuildContext() {
		
		try {
			AccountingContextFactory.buildContext(null, TEST_DB_FILE_NAME);
			fail("No username supplied, should have caught exception");
		} catch (AccountingException e) {
			// TODO use unique error codes?
		}
		
		try {
			AccountingContextFactory.buildContext(TEST_USER_NAME, null);
			fail("No db file name supplied, should have caught exception");
		} catch (AccountingException e) {
			// TODO use unique error codes?
		}
		
		try {
			AccountingContext ctx = AccountingContextFactory.buildContext(TEST_USER_NAME, TEST_DB_FILE_NAME);
			assertEquals(TEST_USER_NAME, ctx.getUserName());
			assertEquals(TEST_DB_FILE_NAME, ctx.getDbFileName());
		} catch (AccountingException e) {
			fail("Got an exception, but supplied all mandatory information: "+e.toString());
		}
	}

}
