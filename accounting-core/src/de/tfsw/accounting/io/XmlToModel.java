/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.io;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import de.tfsw.accounting.io.xml.XmlAddress;
import de.tfsw.accounting.io.xml.XmlBankAccount;
import de.tfsw.accounting.io.xml.XmlClient;
import de.tfsw.accounting.io.xml.XmlClients;
import de.tfsw.accounting.io.xml.XmlExpense;
import de.tfsw.accounting.io.xml.XmlExpenseTemplate;
import de.tfsw.accounting.io.xml.XmlExpenseTemplates;
import de.tfsw.accounting.io.xml.XmlExpenses;
import de.tfsw.accounting.io.xml.XmlInvoice;
import de.tfsw.accounting.io.xml.XmlInvoicePosition;
import de.tfsw.accounting.io.xml.XmlInvoices;
import de.tfsw.accounting.io.xml.XmlPaymentTerms;
import de.tfsw.accounting.io.xml.XmlRecurrenceRule;
import de.tfsw.accounting.io.xml.XmlTaxRate;
import de.tfsw.accounting.io.xml.XmlTaxRates;
import de.tfsw.accounting.io.xml.XmlUser;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.BankAccount;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.DepreciationMethod;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.ExpenseType;
import de.tfsw.accounting.model.Frequency;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoicePosition;
import de.tfsw.accounting.model.PaymentTerms;
import de.tfsw.accounting.model.PaymentType;
import de.tfsw.accounting.model.RecurrenceRule;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.User;

/**
 * @author thorsten
 *
 */
class XmlToModel {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(XmlToModel.class);
	
	private XmlModelDTO model;
	
	/**
	 * 
	 * @param xmlUser
	 */
	protected XmlToModel() {
	}

	/**
	 * 
	 * @param xmlUser
	 */
    protected XmlModelDTO convert(XmlUser xmlUser) {
    	LOG.info("Converting data imported from XML..."); //$NON-NLS-1$
    	
    	this.model = new XmlModelDTO();
    	
    	// convert the user first
    	model.setUser(convertUser(xmlUser));
    	
    	// then the clients
    	model.setClients(convertClients(xmlUser.getClients()));
    	
    	// the invoices
    	model.setInvoices(convertInvoices(xmlUser.getInvoices()));
    	
    	// the expenses
    	model.setExpenses(convertExpenses(xmlUser.getExpenses()));
    	
    	// expense templates
    	model.setExpenseTemplates(convertExpenseTemplates(xmlUser.getExpenseTemplates()));
    	
    	return model;
    }

	/**
	 * 
	 */
	private User convertUser(XmlUser xmlUser) {
		LOG.debug("Converting user " + xmlUser.getName()); //$NON-NLS-1$
		User user = new User();
		user.setName(xmlUser.getName());
		user.setDescription(xmlUser.getDescription());
		user.setTaxNumber(xmlUser.getTaxId());
		
		user.setBankAccount(convertBankAccount(xmlUser.getBankAccount()));
		if (xmlUser.getAddress() != null) {
			user.setAddress(convertAddress(xmlUser.getAddress()));
		}
		user.setTaxRates(convertTaxRates(xmlUser.getTaxRates()));
		return user;
	}

	/**
	 * 
	 */
	private Set<Client> convertClients(XmlClients xmlClients) {
		Set<Client> clients = new HashSet<Client>();
		if (xmlClients != null && !xmlClients.getClient().isEmpty()) {
			LOG.debug("Converting clients..."); //$NON-NLS-1$
			clients = new HashSet<Client>();
			for (XmlClient xmlClient : xmlClients.getClient()) {
				LOG.debug("Converting client " + xmlClient.getName()); //$NON-NLS-1$
				Client client = new Client();
				client.setName(xmlClient.getName());
				client.setClientNumber(xmlClient.getClientNumber());
				client.setAddress(convertAddress(xmlClient.getAddress()));
				client.setDefaultPaymentTerms(convertPaymentTerms(xmlClient.getDefaultPaymentTerms()));
				clients.add(client);
			}
		} else {
			LOG.debug("No clients to convert"); //$NON-NLS-1$
		}
		return clients;
    }
	
	/**
	 * 
	 */
	private Set<Invoice> convertInvoices(XmlInvoices xmlInvoices) {
		Set<Invoice> invoices = null;
		if (xmlInvoices != null) {
			LOG.debug("Converting invoices..."); //$NON-NLS-1$
			invoices = new HashSet<Invoice>();
			
			for (XmlInvoice xmlInvoice : xmlInvoices.getInvoice()) {
				LOG.debug("Converting invoice " + xmlInvoice.getNumber()); //$NON-NLS-1$
				Invoice invoice = new Invoice();
				invoices.add(invoice);
				invoice.setNumber(xmlInvoice.getNumber());
				invoice.setClient(findOrCreateClient(xmlInvoice.getClient()));
				invoice.setUser(this.model.getUser());
				
				invoice.setCancelledDate(convertDate(xmlInvoice.getCancelledDate()));
				invoice.setCreationDate(convertDate(xmlInvoice.getCreationDate()));
				invoice.setInvoiceDate(convertDate(xmlInvoice.getInvoiceDate()));
				invoice.setPaymentDate(convertDate(xmlInvoice.getPaymentDate()));
				invoice.setSentDate(convertDate(xmlInvoice.getSentDate()));
				invoice.setPaymentTerms(convertPaymentTerms(xmlInvoice.getPaymentTerms()));
				
				if (xmlInvoice.getInvoicePositions() != null) {
					invoice.setInvoicePositions(new ArrayList<InvoicePosition>());
					
					for (XmlInvoicePosition xmlPos : xmlInvoice.getInvoicePositions().getInvoicePosition()) {
						InvoicePosition pos = new InvoicePosition();
						pos.setDescription(xmlPos.getDescription());
						pos.setPricePerUnit(xmlPos.getPricePerUnit());
						pos.setQuantity(xmlPos.getQuantity());
						pos.setRevenueRelevant(xmlPos.isRevenueRelevant());
						pos.setUnit(xmlPos.getUnit());
						pos.setTaxRate(findOrCreateTaxRate(xmlPos.getTaxRate()));
						invoice.getInvoicePositions().add(pos);
					}
				}
			}
		} else {
			LOG.debug("No invoices to convert."); //$NON-NLS-1$
		}
		return invoices;
	}
	
	/**
	 * 
	 */
	private Set<Expense> convertExpenses(XmlExpenses xmlExpenses) {
		Set<Expense> expenses = null;
		if (xmlExpenses != null) {
			LOG.debug("Converting expenses..."); //$NON-NLS-1$
			expenses = new HashSet<Expense>();
			
			for (XmlExpense xmlExpense : xmlExpenses.getExpense()) {
				LOG.debug("Converting expense " + xmlExpense.getDescription()); //$NON-NLS-1$
				Expense expense = new Expense();
				expense.setDescription(xmlExpense.getDescription());
				if (xmlExpense.getExpenseType() != null) {
					expense.setExpenseType(ExpenseType.valueOf(xmlExpense.getExpenseType().name()));
				}
				expense.setNetAmount(xmlExpense.getNetAmount());
				expense.setPaymentDate(convertDate(xmlExpense.getPaymentDate()));
				expense.setTaxRate(findOrCreateTaxRate(xmlExpense.getTaxRate()));
				expense.setCategory(xmlExpense.getCategory());
				if (xmlExpense.getDepreciationMethod() != null) {
					expense.setDepreciationMethod(DepreciationMethod.valueOf(xmlExpense.getDepreciationMethod().name()));
				}
				
				expenses.add(expense);
			}
			
		} else {
			LOG.debug("No expenses to convert."); //$NON-NLS-1$
		}
		return expenses;
	}
	
	/**
	 * 
	 * @param xmlTemplates
	 * @return
	 */
	private Set<ExpenseTemplate> convertExpenseTemplates(XmlExpenseTemplates xmlTemplates) {
		Set<ExpenseTemplate> templates = null;
		if (xmlTemplates != null && xmlTemplates.getTemplate().size() > 0) {
			LOG.debug("Converting Expense Templates...");
			
			templates = new HashSet<ExpenseTemplate>(xmlTemplates.getTemplate().size());
			for (XmlExpenseTemplate xmlTemplate : xmlTemplates.getTemplate()) {
				LOG.debug("Converting Expense Template: " + xmlTemplate.getDescription());
				ExpenseTemplate template = new ExpenseTemplate();
				template.setActive(xmlTemplate.isActive());
				template.setCategory(xmlTemplate.getDescription());
				template.setDescription(xmlTemplate.getDescription());
				template.setExpenseType(ExpenseType.valueOf(xmlTemplate.getExpenseType().name()));
				template.setFirstApplication(convertDate(xmlTemplate.getFirstApplication()));
				template.setLastApplication(convertDate(xmlTemplate.getLastApplication()));
				template.setNetAmount(xmlTemplate.getNetAmount());
				template.setNumberOfApplications(xmlTemplate.getNumberOfApplications());
				template.setRule(convertRecurrenceRule(xmlTemplate.getRule()));
				template.setTaxRate(findOrCreateTaxRate(xmlTemplate.getTaxRate()));
				templates.add(template);
			}
		}
		return templates;
	}
	
	/**
	 * 
	 * @param xmlRule
	 * @return
	 */
	private RecurrenceRule convertRecurrenceRule(XmlRecurrenceRule xmlRule) {
		if (xmlRule != null) {
			RecurrenceRule rule = new RecurrenceRule();
			rule.setFrequency(Frequency.valueOf(xmlRule.getFrequency().name()));
			rule.setInterval(xmlRule.getInterval());
			if (xmlRule.getCount() != null) {
				rule.setCount(xmlRule.getCount());
			} else if (xmlRule.getUntil() != null) {
				rule.setUntil(convertDate(xmlRule.getUntil()));
			}
			return rule;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param xmlTaxRate
	 * @return
	 */
	private TaxRate findOrCreateTaxRate(XmlTaxRate xmlTaxRate) {
		if (xmlTaxRate == null) {
			return null;
		}
		for (TaxRate existing : model.getUser().getTaxRates()) {
			if (existing.getLongName().equals(xmlTaxRate.getName()) 
				&& existing.getShortName().equals(xmlTaxRate.getAbbreviation()) 
			    && existing.getRate().equals(xmlTaxRate.getRate())) {
				return existing;
			}
		}
		
		// wasn't found in existing, add
		TaxRate taxRate = convertTaxRate(xmlTaxRate);
		model.getUser().getTaxRates().add(taxRate);
		return taxRate;
	}
	
	/**
	 * 
	 * @param clientName
	 * @return
	 */
	private Client findOrCreateClient(String clientName) {
		for (Client client : model.getClients()) {
			if (client.getName().equals(clientName)) {
				return client;
			}
		}
		
		// client not found, create a new one
		Client client = new Client();
		client.setName(clientName);
		model.getClients().add(client);
		return client;
	}

	/**
	 * 
	 * @param xmlTaxRates
	 * @return
	 */
	private Set<TaxRate> convertTaxRates(XmlTaxRates xmlTaxRates) {
		Set<TaxRate> taxRates = null;
		if (xmlTaxRates != null && !xmlTaxRates.getTaxRate().isEmpty()) {
			LOG.debug("Converting tax rates..."); //$NON-NLS-1$
			taxRates = new HashSet<TaxRate>();
			for (XmlTaxRate xmlRate : xmlTaxRates.getTaxRate()) {
				taxRates.add(convertTaxRate(xmlRate));
			}
		}
		return taxRates;
    }

	/**
	 * 
	 * @param xmlRate
	 * @return
	 */
	private TaxRate convertTaxRate(XmlTaxRate xmlRate) {
	    TaxRate rate = new TaxRate();
	    rate.setLongName(xmlRate.getName());
	    rate.setShortName(xmlRate.getAbbreviation());
	    rate.setRate(xmlRate.getRate());
	    rate.setIsVAT(xmlRate.isIsVat());
	    LOG.debug("Converted tax rate " + rate.toLongString()); //$NON-NLS-1$
	    return rate;
    }
	
	/**
	 * 
	 * @param xmlBankAccount
	 * @return
	 */
	private BankAccount convertBankAccount(XmlBankAccount xmlBankAccount) {
		BankAccount account = null;
		if (xmlBankAccount != null) {
	    	LOG.debug("Converting bank account"); //$NON-NLS-1$
			account = new BankAccount();
			account.setAccountNumber(xmlBankAccount.getAccountNumber());
			account.setBankCode(xmlBankAccount.getBankCode());
			account.setBankName(xmlBankAccount.getBankName());
			account.setBic(xmlBankAccount.getBic());
			account.setIban(xmlBankAccount.getIban());
		} else {
			LOG.debug("No bank account to convert."); //$NON-NLS-1$
		}
		return account;
    }
    
		
	/**
	 * 
	 * @param xmlAddress
	 * @return
	 */
	private Address convertAddress(XmlAddress xmlAddress) {
		Address address = new Address();
		address.setRecipientDetail(xmlAddress.getRecipientDetail());
		address.setCity(xmlAddress.getCity());
		address.setEmail(xmlAddress.getEmail());
		address.setFaxNumber(xmlAddress.getFax());
		address.setMobileNumber(xmlAddress.getMobile());
		address.setPhoneNumber(xmlAddress.getPhone());
		address.setPostalCode(xmlAddress.getPostalCode());
		address.setStreet(xmlAddress.getStreet());
		return address;
	}
	
	/**
	 * 
	 * @param xmlCalendar
	 * @return
	 */
	private LocalDate convertDate(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar == null) {
			return null;
		}
		return LocalDate.of(xmlCalendar.getYear(), xmlCalendar.getMonth(), xmlCalendar.getDay());
	}
	
	/**
	 * 
	 * @param xmlTerms
	 * @return
	 */
	private PaymentTerms convertPaymentTerms(XmlPaymentTerms xmlTerms) {
		if (xmlTerms == null) {
			return PaymentTerms.getDefault();
		}
		return new PaymentTerms(PaymentType.TRADE_CREDIT, xmlTerms.getFullPaymentTargetInDays());
	}
}
