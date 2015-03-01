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
package de.tfsw.accounting.elster.adapter.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.YearMonth;

import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.elster.ElsterDTO;

/**
 * Tests for {@link BaseElsterInterfaceAdapter}.
 * 
 * @author Thorsten Frank
 *
 */
public class BaseElsterInterfaceAdapterTest {

	/**
	 * 
	 */
	private BaseElsterInterfaceAdapter adapter;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		adapter = new BaseElsterInterfaceAdapter();
	}

	/**
	 * Test method for {@link de.tfsw.accounting.elster.adapter.base.BaseElsterInterfaceAdapter#validFrom()}.
	 */
	@Test
	public void testValidFrom() {
		assertEquals(YearMonth.of(2015, 01), adapter.validFrom());
	}

	/**
	 * Test method for {@link de.tfsw.accounting.elster.adapter.base.BaseElsterInterfaceAdapter#mapToInterfaceModel(de.tfsw.accounting.elster.ElsterDTO)}.
	 */
	@Test
	public void testMapToInterfaceModel() {
		UstaAnmeldungssteuernCType usta = adapter.mapToInterfaceModel(new TestDTO());
		assertNotNull(usta);
	}

	/**
	 * Test method for {@link de.tfsw.accounting.elster.adapter.base.BaseElsterInterfaceAdapter#getVersion()}.
	 */
	@Test
	public void testGetVersion() {
		assertEquals("201501", adapter.getVersion());
	}

	/**
	 * 
	 * @author Thorsten Frank
	 *
	 */
	@SuppressWarnings("serial")
	private class TestDTO extends ElsterDTO {

		/**
		 * @see de.tfsw.accounting.elster.ElsterDTO#getFilingPeriod()
		 */
		@Override
		public YearMonth getFilingPeriod() {
			return YearMonth.now();
		}

		/**
		 * @see de.tfsw.accounting.elster.ElsterDTO#generateTaxNumber()
		 */
		@Override
		protected String generateTaxNumber() {
			return "JUnitTaxNumberGenerated";
		}
		
	}
	
}
