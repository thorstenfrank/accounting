/**
 * 
 */
package de.tfsw.accounting.ui.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import de.tfsw.accounting.ClientService;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.PaymentTerms;
import de.tfsw.accounting.model.PaymentType;
import de.tfsw.accounting.ui.AbstractFormBasedEditor;
import de.tfsw.accounting.ui.util.ApplicationModelHelper;

/**
 * @author tfrank1
 *
 */
public class ClientEditor extends AbstractFormBasedEditor {

	public static final String PART_ID = "de.tfsw.accounting.ui.part.clienteditor";
	
	private static final Logger LOG = LogManager.getLogger(ClientEditor.class);

	@Inject
	@Translation
	private Messages messages;
	
	@Inject
	private ClientService clientService;

	private Client client;
	
	@Override
	protected boolean createControl(Composite parent) {
		final String name = Optional
				.ofNullable(getPartProperty(ApplicationModelHelper.KEY_ELEMENT_ID))
				.orElse("New Client");
		
		LOG.trace("Creating editor for client {}", name);
		
		setPartLabel(name);
		
		parent.setLayout(new GridLayout(2, false));
		
		boolean dirty = assignEditedClient();
		
		createBasicInfoSection(parent);
		
		if (client.getDefaultPaymentTerms() == null) {
			client.setDefaultPaymentTerms(PaymentTerms.getDefault());
			dirty = true;
		}
		createPaymentTermsSection(parent);
		
		if (client.getAddress() == null) {
			client.setAddress(new Address());
			// should we set the editor to dirty here too?
		}
		createAddressSection(parent, client.getAddress());
		
		return dirty;
	}
	
	@Override
	protected boolean doSave() {
		clientService.saveClient(client);
		return true;
	}
	
	@PreDestroy
	public void dispose() {
		setDirty(false);
		client = null;
		LOG.trace("PreDestroy too");
	}
	
	@Override
	protected String getEditorHeader() {
		return messages.clientEditorHeader;
	}
	
	private boolean assignEditedClient() {
		final Optional<Client> opt = Optional.ofNullable(getPartObject(Client.class));
		if (opt.isPresent()) {
			this.client = opt.get();
			return false;
		} else {
			this.client = new Client();
			return true;
		}
	}
	
	private void createBasicInfoSection(Composite parent) {
		final Composite group = createGroup(parent, messages.labelBasicInformation);
		createTextWithLabelNotNullable(group, messages.labelClientName, client, Client.FIELD_NAME);
		createTextWithLabel(group, messages.labelClientNumber, client, Client.FIELD_CLIENT_NUMBER);
	}
	
	private void createPaymentTermsSection(Composite parent) {
		final PaymentTerms paymentTerms = client.getDefaultPaymentTerms();
		
		final Composite group = createGroup(parent, messages.labelPaymentTerms);		
		createLabel(group, messages.labelPaymentType);
		
		final Combo paymentTypes = new Combo(group, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(paymentTypes);
		final List<PaymentType> paymentTypeList = new ArrayList<PaymentType>();
		int index = 0;
		for (PaymentType element : PaymentType.values()) {
			paymentTypes.add(element.getTranslatedString());
			paymentTypeList.add(index, element);
			if (element.name().equals(paymentTerms.getPaymentType().name())) {
				paymentTypes.select(index);
			} else {
				index++;
			}
		}
		
		paymentTypes.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
        	final Combo combo = (Combo) e.getSource();
        	PaymentType newType = paymentTypeList.get(combo.getSelectionIndex());
        	if ( ! newType.name().equals(paymentTerms.getPaymentType().name())) {
            	paymentTerms.setPaymentType(newType);
            	setDirty(true);            		
        	}			
		}));
		
		createLabel(group, messages.labelPaymentTarget);
		final Spinner spinner = new Spinner(group, SWT.BORDER);
		spinner.setIncrement(1);
		spinner.setMinimum(0);
		spinner.setSelection(paymentTerms.getFullPaymentTargetInDays());
		
		spinner.addModifyListener(e -> {
			paymentTerms.setFullPaymentTargetInDays(spinner.getSelection());
			setDirty(true);
		});
	}
}
