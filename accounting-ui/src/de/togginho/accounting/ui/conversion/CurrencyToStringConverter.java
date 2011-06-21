/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.conversion;

import java.math.BigDecimal;

import org.eclipse.core.databinding.conversion.IConverter;

import de.togginho.accounting.util.FormatUtil;

/**
 * @author tfrank1
 *
 */
public class CurrencyToStringConverter implements IConverter {

	/** */
	private static final CurrencyToStringConverter INSTANCE = new CurrencyToStringConverter();
	
	/**
	 * 
	 * @return
	 */
	public static final CurrencyToStringConverter getInstance() {
		return INSTANCE;
	}
	
	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		return BigDecimal.class;
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		return String.class;
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		if (fromObject != null) {
			BigDecimal bd = (BigDecimal) fromObject;
			String result = FormatUtil.formatCurrency(bd);
			return result;
		}
		return null;
	}

}
