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
package de.togginho.accounting;

import java.util.Locale;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.reporting.InvoiceGenerator;
import de.togginho.accounting.reporting.ReportingException;

/**
 * @author thorsten
 *
 */
class ReportingServiceImpl implements ReportingService {

	/**
	 * {@inheritDoc}.
	 * @see de.togginho.accounting.ReportingService#generateInvoiceToPdf(de.togginho.accounting.model.Invoice, java.lang.String)
	 */
	@Override
	public void generateInvoiceToPdf(Invoice invoice, String fileLocation) {
		InvoiceGenerator gen = new InvoiceGenerator(invoice, Locale.getDefault());
		
		try {
			gen.generateReportToFile(fileLocation);
		} catch (ReportingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
