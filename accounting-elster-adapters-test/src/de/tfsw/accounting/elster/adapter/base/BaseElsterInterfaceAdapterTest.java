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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.YearMonth;

import org.junit.Before;
import org.junit.Test;

import de.tfsw.accounting.elster.Bundesland;
import de.tfsw.accounting.elster.ElsterDTO;

/**
 * Tests for {@link BaseElsterInterfaceAdapter}.
 * 
 * @author Thorsten Frank
 *
 */
public class BaseElsterInterfaceAdapterTest {

	private YearMonth filingPeriod;
	
	/**
	 * 
	 */
	private BaseElsterInterfaceAdapter adapter;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		filingPeriod = YearMonth.now().minusMonths(1);
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
		TestDTO dto = new TestDTO();
		UstaAnmeldungssteuernCType usta = adapter.mapToInterfaceModel(dto);
		assertNotNull(usta);
		assertEquals(dto.getCreationDate(), usta.getErstellungsdatum());
		assertEquals("UStVA", usta.getArt());
		assertEquals("201501", usta.getVersion());
		assertNull(usta.getDatenLieferant());
		assertNotNull(usta.getSteuerfall());
		
		UstaSteuerfallCType ust = usta.getSteuerfall();
		assertNull(ust.getBerater());
		assertNull(ust.getDauerfristverlaengerung());
		assertNull(ust.getMandant());
		assertNull(ust.umsatzsteuersondervorauszahlung);
		assertUnternehmer(dto, ust.getUnternehmer());
		assertAmounts(dto, ust.getUmsatzsteuervoranmeldung());
	}

	/**
	 * 
	 * @param dto
	 * @param unternehmer
	 */
	private void assertUnternehmer(ElsterDTO dto, AdresseCType unternehmer) {
		assertNotNull(unternehmer);
		
		assertEquals(dto.getCompanyCity(), unternehmer.getOrt());
		assertEquals(dto.getCompanyCountry(), unternehmer.getLand());
		assertEquals(dto.getCompanyEmail(), unternehmer.getEmail());
		assertEquals(dto.getCompanyPhone(), unternehmer.getTelefon());
		assertEquals(dto.getCompanyPostCode(), unternehmer.getPLZ());
		assertEquals(dto.getCompanyStreetName(), unternehmer.getStr());
		assertEquals(dto.getCompanyStreetNumber(), unternehmer.getHausnummer());
		assertEquals(dto.getUserFirstName(), unternehmer.getVorname());
		assertEquals(dto.getUserLastName(), unternehmer.getName());
		assertNull(unternehmer.getAnschriftenZusatz());
		assertNull(unternehmer.getAuslandsPLZ());
		assertNull(unternehmer.getBezeichnung());
		assertNull(unternehmer.getGKPLZ());
		assertNull(unternehmer.getHNrZusatz());
		assertNull(unternehmer.getNamensvorsatz());
		assertNull(unternehmer.getNamenszusatz());
		assertNull(unternehmer.getPostfach());
		assertNull(unternehmer.getPostfachOrt());
		assertNull(unternehmer.getPostfachPLZ());
	}
	
	/**
	 * 
	 * @param dto
	 * @param ustva
	 */
	private void assertAmounts(ElsterDTO dto, UstaUmsatzsteuervoranmeldungCType ustva) {
		assertNotNull(ustva);
		assertEquals(dto.getTimeFrameYear(), ustva.getJahr());
		assertEquals(dto.getTimeFrameMonth(), ustva.getZeitraum());
		assertEquals(dto.getCompanyTaxNumberGenerated(), ustva.getSteuernummer());
		assertEquals("123.45", ustva.getKz66());
		assertEquals("1000", ustva.getKz81());
		assertEquals("66.55", ustva.getKz83());
		assertUnneededValues(ustva);
	}
	
	/**
	 * 
	 * @param ustva
	 */
	private void assertUnneededValues(UstaUmsatzsteuervoranmeldungCType ustva) {
		assertNull(ustva.getKz09());
		assertNull(ustva.getKz10());
		assertNull(ustva.getKz21());
		assertNull(ustva.getKz22());
		assertNull(ustva.getKz26());
		assertNull(ustva.getKz29());
		assertNull(ustva.getKz35());
		assertNull(ustva.getKz36());
		assertNull(ustva.getKz39());
		assertNull(ustva.getKz41());
		assertNull(ustva.getKz42());
		assertNull(ustva.getKz43());
		assertNull(ustva.getKz44());
		assertNull(ustva.getKz45());
		assertNull(ustva.getKz46());
		assertNull(ustva.getKz47());
		assertNull(ustva.getKz48());
		assertNull(ustva.getKz49());
		assertNull(ustva.getKz52());
		assertNull(ustva.getKz53());
		assertNull(ustva.getKz59());
		assertNull(ustva.getKz60());
		assertNull(ustva.getKz61());
		assertNull(ustva.getKz62());
		assertNull(ustva.getKz63());
		assertNull(ustva.getKz64());
		assertNull(ustva.getKz65());
		assertNull(ustva.getKz67());
		assertNull(ustva.getKz68());
		assertNull(ustva.getKz69());
		assertNull(ustva.getKz73());
		assertNull(ustva.getKz74());
		assertNull(ustva.getKz76());
		assertNull(ustva.getKz77());
		assertNull(ustva.getKz78());
		assertNull(ustva.getKz79());
		assertNull(ustva.getKz80());
		assertNull(ustva.getKz84());
		assertNull(ustva.getKz85());
		assertNull(ustva.getKz86());
		assertNull(ustva.getKz89());
		assertNull(ustva.getKz91());
		assertNull(ustva.getKz93());
		assertNull(ustva.getKz94());
		assertNull(ustva.getKz95());
		assertNull(ustva.getKz96());
		assertNull(ustva.getKz98());		
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

		private TestDTO() {
			super();
			setCreationDate("20150302");
			setCompanyCity("JUnitCity");
			setCompanyCountry("Deutschland");
			setCompanyEmail("me@myself.com");
			setCompanyPhone("+12 3456 7890");
			setCompanyPostCode("12345");
			setCompanyStreetName("JUnitStreetName");
			setCompanyStreetNumber("66");
			setCompanyTaxNumberOrig("11223/44556");
			setFinanzAmtBL(Bundesland.BW);
			setInputTax(new BigDecimal("123.444"));
			setRevenue19(new BigDecimal("1000"));
			setRevenue19tax(new BigDecimal("190"));
			setTaxSum(new BigDecimal("66.55"));
			setTimeFrameMonth("03");
			setTimeFrameYear("2015");
		}
		
		/**
		 * @see de.tfsw.accounting.elster.ElsterDTO#getFilingPeriod()
		 */
		@Override
		public YearMonth getFilingPeriod() {
			return filingPeriod;
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
