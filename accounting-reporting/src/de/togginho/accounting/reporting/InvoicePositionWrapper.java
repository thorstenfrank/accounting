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

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * Locale-sensitive wrapper around an {@link InvoicePosition} to provide properly calculated and formatted values to the
 * report generator.
 *
 * @author thorsten frank
 */
public class InvoicePositionWrapper implements Constants {

    private InvoicePosition invoicePosition;
    
    /**
     * Creates a new invoice position wrapper.
     *
     * @param invoicePosition   the invoice position to wrap
     */
    public InvoicePositionWrapper(InvoicePosition invoicePosition) {
        this.invoicePosition = invoicePosition;
    }
    
    /**
     * 
     * @return {@link InvoicePosition#getQuantity()}, formatted
     */
    public String getQuantity() {
        return FormatUtil.formatDecimalValue(invoicePosition.getQuantity());
    }

    /**
     * 
     * @return {@link InvoicePosition#getUnit()}
     */
    public String getUnit() {
        return invoicePosition.getUnit();
    }

    /**
     * 
     * @return {@link InvoicePosition#getDescription()}
     */
    public String getDescription() {
        return invoicePosition.getDescription();
    }

    /**
     * 
     * @return {@link InvoicePosition#getPricePerUnit()}, formatted
     */
    public String getPricePerUnit() {
        return FormatUtil.formatCurrency(invoicePosition.getPricePerUnit());
    }

    /**
     * 
     * @return {@link CalculationUtil#calculateNetPrice(InvoicePosition)}
     */
    public String getTotalPriceNet() {
        return FormatUtil.formatCurrency(CalculationUtil.calculateNetPrice(invoicePosition));
    }

    /**
     * 
     * @return the tax rate, if tax is applicable, a hyphen if not
     */
    public String getTaxRate() {
        if (invoicePosition.isTaxApplicable()) {
            return FormatUtil.formatPercentValue(invoicePosition.getTaxRate().getRate());
        }

        return Constants.HYPHEN;
    }
}
