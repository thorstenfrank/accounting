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

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.reporting.InvoiceGenerator;

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
	public void generateInvoiceToPdf(Invoice invoice, String fileLocation, ReportGenerationMonitor monitor) {
		InvoiceGenerator gen = new InvoiceGenerator(invoice);
		gen.generateReportToFile(fileLocation, monitor);
	}

}
