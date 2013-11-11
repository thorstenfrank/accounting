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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.togginho.accounting.model.ExpenseImportParams;
import de.togginho.accounting.model.ExpenseImportParams.DateFormatPattern;
import de.togginho.accounting.model.ExpenseImportParams.DecimalMark;
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
	private static final String PAGE_NAME = "ImportWizardPageOne"; //$NON-NLS-1$

	/**
	 * 
	 */
	private static final String[] VALID_EXTENSIONS = new String[]{"*.csv", "*.txt"}; //$NON-NLS-1$ //$NON-NLS-2$
		
	/**
	 * 
	 */
	private String sourceFile;
	
	/**
	 * 
	 */
	private ExpenseImportParams params;
	
	/**
	 * @param pageName
	 */
	ImportWizardPageOne() {
		super(PAGE_NAME, Messages.ImportExpensesWizard_title, AccountingUI.getImageDescriptor(Messages.iconsExpenseAdd));
		setMessage(Messages.ImportExpensesWizard_pageOne);
		params = new ExpenseImportParams();
	}
	
	/**
	 * @return the sourceFile
	 */
	protected String getSourceFile() {
		return sourceFile;
	}

	/**
	 * @return the params
	 */
	protected ExpenseImportParams getParams() {
		return params;
	}
	
	/**
	 * {@inheritDoc}.
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        
        buildFileChooser(composite);
        buildCustomDatePatternSelector(composite);
        buildDecimalSeparator(composite);
        
        setPageComplete(false);
        setControl(composite);
	}

	/**
     * @param parent
     * @return
     */
    private void buildFileChooser(final Composite parent) {        
        Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.ImportExpensesWizard_sourceFile);
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        WidgetHelper.grabHorizontal(composite);
        
        final Text fileText = new Text(composite, SWT.BORDER);
        fileText.setEnabled(false);
        WidgetHelper.grabHorizontal(fileText);
        
        Button browse = new Button(composite, SWT.PUSH);
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
		WidgetHelper.createLabel(parent, Messages.ImportExpensesWizard_decimalMark);
		final Combo combo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.add(DecimalMark.DOT.getValue());
		combo.add(DecimalMark.COMMA.getValue());
		switch (params.getDecimalMark()) {
		case COMMA:
			combo.select(1);
			break;
		default:
			combo.select(0);
			break;
		}
		
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (combo.getSelectionIndex()) {
				case 1:
					params.setDecimalMark(DecimalMark.COMMA);
					break;
				default:
					params.setDecimalMark(DecimalMark.DOT);
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
        WidgetHelper.createLabel(parent, Messages.ImportExpensesWizard_datePattern);
		final Combo customDatePatternCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (DateFormatPattern pattern : DateFormatPattern.values()) {
			customDatePatternCombo.add(pattern.getTranslation());
		}
		customDatePatternCombo.select(0);
				
		customDatePatternCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
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
