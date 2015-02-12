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

import org.apache.log4j.Logger;

import de.tfsw.accounting.Constants;
import de.tfsw.accounting.elster.Bundesland;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
class DefaultTaxNumberGenerationStrategy implements TaxNumberGenerationStrategy {

	private static final Logger LOG = Logger.getLogger(DefaultTaxNumberGenerationStrategy.class);
		
	/**
	 * @see de.tfsw.accounting.elster.adapter.TaxNumberGenerationStrategy#generateTaxNumber(de.tfsw.accounting.elster.Bundesland, java.lang.String)
	 */
	@Override
	public String generateTaxNumber(Bundesland blFA, String original) {
		if (original == null) {
			LOG.warn("Supplied tax number was null!");
			return null;
		} else if (blFA == null) {
			LOG.warn("No Bundesland supplied, cannot generate tax number!");
			return null;
		}
		
		LOG.debug("Processing tax number: " + original);
		
		String taxNo = original.replaceAll("\\D", Constants.EMPTY_STRING);
		LOG.debug("Removed all non-digits: " + taxNo);
		
		if (taxNo.length() == 13) {
			LOG.info("Tax number already seems to have the proper format: " + taxNo);
			return taxNo;
		} else if (taxNo.length() < 13) {
			LOG.info("Adding local information and \'0\'");
			StringBuilder sb = new StringBuilder(blFA.getSteuernummerPrefix());
			sb.append(taxNo.substring(0, 2));
			sb.append("0"); // 5. digit is 0 as per spec / by definition
			sb.append(taxNo.substring(2));
			taxNo = sb.toString();
		}
		
		if (taxNo.length() != 13) {
			LOG.warn(String.format("TaxNumber does not have proper length (%d), but %d instead. This might cause problems: %s", 13, taxNo.length(), taxNo));
		} else {
			LOG.info("Finished processing tax number, result: " + taxNo);
		}
		
		return taxNo;
	}
}
