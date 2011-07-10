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
package de.togginho.accounting.xml;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.BankAccount;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.TaxRate;
import de.togginho.accounting.model.User;
import de.togginho.accounting.xml.generated.XmlAddress;
import de.togginho.accounting.xml.generated.XmlBankAccount;
import de.togginho.accounting.xml.generated.XmlClient;
import de.togginho.accounting.xml.generated.XmlClients;
import de.togginho.accounting.xml.generated.XmlInvoice;
import de.togginho.accounting.xml.generated.XmlInvoicePosition;
import de.togginho.accounting.xml.generated.XmlTaxRate;
import de.togginho.accounting.xml.generated.XmlTaxRates;
import de.togginho.accounting.xml.generated.XmlUser;

/**
 * @author thorsten
 *
 */
class XmlToModel {

	private XmlUser xmlUser;
	
	private User user;
	
	private Set<Invoice> invoices;
	
	/**
	 * 
	 * @param xmlUser
	 */
	protected XmlToModel(XmlUser xmlUser) {
		this.xmlUser = xmlUser;
	}

    protected void convert() {
    	convertUser();
    	convertInvoices();
    }
	
	/**
     * @return the user
     */
    protected User getUser() {
    	return user;
    }

	/**
     * @return the invoices
     */
    protected Set<Invoice> getInvoices() {
    	return invoices;
    }
    
	/**
	 * 
	 */
	private void convertUser() {
		user = new User();
		user.setName(xmlUser.getName());
		user.setDescription(xmlUser.getDescription());
		user.setTaxNumber(xmlUser.getTaxId());
		
		convertBankAccount();
		
		if (xmlUser.getAddress() != null) {
			user.setAddress(convertAddress(xmlUser.getAddress()));
		}
		
		convertTaxRates();
		
		convertClients();
	}

	/**
	 * 
	 */
	private void convertInvoices() {
		if (xmlUser.getInvoices() != null) {
			invoices = new HashSet<Invoice>();
			
			for (XmlInvoice xmlInvoice : xmlUser.getInvoices().getInvoice()) {
				Invoice invoice = new Invoice();
				invoice.setNumber(xmlInvoice.getNumber());
				invoice.setClient(user.getClientByName(xmlInvoice.getClient()));
				invoice.setUser(user);
				
				invoice.setCancelledDate(convertDate(xmlInvoice.getCancelledDate()));
				invoice.setCreationDate(convertDate(xmlInvoice.getCreationDate()));
				invoice.setDueDate(convertDate(xmlInvoice.getDueDate()));
				invoice.setInvoiceDate(convertDate(xmlInvoice.getInvoiceDate()));
				invoice.setPaymentDate(convertDate(xmlInvoice.getPaymentDate()));
				invoice.setSentDate(convertDate(xmlInvoice.getSentDate()));
				
				if (xmlInvoice.getInvoicePositions() != null) {
					invoice.setInvoicePositions(new ArrayList<InvoicePosition>());
					
					for (XmlInvoicePosition xmlPos : xmlInvoice.getInvoicePositions().getInvoicePosition()) {
						InvoicePosition pos = new InvoicePosition();
						pos.setDescription(xmlPos.getDescription());
						pos.setPricePerUnit(xmlPos.getPricePerUnit());
						pos.setQuantity(xmlPos.getQuantity());
						pos.setRevenueRelevant(xmlPos.isRevenueRelevant());
						pos.setUnit(xmlPos.getUnit());
						if (xmlPos.getTaxRate() != null) {
							pos.setTaxRate(findOrCreateTaxRate(xmlPos.getTaxRate()));
						}
						invoice.getInvoicePositions().add(pos);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param xmlTaxRate
	 * @return
	 */
	private TaxRate findOrCreateTaxRate(XmlTaxRate xmlTaxRate) {		
		for (TaxRate existing : user.getTaxRates()) {
			if (existing.getLongName().equals(xmlTaxRate.getName()) 
				&& existing.getShortName().equals(xmlTaxRate.getAbbreviation()) 
			    && existing.getRate().equals(xmlTaxRate.getRate())) {
				return existing;
			}
		}
		
		// wasn't found in existing, add
		TaxRate taxRate = convertTaxRate(xmlTaxRate);
		user.getTaxRates().add(taxRate);
		return taxRate;
	}
	
	/**
	 * 
	 */
	private void convertClients() {
	    XmlClients xmlClients = xmlUser.getClients();
		if (xmlClients != null && !xmlClients.getClient().isEmpty()) {
			Set<Client> clients = new HashSet<Client>();
			user.setClients(clients);
			for (XmlClient xmlClient : xmlClients.getClient()) {
				Client client = new Client();
				client.setAddress(convertAddress(xmlClient.getAddress()));
				client.setName(xmlClient.getName());
				clients.add(client);
			}
		}
    }

	/**
	 * 
	 */
	private void convertTaxRates() {
	    XmlTaxRates xmlTaxRates = xmlUser.getTaxRates();
		if (xmlTaxRates != null && !xmlTaxRates.getTaxRate().isEmpty()) {
			Set<TaxRate> taxRates = new HashSet<TaxRate>();
			user.setTaxRates(taxRates);
			for (XmlTaxRate xmlRate : xmlTaxRates.getTaxRate()) {
				taxRates.add(convertTaxRate(xmlRate));
			}
		}
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
	    return rate;
    }

	/**
	 * 
	 */
	private void convertBankAccount() {
	    if (xmlUser.getBankAccount() != null) {
			XmlBankAccount xmlAccount = xmlUser.getBankAccount();
			BankAccount account = new BankAccount();
			user.setBankAccount(account);
			account.setAccountNumber(xmlAccount.getAccountNumber());
			account.setBankCode(xmlAccount.getBankCode());
			account.setBankName(xmlAccount.getBankName());
		}
    }
    
	/**
	 * 
	 * @param xmlAddress
	 * @return
	 */
	private Address convertAddress(XmlAddress xmlAddress) {
		Address address = new Address();
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
	private Date convertDate(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar == null) {
			return null;
		}
		Calendar cal = xmlCalendar.toGregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
}
