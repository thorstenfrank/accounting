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
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.User;
import de.togginho.accounting.xml.generated.XmlUser;

/**
 * @author thorsten
 *
 */
public class ModelMapper {

	private static final Logger LOG = Logger.getLogger(ModelMapper.class);
	
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
	        
	        LOG.info("Exporting model data to file " + targetFile);
	        marshaller.marshal(new ModelToXml().convertToXml(user, invoices), new File(targetFile));
	        LOG.info("export finished successfully");
        } catch (JAXBException e) {
        	LOG.error("Error exporting data to xml", e);
        	throw new AccountingException("Error exporting data to XML", e);
        }
	}
	
	/**
	 * @param fileName
	 */
	public static void xmlToModel(String fileName) {
		try {
			LOG.info("Importing model data from file " + fileName);
	        Unmarshaller unmarshaller = JAXBContext.newInstance(JAXB_CONTEXT).createUnmarshaller();
	        final XmlUser xmlUser = (XmlUser) unmarshaller.unmarshal(new File(fileName));
	        
	        XmlToModel xmlToModel = new XmlToModel(xmlUser);
	        xmlToModel.getUser();
	        xmlToModel.getInvoices();
	        LOG.info("import finished successfully");
        } catch (JAXBException e) {
        	LOG.error("Error importing data from XML", e);
	        throw new AccountingException("Error importing data from XML", e);
        }
	}
}
