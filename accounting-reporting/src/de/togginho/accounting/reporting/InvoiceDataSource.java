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

import java.util.Locale;
import java.util.Map;

/**
 * Jasperreport data source to produce invoices.
 *  
 * @author thorsten frank
 */
public class InvoiceDataSource extends AbstractReportDataSource {

    //private final static Log LOG = LogFactory.getLog(InvoiceDataSource.class);

    private static final String INVOICE_KEY = "invoice";
    
	private InvoiceWrapper wrapper;
    
    /**
     * Creates a new invoice generator that uses the supplied wrapper to fill the report with dynamic data.
     * 
     * @param wrapper	the {@link InvoiceWrapper} to use for report generation
     */
    public InvoiceDataSource(InvoiceWrapper wrapper) {
    	this.wrapper = wrapper;
    }
    	
	@Override
	protected Locale getLocaleForReport() {
		return wrapper.getLocale();
	}

	/**
	 * Adds the {@link InvoiceWrapper} that holds dynamic information about the invoice being produced.
	 * 
	 * <p>The map key is <code>invoice</code>.</p>
	 */
	@Override
	protected void addFieldsToMap(Map<String, Object> fieldMap) {
		fieldMap.put(INVOICE_KEY, wrapper);
	}
}
