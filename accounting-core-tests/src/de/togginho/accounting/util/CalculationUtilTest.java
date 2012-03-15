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
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import de.togginho.accounting.BaseTestFixture;
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
public class CalculationUtilTest extends BaseTestFixture {
	
	private static final BigDecimal TOTAL_NET = new BigDecimal("1171");
	private static final BigDecimal TOTAL_TAX = new BigDecimal("22.99");
	private static final BigDecimal TOTAL_GROSS = new BigDecimal("1193.99");
	private static final BigDecimal NET_ONE = new BigDecimal("21");
	private static final BigDecimal TAX_ONE = new BigDecimal("3.99");
	private static final BigDecimal GROSS_ONE = new BigDecimal("24.99");
	private static final BigDecimal NET_TWO = new BigDecimal("1050");
	private static final BigDecimal REVENUE_NET = new BigDecimal("1071");
	private static final BigDecimal REVENUE_TAX = new BigDecimal("3.99");
	private static final BigDecimal REVENUE_GROSS = new BigDecimal("1074.99");
	
	private static final TaxRate TEST_VAT = new TaxRate();
	
	private static final Invoice TEST_INVOICE = new Invoice();
	
	@BeforeClass
	public static void createTestInvoice() {
    	TEST_VAT.setShortName("USt.");
    	TEST_VAT.setLongName("Umsatzsteuer");
    	TEST_VAT.setRate(new BigDecimal("0.19"));
    	
    	TEST_INVOICE.setNumber("JUnitTestInvoice");
    	
        InvoicePosition ip1 = new InvoicePosition();
        ip1.setQuantity(new BigDecimal("2"));
        ip1.setPricePerUnit(new BigDecimal("10.5"));
        ip1.setTaxRate(TEST_VAT);
        ip1.setRevenueRelevant(true);

        InvoicePosition ip2 = new InvoicePosition();
        ip2.setQuantity(new BigDecimal("10.5"));
        ip2.setPricePerUnit(new BigDecimal("100.00"));
        ip2.setRevenueRelevant(true);

        InvoicePosition ip3 = new InvoicePosition();
        ip3.setQuantity(new BigDecimal("1"));
        ip3.setPricePerUnit(new BigDecimal("100.00"));
        ip3.setTaxRate(TEST_VAT);
        ip3.setRevenueRelevant(false);
        
        List<InvoicePosition> positions = new ArrayList<InvoicePosition>();
        positions.add(ip1);
        positions.add(ip2);
        positions.add(ip3);
        
        TEST_INVOICE.setInvoicePositions(positions);
	}
	
    /**
     * Test method for {@link CalculationUtil#calculateNetPrice(de.togginho.accounting.model.InvoicePosition)}.
     */
    @Test
    public void testCalculateNetPrice() {
    	// check null handling
    	assertEquals(BigDecimal.ZERO, CalculationUtil.calculateNetPrice(new InvoicePosition()));
        assertAreEqual(NET_ONE, CalculationUtil.calculateNetPrice(TEST_INVOICE.getInvoicePositions().get(0)));
        assertAreEqual(NET_TWO, CalculationUtil.calculateNetPrice(TEST_INVOICE.getInvoicePositions().get(1)));
    }
    
    /**
     * Test method for {@link CalculationUtil#calculateTotalPrice(Invoice)}.
     */
    @Test
    public void testCalculateTotalPrice() {
    	Price price = CalculationUtil.calculateTotalPrice(TEST_INVOICE);
    	assertNotNull(price);
    	
    	assertAreEqual(TOTAL_GROSS, price.getGross());
    	assertAreEqual(TOTAL_NET, price.getNet());
    	assertAreEqual(TOTAL_TAX, price.getTax());
    }
    
    /**
     * Test method for {@link CalculationUtil#calculateSubTotalsByTaxRate(Invoice)}.
     */    
    @Test
    public void testCalculateSubTotalsByTaxRate() {
    	final Map<TaxRate, BigDecimal> subtotals = CalculationUtil.calculateSubTotalsByTaxRate(TEST_INVOICE);
    	assertNotNull(subtotals);
    	assertEquals(2, subtotals.size());
    	assertNotNull(subtotals.get(null));
    	assertAreEqual(NET_TWO, subtotals.get(null));
    	assertNotNull(subtotals.get(TEST_VAT));
    	assertAreEqual(new BigDecimal("121"), subtotals.get(TEST_VAT));
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
    	assertAreEqual(NET_ONE, price.getNet());
    	assertAreEqual(TAX_ONE, price.getTax());
    	assertAreEqual(GROSS_ONE, price.getGross());
    	
    	price = CalculationUtil.calculatePrice(TEST_INVOICE.getInvoicePositions().get(1));
    	assertAreEqual(NET_TWO, price.getNet());
    	assertNull(price.getTax());
    	assertEquals(price.getNet(), price.getGross());
    }
    
    /**
     * Test method for {@link CalculationUtil#calculateRevenue(Invoice)}.
     */
    @Test
    public void testCalculateRevenue() {
    	Price price = CalculationUtil.calculateRevenue(TEST_INVOICE);
    	assertAreEqual(REVENUE_GROSS, price.getGross());
    	assertAreEqual(REVENUE_NET, price.getNet());
    	assertAreEqual(REVENUE_TAX, price.getTax());
    }
}
