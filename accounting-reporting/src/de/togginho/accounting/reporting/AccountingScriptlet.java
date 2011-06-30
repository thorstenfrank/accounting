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
package de.togginho.accounting.reporting;

import org.apache.log4j.Logger;

import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

/**
 * @author tfrank1
 *
 */
public class AccountingScriptlet extends JRAbstractScriptlet {

	private static final Logger LOG = Logger.getLogger(AccountingScriptlet.class);
	
	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#beforeReportInit()
	 */
	@Override
	public void beforeReportInit() throws JRScriptletException {
		LOG.debug("beforeReportInit");
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#afterReportInit()
	 */
	@Override
	public void afterReportInit() throws JRScriptletException {
		LOG.debug("afterReportInit");
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#beforePageInit()
	 */
	@Override
	public void beforePageInit() throws JRScriptletException {
		LOG.debug("beforePageInit");
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#afterPageInit()
	 */
	@Override
	public void afterPageInit() throws JRScriptletException {
		LOG.debug("afterPageInit");
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#beforeDetailEval()
	 */
	@Override
	public void beforeDetailEval() throws JRScriptletException {
		LOG.debug("beforeDetailEval");
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#afterDetailEval()
	 */
	@Override
	public void afterDetailEval() throws JRScriptletException {
		LOG.debug("afterDetailEval");
	}

	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#beforeGroupInit(java.lang.String)
	 */
	@Override
	public void beforeGroupInit(String groupName) throws JRScriptletException {
		LOG.debug("beforeGroupInit: " + groupName);
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#afterGroupInit(java.lang.String)
	 */
	@Override
	public void afterGroupInit(String groupName) throws JRScriptletException {
		LOG.debug("afterGroupInit: " + groupName);
	}

	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#beforeColumnInit()
	 */
	@Override
	public void beforeColumnInit() throws JRScriptletException {
		LOG.debug("beforeColumnInit");
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRAbstractScriptlet#afterColumnInit()
	 */
	@Override
	public void afterColumnInit() throws JRScriptletException {
		LOG.debug("afterColumnInit");
	}
}
