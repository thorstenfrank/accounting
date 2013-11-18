/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.util;

import de.togginho.accounting.Messages;

/**
 * @author thorsten
 *
 */
public enum TimeFrameType {

	CUSTOM(Messages.TimeFrameType_custom), 
	CURRENT_MONTH(Messages.TimeFrameType_currentMonth),
	CURRENT_YEAR(Messages.TimeFrameType_currentYear),
	LAST_MONTH(Messages.TimeFrameType_lastMonth),
	LAST_YEAR(Messages.TimeFrameType_lastYear),
	SINGLE_MONTH(Messages.TimeFrameType_month),
	WHOLE_YEAR(Messages.TimeFrameType_wholeYear);
	
	private String translated;

	/**
     * @param translated
     */
    private TimeFrameType(String translated) {
	    this.translated = translated;
    }

	/**
     * @return the translated name of this time frame type
     */
    public String getTranslatedName() {
    	return translated;
    }
}
