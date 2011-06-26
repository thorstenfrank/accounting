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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.InvoiceState;
import de.togginho.accounting.ui.AbstractAccountingHandler;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.ModelHelper;
import de.togginho.accounting.ui.WidgetHelper;

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
		currentFilter = ModelHelper.getInvoiceFilter();
		TitleAreaDialog tad = new TitleAreaDialog(getShell(event)) {
			@Override
			public void create() {
			    super.create();
			    setTitle(Messages.ChangeInvoicesFilterCommand_title);
			    setMessage(Messages.ChangeInvoicesFilterCommand_message);
			}
			
			@Override
			protected Control createDialogArea(Composite parent) {
            	Composite composite = new Composite(parent, SWT.NONE);
            	GridLayout layout = new GridLayout(1, false);
            	layout.marginHeight = 0;
            	layout.marginWidth = 0;
            	
            	composite.setLayout(layout);
            	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
            	
        		final Label topSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        		GridDataFactory.fillDefaults().grab(true, false).applyTo(topSeparator);
        		
        		GridDataFactory gdf = GridDataFactory.fillDefaults().indent(5, 0);
        		        		
        		for (InvoiceState state : InvoiceState.values()) {
        			if (state.equals(InvoiceState.UNSAVED)) {
        				continue;
        			}
        			Button b = new Button(composite, SWT.CHECK);
        			b.setData(state);
        			b.setText(state.getTranslatedString());
        			gdf.applyTo(b);
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
        		
        		final Label fillToBottom = WidgetHelper.createLabel(composite, Constants.EMPTY_STRING);
        		GridDataFactory.fillDefaults().grab(true, true).applyTo(fillToBottom);
        		
        		final Label bottomSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        		GridDataFactory.fillDefaults().grab(true, false).applyTo(bottomSeparator);
        		
        		return composite;
			}
		};
		
		if (tad.open() == IDialogConstants.OK_ID) {
			StringBuilder sb = new StringBuilder("Changing invoice filter, now contains:"); //$NON-NLS-1$
			for (InvoiceState state : currentFilter) {
				sb.append(" [").append(state.name()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			LOG.info(sb.toString());
			
			ModelHelper.setInvoiceFilter(currentFilter);
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
