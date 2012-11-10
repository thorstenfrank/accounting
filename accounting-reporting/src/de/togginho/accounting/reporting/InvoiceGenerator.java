/*
 *  Copyright 2010 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.reporting;

import java.util.Map;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import de.togginho.accounting.model.Invoice;

/**
 *
 * @author thorsten frank
 */
public class InvoiceGenerator extends AbstractReportGenerator {

    //private final static Log LOG = LogFactory.getLog(InvoiceDataSource.class);

	/**
	 * 
	 */
    private static final String INVOICE_POSITION_DATASOURCE = "INVOICE_POSITION_DATASOURCE";

	/**
     * 
     */
    private static final String JASPER_PATH = "InvoiceTemplate.jasper";

    /**
     * 
     */
    private InvoiceWrapper wrapper;
    
    /**
	 * @param invoice
	 */
	public InvoiceGenerator(Invoice invoice) {
		this.wrapper = new InvoiceWrapper(invoice);
	}

	@Override
	protected void addReportParameters(Map<String, Object> params) {
		params.put(INVOICE_POSITION_DATASOURCE, new JRBeanCollectionDataSource(wrapper.getPositions()));
	}

	@Override
	protected String getReportTemplatePath() {
		return JASPER_PATH;
	}

	@Override
	protected AbstractReportDataSource getReportDataSource() {
		return new InvoiceDataSource(wrapper);
	}
}
