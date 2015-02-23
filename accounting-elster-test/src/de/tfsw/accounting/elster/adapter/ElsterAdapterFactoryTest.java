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

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class ElsterAdapterFactoryTest {

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
	 * Test method for {@link de.tfsw.accounting.elster.adapter.ElsterAdapterFactory#ElsterAdapterFactory(de.tfsw.accounting.elster.adapter.ServiceProvider)}.
	 */
	@Test
	public void testElsterAdapterFactory() {
		ServiceProvider spMock = createMock(ServiceProvider.class);
		IExtensionRegistry erMock = createMock(IExtensionRegistry.class);
		
		expect(spMock.getExtensionRegistry()).andReturn(erMock);
		expect(erMock.getConfigurationElementsFor("de.tfsw.accounting.elster.elsterApdapter")).andReturn(new IConfigurationElement[0]);
		
		replay(spMock, erMock);
		
		new ElsterAdapterFactory(spMock);
		
		verify(spMock, erMock);
	}

}
