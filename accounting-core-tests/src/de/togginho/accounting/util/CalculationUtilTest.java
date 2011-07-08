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
package de.togginho.accounting.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.model.TaxRate;

/**
 * Tests for {@link CalculationUtil}.
 * 
 * @author thorsten
 *
 */
public class CalculationUtilTest {
	
	private static final BigDecimal TOTAL_NET = new BigDecimal("1071");
	private static final BigDecimal TOTAL_TAX = new BigDecimal("3.99");
	private static final BigDecimal TOTAL_GROSS = new BigDecimal("1074.99");
	private static final BigDecimal NET_ONE = new BigDecimal("21");
	private static final BigDecimal TAX_ONE = new BigDecimal("3.99");
	private static final BigDecimal GROSS_ONE = new BigDecimal("24.99");
	private static final BigDecimal NET_TWO = new BigDecimal("1050");
	
	private static final Invoice TEST_INVOICE = new Invoice();
	
	@BeforeClass
	public static void createTestInvoice() {
    	final TaxRate taxRate = new TaxRate();
    	taxRate.setShortName("USt.");
    	taxRate.setLongName("Umsatzsteuer");
    	taxRate.setRate(new BigDecimal("0.19"));
    	
    	TEST_INVOICE.setNumber("JUnitTestInvoice");
    	
        InvoicePosition ip1 = new InvoicePosition();
        ip1.setQuantity(new BigDecimal("2"));
        ip1.setPricePerUnit(new BigDecimal("10.5"));
        ip1.setTaxRate(taxRate);

        InvoicePosition ip2 = new InvoicePosition();
        ip2.setQuantity(new BigDecimal("10.5"));
        ip2.setPricePerUnit(new BigDecimal("100.00"));
        
        List<InvoicePosition> positions = new ArrayList<InvoicePosition>();
        positions.add(ip1);
        positions.add(ip2);
        
        TEST_INVOICE.setInvoicePositions(positions);
	}
	
    /**
     * Test method for {@link CalculationUtil#calculateNetPrice(de.togginho.accounting.model.InvoicePosition)}.
     */
    @Test
    public void testCalculateNetPrice() {
    	// check null handling
    	assertEquals(BigDecimal.ZERO, CalculationUtil.calculateNetPrice(new InvoicePosition()));
    	        
        assertEquals(0, NET_ONE.compareTo(CalculationUtil.calculateNetPrice(TEST_INVOICE.getInvoicePositions().get(0))));
        assertEquals(0, NET_TWO.compareTo(CalculationUtil.calculateNetPrice(TEST_INVOICE.getInvoicePositions().get(1))));
    }
    
    /**
     * Test method for {@link CalculationUtil#calculateTotalPrice(Invoice)}.
     */
    @Test
    public void testCalculateTotalPrice() {
    	Price price = CalculationUtil.calculateTotalPrice(TEST_INVOICE);
    	assertNotNull(price);
    	assertEquals(0, TOTAL_GROSS.compareTo(price.getGross()));
    	assertEquals(0, TOTAL_NET.compareTo(price.getNet()));
    	assertEquals(0, TOTAL_TAX.compareTo(price.getTax()));
    }
    
    /**
     * Test method for {@link CalculationUtil#calculatePrice(InvoicePosition)}.
     */
    @Test
    public void testCalculatePrice() {
    	Price price = CalculationUtil.calculatePrice(new InvoicePosition());
    	assertEquals(BigDecimal.ZERO, price.getNet());
    	assertEquals(BigDecimal.ZERO, price.getGross());
    	assertNull(price.getTax());
    	
    	price = CalculationUtil.calculatePrice(TEST_INVOICE.getInvoicePositions().get(0));
    	assertEquals(0, NET_ONE.compareTo(price.getNet()));
    	assertEquals(0, TAX_ONE.compareTo(price.getTax()));
    	assertEquals(0, GROSS_ONE.compareTo(price.getGross()));
    	
    	price = CalculationUtil.calculatePrice(TEST_INVOICE.getInvoicePositions().get(1));
    	assertEquals(0, NET_TWO.compareTo(price.getNet()));
    	assertNull(price.getTax());
    	assertEquals(price.getNet(), price.getGross());
    }
}
