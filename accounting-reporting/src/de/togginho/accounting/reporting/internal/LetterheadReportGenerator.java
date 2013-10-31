/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.reporting.internal;

import java.util.Map;

import de.togginho.accounting.model.User;
import de.togginho.accounting.reporting.model.LetterheadWrapper;

/**
 * @author thorsten
 *
 */
public class LetterheadReportGenerator extends AbstractReportGenerator {

	private User user;
	
	/**
	 * 
	 */
	public LetterheadReportGenerator(User user) {
		this.user = user;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.internal.AbstractReportGenerator#addReportParameters(java.util.Map)
	 */
	@Override
	protected void addReportParameters(Map<String, Object> params) {
		// nothing to do here...
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.internal.AbstractReportGenerator#getReportTemplatePath()
	 */
	@Override
	protected String getReportTemplatePath() {
		return "Letterhead.jasper";
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.reporting.internal.AbstractReportGenerator#getReportDataSource()
	 */
	@Override
	protected AbstractReportDataSource getReportDataSource() {
		return new LetterheadDataSource(new LetterheadWrapper(user));
	}

}
