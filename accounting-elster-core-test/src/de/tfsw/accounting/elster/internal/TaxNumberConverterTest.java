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

import org.junit.Test;

import de.tfsw.accounting.elster.Bundesland;

/**
 * Abstract base test - all implementations should subclass their unit test from this class and supply their respective
 * converter implementation.
 * 
 * @author Thorsten Frank
 */
public abstract class TaxNumberConverterTest {
	
	/**
	 * Return the concrete converter implementation to test.
	 * 
	 * @return converter impl to test
	 */
	protected abstract TaxNumberConverter getConverterToTest();
	
	/**
	 * Simple conversion tests using values taken directly from the ELSTER spec.
	 * 
	 * Test method for {@link TaxNumberConverter#convertToInterfaceFormat(de.tfsw.accounting.elster.Bundesland, java.lang.String)}.
	 */
	@Test
	public void testConvertToInterfaceFormat() {		
		TaxNumberConverter converter = getConverterToTest();
		
		// these numbers are taken directly from the ELSTER spec...
		
		assertEquals("2866081508156", converter.convertToInterfaceFormat(Bundesland.BW, "66815/08156"));
		assertEquals("9198081508152", converter.convertToInterfaceFormat(Bundesland.BAYERN, "198/815/08152"));
		assertEquals("9296081508153", converter.convertToInterfaceFormat(Bundesland.BAYERN, "296/815/08153"));
		assertEquals("1197081508154", converter.convertToInterfaceFormat(Bundesland.BERLIN, "97/815/08154"));
		assertEquals("3098081508157", converter.convertToInterfaceFormat(Bundesland.BRANDENBURG, "098/815/08157"));
		assertEquals("2497012301233", converter.convertToInterfaceFormat(Bundesland.BREMEN, "97 123 01233"));
		assertEquals("2241081508154", converter.convertToInterfaceFormat(Bundesland.HAMBURG, "41/815/08154"));
		assertEquals("2653081508158", converter.convertToInterfaceFormat(Bundesland.HESSEN, "053 815 08158"));
		assertEquals("4098081508157", converter.convertToInterfaceFormat(Bundesland.MECK_POMM, "098/815/08157"));
		assertEquals("2388081508158", converter.convertToInterfaceFormat(Bundesland.NIEDERSACHSEN, "88/815/08158"));
		assertEquals("5400081508159", converter.convertToInterfaceFormat(Bundesland.NRW, "400/8150/8159"));
		assertEquals("5500081508151", converter.convertToInterfaceFormat(Bundesland.NRW, "500/8150/8151"));
		assertEquals("5600081508154", converter.convertToInterfaceFormat(Bundesland.NRW, "600/8150/8154"));
		assertEquals("2799081508152", converter.convertToInterfaceFormat(Bundesland.RP, "99/815/08152"));
		assertEquals("1096081508187", converter.convertToInterfaceFormat(Bundesland.SAARLAND, "096/815/08187"));
		assertEquals("3248081508156", converter.convertToInterfaceFormat(Bundesland.SACHSEN, "248/815/08156"));
		assertEquals("3198081508152", converter.convertToInterfaceFormat(Bundesland.SACHSEN_ANHALT, "198/815/08152"));
		assertEquals("2138081508154", converter.convertToInterfaceFormat(Bundesland.SCHLESWIG_HOLSTEIN, "38/815/08154"));
		assertEquals("4198081508152", converter.convertToInterfaceFormat(Bundesland.THUERINGEN, "198/815/08152"));
	}

	/**
	 * Tests behaviour when supplying <code>null</code> arguments.
	 * 
	 * Test method for {@link TaxNumberConverter#convertToInterfaceFormat(de.tfsw.accounting.elster.Bundesland, java.lang.String)}.
	 */
	@Test
	public void testConvertInvalidArguments() {
		TaxNumberConverter converter = getConverterToTest();
		assertNull(converter.convertToInterfaceFormat(null, null));
		assertNull(converter.convertToInterfaceFormat(Bundesland.BAYERN, null));
		assertNull(converter.convertToInterfaceFormat(null, "123"));
	}
}
