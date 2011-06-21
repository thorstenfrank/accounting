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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.config.ObjectField;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.ext.ExtObjectSet;
import com.db4o.internal.encoding.UTF8StringEncoding;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.User;

/**
 * @author thorsten
 *
 */
public class AccountingServiceImplTest extends BaseTestFixture {

	private ObjectContainer ocMock;
	
	private Object[] initMocks;
	
	private AccountingServiceImpl serviceUnderTest;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File daFile = new File(TEST_DB_FILE);
		if (daFile.exists()) {
			System.out.println("deleting existing test db file");
			daFile.delete();
		} else {
			System.out.println("Using db file: " + daFile.getAbsolutePath());
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File daFile = new File(TEST_DB_FILE);
		if (!daFile.exists()) {
			System.out.println("Fuckit");
		} else {
			daFile.delete();
			System.out.println("Deleting");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Db4oService db4oServiceMock = createMock(Db4oService.class);
		ocMock = createMock(ObjectContainer.class);
		serviceUnderTest = new AccountingServiceImpl(db4oServiceMock);
		
		// init mock behavior
		Configuration configurationMock = createMock(Configuration.class);
		expect(db4oServiceMock.newConfiguration()).andReturn(configurationMock);
		configurationMock.stringEncoding(anyObject(UTF8StringEncoding.class));
		
		ObjectClass userClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(User.class)).andReturn(userClassMock);
		userClassMock.cascadeOnUpdate(true);
		userClassMock.cascadeOnDelete(true);
		ObjectField userNameFieldMock = createMock(ObjectField.class);
		expect(userClassMock.objectField(User.FIELD_NAME)).andReturn(userNameFieldMock);
		userNameFieldMock.indexed(true);
		configurationMock.add(anyObject(UniqueFieldValueConstraint.class)); // TODO check arguments too
		
		ObjectClass clientClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(Client.class)).andReturn(clientClassMock);
		clientClassMock.cascadeOnUpdate(true);
		clientClassMock.cascadeOnDelete(true);
		ObjectField clientNameMock = createMock(ObjectField.class);
		expect(clientClassMock.objectField(Client.FIELD_NAME)).andReturn(clientNameMock);
		clientNameMock.indexed(true);
		configurationMock.add(anyObject(UniqueFieldValueConstraint.class)); // TODO check arguments too
		
		ObjectClass invoiceClassMock = createMock(ObjectClass.class);
		expect(configurationMock.objectClass(Invoice.class)).andReturn(invoiceClassMock);
		ObjectField invoicePositionsMock = createMock(ObjectField.class);
		expect(invoiceClassMock.objectField(Invoice.FIELD_INVOICE_POSITIONS)).andReturn(invoicePositionsMock);
		invoicePositionsMock.cascadeOnUpdate(true);
		invoicePositionsMock.cascadeOnDelete(true);
		ObjectField invoiceNumberMock = createMock(ObjectField.class);
		expect(invoiceClassMock.objectField(Invoice.FIELD_NUMBER)).andReturn(invoiceNumberMock);
		invoiceNumberMock.indexed(true);
		configurationMock.add(anyObject(UniqueFieldValueConstraint.class)); // TODO check arguments too
				
		initMocks = new Object[]{db4oServiceMock, configurationMock, userClassMock, userNameFieldMock, clientClassMock, 
				clientNameMock, invoiceClassMock, invoicePositionsMock, invoiceNumberMock};
		
		expect(db4oServiceMock.openFile(configurationMock, TEST_DB_FILE)).andReturn(ocMock);
		
		replay(initMocks);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		verify(initMocks);
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#init(de.togginho.accounting.AccountingContext)}.
	 */
	@Test
	public void testInit() {
				
		replay(ocMock);
		
		try {
			serviceUnderTest.init(null);
			fail("Initialising the service without a context should have yielded an exception");
		} catch (AccountingException e) {
			// this is what we want
		} catch (Exception e) {
			fail("Wrong type of exception during init");
		}
		
		try {
			serviceUnderTest.init(getTestContext());
		} catch (Exception e) {
			fail("Service should have initialised properly");
		}
		
		// subsequent calls should have absolutely no result
		try {
			serviceUnderTest.init(getTestContext());
		} catch (Exception e) {
			fail("Service should not have done anything!");
		}
	
		verify(ocMock);
	}
	
	/**
	 * Test method for {@link AccountingServiceImpl#shutDown()}.
	 */
	@Test
	public void testShutDown() {
		expect(ocMock.close()).andReturn(true);
		replay(ocMock);
		
		try {
			serviceUnderTest.init(getTestContext());
			serviceUnderTest.shutDown();
		} catch (AccountingException e) {
			fail("Unexpected exception during shutdown");
		}
		
		// subsequent calls to shutDown() shouldn't do anything
		serviceUnderTest.shutDown();
		
		verify(ocMock);
	}
	
	/**
	 * Test method for {@link AccountingService#getCurrentUser()} and {@link AccountingService#saveCurrentUser(User)}.
	 */
	@Test
	public void testUser() {
		expect(ocMock.query(anyObject(FindCurrentUserPredicate.class))).andReturn(new UserObjectSet());
		replay(ocMock);
		
		serviceUnderTest.init(getTestContext());
		
		assertNull(serviceUnderTest.getCurrentUser());
		
//		serviceUnderTest.saveCurrentUser(getTestUser());
//		
//		User fromDB = serviceUnderTest.getCurrentUser();
//		
//		assertEquals(getTestUser(), fromDB);
		
		verify(ocMock);
	}
	
	/**
	 * 
	 */
	private class UserObjectSet extends ArrayList<User> implements ObjectSet<User> {
		@Override
		public ExtObjectSet ext() { return null;}

		@Override
		public boolean hasNext() { return false; }

		@Override
		public User next() { return null; }
		
		@Override
		public void reset() { }
	}
}
