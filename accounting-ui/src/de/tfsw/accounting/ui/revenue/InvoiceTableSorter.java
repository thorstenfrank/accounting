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
package de.tfsw.accounting.ui.revenue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tfsw.accounting.ui.AbstractTableSorter;

/**
 * @author thorsten
 * 
 */
public class InvoiceTableSorter extends AbstractTableSorter<InvoiceWrapper> {
	
	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(InvoiceTableSorter.class);
	
	/**
	 * 
	 */
	protected InvoiceTableSorter() {
		super(InvoiceWrapper.class);
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.tfsw.accounting.ui.AbstractTableSorter#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	/**
     * {@inheritDoc}.
     * @see AbstractTableSorter#doCompare(Object, Object, int)
     */
    @Override
    protected int doCompare(InvoiceWrapper i1, InvoiceWrapper i2, int columnIndex) {
		switch (columnIndex) {
		case RevenueDialog.COLUMN_INDEX_INVOICE_NUMBER:
			return i1.getNumber().compareTo(i2.getNumber());
		case RevenueDialog.COLUMN_INDEX_INVOICE_DATE:
			return i1.getInvoice().getInvoiceDate().compareTo(i2.getInvoice().getInvoiceDate());
		case RevenueDialog.COLUMN_INDEX_PAYMENT_DATE:
			return i1.getInvoice().getPaymentDate().compareTo(i2.getInvoice().getPaymentDate());
		case RevenueDialog.COLUMN_INDEX_CLIENT:
			return i1.getClient().compareTo(i2.getClient());
		case RevenueDialog.COLUMN_INDEX_NET_PRICE:
			return i1.getPrice().getNet().compareTo(i2.getPrice().getNet());
		case RevenueDialog.COLUMN_INDEX_TAX_AMOUNT:
			return i1.getPrice().getTax().compareTo(i2.getPrice().getTax());
		case RevenueDialog.COLUMN_INDEX_GROSS_PRICE:
			return i1.getPrice().getGross().compareTo(i2.getPrice().getGross());
		default:
			return 0;
		}
    }
}
