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

/**
 * @author Thorsten Frank
 *
 */
final class RegexPatterns {
	/**	*/
	protected static final String SINGLE_NON_DIGIT = "\\D"; //$NON-NLS-1$

	/** */
	protected static final String NON_DIGITS = "\\D+"; //$NON-NLS-1$
	
	/** */
	protected static final String STREET_WITH_ADDENDUM_PATTERN_2 = "\\d+.*"; //$NON-NLS-1$

	/** One or more non-word characters. */
	protected static final String NON_WORD_CHARS = "\\W+"; //$NON-NLS-1$

	/** */
	protected static final String STREET_WITH_ADDENDUM_PATTERN = "\\d+\\W+\\w+"; //$NON-NLS-1$

	/** One ore more number. */
	protected static final String NUMBERS_ONLY_PATTERN = "\\d+"; //$NON-NLS-1$

	/** Padded-zero, 2-digit month formatting pattern. */
	protected static final String MONTH_FORMAT_PATTERN = "%02d"; //$NON-NLS-1$
	
	/** One or more leading zeroes. */
	protected static final String LEADING_ZEROES = "^[0]+"; //$NON-NLS-1$
	
	/**
	 * No need to be instantiated.
	 */
	private RegexPatterns() {
		
	}
}
