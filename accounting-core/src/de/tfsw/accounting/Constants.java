/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting;

/**
 * Contains mostly simple strings used throughout the application.
 * 
 * <p>Clients are free to use these constants.</p>
 * 
 * @author thorsten
 *
 */
public interface Constants {

	/** An empty string. */
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	/** A blank. */
	public static final String BLANK_STRING = " "; //$NON-NLS-1$
	
	/** A hyphen (-). */
	public static final String HYPHEN = " - "; //$NON-NLS-1$
	
	/** The percentage sign (%). */
	public static final String PERCENTAGE_SIGN = "%"; //$NON-NLS-1$
	
	/** An underscore (_). */
	public static final String UNDERSCORE = "_"; //$NON-NLS-1$
	
	/** A dot (.). */
	public static final String DOT = "."; //$NON-NLS-1$
	
	/** A comma (,). */
	public static final String COMMA = ","; //$NON-NLS-1$
	
	/** A semicolon (;). */
	public static final String SEMICOLON = ";"; //$NON-NLS-1$
	
	/*** An asterisk (*). */
	public static final String ASTERISK = "*"; //$NON-NLS-1$
	
	/** XML file shorthand (*.xml). */
	public static final String XML_FILES = "*.xml"; //$NON-NLS-1$
	
	/** A zero digit character (0). */
	public static final String ZERO = "0"; //$NON-NLS-1$
}
