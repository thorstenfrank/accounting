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
package de.tfsw.accounting.elster.internal;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;

import org.junit.Test;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.elster.ElsterDTO;
import de.tfsw.accounting.elster.ElsterInterfaceAdapter;
import de.tfsw.accounting.elster.ElsterService;

/**
 * Tests for {@link ElsterServiceImpl}.
 * 
 * @author Thorsten Frank
 *
 */
public class ElsterServiceTest {

	/**
	 * 
	 */
	@Test
	public void testRequestedPeriodIsNull() {
		ElsterService service = new ElsterServiceImpl();
		@SuppressWarnings("serial")
		ElsterDTO dto = new ElsterDTO() {
			
			@Override
			public YearMonth getFilingPeriod() {
				return null;
			}
			
			@Override
			protected String generateTaxNumber() {
				return null;
			}
		};
		
		assertTrue(service.generateXML(dto).startsWith("de.tfsw.accounting.AccountingException"));
		
		try {
			service.writeToXmlFile(dto, null);
			fail("AccountingException should have been thrown");
		} catch (Exception e) {
			assertTrue("Exception should have been AccountingException", e instanceof AccountingException);
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAdapterSetupValidFromIsNull() {
		AccountingService asMock = createMock(AccountingService.class);
		ElsterInterfaceAdapter<SampleJaxbModel> adapterMock = createMock(ElsterInterfaceAdapter.class);
		expect(adapterMock.validFrom()).andReturn(null);
		replay(asMock, adapterMock);
		ElsterServiceImpl service = new ElsterServiceImpl(asMock, Collections.singleton(adapterMock));
		verify(asMock, adapterMock);
		assertTrue(service.getAvailableYears().length == 0);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAdapterSetupFillingYears() {
		Year now = Year.now();
		Year then = now.minusYears(5);
		
		AccountingService asMock = createMock(AccountingService.class);
		ElsterInterfaceAdapter<SampleJaxbModel> adapterMock = createMock(ElsterInterfaceAdapter.class);
		expect(adapterMock.validFrom()).andReturn(YearMonth.of(then.getValue(), Month.JULY.getValue()));
		replay(asMock, adapterMock);
		ElsterServiceImpl service = new ElsterServiceImpl(asMock, Collections.singleton(adapterMock));
		verify(asMock, adapterMock);
		
		Year[] available = service.getAvailableYears();
		
		assertEquals(6, available.length);
		assertEquals(then, available[0]);
		assertEquals(now, available[5]);
	}
}
