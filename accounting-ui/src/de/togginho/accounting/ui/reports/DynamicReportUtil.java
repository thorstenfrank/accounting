/*
 *  Copyright 2014 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.reports;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import de.togginho.accounting.ui.IDs;

/**
 * @author thorsten
 *
 */
public class DynamicReportUtil {

	private static final Logger LOG = Logger.getLogger(DynamicReportUtil.class);
	
	public static final String COMMAND_ID = "commandId"; //$NON-NLS-1$
	
	public static final String REPORT_ID = "reportId"; //$NON-NLS-1$
	
	public static final String LABEL = "label"; //$NON-NLS-1$
	
	public static final String ICON = "icon"; //$NON-NLS-1$
	
	/**
	 * 
	 * @param handler
	 */
	public static void parseReportHandlers(DynamicReportHandler handler) {
		LOG.info("Searching for report handler implementations"); //$NON-NLS-1$
		
    	final IConfigurationElement[] elements = 
    			Platform.getExtensionRegistry().getConfigurationElementsFor(IDs.EXTENSION_POINT_REPORT_HANDLERS);
		
    	LOG.info(String.format("Found %d reports", elements.length)); //$NON-NLS-1$
		
		final ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		@SuppressWarnings("unchecked")
        final Collection<String> definedCommandIds = commandService.getDefinedCommandIds();
		
    	for (int i = 0; i < elements.length; i++) {
    		String commandId = elements[i].getAttribute(COMMAND_ID);
    		if (definedCommandIds.contains(commandId)) {
    			LOG.debug("Found defined command: " + commandId); //$NON-NLS-1$
    			
    			handler.handleDynamicReport(elements[i], commandService.getCommand(commandId), i);
    		} else {
    			LOG.warn("Undefined command ID, will skip: " + commandId); //$NON-NLS-1$
    		}
    	}
	}
	
	/**
	 * 
	 * @author thorsten
	 *
	 */
	public interface DynamicReportHandler {
		
		/**
		 * 
		 * @param configElement
		 * @param command
		 * @param index
		 */
		void handleDynamicReport(IConfigurationElement configElement, Command command, int index);
	}
}
