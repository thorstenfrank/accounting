/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.expense;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.ExpenseImportParams;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
class ImportWizardPageOne extends WizardPage {

	/**
	 * 
	 */
	private static final String[] VALID_EXTENSIONS = new String[]{"*.csv", "*.txt"}; //$NON-NLS-1$ //$NON-NLS-2$
	
	/**
	 * 
	 */
	private static final Date TODAY = new Date();
	
	/**
	 * 
	 */
	private String sourceFile;
	
	
	private ExpenseImportParams params;
	
	/**
	 * 
	 */
	private DateFormat format = new SimpleDateFormat();
	
	/**
	 * @param pageName
	 */
	ImportWizardPageOne() {
		super("ChooseFilePage", Messages.ImportExpensesWizard_title, AccountingUI.getImageDescriptor(Messages.iconsExpenseAdd));
		setMessage(Messages.ImportExpensesWizard_message);
		params = new ExpenseImportParams();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible == false) {
			((ImportExpensesFromCsvWizard)getWizard()).runImport(sourceFile, params);			
		}
	    super.setVisible(visible);
	}
	
	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        
        buildMandatoryInfo(composite);
        buildCustomDatePatternSelector(composite);
        buildDecimalSeparator(composite);
        
        setPageComplete(false);
        setControl(composite);
	}

	/**
     * @param parent
     * @return
     */
    private void buildMandatoryInfo(final Composite parent) {        
        Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.ImportExpensesWizard_sourceFile);
        
        final Text fileText = new Text(parent, SWT.BORDER);
        fileText.setEnabled(false);
        WidgetHelper.grabHorizontal(fileText);
        
        Button browse = new Button(parent, SWT.PUSH);
        browse.setText(Messages.labelBrowse);
        browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(parent.getShell(), SWT.OPEN);
				fd.setFilterExtensions(VALID_EXTENSIONS);
				fd.setText(Messages.ImportExpensesWizard_fileDialogTitle);
				fd.setFileName(fileText.getText());
				String selected = fd.open();
				if (selected != null) {
					fileText.setText(selected);
					sourceFile = fileText.getText();
					checkSourceFile();
				}
			}
		});
    }
	
    /**
     * 
     * @param parent
     */
    private void buildDecimalSeparator(Composite parent) {
		WidgetHelper.createLabel(parent, "Decimal Separator:");
		final Combo combo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(combo);
		combo.add(Constants.DOT);
		combo.add(Constants.COMMA);
		WidgetHelper.createLabel(parent, Constants.EMPTY_STRING);
		if (Constants.COMMA.equals(params.getDecimalMark())) {
			combo.select(1);
		} else {
			combo.select(0);
		}
		
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (combo.getSelectionIndex()) {
				case 1:
					params.setDecimalMark(Constants.COMMA);
					break;
				default:
					params.setDecimalMark(Constants.DOT);
					break;
				}
			}
		});
    }
    
	/**
	 * 
	 * @param parent
	 */
	private void buildCustomDatePatternSelector(Composite parent) {
        WidgetHelper.createLabel(parent, "Date Format Pattern:");
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(composite);
        
		final Combo customDatePatternCombo = new Combo(composite, SWT.DROP_DOWN);
		WidgetHelper.grabHorizontal(customDatePatternCombo);
		customDatePatternCombo.add("dd.MM.yyyy");
		customDatePatternCombo.add("dd/MM/yyyy");
		customDatePatternCombo.add("dd-MM-yyyy");
		customDatePatternCombo.add("MM/dd/yyyy");
		customDatePatternCombo.add("yyyy-MM-dd");
		
		customDatePatternCombo.setEnabled(false);
		customDatePatternCombo.select(0);
		
		WidgetHelper.createLabel(composite, "Example:");
		
		final Text example = new Text(composite, SWT.BORDER);
		WidgetHelper.grabHorizontal(example);
		example.setEnabled(false);
		
		customDatePatternCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDateFormatExample(customDatePatternCombo.getText(), example);
			}
		});
		
		customDatePatternCombo.addTraverseListener(new TraverseListener() {
			
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (updateDateFormatExample(customDatePatternCombo.getText(), example)) {
					customDatePatternCombo.add(customDatePatternCombo.getText());
					customDatePatternCombo.select(customDatePatternCombo.getItemCount() - 1);					
				}
			}
		});
	}
	
	/**
	 * 
	 * @param pattern
	 * @param example
	 */
	private boolean updateDateFormatExample(String pattern, Text example) {
		try {
	        format = new SimpleDateFormat(pattern);
	        example.setText(format.format(TODAY));
	        params.setDateFormatPattern(pattern);
	        checkSourceFile();
	        return true;
        } catch (Exception e) {
        	example.setText(Constants.EMPTY_STRING);
        	setErrorMessage(String.format("[%s] is not a valid date formatting pattern!", pattern));
        	if (isPageComplete()) {
        		setPageComplete(false);
        	}
        	return false;
        }
	}
	
    /**
     * 
     */
    private void checkSourceFile() {
    	File file = new File(sourceFile);
    	if (!file.exists() || file.isDirectory()) {
    		setErrorMessage(Messages.ImportExpensesWizard_errorFileNotValid);
    		setPageComplete(false);
    	} else {
    		setErrorMessage(null);
    		setPageComplete(true);
    	}
    }
}
