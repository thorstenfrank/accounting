/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.togginho.accounting.AccountingService;
import de.togginho.accounting.Constants;
import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.model.Revenue;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.util.FormatUtil;
import de.togginho.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class ProfitDialog extends AbstractReportDialog {

	private FormToolkit formToolkit;
	
	private Text revenueNet;
	private Text revenueTax;
	private Text revenueGross;
	
	private Text expensesNet;
	private Text expensesTax;
	private Text expensesGross;
	
	/**
     * @param shell
     */
    protected ProfitDialog(Shell shell) {
	    super(shell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        formToolkit = new FormToolkit(parent.getDisplay());
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        
        createQuerySection(container);
        
        createRevenueSection(container);
        
        createExpensesSection(container);
        
        updateModel(TimeFrame.lastMonth());
        
        return container;
    }
    
    /**
     * 
     */
    private void createRevenueSection(Composite parent) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(section);
		section.setText("Umsatzerlöse");
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		formToolkit.adapt(sectionClient);
		formToolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(6, false));
		
		GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, false);
		
		formToolkit.createLabel(sectionClient, Messages.labelNet);
		revenueNet = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		revenueNet.setOrientation(SWT.RIGHT_TO_LEFT);
		revenueNet.setEnabled(false);
		revenueNet.setEditable(false);
		gdf.applyTo(revenueNet);
		
		formToolkit.createLabel(sectionClient, Messages.labelTaxes);
		revenueTax = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		revenueTax.setOrientation(SWT.RIGHT_TO_LEFT);
		revenueTax.setEnabled(false);
		revenueTax.setEditable(false);
		gdf.applyTo(revenueTax);
		
		formToolkit.createLabel(sectionClient, Messages.labelGross);
		revenueGross = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		revenueGross.setOrientation(SWT.RIGHT_TO_LEFT);
		revenueGross.setEnabled(false);
		revenueGross.setEditable(false);
		gdf.applyTo(revenueGross);
    }
    
    /**
     * 
     * @param parent
     */
    private void createExpensesSection(Composite parent) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(section);
		section.setText("Betriebskosten");
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		formToolkit.adapt(sectionClient);
		formToolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(6, false));
		
		GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, false);
		
		formToolkit.createLabel(sectionClient, Messages.labelNet);
		expensesNet = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		expensesNet.setOrientation(SWT.RIGHT_TO_LEFT);
		expensesNet.setEnabled(false);
		expensesNet.setEditable(false);
		gdf.applyTo(expensesNet);
		
		formToolkit.createLabel(sectionClient, Messages.labelTaxes);
		expensesTax = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		expensesTax.setOrientation(SWT.RIGHT_TO_LEFT);
		expensesTax.setEnabled(false);
		expensesTax.setEditable(false);
		gdf.applyTo(expensesTax);
		
		formToolkit.createLabel(sectionClient, Messages.labelGross);
		expensesGross = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		expensesGross.setOrientation(SWT.RIGHT_TO_LEFT);
		expensesGross.setEnabled(false);
		expensesGross.setEditable(false);
		gdf.applyTo(expensesGross);
    }
    
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return formToolkit;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#updateModel(de.togginho.accounting.util.TimeFrame)
	 */
	@Override
	protected void updateModel(TimeFrame timeFrame) {
		AccountingService service = AccountingUI.getAccountingService();
		
		Revenue revenue = service.getRevenue(timeFrame);
		ExpenseCollection expenses = service.getExpenses(timeFrame);
		
		revenueNet.setText(FormatUtil.formatCurrency(revenue.getRevenueNet()));
		revenueTax.setText(FormatUtil.formatCurrency(revenue.getRevenueTax()));
		revenueGross.setText(FormatUtil.formatCurrency(revenue.getRevenueGross()));
		
		if (expenses.getOperatingCosts() != null) {
			expensesNet.setText(FormatUtil.formatCurrency(expenses.getOperatingCosts().getNet()));
			expensesTax.setText(FormatUtil.formatCurrency(expenses.getOperatingCosts().getTax()));
			expensesGross.setText(FormatUtil.formatCurrency(expenses.getOperatingCosts().getGross()));			
		} else {
			expensesNet.setText(Constants.HYPHEN);
			expensesTax.setText(Constants.HYPHEN);
			expensesGross.setText(Constants.HYPHEN);
		}
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#handleExport()
	 */
	@Override
	protected void handleExport() {
		// TODO Auto-generated method stub
	}
}