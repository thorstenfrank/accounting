/**
 * 
 */
package de.tfsw.accounting.ui.user;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.Constants;
import de.tfsw.accounting.UserProfileService;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.BankAccount;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.UserProfile;
import de.tfsw.accounting.ui.AbstractFormBasedEditor;
import de.tfsw.accounting.ui.util.AccountingPreferences;
import de.tfsw.accounting.ui.util.WidgetHelper;
import de.tfsw.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class UserEditor extends AbstractFormBasedEditor {
	
	public static final String PART_ID = "de.tfsw.accounting.ui.part.user";
	
	private static final Logger LOG = LogManager.getLogger(UserEditor.class);
	
	private static final int COLUMN_TAX_RATE_ABBREVIATION = 0;
	private static final int COLUMN_TAX_RATE_NAME = 1;
	private static final int COLUMN_TAX_RATE = 2;
	
	@Inject
	private UserProfileService userService;
	
	@Inject
	@Translation
	private Messages messages;
	
	@Inject
	private IEclipseContext context;
	
	private TableViewer taxRateViewer;
	
	private UserProfile currentUser;
	
	@Override
	protected boolean createControl(Composite parent) {
		GridLayout layout = new GridLayout(2, true);
		parent.setLayout(layout);
		
		boolean dirty = false;
		
		currentUser = userService.getCurrentUserProfile();
		if (currentUser == null) {
			LOG.debug("Building editor for new user");
			currentUser = new UserProfile();
			dirty = true;
			//MessageDialog.openWarning(parent.getShell(), "You are new!", "Welcome to accounting. Please edit and save your data!");
		} else {
			LOG.trace("Building user editor for {}", currentUser.getName());
			setPartLabel(currentUser.getName());
		}
		
		createBasicInfoSection(parent);
		
		if (currentUser.getPrimaryAddress() == null) {
			currentUser.setPrimaryAddress(new Address());
		}
		createAddressSection(parent, currentUser.getPrimaryAddress());
		
		if (currentUser.getBankAccount() == null) {
			currentUser.setBankAccount(new BankAccount());
		}
		createBankAccountSection(parent, currentUser.getBankAccount());
				
		createTaxRateSection(parent);
		
		return dirty;
	}
	
	@Override
	protected String getEditorHeader() {
		return messages.userEditorHeader;
	}
	
	@Override
	protected String getEditorHeaderDesc() {
		return messages.userEditorHeaderDesc;
	}
	
	@Override
	protected boolean doSave() {
		LOG.debug("Saving user {}", currentUser.getName());
		userService.saveUserProfile(currentUser);
		
		AccountingContext accCtx = context.get(AccountingContext.class);
		if (accCtx != null) {
			if (!currentUser.getName().equals(accCtx.getUserName())) {
				LOG.info("Change in user name, from {} to {}. Saving context now", accCtx.getUserName(), currentUser.getName());
				accCtx = new AccountingContext(currentUser.getName(), accCtx.getDbLocation());
				context.set(AccountingContext.class, accCtx);
				AccountingPreferences.storeContext(accCtx);
			}
		} else {
			LOG.warn("No accounting context available, can't check for user name change!");
		}
		
		return true;
	}
	
	@PreDestroy
	public void cleanup() {
		this.currentUser = null;
		this.taxRateViewer.getTable().dispose();
	}
	
	/**
	 * 
	 * @param user
	 */
	private void createBasicInfoSection(Composite parent) {
		final Composite group = createGroup(parent, messages.labelBasicInformation);
		
		createTextWithLabelNotNullable(group, messages.labelName, currentUser, "name");
		createTextWithLabel(group, messages.labelTaxId, currentUser, "taxId");
				
		createLabel(group, messages.labelDescription);
		final Text description = new Text(group, SWT.MULTI | SWT.BORDER);
		WidgetHelper.grabBoth(description);
		createBindings(description, currentUser, "description");
	}
	
	private void createBankAccountSection(Composite parent, BankAccount account) {		
		final Composite group = createGroup(parent, messages.labelBankAccount);
		createTextWithLabel(group, messages.labelAccountNumber, account, BankAccount.FIELD_ACCOUNT_NUMBER);
		createTextWithLabel(group, messages.labelBankCode, account, BankAccount.FIELD_BANK_CODE);
		createTextWithLabel(group, messages.labelBankName, account, BankAccount.FIELD_BANK_NAME);
		createTextWithLabel(group, messages.labelBIC, account, BankAccount.FIELD_BIC);
		createTextWithLabel(group, messages.labelIBAN, account, BankAccount.FIELD_IBAN);
	}

	private void createTaxRateSection(Composite parent) {
//		taxRateSection.setDescription(messages.userEditorTaxRateDescription);		
		final Composite group = createGroup(parent, messages.userEditorTaxRates);
		
		Composite tableComposite = new Composite(group, SWT.NONE);
		WidgetHelper.grabBoth(tableComposite);
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableComposite.setLayout(tableLayout);
		
		taxRateViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);
		
		Table table = taxRateViewer.getTable();
		table.setHeaderVisible(true);
		
		createColumn(COLUMN_TAX_RATE_ABBREVIATION, messages.userEditorTaxRateAbbreviation, tableLayout, 30);
		createColumn(COLUMN_TAX_RATE_NAME, messages.userEditorTaxRateName, tableLayout, 50);
		createColumn(COLUMN_TAX_RATE, messages.userEditorTaxRate, tableLayout, 20);

		final Set<TaxRate> taxRates = new HashSet<>();
//		Set<TaxRate> taxRates = currentUser.getTaxRates();
//		if (taxRates == null) {
//			taxRates = new HashSet<>();
//			currentUser.setTaxRates(taxRates);
//		}

		taxRateViewer.setLabelProvider(new TaxRateCellLabelProvider());
		taxRateViewer.setContentProvider(ArrayContentProvider.getInstance());
		taxRateViewer.setInput(taxRates);
		
		Composite buttons = new Composite(group, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		buttons.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button add = new Button(buttons, SWT.PUSH);
		add.setText(messages.labelAdd);
		add.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
			final Shell shell = (Shell) context.get(IServiceConstants.ACTIVE_SHELL);
			final NewTaxRateWizard wizard = new NewTaxRateWizard(messages);
			final WizardDialog dialog = new WizardDialog(shell, wizard);
			if (WizardDialog.OK == dialog.open()) {
				taxRateViewer.getTable().setRedraw(false);
				//currentUser.addTaxRate(wizard.getNewTaxRate());
				taxRates.add(wizard.getNewTaxRate());
				taxRateViewer.refresh();
				taxRateViewer.getTable().setRedraw(true);
				setDirty(true);
			}
		}));
	}
	
	private void createColumn(final int index, final String label, final TableColumnLayout tableLayout, final int weight) {
		final TableViewerColumn col = new TableViewerColumn(taxRateViewer, SWT.NONE, index);
		col.getColumn().setText(label);
		col.getColumn().setAlignment(SWT.CENTER);
		tableLayout.setColumnData(col.getColumn(), new ColumnWeightData(weight));
	}
	
	/**
	 *
	 */
	private class TaxRateCellLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
		
       @Override
       public Image getColumnImage(Object element, int columnIndex) {
	        return null;
       }

       @Override
       public String getColumnText(Object element, int columnIndex) {
       	if (element == null || !(element instanceof TaxRate)) {
       		return Constants.HYPHEN;
       	}
       	
       	final TaxRate tr = (TaxRate) element;

       	switch (columnIndex) {
			case COLUMN_TAX_RATE_ABBREVIATION:
				return tr.getShortName();
			case COLUMN_TAX_RATE_NAME:
				return tr.getLongName();
			case COLUMN_TAX_RATE:
				return FormatUtil.formatPercentValue(tr.getRate());
			default:
				return Constants.HYPHEN;
			}
       }
	}
}
