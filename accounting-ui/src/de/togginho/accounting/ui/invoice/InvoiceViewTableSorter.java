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
package de.togginho.accounting.ui.invoice;

import org.apache.log4j.Logger;

import de.togginho.accounting.model.Client;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.ui.AbstractTableSorter;

/**
 * @author thorsten
 *
 */
public class InvoiceViewTableSorter extends AbstractTableSorter<Invoice> {

	private static final Logger LOG = Logger.getLogger(InvoiceViewTableSorter.class);
	
	/**
	 * 
	 */
	protected InvoiceViewTableSorter() {
		super(Invoice.class);
	}
	
	/**
	 * {@inheritDoc}
	 * @see de.togginho.accounting.ui.AbstractTableSorter#getLogger()
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
	protected int doCompare(Invoice i1, Invoice i2, int columnIndex) {
		int result = 0;
		switch (columnIndex) {
		case InvoiceView.COL_INDEX_INVOICE_NUMBER:
			result = i1.getNumber().compareTo(i2.getNumber());
		case InvoiceView.COL_INDEX_INVOICE_STATE:
			result = i1.getState().compareTo(i2.getState());
		case InvoiceView.COL_INDEX_CLIENT:
			result = compareClients(i1.getClient(), i2.getClient());
		case InvoiceView.COL_INDEX_DUE_DATE:
			result = i1.getDueDate().compareTo(i2.getDueDate());
		default:
			break;
		}
		
		return result;
	}

	/**
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	private int compareClients(Client c1, Client c2) {
		if (c1 == null && c2 != null) {
			return 1;
		} else if (c1 != null && c2 == null) {
			return -1;
		}
		
		return c1.getName().compareTo(c2.getName());
	}
}
