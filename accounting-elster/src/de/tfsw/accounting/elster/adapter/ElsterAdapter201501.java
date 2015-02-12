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

import de.tfsw.accounting.elster.adapter.usta_201501.AdresseCType;
import de.tfsw.accounting.elster.adapter.usta_201501.UstaAnmeldungssteuernCType;
import de.tfsw.accounting.elster.adapter.usta_201501.UstaSteuerfallCType;
import de.tfsw.accounting.elster.adapter.usta_201501.UstaUmsatzsteuervoranmeldungCType;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class ElsterAdapter201501 extends AbstractElsterAdapter<UstaAnmeldungssteuernCType> {
	
	/**
	 * 
	 */
	private static final String VERSION = "201501";
	
	/**
	 * 
	 */
	private static final String TYPE = "UStVA";
	
	/**
	 * @see de.tfsw.accounting.elster.adapter.AbstractElsterAdapter#mapToInterfaceModel()
	 */
	@Override
	protected UstaAnmeldungssteuernCType mapToInterfaceModel() {
		ElsterDTO data = getData();
		
		UstaAnmeldungssteuernCType anmeldungssteuern = new UstaAnmeldungssteuernCType();
		anmeldungssteuern.setArt(getType());
		anmeldungssteuern.setVersion(getVersion());
		anmeldungssteuern.setErstellungsdatum(data.getCreationDate());
		
		UstaSteuerfallCType steuerfall = new UstaSteuerfallCType();
		anmeldungssteuern.setSteuerfall(steuerfall);
		
		AdresseCType unternehmer = new AdresseCType();
		unternehmer.setBezeichnung(data.getCompanyName());
		unternehmer.setName(data.getUserLastName());
		unternehmer.setVorname(data.getUserFirstName());
		unternehmer.setEmail(data.getCompanyEmail());
		unternehmer.setTelefon(data.getCompanyPhone());
		unternehmer.setStr(data.getCompanyStreetName());
		unternehmer.setHausnummer(data.getCompanyStreetNumber());
		unternehmer.setHNrZusatz(data.getCompanyStreetAddendum());
		steuerfall.setUnternehmer(unternehmer);
		
		UstaUmsatzsteuervoranmeldungCType ustva = new UstaUmsatzsteuervoranmeldungCType();
		ustva.setJahr(data.getTimeFrameYear());
		ustva.setZeitraum(data.getTimeFrameMonth());
		ustva.setSteuernummer(data.getCompanyTaxNumberGenerated());
		ustva.setKz81(data.getRevenue19());
		ustva.setKz86(data.getRevenue7());
		ustva.setKz66(data.getInputTax());
		ustva.setKz83(data.getTaxSum());
		steuerfall.setUmsatzsteuervoranmeldung(ustva);
		
		return anmeldungssteuern;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getVersion() {
		return VERSION;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getType() {
		return TYPE;
	}
}
