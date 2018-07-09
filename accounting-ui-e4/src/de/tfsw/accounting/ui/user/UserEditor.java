/**
 * 
 */
package de.tfsw.accounting.ui.user;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.UserService;
import de.tfsw.accounting.model.User;
import de.tfsw.accounting.ui.AbstractFormBasedEditor;
import de.tfsw.accounting.ui.util.AccountingPreferences;
import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * @author thorsten
 *
 */
public class UserEditor extends AbstractFormBasedEditor {
	
	private static final Logger LOG = LogManager.getLogger(UserEditor.class);
	
	private static final int COLUMN_TAX_RATE_ABBREVIATION = 0;
	private static final int COLUMN_TAX_RATE_NAME = 1;
	private static final int COLUMN_TAX_RATE = 2;
	
	@Inject
	private UserService userService;
	
	@Inject
	@Translation
	private Messages messages;

	@Inject
	private IEclipseContext context;
	
	private TableViewer taxRateViewer;
	
	private User currentUser;
	
	@Override
	public void createControl(Composite parent) {
		LOG.debug("createControl");
//	    getForm().setText(messages.userEditorHeader);
		
		GridLayout layout = new GridLayout(2, true);
		parent.setLayout(layout);
		
		currentUser = userService.getCurrentUser();
		if (currentUser == null) {
			currentUser = new User();
			setDirty(true);
			//MessageDialog.openWarning(parent.getShell(), "You are new!", "Welcome to accounting. Please edit and save your data!");
		} else {
			LOG.debug("Building user editor for {}", userService.getCurrentUser().getName());
			setPartLabel(currentUser.getName());
		}
		
		createBasicInfoSection(parent);
//		
//		if (currentUser.getAddress() == null) {
//			currentUser.setAddress(new Address());
//		}
//		createAddressSection(parent, currentUser.getAddress());
//		
//		if (currentUser.getBankAccount() == null) {
//			currentUser.setBankAccount(new BankAccount());
//		}
//		createBankAccountSection(parent, currentUser.getBankAccount());
//				
//		createTaxRateSection(parent);
	}
	
	/**
	 * 
	 * @param user
	 */
	private void createBasicInfoSection(Composite parent) {
//		final FormToolkit toolkit = getToolkit();
//		Section basicSection = toolkit.createSection(parent, Section.TITLE_BAR);
//		basicSection.setText(messages.labelBasicInformation);
		
		// TODO check if we can move this to WidgetHelper
//		basicSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//		
//		Composite basicSectionClient = toolkit.createComposite(basicSection);
		
		
		Group basicSectionClient = new Group(parent, SWT.SHADOW_ETCHED_OUT);
		basicSectionClient.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		basicSectionClient.setText(messages.labelBasicInformation);
		basicSectionClient.setLayout(new GridLayout(2, false));
		
		createTextWithLabel(basicSectionClient, messages.labelName, currentUser.getName(), currentUser, User.FIELD_NAME);
		createTextWithLabel(basicSectionClient, messages.labelTaxId, currentUser.getTaxNumber(), currentUser, User.FIELD_TAX_NUMBER);
		
//		toolkit.createLabel(basicSectionClient, "Default Currency:");
//		final Combo currencyCombo = new Combo(basicSectionClient, SWT.DROP_DOWN | SWT.READ_ONLY);
//		WidgetHelper.grabHorizontal(currencyCombo);
//		
//		List<String> currencies = new ArrayList<String>();
//		for (Currency currency : Currency.getAvailableCurrencies()) {
//			currencies.add(String.format("%s - %s - %s", currency.getCurrencyCode(), currency.getDisplayName(), currency.getSymbol()));
//		}
//		
//		Collections.sort(currencies);
//		
//		int index = 0;
//		for (String currency : currencies) {
//			currencyCombo.add(currency);
//			index++;
//		}
//		
//		// TODO actually hook this up to the User object!
//		currencyCombo.addSelectionListener(SelectionListener.widgetSelectedAdapter(
//				e -> LOG.debug("Currency " + currencyCombo.getText())));
//		LOG.debug("Total number of available currencies: " + index); //$NON-NLS-1$
		
		Label descLabel = new Label(basicSectionClient, SWT.NONE);
		descLabel.setText(messages.labelDescription);
		Text description = new Text(basicSectionClient, SWT.MULTI | SWT.BORDER);
		WidgetHelper.grabBoth(description);
		createBindings(description, currentUser, User.FIELD_DESCRIPTION);
		
//		basicSection.setClient(basicSectionClient);
	}
//	
//	private void createAddressSection(Composite parent, Address address) {
//		FormToolkit toolkit = getToolkit();
//		Section addressSection = toolkit.createSection(parent, Section.TITLE_BAR);
//		addressSection.setText(messages.labelAddress);
//		addressSection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
//		
//		Composite addressSectionClient = getToolkit().createComposite(addressSection);
//		addressSectionClient.setLayout(new GridLayout(2, false));
//		
//		createTextWithLabel(addressSectionClient, messages.labelRecipientDetail, address.getRecipientDetail(), address, Address.FIELD_RECIPIENT_DETAIL);
//		createTextWithLabel(addressSectionClient, messages.labelStreet, address.getStreet(), address, Address.FIELD_STREET);
//		createTextWithLabel(addressSectionClient, messages.labelPostalCode, address.getPostalCode(), address, Address.FIELD_POSTAL_CODE);
//		createTextWithLabel(addressSectionClient, messages.labelCity, address.getCity(), address, Address.FIELD_CITY);
//		createTextWithLabel(addressSectionClient, messages.labelEmail, address.getEmail(), address, Address.FIELD_EMAIL);
//		createTextWithLabel(addressSectionClient, messages.labelPhone, address.getPhoneNumber(), address, Address.FIELD_PHONE_NUMBER);
//		createTextWithLabel(addressSectionClient, messages.labelMobile, address.getMobileNumber(), address, Address.FIELD_MOBILE_NUMBER);
//		createTextWithLabel(addressSectionClient, messages.labelFax, address.getFaxNumber(), address, Address.FIELD_FAX_NUMBER);
//		
//		addressSection.setClient(addressSectionClient);
//	}
//	
//	private void createBankAccountSection(Composite parent, BankAccount account) {
//		FormToolkit toolkit = getToolkit();
//		Section accountSection = toolkit.createSection(parent, Section.TITLE_BAR);
//		accountSection.setText(messages.labelBankAccount);
//		accountSection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
//		
//		Composite client = toolkit.createComposite(accountSection);
//		client.setLayout(new GridLayout(2, false));
//		
//		createTextWithLabel(client, messages.labelAccountNumber, account.getAccountNumber(), account, BankAccount.FIELD_ACCOUNT_NUMBER);
//		createTextWithLabel(client, messages.labelBankCode, account.getBankCode(), account, BankAccount.FIELD_BANK_CODE);
//		createTextWithLabel(client, messages.labelBankName, account.getBankName(), account, BankAccount.FIELD_BANK_NAME);
//		createTextWithLabel(client, messages.labelBIC, account.getBic(), account, BankAccount.FIELD_BIC);
//		createTextWithLabel(client, messages.labelIBAN, account.getBic(), account, BankAccount.FIELD_IBAN);
//		
//		accountSection.setClient(client);
//	}
//		
//	/**
//	 * 
//	 */
//	private void createTaxRateSection(Composite parent) {
//		FormToolkit toolkit = getToolkit();
//		Section taxRateSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION);
//		taxRateSection.setText(messages.userEditorTaxRates);
//		taxRateSection.setDescription(messages.userEditorTaxRateDescription);
//		
//		// TODO check if we can move this to WidgetHelper
//		taxRateSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		
//		Composite client = toolkit.createComposite(taxRateSection);
//		client.setLayout(new GridLayout(2, false));
//		
//		Composite tableComposite = toolkit.createComposite(client);
//		WidgetHelper.grabBoth(tableComposite);
//		TableColumnLayout tableLayout = new TableColumnLayout();
//		tableComposite.setLayout(tableLayout);
//		
//		taxRateViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION);
//		
//		Table table = taxRateViewer.getTable();
//		table.setHeaderVisible(true);
//		
//		createColumn(COLUMN_TAX_RATE_ABBREVIATION, messages.userEditorTaxRateAbbreviation, tableLayout, 30);
//		createColumn(COLUMN_TAX_RATE_NAME, messages.userEditorTaxRateName, tableLayout, 50);
//		createColumn(COLUMN_TAX_RATE, messages.userEditorTaxRate, tableLayout, 20);
//
//		Set<TaxRate> taxRates = currentUser.getTaxRates();
//		if (taxRates == null) {
//			taxRates = new HashSet<TaxRate>();
//			currentUser.setTaxRates(taxRates);
//		}
//
//		taxRateViewer.setLabelProvider(new TaxRateCellLabelProvider());
//		taxRateViewer.setContentProvider(ArrayContentProvider.getInstance());
//		taxRateViewer.setInput(taxRates);
//		
//		Composite buttons = toolkit.createComposite(client);
//		buttons.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
//		buttons.setLayout(new FillLayout(SWT.VERTICAL));
//		
//		Button add = toolkit.createButton(buttons, messages.labelAdd, SWT.PUSH);
//		add.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
//			final Shell shell = (Shell) context.get(IServiceConstants.ACTIVE_SHELL);
//			final NewTaxRateWizard wizard = new NewTaxRateWizard(messages);
//			final WizardDialog dialog = new WizardDialog(shell, wizard);
//			if (WizardDialog.OK == dialog.open()) {
//				taxRateViewer.getTable().setRedraw(false);
//				currentUser.addTaxRate(wizard.getNewTaxRate());
//				taxRateViewer.refresh();
//				taxRateViewer.getTable().setRedraw(true);
//				setDirty(true);
//			}
//		}));
//				
//		taxRateSection.setClient(client);
//	}
//	
//	private void createColumn(final int index, final String label, final TableColumnLayout tableLayout, final int weight) {
//		final TableViewerColumn col = new TableViewerColumn(taxRateViewer, SWT.NONE, index);
//		col.getColumn().setText(label);
//		col.getColumn().setAlignment(SWT.CENTER);
//		tableLayout.setColumnData(col.getColumn(), new ColumnWeightData(weight));
//	}
//	
	@Persist
	public void save() {
		LOG.debug("Saving user {}", currentUser.getName());
		userService.saveCurrentUser(currentUser);
		
		AccountingContext accCtx = context.get(AccountingContext.class);
		if (accCtx != null) {
			if (!currentUser.getName().equals(accCtx.getUserName())) {
				LOG.info("Change in user name, from {} to {}. Saving context now", accCtx.getUserName(), currentUser.getName());
				accCtx = new AccountingContext(currentUser.getName(), accCtx.getDbFileName());
				context.set(AccountingContext.class, accCtx);
				AccountingPreferences.storeContext(accCtx);
			}
		} else {
			LOG.debug("No context here!");
		}
		
		setDirty(false);
	}
//	
//	/**
//	 *
//	 */
//	private class TaxRateCellLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
//		
//       @Override
//       public Image getColumnImage(Object element, int columnIndex) {
//	        return null;
//       }
//
//       @Override
//       public String getColumnText(Object element, int columnIndex) {
//       	if (element == null || !(element instanceof TaxRate)) {
//       		return Constants.HYPHEN;
//       	}
//       	
//       	final TaxRate tr = (TaxRate) element;
//
//       	switch (columnIndex) {
//			case COLUMN_TAX_RATE_ABBREVIATION:
//				return tr.getShortName();
//			case COLUMN_TAX_RATE_NAME:
//				return tr.getLongName();
//			case COLUMN_TAX_RATE:
//				return FormatUtil.formatPercentValue(tr.getRate());
//			default:
//				return Constants.HYPHEN;
//			}
//       }
//	}
}
