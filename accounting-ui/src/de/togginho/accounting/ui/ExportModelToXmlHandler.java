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
package de.togginho.accounting.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author thorsten
 *
 */
public class ExportModelToXmlHandler extends AbstractAccountingHandler {

	private static final Logger LOG = Logger.getLogger(ExportModelToXmlHandler.class);
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		FileDialog fd = new FileDialog(getShell(event), SWT.SAVE);
		fd.setFilterExtensions(new String[]{"*.xml"}); //$NON-NLS-1$
		fd.setFilterNames(new String[]{"XML files"}); //$NON-NLS-1$
		
		final String targetFileName = fd.open();
		
		if (targetFileName == null) {
			getLogger().info("Exporting cancelled by user"); //$NON-NLS-1$
		} else {
			AccountingUI.getAccountingService().exportModelToXml(targetFileName);
		}
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

}
