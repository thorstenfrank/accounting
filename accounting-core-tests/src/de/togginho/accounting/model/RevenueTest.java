/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author thorsten
 *
 */
public class RevenueTest {

	/**
	 * Test method for {@link de.togginho.accounting.model.Revenue#getRevenueNet()}.
	 */
	@Test
	public void testRevenueCalculation() {
		TaxRate taxRate = new TaxRate();
		taxRate.setLongName("JUnitTax");
		taxRate.setRate(new BigDecimal("0.15"));
		taxRate.setShortName("JUT");
		
		// INVOICE 1
		InvoicePosition ip1 = new InvoicePosition();
		ip1.setPricePerUnit(new BigDecimal("50"));
		ip1.setQuantity(new BigDecimal("160"));
		ip1.setTaxRate(taxRate);
		
		List<InvoicePosition> ipList1 = new ArrayList<InvoicePosition>();
		ipList1.add(ip1);
		
		Invoice invoice1 = new Invoice();
		invoice1.setInvoicePositions(ipList1);
		
		// INVOICE 2
		InvoicePosition ip2 = new InvoicePosition();
		ip2.setPricePerUnit(new BigDecimal("598.78"));
		ip2.setQuantity(BigDecimal.ONE);

		List<InvoicePosition> ipList2 = new ArrayList<InvoicePosition>();
		ipList2.add(ip2);
		
		Invoice invoice2 = new Invoice();
		invoice2.setInvoicePositions(ipList2);		
		
		List<Invoice> invoices = new ArrayList<Invoice>();
		invoices.add(invoice1);
		invoices.add(invoice2);
		
		Revenue revenue = new Revenue();
		revenue.setInvoices(invoices);
		
		assertEquals(0, new BigDecimal("8598.78").compareTo(revenue.getRevenueNet()));
		assertEquals(0, new BigDecimal("9798.78").compareTo(revenue.getRevenueGross()));
		assertEquals(0, new BigDecimal("1200").compareTo(revenue.getRevenueTax()));
	}
}
