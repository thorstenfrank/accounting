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
package de.tfsw.accounting.elster;

/**
 * German states.
 * 
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public enum Bundesland {

	BW("Baden-Württemberg", "28"),
	BAYERN("Bayern", "9"),
	BERLIN("Berlin", "11"),
	BRANDENBURG("Brandenburg", "3"),
	BREMEN("Bremen", "24"),
	HAMBURG("Hamburg", "22"),
	HESSEN("Hessen", "26"),
	MECK_POMM("Mecklenburg-Vorpommern", "4"),
	NIEDERSACHSEN("Niedersachsen", "23"),
	NRW("Nordrhein-Westfalen", "5"),
	RP("Rheinland-Pfalz", "25"),
	SAARLAND("Saarland", "1"),
	SACHSEN("Sachsen", "3"),
	SACHSEN_ANHALT("Sachsen-Anhalt", "3"),
	SCHLESWIG_HOLSTEIN("Schleswig-Holstein", "21"),
	THUERINGEN("Thüringen", "4");
	
	private String officialName;
	private String steuernummerPrefix;

	/**
	 * @param steuernummerPrefix
	 */
	private Bundesland(String officialName, String steuernummerPrefix) {
		this.officialName = officialName;
		this.steuernummerPrefix = steuernummerPrefix;
	}

	/**
	 * @return the officialName
	 */
	public String getOfficialName() {
		return officialName;
	}

	/**
	 * @return the steuernummerPrefix
	 */
	public String getSteuernummerPrefix() {
		return steuernummerPrefix;
	}
	
	/**
	 * 
	 * @param officialName
	 * @return
	 */
	public static Bundesland valueOfOfficialName(String officialName) {
		for (Bundesland bundesland : values()) {
			if (bundesland.getOfficialName().equals(officialName)) {
				return bundesland;
			}
		}
		
		return null;
	}
}
