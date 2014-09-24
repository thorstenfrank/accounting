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
package de.tfsw.accounting.ui.conversion;

import java.math.BigDecimal;

import org.eclipse.core.databinding.conversion.IConverter;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.util.FormatUtil;


/**
 * @author tfrank1
 *
 */
public class StringToBigDecimalConverter implements IConverter {
	
	/** */
	private static final StringToBigDecimalConverter INSTANCE = new StringToBigDecimalConverter();
	
	/**
	 * 
	 * @return
	 */
	public static final StringToBigDecimalConverter getInstance() {
		return INSTANCE;
	}
	
	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		return String.class;
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		return BigDecimal.class;
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		if (fromObject != null) {
			String theString = (String) fromObject;
			if (!theString.isEmpty()) {
				
				BigDecimal result = null;
				try {
					result = FormatUtil.parseDecimalValue(theString);
				} catch (AccountingException e) {
					result = BigDecimal.ZERO;
				}
				return result;
			}
		}
		return null;
	}

}
