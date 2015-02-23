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

import de.tfsw.accounting.elster.Bundesland;

/**
 * Tax number conversion refers to the adaption of a person's or company's German tax number for the purpose of
 * transmitting it electronically. 
 * <p>
 * Length and format of the tax number as used on documents and for human interaction varies depending on a person's or 
 * company's local branch of the German revenue service. But for electronic communication, a unified format is used. 
 * </p>
 * 
 * <p>
 * Implementations of this interface are responsible for converting a human-reabable German tax number into a machine
 * one as defined by document <i>Prüfung der Steuer- und Identifikationsnummer</i>.
 * </p>
 * 
 * @author Thorsten Frank
 */
public interface TaxNumberConverter {

	/**
	 * Converts the supplied tax number as used on official documents and human interaction into the format used for
	 * machine-to-machine communication as specified by the German revenue service.
	 * 
	 * @param blFA     (Bundesland des Finanzamts) German local state where the revenue service branch is located
	 * @param original the regular tax number to convert
	 * @return the converted tax number for use in interface messages
	 */
	String convertToInterfaceFormat(Bundesland blFA, String original);
}
