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

import org.apache.log4j.Logger;

import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.BankAccount;
import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.PaymentTerms;
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
import de.togginho.accounting.xml.generated.XmlPaymentTerms;
import de.togginho.accounting.xml.generated.XmlPaymentType;
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
	private static final Logger LOG = Logger.getLogger(ModelMapper.class);
	
	/**
	 * 
	 */
	private XmlUser xmlUser;
	
	/**
	 * 
	 */
	protected ModelToXml() {
		// nothing to do here...
	}

	/**
	 * 
	 * @param user
	 * @param invoices
	 * @return
	 */
	protected XmlUser convertToXml(User user, Set<Client> clients, Set<Invoice> invoices) {
		LOG.info("Exporting User to XML: " + user.getName()); //$NON-NLS-1$
		
		xmlUser = new XmlUser();
		xmlUser.setName(user.getName());
		xmlUser.setTaxId(user.getTaxNumber());
		xmlUser.setDescription(user.getDescription());
		
		convertBankAccount(user);
		
		if (user.getAddress() != null) {
			xmlUser.setAddress(convertAddress(user.getAddress()));
		}
		
		convertClients(clients);
		
		convertTaxRates(user);
		
		if (invoices != null && !invoices.isEmpty()) {
			LOG.debug("Converting invoices..."); //$NON-NLS-1$
        	XmlInvoices xmlInvoices = new XmlInvoices();
        	xmlUser.setInvoices(xmlInvoices);
        	
        	for (Invoice invoice : invoices) {
        		xmlInvoices.getInvoice().add(convertInvoice(invoice));
        	}
		} else {
			LOG.debug("No invoices to convert!"); //$NON-NLS-1$
		}
		
		return xmlUser;
	}

	/**
	 * 
	 * @param user
	 */
	private void convertBankAccount(User user) {
	    if (user.getBankAccount() != null) {
	    	LOG.debug("Converting bank account"); //$NON-NLS-1$
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
	private void convertClients(Set<Client> clients) {
	    if (clients != null && !clients.isEmpty()) {
	    	LOG.debug("Converting clients..."); //$NON-NLS-1$
			XmlClients xmlClients = new XmlClients();
			xmlUser.setClients(xmlClients);
			for (Client client : clients) {
				LOG.debug("Converting client " + client.getName()); //$NON-NLS-1$
				XmlClient xmlClient = new XmlClient();
				xmlClient.setName(client.getName());
				xmlClient.setAddress(convertAddress(client.getAddress()));
				xmlClient.setDefaultPaymentTerms(convertPaymentTerms(client.getDefaultPaymentTerms()));
				xmlClients.getClient().add(xmlClient);
			}
		} else {
			LOG.debug("No clients to convert..."); //$NON-NLS-1$
		}
    }
	
	/**
	 * 
	 * @param address
	 * @return
	 */
	private XmlAddress convertAddress(Address address) {
		if (LOG.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("Converting address "); //$NON-NLS-1$
			sb.append(address.getStreet());
			sb.append(" | "); //$NON-NLS-1$
			sb.append(address.getPostalCode());
			sb.append(" | "); //$NON-NLS-1$
			sb.append(address.getCity());
			LOG.debug(sb.toString());
		}
		
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
		LOG.debug("Converting tax rate " + taxRate.toLongString()); //$NON-NLS-1$
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
		LOG.debug("Converting invoice " + invoice.getNumber()); //$NON-NLS-1$
		XmlInvoice xmlInvoice = new XmlInvoice();
		xmlInvoice.setNumber(invoice.getNumber());
		
		xmlInvoice.setCancelledDate(convertDate(invoice.getCancelledDate()));
		xmlInvoice.setClient(invoice.getClient().getName());
		xmlInvoice.setCreationDate(convertDate(invoice.getCreationDate()));
		// xmlInvoice.setDueDate(convertDate(invoice.getDueDate()));
		xmlInvoice.setInvoiceDate(convertDate(invoice.getInvoiceDate()));		
		xmlInvoice.setPaymentDate(convertDate(invoice.getPaymentDate()));
		xmlInvoice.setSentDate(convertDate(invoice.getSentDate()));
		xmlInvoice.setPaymentTerms(convertPaymentTerms(invoice.getPaymentTerms()));
		
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
	private XMLGregorianCalendar convertDate(Date date) {
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
	
	/**
	 * 
	 * @param paymentTerms
	 * @return
	 */
	private XmlPaymentTerms convertPaymentTerms(PaymentTerms paymentTerms) {
		if (paymentTerms == null) {
			paymentTerms = PaymentTerms.getDefault();
		}
		XmlPaymentTerms xmlTerms = new XmlPaymentTerms();
		xmlTerms.setFullPaymentTargetInDays(paymentTerms.getFullPaymentTargetInDays());
		xmlTerms.setType(XmlPaymentType.NET);
		return xmlTerms;
	}
}
