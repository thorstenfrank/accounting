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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.togginho.accounting.Constants;
import de.togginho.accounting.ReportGenerationMonitor;
import de.togginho.accounting.ReportingService;
import de.togginho.accounting.model.ExpenseType;
import de.togginho.accounting.model.IncomeStatement;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;
import de.togginho.accounting.util.TimeFrame;
import de.togginho.accounting.util.TimeFrameType;

/**
 * @author thorsten
 *
 */
public class IncomeStatementDialog extends AbstractReportDialog {

	private IncomeStatement incomeStatement;
	
	private FormToolkit toolkit;
	
	private Text revenueNet;
	private Text revenueTax;
	private Text revenueGross;
	
	private Text opexNet;
	private Text opexTax;
	private Text opexGross;
	
	private Text grossProfitNet;
	private Text grossProfitTax;
	private Text grossProfitGross;
	
	private Text capexNet;
	private Text capexTax;
	private Text capexGross;

	private Text otherExpensesNet;
	private Text otherExpensesTax;
	private Text otherExpensesGross;
	
	/**
     * @param shell
     */
    IncomeStatementDialog(Shell shell) {
	    super(shell);
    }

    /**
     * 
     * {@inheritDoc}.
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
    	getShell().setText("Shell Text");
    	
    	toolkit = new FormToolkit(parent.getDisplay());
    	Composite container = new Composite(parent, SWT.NONE);
    	container.setLayout(new GridLayout(1, true));
    	
    	createQuerySection(container);
    	
    	createRevenueSection(container);
    	
    	createOpexSection(container);
    	
    	createGrossProfitSection(container);

    	createCapexSection(container);
    	
    	createOtherExpensesSection(container);
    	
    	updateModel();
    	
    	return container;
    }
    
    /**
     * 
     */
    private void createRevenueSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		WidgetHelper.grabBoth(section);
		toolkit.paintBordersFor(section);
		section.setText(Messages.labelRevenue);
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		toolkit.adapt(sectionClient);
		toolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(6, false));
		
		toolkit.createLabel(sectionClient, Messages.labelNet);
		revenueNet = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		revenueNet.setOrientation(SWT.RIGHT_TO_LEFT);
		revenueNet.setEnabled(false);
		revenueNet.setEditable(false);
		WidgetHelper.grabHorizontal(revenueNet);
		
		toolkit.createLabel(sectionClient, Messages.labelTaxes);
		revenueTax = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		revenueTax.setOrientation(SWT.RIGHT_TO_LEFT);
		revenueTax.setEnabled(false);
		revenueTax.setEditable(false);
		WidgetHelper.grabHorizontal(revenueTax);
		
		toolkit.createLabel(sectionClient, Messages.labelGross);
		revenueGross = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		revenueGross.setOrientation(SWT.RIGHT_TO_LEFT);
		revenueGross.setEnabled(false);
		revenueGross.setEditable(false);
		WidgetHelper.grabHorizontal(revenueGross);
    }
    
    /**
     * 
     * @param parent
     */
    private void createOpexSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		WidgetHelper.grabBoth(section);
		toolkit.paintBordersFor(section);
		section.setText(ExpenseType.OPEX.getTranslatedString());
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		toolkit.adapt(sectionClient);
		toolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(6, false));
				
		toolkit.createLabel(sectionClient, Messages.labelNet);
		opexNet = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		opexNet.setOrientation(SWT.RIGHT_TO_LEFT);
		opexNet.setEnabled(false);
		opexNet.setEditable(false);
		WidgetHelper.grabHorizontal(opexNet);
		
		toolkit.createLabel(sectionClient, Messages.labelTaxes);
		opexTax = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		opexTax.setOrientation(SWT.RIGHT_TO_LEFT);
		opexTax.setEnabled(false);
		opexTax.setEditable(false);
		WidgetHelper.grabHorizontal(opexTax);
		
		toolkit.createLabel(sectionClient, Messages.labelGross);
		opexGross = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		opexGross.setOrientation(SWT.RIGHT_TO_LEFT);
		opexGross.setEnabled(false);
		opexGross.setEditable(false);
		WidgetHelper.grabHorizontal(opexGross);
    }

    /**
     * 
     * @param parent
     */
    private void createGrossProfitSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		WidgetHelper.grabBoth(section);
		toolkit.paintBordersFor(section);
		section.setText("Gross Profit");
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		toolkit.adapt(sectionClient);
		toolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(6, false));
				
		toolkit.createLabel(sectionClient, Messages.labelNet);
		grossProfitNet = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		grossProfitNet.setOrientation(SWT.RIGHT_TO_LEFT);
		grossProfitNet.setEnabled(false);
		grossProfitNet.setEditable(false);
		WidgetHelper.grabHorizontal(grossProfitNet);
		
		toolkit.createLabel(sectionClient, Messages.labelTaxes);
		grossProfitTax = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		grossProfitTax.setOrientation(SWT.RIGHT_TO_LEFT);
		grossProfitTax.setEnabled(false);
		grossProfitTax.setEditable(false);
		WidgetHelper.grabHorizontal(grossProfitTax);
		
		toolkit.createLabel(sectionClient, Messages.labelGross);
		grossProfitGross = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		grossProfitGross.setOrientation(SWT.RIGHT_TO_LEFT);
		grossProfitGross.setEnabled(false);
		grossProfitGross.setEditable(false);
		WidgetHelper.grabHorizontal(grossProfitGross);
    }
    
    /**
     * 
     * @param parent
     */
    private void createCapexSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		WidgetHelper.grabBoth(section);
		toolkit.paintBordersFor(section);
		section.setText(ExpenseType.CAPEX.getTranslatedString());
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		toolkit.adapt(sectionClient);
		toolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(6, false));
				
		toolkit.createLabel(sectionClient, Messages.labelNet);
		capexNet = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		capexNet.setOrientation(SWT.RIGHT_TO_LEFT);
		capexNet.setEnabled(false);
		capexNet.setEditable(false);
		WidgetHelper.grabHorizontal(capexNet);
		
		toolkit.createLabel(sectionClient, Messages.labelTaxes);
		capexTax = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		capexTax.setOrientation(SWT.RIGHT_TO_LEFT);
		capexTax.setEnabled(false);
		capexTax.setEditable(false);
		WidgetHelper.grabHorizontal(capexTax);
		
		toolkit.createLabel(sectionClient, Messages.labelGross);
		capexGross = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		capexGross.setOrientation(SWT.RIGHT_TO_LEFT);
		capexGross.setEnabled(false);
		capexGross.setEditable(false);
		WidgetHelper.grabHorizontal(capexGross);
    }

    /**
     * 
     * @param parent
     */
    private void createOtherExpensesSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		WidgetHelper.grabBoth(section);
		toolkit.paintBordersFor(section);
		section.setText(ExpenseType.OTHER.getTranslatedString());
		
		Composite sectionClient = new Composite(section, SWT.NONE);
		toolkit.adapt(sectionClient);
		toolkit.paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		sectionClient.setLayout(new GridLayout(6, false));
				
		toolkit.createLabel(sectionClient, Messages.labelNet);
		otherExpensesNet = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		otherExpensesNet.setOrientation(SWT.RIGHT_TO_LEFT);
		otherExpensesNet.setEnabled(false);
		otherExpensesNet.setEditable(false);
		WidgetHelper.grabHorizontal(otherExpensesNet);
		
		toolkit.createLabel(sectionClient, Messages.labelTaxes);
		otherExpensesTax = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		otherExpensesTax.setOrientation(SWT.RIGHT_TO_LEFT);
		otherExpensesTax.setEnabled(false);
		otherExpensesTax.setEditable(false);
		WidgetHelper.grabHorizontal(otherExpensesTax);
		
		toolkit.createLabel(sectionClient, Messages.labelGross);
		otherExpensesGross = toolkit.createText(sectionClient, Constants.EMPTY_STRING);
		otherExpensesGross.setOrientation(SWT.RIGHT_TO_LEFT);
		otherExpensesGross.setEnabled(false);
		otherExpensesGross.setEditable(false);
		WidgetHelper.grabHorizontal(otherExpensesGross);
    }
    
	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#getToolkit()
	 */
	@Override
	protected FormToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#getDefaultTimeFrameType()
	 */
	@Override
	protected TimeFrameType getDefaultTimeFrameType() {
		return TimeFrameType.LAST_YEAR;
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#updateModel(de.togginho.accounting.util.TimeFrame)
	 */
	@Override
	protected void updateModel(TimeFrame timeFrame) {
		incomeStatement = AccountingUI.getAccountingService().getIncomeStatement(timeFrame);
		
		setCurrencyValue(revenueNet, incomeStatement.getRevenue().getRevenueNet());
		setCurrencyValue(revenueTax, incomeStatement.getRevenue().getRevenueTax());
		setCurrencyValue(revenueGross, incomeStatement.getRevenue().getRevenueGross());
		
		final Price opex = incomeStatement.getOperatingExpenses().getTotalCost();
		setCurrencyValue(opexNet, opex.getNet());
		setCurrencyValue(opexTax, opex.getTax());
		setCurrencyValue(opexGross, opex.getGross());
		
		final Price ebitda = incomeStatement.getGrossProfit();
		setCurrencyValue(grossProfitNet, ebitda.getNet());
		setCurrencyValue(grossProfitTax, ebitda.getTax());
		setCurrencyValue(grossProfitGross, ebitda.getGross());
		
		if (incomeStatement.getCapitalExpenses() != null) {
			final Price capex = incomeStatement.getCapitalExpenses().getTotalCost();
			setCurrencyValue(capexNet, capex.getNet());
			setCurrencyValue(capexTax, capex.getTax());
			setCurrencyValue(capexGross, capex.getGross());
		} else {
			capexNet.setText(Constants.EMPTY_STRING);
			capexTax.setText(Constants.EMPTY_STRING);
			capexGross.setText(Constants.EMPTY_STRING);
		}
		
		if (incomeStatement.getOtherExpenses() != null) {
			final Price other = incomeStatement.getOtherExpenses().getTotalCost();
			setCurrencyValue(otherExpensesNet, other.getNet());
			setCurrencyValue(otherExpensesTax, other.getTax());
			setCurrencyValue(otherExpensesGross, other.getGross());
		} else {
			otherExpensesNet.setText(Constants.EMPTY_STRING);
			otherExpensesTax.setText(Constants.EMPTY_STRING);
			otherExpensesGross.setText(Constants.EMPTY_STRING);
		}
	}

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ui.reports.AbstractReportDialog#handleExport()
	 */
	@Override
	protected void handleExport() {
		ReportGenerationUtil.executeReportGeneration(new IncomeStatementGenerationHandler(), getShell());
	}

	/**
	 * 
	 * @author thorsten
	 *
	 */
	private class IncomeStatementGenerationHandler implements ReportGenerationHandler {
		
		@Override
		public void handleReportGeneration(ReportingService reportingService, String targetFileName, ReportGenerationMonitor monitor) {
			reportingService.generateIncomeStatementToPdf(incomeStatement, targetFileName, monitor);
			
		}
		
		@Override
		public String getTargetFileNameSuggestion() {
			return "IncomeStatement.pdf";
		}		
	}
}
