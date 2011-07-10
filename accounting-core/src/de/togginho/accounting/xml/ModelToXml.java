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

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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
import de.togginho.accounting.xml.generated.XmlInvoicePositions;
import de.togginho.accounting.xml.generated.XmlInvoices;
import de.togginho.accounting.xml.generated.XmlTaxRate;
import de.togginho.accounting.xml.generated.XmlTaxRates;
import de.togginho.accounting.xml.generated.XmlUser;

/**
 * @author thorsten
 *
 */
class ModelToXml {

	/**
	 * 
	 */
	private XmlUser xmlUser;
	
	/**
	 * 
	 */
	protected ModelToXml() {
		
	}

	/**
	 * 
	 * @param user
	 * @param invoices
	 * @return
	 */
	protected XmlUser convertToXml(User user, Set<Invoice> invoices) {
		xmlUser = new XmlUser();
		xmlUser.setName(user.getName());
		xmlUser.setTaxId(user.getTaxNumber());
		xmlUser.setDescription(user.getDescription());
		
		convertBankAccount(user);
		
		if (user.getAddress() != null) {
			xmlUser.setAddress(convertAddress(user.getAddress()));
		}
		
		convertClients(user);
		
		convertTaxRates(user);
		
		if (invoices != null && !invoices.isEmpty()) {
        	XmlInvoices xmlInvoices = new XmlInvoices();
        	xmlUser.setInvoices(xmlInvoices);
        	
        	for (Invoice invoice : invoices) {
        		xmlInvoices.getInvoice().add(convertInvoice(invoice));
        	}
		}
		
		return xmlUser;
	}

	/**
	 * 
	 * @param user
	 */
	private void convertBankAccount(User user) {
	    if (user.getBankAccount() != null) {
			BankAccount account = user.getBankAccount();
			XmlBankAccount xmlBankAccount = new XmlBankAccount();
			xmlUser.setBankAccount(xmlBankAccount);
			
			xmlBankAccount.setAccountNumber(account.getAccountNumber());
			xmlBankAccount.setBankCode(account.getBankCode());
			xmlBankAccount.setBankName(account.getBankName());
		}
    }

	/**
	 * 
	 * @param user
	 */
	private void convertTaxRates(User user) {
	    if (user.getTaxRates() != null && !user.getTaxRates().isEmpty()) {
			XmlTaxRates xmlTaxRates = new XmlTaxRates();
			xmlUser.setTaxRates(xmlTaxRates);
			
			for (TaxRate taxRate : user.getTaxRates()) {
				xmlTaxRates.getTaxRate().add(converTaxRate(taxRate));
			}
		}
    }

	/**
	 * 
	 * @param user
	 */
	private void convertClients(User user) {
	    if (user.getClients() != null && !user.getClients().isEmpty()) {
			XmlClients xmlClients = new XmlClients();
			xmlUser.setClients(xmlClients);
			for (Client client : user.getClients()) {
				XmlClient xmlClient = new XmlClient();
				xmlClient.setName(client.getName());
				xmlClient.setAddress(convertAddress(client.getAddress()));
				xmlClients.getClient().add(xmlClient);
			}
		}
    }
	
	/**
	 * 
	 * @param address
	 * @return
	 */
	private XmlAddress convertAddress(Address address) {
		XmlAddress xmlAddress = new XmlAddress();
		xmlAddress.setCity(address.getCity());
		xmlAddress.setEmail(address.getEmail());
		xmlAddress.setFax(address.getFaxNumber());
		xmlAddress.setMobile(address.getMobileNumber());
		xmlAddress.setPhone(address.getPhoneNumber());
		xmlAddress.setPostalCode(address.getPostalCode());
		xmlAddress.setStreet(address.getStreet());
		return xmlAddress;
	}
	
	/**
	 * 
	 * @param taxRate
	 * @return
	 */
	private XmlTaxRate converTaxRate(TaxRate taxRate) {
		XmlTaxRate xmlTaxRate = new XmlTaxRate();
		xmlTaxRate.setAbbreviation(taxRate.getShortName());
		xmlTaxRate.setName(taxRate.getLongName());
		xmlTaxRate.setRate(taxRate.getRate());
		return xmlTaxRate;
	}
	
	/**
	 * 
	 * @param invoice
	 * @return
	 */
	private XmlInvoice convertInvoice(Invoice invoice) {
		XmlInvoice xmlInvoice = new XmlInvoice();
		xmlInvoice.setNumber(invoice.getNumber());
		
		xmlInvoice.setCancelledDate(convertDate(invoice.getCancelledDate()));
		xmlInvoice.setClient(invoice.getClient().getName());
		xmlInvoice.setCreationDate(convertDate(invoice.getCreationDate()));
		xmlInvoice.setDueDate(convertDate(invoice.getDueDate()));
		xmlInvoice.setInvoiceDate(convertDate(invoice.getInvoiceDate()));		
		xmlInvoice.setPaymentDate(convertDate(invoice.getPaymentDate()));
		xmlInvoice.setSentDate(convertDate(invoice.getSentDate()));
		
		if (invoice.getInvoicePositions() != null) {
			XmlInvoicePositions xmlInvoicePositions = new XmlInvoicePositions();
			xmlInvoice.setInvoicePositions(xmlInvoicePositions);
			for (InvoicePosition invoicePosition : invoice.getInvoicePositions()) {
				XmlInvoicePosition xmlInvoicePosition = new XmlInvoicePosition();
				xmlInvoicePositions.getInvoicePosition().add(xmlInvoicePosition);
				
				xmlInvoicePosition.setDescription(invoicePosition.getDescription());
				xmlInvoicePosition.setPricePerUnit(invoicePosition.getPricePerUnit());
				xmlInvoicePosition.setQuantity(invoicePosition.getQuantity());
				if (invoicePosition.isTaxApplicable()) {
					xmlInvoicePosition.setTaxRate(converTaxRate(invoicePosition.getTaxRate()));
				}
				xmlInvoicePosition.setTaxRate(null);
				xmlInvoicePosition.setUnit(invoicePosition.getUnit());
				xmlInvoicePosition.setRevenueRelevant(invoicePosition.isRevenueRelevant());
			}
		}
		
		return xmlInvoice;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	protected XMLGregorianCalendar convertDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		try {
	        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
	        xmlCal.setDay(cal.get(Calendar.DAY_OF_MONTH));
	        xmlCal.setMonth(cal.get(Calendar.MONTH) + 1);
	        xmlCal.setYear(cal.get(Calendar.YEAR));
	        return xmlCal;
        } catch (DatatypeConfigurationException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
		return null;
	}
}
