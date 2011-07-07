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
package de.togginho.accounting.util;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.Price;

/**
 *
 * @author thorsten frank
 */
public final class CalculationUtil {

    /** Logging instance. */
    private static final Logger LOG = Logger.getLogger(CalculationUtil.class);

    /**
     * 
     */
    private CalculationUtil() {
    	
    }
    
    /**
     * Calculates the total price of an invoice.
     * 
     * @param invoice the invoice to calculate
     * @return the total {@link Price} of the invoice
     */
    public static Price calculateTotalPrice(Invoice invoice) {
    	Price price = new Price();
    	
    	for (InvoicePosition position : invoice.getInvoicePositions()) {
    		price.setNet(calculateNetPrice(position));
    		if (position.isTaxApplicable()) {
    			price.setTax(price.getNet().multiply(position.getTaxRate().getRate()));
    			price.setGross(price.getNet().add(price.getTax()));
    		} else {
    			price.setGross(price.getNet());
    		}
    	}
    	
    	return price;
    }
    
    /**
     * Calculates the total net price of an invoice by adding up the net prices of all the positions contained within
     * that invoice.
     *
     * @param invoice the invoice for which calculate the total net price
     *
     * @return  the total net price of the invoice
     *
     * @see #calculateNetPrice(de.togginho.accounting.model.InvoicePosition) 
     */
    public static BigDecimal calculateTotalNetPrice(Invoice invoice) {
        LOG.debug("Calculating total net price for invoice no "+invoice.getNumber());
        
        BigDecimal totalNet = BigDecimal.ZERO;

        if (invoice.getInvoicePositions() != null) {
            for (InvoicePosition pos : invoice.getInvoicePositions()) {
                totalNet = totalNet.add(calculateNetPrice(pos));
            }
            LOG.debug(String.format("Total net price for invoice no [%s] is [%s]",invoice.getNumber(), totalNet.toString()));
        } else {
        	LOG.warn("No invoice positions, cannot compute total net price for invoice " + invoice.getNumber());
        }

        

        return totalNet;
    }

    /**
     * Calculates the net price of a single invoice position by multiplying its quantity by the price per unit, i.e.
     * the price excluding taxes.
     *
     * @param invoicePosition   the invoice position to calculate
     *
     * @return  the net price of the invoice position, 
     * 			or {@link BigDecimal#ZERO} if either quantity or pricePerUnit is <code>null</code>
     */
    public static BigDecimal calculateNetPrice(InvoicePosition invoicePosition) {
    	if (invoicePosition.getQuantity() == null || invoicePosition.getPricePerUnit() == null) {
    		return BigDecimal.ZERO;
    	}
    	BigDecimal netPrice = invoicePosition.getQuantity().multiply(invoicePosition.getPricePerUnit());
    	LOG.debug(String.format("Net price for Invoice Position is [%s]", netPrice.toString()));
    	return netPrice;
    }

    /**
     * Calculates the total gross price of a single invoice position by applying the configured tax rate to its net price.
     * <p>If the {@link InvoicePosition} has no tax rate, the net price is returned.</p>
     * 
     * @param invoicePosition the invoice position to calculate
     * 
     * @return the gross price of the invoice position
     * 
     * @see #calculateNetPrice(InvoicePosition)
     */
    public static BigDecimal calculateGrossPrice(InvoicePosition invoicePosition) {
    	BigDecimal netPrice = calculateNetPrice(invoicePosition);
    	if (!invoicePosition.isTaxApplicable()) {
    		return netPrice;
    	}
    	
    	return netPrice.add(netPrice.multiply(invoicePosition.getTaxRate().getRate()));
    }
    
    /**
     * Calculates the tax amount for a single {@link InvoicePosition} by multiplying its net price with the applicabe tax rate.
     * <p>If the {@link InvoicePosition} has no tax rate, <code>null</code> is returned.</p>
     * 
     * @param invoicePosition the {@link InvoicePosition} to calculate the tax amount for
     * 
     * @return the tax amount, or <code>null</code> if the invoice position has no tax rate
     */
    public static BigDecimal calculateTaxAmount(InvoicePosition invoicePosition) {
    	BigDecimal netPrice = calculateNetPrice(invoicePosition);
    	if (!invoicePosition.isTaxApplicable()) {
    		return null;
    	}
    	LOG.debug(String.format("Applying tax rate %s (%s)", invoicePosition.getTaxRate().getShortName(), invoicePosition.getTaxRate().getRate()));
    	return netPrice.multiply(invoicePosition.getTaxRate().getRate());
    }
    
    /**
     *
     * @param invoice
     * @return
     */
    public static BigDecimal calculateTotalTaxAmount(Invoice invoice) {
    	BigDecimal totalTax = BigDecimal.ZERO;

    	if (invoice.getInvoicePositions() != null) {
            for (InvoicePosition pos : invoice.getInvoicePositions()) {
                if (pos.isTaxApplicable()) {
                    totalTax = totalTax.add(calculateTaxAmount(pos));
                }
            }

            LOG.debug(String.format("Total tax amount for invoice [%s] is [%s]", invoice.getNumber(), totalTax.toString()));    		
    	} else {
    		LOG.warn("No positions, cannot compute total tax amount for invoice " + invoice.getNumber());
    	}
        
        return totalTax;
    }

    /**
     *
     * @param invoice
     * @return
     */
    public static BigDecimal calculateTotalGrossPrice(Invoice invoice) {
    	BigDecimal totalGross = BigDecimal.ZERO;
    	
    	if (invoice.getInvoicePositions() != null) {
            for (InvoicePosition pos : invoice.getInvoicePositions()) {
                totalGross = totalGross.add(calculateGrossPrice(pos));
            }
            
            LOG.debug(String.format("Total gross price for invoice [%s] is [%s]", invoice.getNumber(), totalGross.toString()));    		
    	} else {
    		LOG.warn("No positions, cannot compute total gross price for invoice " + invoice.getNumber());
    	}
        
        return totalGross;
    }
}
