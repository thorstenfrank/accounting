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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.IncomeStatement;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class IncomeStatementDialog extends AbstractReportDialog {

	private static final String HELP_CONTEXT_ID = AccountingUI.PLUGIN_ID + ".IncomeStatementDialog"; //$NON-NLS-1$
	
	private FormToolkit formToolkit;
	
	private IncomeStatement incomeStatement;
	
	private Text revenueNet;
	private Text revenueTax;
	private Text revenueGross;
	
	private Text opexNet;
	private Text opexTax;
	private Text opexGross;
	
	private Text totalExpensesNet;
	private Text totalExpensesTax;
	private Text totalExpensesGross;
	
	private Text grossProfit;
	
	private Text outputTax;
	private Text inputTax;
	private Text taxPayable;
	
	/**
     * @param shell
     */
    protected IncomeStatementDialog(Shell shell) {
	    super(shell, TimeFrame.lastMonth());
    }

    /**
     * 
     * {@inheritDoc}
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
    	getShell().setText(Messages.IncomeStatementDialog_title);
    	getShell().setImage(AccountingUI.getImageDescriptor(Messages.iconsIncomeStatement).createImage());
    	
    	PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT_ID);
    	
        formToolkit = new FormToolkit(parent.getDisplay());
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, true));
        
        createQuerySection(container, new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        
        createRevenueSection(container);
        
        createOpexSection(container);
        
        createTotalExpensesSection(container);

        createTaxSection(container);
        
        createProfitSection(container);
        
        updateModel();
        
        return container;
    }
    
    /**
     * 
     */
    private void createRevenueSection(Composite parent) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		formToolkit.paintBordersFor(section);
		section.setText(Messages.labelRevenue);
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		formToolkit.adapt(sectionClient);
		formToolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(2, false));
		
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
    private void createOpexSection(Composite parent) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(section);
		section.setText(Messages.IncomeStatementDialog_operatingExpenses);
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		formToolkit.adapt(sectionClient);
		formToolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(2, false));
		
		GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, false);
		
		formToolkit.createLabel(sectionClient, Messages.labelNet);
		opexNet = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		opexNet.setOrientation(SWT.RIGHT_TO_LEFT);
		opexNet.setEnabled(false);
		opexNet.setEditable(false);
		gdf.applyTo(opexNet);
		
		formToolkit.createLabel(sectionClient, Messages.labelTaxes);
		opexTax = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		opexTax.setOrientation(SWT.RIGHT_TO_LEFT);
		opexTax.setEnabled(false);
		opexTax.setEditable(false);
		gdf.applyTo(opexTax);
		
		formToolkit.createLabel(sectionClient, Messages.labelGross);
		opexGross = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		opexGross.setOrientation(SWT.RIGHT_TO_LEFT);
		opexGross.setEnabled(false);
		opexGross.setEditable(false);
		gdf.applyTo(opexGross);
    }
    
    /**
     * 
     * @param parent
     */
    private void createTotalExpensesSection(Composite parent) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(section);
		section.setText(Messages.IncomeStatementDialog_totalExpenses);
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		formToolkit.adapt(sectionClient);
		formToolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(2, false));
		
		GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, false);
		
		formToolkit.createLabel(sectionClient, Messages.labelNet);
		totalExpensesNet = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		totalExpensesNet.setOrientation(SWT.RIGHT_TO_LEFT);
		totalExpensesNet.setEnabled(false);
		totalExpensesNet.setEditable(false);
		gdf.applyTo(totalExpensesNet);
		
		formToolkit.createLabel(sectionClient, Messages.labelTaxes);
		totalExpensesTax = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		totalExpensesTax.setOrientation(SWT.RIGHT_TO_LEFT);
		totalExpensesTax.setEnabled(false);
		totalExpensesTax.setEditable(false);
		gdf.applyTo(totalExpensesTax);
		
		formToolkit.createLabel(sectionClient, Messages.labelGross);
		totalExpensesGross = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		totalExpensesGross.setOrientation(SWT.RIGHT_TO_LEFT);
		totalExpensesGross.setEnabled(false);
		totalExpensesGross.setEditable(false);
		gdf.applyTo(totalExpensesGross);
    }
    
    /**
     * 
     */
    private void createProfitSection(Composite parent) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(section);
		section.setText(Messages.IncomeStatementDialog_Result);
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		formToolkit.adapt(sectionClient);
		formToolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(2, false));
		
		GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, false);
		
		formToolkit.createLabel(sectionClient, Messages.IncomeStatementDialog_grossProfit); // gross profit
		grossProfit = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		grossProfit.setOrientation(SWT.RIGHT_TO_LEFT);
		grossProfit.setEnabled(false);
		grossProfit.setEditable(false);
		gdf.applyTo(grossProfit);
    }
    
    /**
     * 
     * @param parent
     */
    private void createTaxSection(Composite parent) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(section);
		section.setText(Messages.labelTaxes);
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		formToolkit.adapt(sectionClient);
		formToolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(2, false));
		
		GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, false);
		
		formToolkit.createLabel(sectionClient, Messages.IncomeStatementDialog_outputTax);
		outputTax = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		outputTax.setOrientation(SWT.RIGHT_TO_LEFT);
		outputTax.setEnabled(false);
		outputTax.setEditable(false);
		gdf.applyTo(outputTax);
		
		formToolkit.createLabel(sectionClient, Messages.IncomeStatementDialog_inputTax);
		inputTax = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		inputTax.setOrientation(SWT.RIGHT_TO_LEFT);
		inputTax.setEnabled(false);
		inputTax.setEditable(false);
		gdf.applyTo(inputTax);
		
		formToolkit.createLabel(sectionClient, Messages.IncomeStatementDialog_sum);
		taxPayable = formToolkit.createText(sectionClient, Constants.EMPTY_STRING);
		taxPayable.setOrientation(SWT.RIGHT_TO_LEFT);
		taxPayable.setEnabled(false);
		taxPayable.setEditable(false);
		gdf.applyTo(taxPayable);
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
	 * 
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#updateModel()
	 */
	@Override
	protected void updateModel() {		
		incomeStatement = AccountingUI.getAccountingService().getIncomeStatement(getTimeFrame());
		
		Price revenue = incomeStatement.getTotalRevenue();
		Price totalExpenses = incomeStatement.getTotalExpenses();
		Price opex = incomeStatement.getTotalOperatingCosts();
		
		setCurrencyValue(revenueNet, revenue.getNet());
		setCurrencyValue(revenueGross, revenue.getGross());
		setCurrencyValue(revenueTax, revenue.getTax());
		
		setCurrencyValue(totalExpensesNet, totalExpenses.getNet());
		setCurrencyValue(totalExpensesTax, totalExpenses.getTax());
		setCurrencyValue(totalExpensesGross, totalExpenses.getGross());
		
		setCurrencyValue(opexNet, opex.getNet());
		setCurrencyValue(opexGross, opex.getGross());
		setCurrencyValue(opexTax, opex.getTax());
		
		setCurrencyValue(grossProfit, incomeStatement.getGrossProfit().getNet());
		
		setCurrencyValue(outputTax, revenue.getTax());
		setCurrencyValue(inputTax, totalExpenses.getTax());
		setCurrencyValue(taxPayable, incomeStatement.getTaxSum());
//		
//		if (BigDecimal.ZERO.compareTo(cashFlow.getGrossProfit()) > 0) {
//			grossProfit.setEnabled(true);
//			grossProfit.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
//		}
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#handleExport()
	 */
	@Override
	protected void handleExport() {
		ReportGenerationUtil.executeReportGeneration(new IncomeStatementHandler(), getShell());
	}
	
	/**
	 *
	 */
	private class IncomeStatementHandler implements ReportGenerationHandler {

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ui.reports.ReportGenerationHandler#getTargetFileNameSuggestion()
         */
        @Override
        public String getTargetFileNameSuggestion() {
	        return ReportGenerationUtil.appendTimeFrameToFileNameSuggestion(
	        		Messages.IncomeStatementDialog_fileNameSuggestion, incomeStatement.getTimeFrame());
        }

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ui.reports.ReportGenerationHandler#getModelObject()
         */
        @Override
        public Object getModelObject() {
	        return incomeStatement;
        }

		/**
         * {@inheritDoc}.
         * @see de.togginho.accounting.ui.reports.ReportGenerationHandler#getReportId()
         */
        @Override
        public String getReportId() {
	        return "CashFlowStatement";
        }
	}
}