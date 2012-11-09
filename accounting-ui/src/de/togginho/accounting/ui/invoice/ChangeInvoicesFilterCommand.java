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
package de.togginho.accounting.ui.invoice;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;

import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.ui.AbstractAccountingHandler;
import de.togginho.accounting.ui.AbstractModalDialog;
import de.togginho.accounting.ui.IDs;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
public class ChangeInvoicesFilterCommand extends AbstractAccountingHandler {

	private static final Logger LOG = Logger.getLogger(ChangeInvoicesFilterCommand.class);
	
	private Set<InvoiceState> currentFilter;
	
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.AbstractAccountingHandler#doExecute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected void doExecute(ExecutionEvent event) throws ExecutionException {
		IViewPart viewPart = getActivePage(event).findView(IDs.VIEW_INVOICES_ID);
		InvoiceView invoiceView = null;
		if (viewPart != null && viewPart instanceof InvoiceView) {
			invoiceView = (InvoiceView) viewPart;
			currentFilter = invoiceView.getInvoiceFilter();
		} else {
			LOG.warn("No active invoice view found! This command should not have been fired..."); //$NON-NLS-1$
			return;
		}
		
		AbstractModalDialog dialog = new AbstractModalDialog(
				getShell(event), 
				Messages.ChangeInvoicesFilterCommand_title, 
				Messages.ChangeInvoicesFilterCommand_message) {
			
			@Override
			protected void createMainContents(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));
				WidgetHelper.grabHorizontal(composite);
				        		
        		for (InvoiceState state : InvoiceState.values()) {
        			if (state.equals(InvoiceState.UNSAVED)) {
        				continue;
        			}
        			Button b = new Button(composite, SWT.CHECK);
        			b.setData(state);
        			b.setText(state.getTranslatedString());
        			WidgetHelper.grabHorizontal(b);
        			if (currentFilter.contains(state)) {
        				b.setSelection(true);
        			}
        			b.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                        	InvoiceState theState = (InvoiceState)((Button)e.getSource()).getData();
                        	if (currentFilter.contains(theState)) {
                        		currentFilter.remove(theState);
                        	} else {
                        		currentFilter.add(theState);
                        	}
                        }
        				
					});
        		}
				
			}
		};
		
		
		if (dialog.show()) {
			StringBuilder sb = new StringBuilder("Changing invoice filter, now contains:"); //$NON-NLS-1$
			for (InvoiceState state : currentFilter) {
				sb.append(" [").append(state.name()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			LOG.info(sb.toString());
			
			invoiceView.modelChanged();
		} else {
			LOG.debug("Change invoice filter was cancelled"); //$NON-NLS-1$
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
