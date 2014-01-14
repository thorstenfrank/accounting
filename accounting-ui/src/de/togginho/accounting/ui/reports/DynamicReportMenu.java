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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.reports.DynamicReportUtil.DynamicReportHandler;

/**
 * @author thorsten
 *
 */
public class DynamicReportMenu extends CompoundContributionItem implements DynamicReportHandler {

	/**
	 * 
	 */
	private Collection<IContributionItem> items;
	
	/**
	 * 
	 */
	public DynamicReportMenu() {
		
	}

	/**
	 * @param id
	 */
	public DynamicReportMenu(String id) {
		super(id);
	}

	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		items = new ArrayList<IContributionItem>();
		
		DynamicReportUtil.parseReportHandlers(this);
		
		return items.toArray(new IContributionItem[items.size()]);
	}

	/**
     * {@inheritDoc}.
     * @see de.togginho.accounting.ui.reports.DynamicReportUtil.DynamicReportHandler#handleDynamicReport(org.eclipse.core.runtime.IConfigurationElement, org.eclipse.core.commands.Command, int)
     */
    @Override
    public void handleDynamicReport(IConfigurationElement configElement, Command command, int index) {
	    CommandContributionItemParameter params = new CommandContributionItemParameter(
	    		PlatformUI.getWorkbench(), null, command.getId(), CommandContributionItem.STYLE_PUSH);
	    if (configElement.getAttribute(DynamicReportUtil.ICON) != null) {
	    	params.icon = AccountingUI.getImageDescriptor(configElement.getAttribute(DynamicReportUtil.ICON));
        }
	    items.add(new CommandContributionItem(params));
    }

}
