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

import org.apache.log4j.Logger;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.elster.Bundesland;

/**
 * Converts German tax number from their regular format as used on (paper) documents to the XML interface format as 
 * specified in document <i>Prüfung der Steuer- und Identifikationsnummer</i> by ELSTER.
 * 
 * @author Thorsten Frank
 *
 */
class DefaultTaxNumberConverter implements TaxNumberConverter {

	/** Logging instance */
	private static final Logger LOG = Logger.getLogger(DefaultTaxNumberConverter.class);
		
	/**
	 * @see TaxNumberConverter#convertToInterfaceFormat(Bundesland, java.lang.String)
	 */
	@Override
	public String convertToInterfaceFormat(Bundesland blFA, String original) {
		if (original == null) {
			LOG.warn("Supplied tax number was null!"); //$NON-NLS-1$
			return null;
		} else if (blFA == null) {
			LOG.warn("No Bundesland supplied, cannot generate tax number!"); //$NON-NLS-1$
			return null;
		}
		
		LOG.debug("Processing tax number: " + original); //$NON-NLS-1$
		
		String taxNo = original.replaceAll(RegexPatterns.SINGLE_NON_DIGIT, Constants.EMPTY_STRING);
		
		taxNo = stripLeadingZeroesIfNecessary(blFA, taxNo);

		LOG.debug("Done pre-processing, now adapting raw tax number: " + taxNo); //$NON-NLS-1$
		
		if (taxNo.length() == 13) {
			LOG.info("Tax number already seems to have the proper format: " + taxNo); //$NON-NLS-1$
			return taxNo;
		} else if (taxNo.length() < 13) {
			StringBuilder sb = new StringBuilder(blFA.getSteuernummerPrefix());
			for (int i = 0; i < taxNo.length(); i++) {
				if (sb.length() == 4) {
					// fifth digit is a 0 as per spec
					sb.append(Constants.ZERO); //$NON-NLS-1$
				}
				sb.append(taxNo.charAt(i));
			}
			taxNo = sb.toString();
		}
		
		if (taxNo.length() != 13) {
			LOG.warn(String.format(
					"TaxNumber does not have proper length (%d), but %d instead. This might cause problems: %s", //$NON-NLS-1$
					13, taxNo.length(), taxNo));
		} else {
			LOG.info("Finished processing tax number, result: " + taxNo); //$NON-NLS-1$
		}
		
		return taxNo;
	}
	
	/**
	 * 
	 * @param blFA
	 * @param taxNo
	 * @return
	 */
	protected String stripLeadingZeroesIfNecessary(Bundesland blFA, String taxNo) {
		if (blFA == Bundesland.HESSEN && taxNo.startsWith(Constants.ZERO)) {
			LOG.debug("Removing leading zeroes for " + blFA.getOfficialName()); //$NON-NLS-1$
			return taxNo.replaceAll(RegexPatterns.LEADING_ZEROES, Constants.EMPTY_STRING); //$NON-NLS-1$
		}
		return taxNo;
	}
}
