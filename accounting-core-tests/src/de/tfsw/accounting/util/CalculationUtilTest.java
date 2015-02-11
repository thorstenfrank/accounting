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
package de.tfsw.accounting.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import de.tfsw.accounting.BaseTestFixture;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.InvoicePosition;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.model.TaxRate;

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
	private static final TaxRate TEST_VAT_2 = new TaxRate();
	
	private static final Invoice TEST_INVOICE = new Invoice();
	
	private static final Invoice TEST_INVOICE_2 = new Invoice();
	
	/**
	 * 
	 */
	@BeforeClass
	public static void createTestInvoice() {
    	TEST_VAT.setShortName("USt.");
    	TEST_VAT.setLongName("Umsatzsteuer");
    	TEST_VAT.setRate(new BigDecimal("0.19"));
    	TEST_VAT.setIsVAT(true);
    	
    	TEST_VAT_2.setShortName("USt.");
    	TEST_VAT_2.setLongName("Umsatzsteuer");
    	TEST_VAT_2.setRate(new BigDecimal("0.07"));
    	TEST_VAT_2.setIsVAT(true);
    	
    	buildTestInvoice1();
        buildTestInvoice2();
	}

	/**
	 * 
	 */
	private static void buildTestInvoice1() {
		TEST_INVOICE.setNumber("JUnitTestInvoice");
        
        List<InvoicePosition> positions = new ArrayList<InvoicePosition>();
        positions.add(buildIP("2", "10.5", TEST_VAT, true)); // 21 / 3.99 / 24.99
        positions.add(buildIP("10.5", "100.00", null, true)); // 1050 / - / 1050
        positions.add(buildIP("1", "100.00", TEST_VAT, false)); // 100 / 19 / 119
        
        TEST_INVOICE.setInvoicePositions(positions);
	}
	
	/**
	 * 
	 */
	private static void buildTestInvoice2() {
		TEST_INVOICE_2.setNumber("JUnitTestInvoice2");
		List<InvoicePosition> positions = new ArrayList<InvoicePosition>();
		positions.add(buildIP("15", "67.34", TEST_VAT, true)); // 1010.10 / 191.919 / 1202.019
		positions.add(buildIP("88.5", "123.77", TEST_VAT_2, true)); // 10953.645 / 766.75515 / 11720.40015
		positions.add(buildIP("2", "77.75", null, true)); // 155.5 / - / 155.5
		TEST_INVOICE_2.setInvoicePositions(positions);
	}
	
	/**
	 * 
	 * @param quantity
	 * @param price
	 * @param tax
	 * @param revenueRelevant
	 * @return
	 */
	private static InvoicePosition buildIP(String quantity, String price, TaxRate tax, boolean revenueRelevant) {
		InvoicePosition ip = new InvoicePosition();
		ip.setQuantity(new BigDecimal(quantity));
		ip.setPricePerUnit(new BigDecimal(price));
		ip.setTaxRate(tax);
		ip.setRevenueRelevant(revenueRelevant);
		return ip;
	}
	
    /**
     * Test method for {@link CalculationUtil#calculateNetPrice(de.tfsw.accounting.model.InvoicePosition)}.
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
    
    /**
     * Test method for {@link CalculationUtil#calculateRevenueByTaxRate(Invoice)}.
     */
    @Test
    public void testCalculateRevenueByTaxRate() {
    	Map<TaxRate, Price> rev = CalculationUtil.calculateRevenueByTaxRate(TEST_INVOICE);
    	assertNotNull(rev);
    	assertEquals(2, rev.size());
    	assertTrue(rev.containsKey(null));
    	assertTrue(rev.containsKey(TEST_VAT));
    	
    	Price p = rev.get(null);
    	assertAreEqual(NET_TWO, p.getNet());
    	assertNull(p.getTax());
    	
    	p = rev.get(TEST_VAT);
    	assertAreEqual(new BigDecimal("121"), p.getNet());
    	assertAreEqual(new BigDecimal("22.99"), p.getTax());
    	
    	rev = CalculationUtil.calculateRevenueByTaxRate(TEST_INVOICE_2);
    	assertNotNull(rev);
    	assertEquals(3, rev.size());
    	assertTrue(rev.containsKey(null));
    	assertTrue(rev.containsKey(TEST_VAT));
    	assertTrue(rev.containsKey(TEST_VAT_2));
    	
    	p = rev.get(null);
    	assertAreEqual(new BigDecimal("155.5"), p.getNet());
    	assertNull(p.getTax());
    	
    	p = rev.get(TEST_VAT);
    	assertAreEqual(new BigDecimal("1010.1"), p.getNet());
    	assertAreEqual(new BigDecimal("191.919"), p.getTax());
    	
    	p = rev.get(TEST_VAT_2); // 10953.65 / 766.76
    	assertAreEqual(new BigDecimal("10953.645"), p.getNet());
    	assertAreEqual(new BigDecimal("766.75515"), p.getTax());
    }
    
    /**
     * Test method for {@link CalculationUtil#calculateTotalRevenueByTaxRate(java.util.Collection)}.
     */
    public void testCalculateTotalRevenueByTaxRate() {
    	Collection<Invoice> invoices = new ArrayList<Invoice>();
    	invoices.add(TEST_INVOICE);
    	invoices.add(TEST_INVOICE_2);
    	
    	Map<TaxRate, Price> rev = CalculationUtil.calculateTotalRevenueByTaxRate(invoices);
    	
    	assertNotNull(rev);
    	assertEquals(3, rev.size());
    	assertTrue(rev.containsKey(null));
    	assertTrue(rev.containsKey(TEST_VAT));
    	assertTrue(rev.containsKey(TEST_VAT_2));
    	
    	Price p = rev.get(TEST_VAT);
    	assertAreEqual(new BigDecimal("1131.1"), p.getNet());
    	assertAreEqual(new BigDecimal("214.909"), p.getTax());
    }
}
