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

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
public class ModelMapper {

	/**
	 * 
	 */
	private static final String JAXB_CONTEXT = "de.togginho.accounting.xml.generated";	
	
	/**
	 * 
	 */
	private ModelMapper() {
		
	}
	
	/**
	 * 
	 * @param user
	 * @param invoices
	 * @param targetFile
	 */
	public static void modelToXml(User user, Set<Invoice> invoices, String targetFile) {
		try {
	        Marshaller marshaller = JAXBContext.newInstance(JAXB_CONTEXT).createMarshaller();
	        
	        XmlUser xmlUser = convertUser(user);
	        
	        if (invoices != null) {
	        	XmlInvoices xmlInvoices = new XmlInvoices();
	        	xmlUser.setInvoices(xmlInvoices);
	        	
	        	for (Invoice invoice : invoices) {
	        		xmlInvoices.getInvoice().add(convertInvoice(invoice));
	        	}
	        }
	        
	        marshaller.marshal(xmlUser, new File(targetFile));
        } catch (JAXBException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	private static XmlUser convertUser(User user) {
		XmlUser xmlUser = new XmlUser();
		xmlUser.setName(user.getName());
		xmlUser.setTaxId(user.getTaxNumber());
		xmlUser.setDescription(user.getDescription());
		
		if (user.getBankAccount() != null) {
			BankAccount account = user.getBankAccount();
			XmlBankAccount xmlBankAccount = new XmlBankAccount();
			xmlUser.setBankAccount(xmlBankAccount);
			
			xmlBankAccount.setAccountNumber(account.getAccountNumber());
			xmlBankAccount.setBankCode(account.getBankCode());
			xmlBankAccount.setBankName(account.getBankName());
		}
		
		if (user.getAddress() != null) {
			xmlUser.setAddress(convertAddress(user.getAddress()));
		}
		
		if (user.getClients() != null) {
			XmlClients xmlClients = new XmlClients();
			xmlUser.setClients(xmlClients);
			for (Client client : user.getClients()) {
				XmlClient xmlClient = new XmlClient();
				xmlClient.setName(client.getName());
				xmlClient.setAddress(convertAddress(client.getAddress()));
				xmlClients.getClient().add(xmlClient);
			}
		}
		
		if (user.getTaxRates() != null && !user.getTaxRates().isEmpty()) {
			XmlTaxRates xmlTaxRates = new XmlTaxRates();
			xmlUser.setTaxRates(xmlTaxRates);
			
			for (TaxRate taxRate : user.getTaxRates()) {
				xmlTaxRates.getTaxRate().add(converTaxRate(taxRate));
			}
		}
		
		return xmlUser;
	}
	
	/**
	 * 
	 * @param address
	 * @return
	 */
	private static XmlAddress convertAddress(Address address) {
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
	 * @param invoice
	 * @return
	 */
	private static XmlInvoice convertInvoice(Invoice invoice) {
		XmlInvoice xmlInvoice = new XmlInvoice();
		xmlInvoice.setNumber(invoice.getNumber());
		
		xmlInvoice.setCancelledDate(toXml(invoice.getCancelledDate()));
		xmlInvoice.setClient(invoice.getClient().getName());
		xmlInvoice.setCreationDate(toXml(invoice.getCreationDate()));
		xmlInvoice.setDueDate(toXml(invoice.getDueDate()));
		xmlInvoice.setInvoiceDate(toXml(invoice.getInvoiceDate()));		
		xmlInvoice.setPaymentDate(toXml(invoice.getPaymentDate()));
		xmlInvoice.setSentDate(toXml(invoice.getSentDate()));
		
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
			}
		}
		
		return xmlInvoice;
	}
	
	/**
	 * 
	 * @param taxRate
	 * @return
	 */
	private static XmlTaxRate converTaxRate(TaxRate taxRate) {
		XmlTaxRate xmlTaxRate = new XmlTaxRate();
		xmlTaxRate.setAbbreviation(taxRate.getShortName());
		xmlTaxRate.setName(taxRate.getLongName());
		xmlTaxRate.setRate(taxRate.getRate());
		return xmlTaxRate;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	private static XMLGregorianCalendar toXml(Date date) {
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
