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
package de.togginho.accounting.ui.wizard;

import java.math.BigDecimal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.togginho.accounting.Constants;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.WidgetHelper;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
class TaxRateWizardPage extends WizardPage implements KeyListener, Constants {

	private Text abbreviation;
	private Text longName;
	private Text rate;
	
	TaxRateWizardPage() {
		super("TaxRateWizardPage"); //$NON-NLS-1$
		setTitle(Messages.NewTaxRateWizard_pageTitle);
		setDescription(Messages.NewTaxRateWizard_description);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, NewTaxRateWizard.HELP_CONTEXT_ID);
		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		
		GridDataFactory grabHorizontal = GridDataFactory.fillDefaults().grab(true, false);
		
		WidgetHelper.createLabel(composite, Messages.NewTaxRateWizard_abbreviation);
		abbreviation = new Text(composite, SWT.SINGLE | SWT.BORDER);
		grabHorizontal.applyTo(abbreviation);
		abbreviation.addKeyListener(this);
		WidgetHelper.createLabel(composite, EMPTY_STRING); // filler widget
		
		WidgetHelper.createLabel(composite, Messages.labelName);
		longName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		grabHorizontal.applyTo(longName);
		longName.addKeyListener(this);
		WidgetHelper.createLabel(composite, EMPTY_STRING); // filler widget
		
		WidgetHelper.createLabel(composite, Messages.NewTaxRateWizard_rate);
		Composite rateComposite = new Composite(composite, SWT.NULL);
		rateComposite.setLayout(new FillLayout());
		rate = new Text(rateComposite, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
		rate.addKeyListener(this);
		WidgetHelper.createLabel(rateComposite, PERCENTAGE_SIGN);
		
		setControl(composite);
		setPageComplete(false);
	}

	/**
	 * 
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.dialogs.DialogPage#performHelp()
	 */
	@Override
	public void performHelp() {
	    PlatformUI.getWorkbench().getHelpSystem().displayHelp(NewTaxRateWizard.HELP_CONTEXT_ID);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// nothing to do here
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (abbreviation.getText().isEmpty() || longName.getText().isEmpty() || rate.getText().isEmpty()) {
			setPageComplete(false);
		} else {
			setPageComplete(true);
		}
	}

	/**
	 * 
	 * @return
	 */
	protected String getAbbreviation() {
		return abbreviation.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getLongName() {
		return longName.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	protected BigDecimal getRate() {
		final String fullString = rate.getText() + PERCENTAGE_SIGN;
		return FormatUtil.parsePercentValue(fullString);
	}
}
