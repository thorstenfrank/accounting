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
package de.tfsw.accounting.elster.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tfsw.accounting.elster.ElsterService;

/**
 * @author Thorsten Frank
 *
 */
public class ElsterServiceProvider {

	private static final Logger LOG = LogManager.getLogger(ElsterServiceProvider.class);
	
	private ElsterService elsterService;
		
	/**
	 * @return the elsterService
	 */
	public ElsterService getElsterService() {
		return elsterService;
	}

	/**
	 * OSGi DS bind method.
	 * 
	 * @param elsterService
	 */
	public synchronized void setElsterService(ElsterService elsterService) {
		LOG.info("ElsterService was bound"); //$NON-NLS-1$
		this.elsterService = elsterService;
		ElsterUI.getDefault().registerElsterServiceProvider(this);
	}
	
	/**
	 * OSGi DS unbind method.
	 * 
	 * @param elsterService
	 */
	public synchronized void unsetElsterService(ElsterService elsterService) {
		if (this.elsterService == elsterService) {
			LOG.info("ElsterService was unbound"); //$NON-NLS-1$
			this.elsterService = null;
			ElsterUI.getDefault().unregisterElsterServiceProvider(this);
		}
	}
}
