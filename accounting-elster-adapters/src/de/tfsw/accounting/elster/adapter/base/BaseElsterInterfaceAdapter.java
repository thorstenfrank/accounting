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

import java.math.RoundingMode;
import java.time.YearMonth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tfsw.accounting.elster.ElsterDTO;
import de.tfsw.accounting.elster.ElsterInterfaceAdapter;

/**
 * Base implementation of the {@link ElsterInterfaceAdapter} interface valid from January 2015 onwards.
 * 
 * <p>
 * As long as the ELSTER XML interface remains structurally stable, this adapter will be able to create valid messages.
 * </p>
 * 
 * @author Thorsten Frank
 */
public class BaseElsterInterfaceAdapter implements ElsterInterfaceAdapter<UstaAnmeldungssteuernCType> {

	private static final Logger LOG = LogManager.getLogger(BaseElsterInterfaceAdapter.class);
	
	private static final String VERSION_FORMAT = "%04d01";
	
	/**
	 * 
	 */
	private static final YearMonth VALID_FROM = YearMonth.of(2015, 1);
	
	/**
	 * 
	 */
	private static final String TYPE = "UStVA"; //$NON-NLS-1$
	
	/**
	 * This base adapter is valid starting January 2015.
	 * 
	 * @return <code>YearMonth.of(2015, 1)</code>
	 * 
	 * @see de.tfsw.accounting.elster.ElsterInterfaceAdapter#validFrom()
	 */
	@Override
	public YearMonth validFrom() {
		return VALID_FROM;
	}
	
	/**
	 * Returns the version string of this interface adapter, defined in the XML schema as
	 * 
	 * <pre>
	 * &lt;complexType name="usta_AnmeldungssteuernCType">
	 *     ...
	 *     &lt;attribute name="version" use="required">
	 *     ...
	 * </pre> 
	 * 
	 * <p>
	 * This method dynamically builds the version string using the format <pre>yyyymm</pre>. While the format is not
	 * officially documented as such, all previous versions of the ELSTER interface have used it, so until that
	 * changes (or the XSD changes structurally), this adapter will work just fine.
	 * </p>
	 * 
	 * @param dto data source
	 * 
	 * @return the interface version this adapter is currently working under
	 */	
	protected String getVersion(ElsterDTO dto) {
		return String.format(VERSION_FORMAT, dto.getFilingPeriod().getYear());
	}
	
	/**
	 * @see de.tfsw.accounting.elster.ElsterInterfaceAdapter#mapToInterfaceModel(de.tfsw.accounting.elster.ElsterDTO)
	 */
	@Override
	public UstaAnmeldungssteuernCType mapToInterfaceModel(ElsterDTO data) {
		LOG.info("Mapping Elster data [{}] to interface model", data.getFilingPeriod());
		UstaAnmeldungssteuernCType anmeldungssteuern = new UstaAnmeldungssteuernCType();
		anmeldungssteuern.setArt(TYPE);
		anmeldungssteuern.setVersion(getVersion(data));
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
		unternehmer.setPLZ(data.getCompanyPostCode());
		unternehmer.setOrt(data.getCompanyCity());
		unternehmer.setLand(data.getCompanyCountry());
		steuerfall.setUnternehmer(unternehmer);
		
		UstaUmsatzsteuervoranmeldungCType ustva = new UstaUmsatzsteuervoranmeldungCType();
		ustva.setJahr(data.getTimeFrameYear());
		ustva.setZeitraum(data.getTimeFrameMonth());
		ustva.setSteuernummer(data.getCompanyTaxNumberGenerated());
		
		// Revenue amounts don't need to be rounded, as they already are scaled to a zero precision
		
		if (data.getRevenue19() != null) {
			ustva.setKz81(data.getRevenue19().toString());
		}
		
		if (data.getRevenue7() != null) {
			ustva.setKz86(data.getRevenue7().toString());
		}
		
		if (data.getInputTax() != null) {
			ustva.setKz66(data.getInputTax().setScale(2, RoundingMode.UP).toString());
		}
		
		if (data.getTaxSum() != null) {
			ustva.setKz83(data.getTaxSum().setScale(2, RoundingMode.HALF_EVEN).toString());
		}
		
		steuerfall.setUmsatzsteuervoranmeldung(ustva);
		
		return anmeldungssteuern;
	}
}
