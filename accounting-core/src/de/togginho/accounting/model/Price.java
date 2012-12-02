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
package de.togginho.accounting.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A container for a price including tax, which applies to both an {@link InvoicePosition} and an entire {@link Invoice}.
 * 
 * <p> Each {@link Price} instance can hold net, gross and tax amounts that make up a single price. 
 * {@link #getTax()} may be <code>null</code> if the invoice position or invoice is not tax applicable, in which
 * case net and gross price will be equal.</p> 
 * 
 * <p>This class is not meant to be used directly by clients, but should rather be obtained using the calculation
 * utility {@link de.togginho.accounting.util.CalculationUtil}.</p>
 * 
 * @author thorsten
 *
 * @see de.togginho.accounting.util.CalculationUtil#calculatePrice(InvoicePosition)
 * @see de.togginho.accounting.util.CalculationUtil#calculateTotalPrice(Invoice)
 */
public class Price implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -58766435614756711L;
    
    public static final String FIELD_NET = "net";
    public static final String FIELD_TAX = "tax";
    public static final String FIELD_GROSS = "gross";
    
    /** **/
    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);
    private static final int SCALE = 2;
           
	private BigDecimal net;
	
	private BigDecimal tax;
	
	private BigDecimal gross;

	private PropertyChangeSupport propertyChangeSupport;
	
	/**
     * Creates a new price with no values.
     */
    public Price() {
    	this (BigDecimal.ZERO, null, BigDecimal.ZERO);
    }
    
	/**
	 * Creates a new price with the specified values.
	 * 
     * @param net net price
     * @param tax tax amount
     * @param gross gross price
     */
    public Price(BigDecimal net, BigDecimal tax, BigDecimal gross) {
    	propertyChangeSupport = new PropertyChangeSupport(this);
	    this.net = net;
	    this.tax = tax;
	    this.gross = gross;
    }

	/**
     * @return the net price
     */
    public BigDecimal getNet() {
    	return net;
    }

	/**
     * @param net the net price
     */
    public void setNet(BigDecimal net) {
    	final BigDecimal oldValue = this.net;
    	this.net = net;
    	propertyChangeSupport.firePropertyChange("net", oldValue, net);
    }

	/**
     * @return the tax amount
     */
    public BigDecimal getTax() {
    	return tax;
    }

	/**
     * @param tax the tax amount
     */
    public void setTax(BigDecimal tax) {
    	final BigDecimal oldValue = this.tax;
    	this.tax = tax;
    	propertyChangeSupport.firePropertyChange("tax", oldValue, this.tax);
    }

	/**
     * @return the gross price
     */
    public BigDecimal getGross() {
    	return gross;
    }

	/**
     * @param gross the gross price
     */
    public void setGross(BigDecimal gross) {
    	final BigDecimal oldValue = this.gross;
    	this.gross = gross;
    	propertyChangeSupport.firePropertyChange("gross", oldValue, this.gross);
    }
    
    /**
     * 
     * @param net
     */
    public void addToNet(BigDecimal net) {
    	if (net == null) {
    		return;
    	}
    	
    	if (this.net == null) {
    		setNet(BigDecimal.ZERO);
    	}
    	
    	setNet(this.net.add(net));
    }
    
    /**
     * 
     * @param tax
     */
    public void addToTax(BigDecimal tax) {
    	if (tax == null) {
    		return;
    	}
    	
    	if (this.tax == null) {
    		setTax(BigDecimal.ZERO);
    	}
    	
    	setTax(this.tax.add(tax));
    }
    
    /**
     * 
     * @param gross
     */
    public void addToGross(BigDecimal gross) {
    	if (gross == null) {
    		return;
    	}
    	
    	if (this.gross == null) {
    		setGross(BigDecimal.ZERO);
    	}
    	setGross(this.gross.add(gross));
    }
    
    /**
     * 
     * @param price
     */
    public void add(Price price) {
    	if (price == null) {
    		return;
    	}
    	addToNet(price.getNet());
    	addToTax(price.getTax());
    	addToGross(price.getGross());
    }
    
    /**
     * 
     * @param price
     */
    public void subtract(Price price) {
    	if (price == null) {
    		return;
    	}
    	
    	if (net == null) {
    		setNet(BigDecimal.ZERO);
    	}
    	if (price.getNet() != null) {
    		setNet(net.subtract(price.getNet()));
    	}
    	
    	if (gross == null) {
    		setGross(BigDecimal.ZERO);
    	}
    	if (price.getGross() != null) {
    		setGross(gross.subtract(price.getGross()));
    	}
    	
    	if (price.getTax() != null) {
    		if (tax == null) {
    			setTax(BigDecimal.ZERO);
    		}
    		setTax(tax.subtract(price.getTax()));
    	}
    }
    
    /**
     * 
     * @param taxRate may be <code>null</code>
     */
    public void calculateGrossFromNet(TaxRate taxRate) {
    	if (taxRate == null || taxRate.getRate() == null) {
    		setTax(null);
    		setGross(net);
    	} else if (net == null) {
    		setNet(BigDecimal.ZERO);
    		setGross(net);
    	} else {
    		setTax(net.multiply(taxRate.getRate()));
    		setGross(net.add(tax));
    	}
    }
    
    /**
     * 
     * @param taxRate may be <code>null</code>
     */
    public void calculateNetFromGross(TaxRate taxRate) {
    	if (taxRate == null || taxRate.getRate() == null) {
    		setTax(null);
    		setNet(gross);
    	} else if (gross == null) {
    		setGross(BigDecimal.ZERO);
    		setNet(gross);
    	} else {
    		// Net = Gross / (1 + taxRate)
    		setNet(gross.divide(BigDecimal.ONE.add(taxRate.getRate()), MATH_CONTEXT).setScale(SCALE, MATH_CONTEXT.getRoundingMode()));
    		setTax(gross.subtract(net));
    	}
    }
    
	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}
}