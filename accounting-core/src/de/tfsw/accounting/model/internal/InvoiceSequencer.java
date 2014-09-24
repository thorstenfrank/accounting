/*
 *  Copyright 2012 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.model.internal;

import java.io.Serializable;

/**
 * @author thorsten
 *
 */
public class InvoiceSequencer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5138920134119398843L;

	private int year;
	
	private int currentSequenceNumber;

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the currentSequenceNumber
	 */
	public int getCurrentSequenceNumber() {
		return currentSequenceNumber;
	}

	/**
	 * @param currentSequenceNumber the currentSequenceNumber to set
	 */
	public void setCurrentSequenceNumber(int currentSequenceNumber) {
		this.currentSequenceNumber = currentSequenceNumber;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("InvoiceSequencer | Year: ");
		sb.append(year);
		sb.append(" | SequenceNumber: ").append(currentSequenceNumber);
		return sb.toString();
	}
}
