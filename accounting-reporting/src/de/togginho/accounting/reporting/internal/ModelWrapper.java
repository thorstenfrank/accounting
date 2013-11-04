/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.reporting.internal;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;

import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.Invoice;
import de.togginho.accounting.model.InvoicePosition;
import de.togginho.accounting.model.PaymentType;
import de.togginho.accounting.model.Price;
import de.togginho.accounting.util.CalculationUtil;
import de.togginho.accounting.util.FormatUtil;

/**
 * @author thorsten
 *
 */
public class ModelWrapper {
	
	public static final String CALCULATED_TOTAL = "CALCULATED_TOTAL"; //$NON-NLS-1$
	
	private static final String DOT = "."; //$NON-NLS-1$

	private static final String GET = "get"; //$NON-NLS-1$
	
	private static final Logger LOG = Logger.getLogger(ModelWrapper.class);
	
	private Object model;
	
	private Price calculatedTotal;
	
	/**
     * 
     */
    public ModelWrapper() {
	    super();
    }
    
    /**
     * 
     * @param model
     */
    public ModelWrapper(Object model) {
    	setModel(model);
    }
    
	/**
     * @param model the model to set
     */
    public void setModel(Object model) {
    	this.model = model;
    	
    	if (model instanceof InvoicePosition) {
    		calculatedTotal = CalculationUtil.calculatePrice((InvoicePosition) model);
    	} else if (model instanceof Invoice) {
    		calculatedTotal = CalculationUtil.calculateTotalPrice((Invoice) model);
    	} else if (model instanceof Expense) {
    		calculatedTotal = CalculationUtil.calculatePrice((Expense) model);
    	}
    }
    
    /**
     * 
     * @param property
     * @return
     */
    public String get(String property) {
    	
    	try {    		
	        Object result = get(model, property);
	        
	        if (result != null) {
	        	if (result instanceof String) {
	        		return (String) result;
	        	} else if (result instanceof BigDecimal) {
	        		return FormatUtil.formatDecimalValue((BigDecimal) result);
	        	}
	        	
	        	return result.toString();
	        }
        } catch (Exception e) {
        	LOG.error(String.format("Error reading property [%s] from model of type [%s]", property, model.getClass().getName()), e);
        }
    	
    	return null;
    }
    
    /**
     * 
     * @param property
     * @param defaultValue
     * @return
     */
    public String get(String property, String defaultValue) {
    	String result = get(property);
    	return result != null ? result : defaultValue;
    }
    
    /**
     * 
     * @param msgKey
     * @param propertyToBind
     * @return
     */
    public String bind(String msgKey, String propertyToBind) {
    	String msg = getMessage(msgKey);
    	String binding = get(propertyToBind);
    	if (msg != null) {
    		return binding != null ? Messages.bind(msg, binding) : msg;
    	}
    
    	return null;
    }
    
    /**
     * 
     * @param msgKey
     * @param propertyToBind
     * @return
     */
    public String bindAsDate(String msgKey, String propertyToBind) {
    	return Messages.bind(getMessage(msgKey), formatAsDate(propertyToBind));
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public String getMessage(String key) {
    	try {
	        return (String) Messages.class.getField(key).get(new String());
        } catch (NoSuchFieldException e) {
	        LOG.error(String.format("Message key [%s] doesn't exist", key), e); //$NON-NLS-1$
        } catch (SecurityException e) {
        	LOG.error("Error accessing message key " + key, e); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
        	LOG.error("Error accessing message key " + key, e); //$NON-NLS-1$
        } catch (IllegalAccessException e) {
        	LOG.error("Error accessing message key " + key, e); //$NON-NLS-1$
        }
    	
    	return null;
    }
    
    /**
     * 
     * @param property
     * @return
     */
    public String formatAsDate(String property) {
    	try {
	        Object result = get(model, property);
	        
	        if (result != null && result instanceof Date) {
	        	return FormatUtil.formatDate((Date) result);
	        }
        } catch (Exception e) {
        	LOG.error(String.format("Error parsing date from property [%s]", property), e); //$NON-NLS-1$
        }
    	
    	return null;
    }
    
    /**
     * This is a shortcut for calling <code>formatAsCurrency(property, Constants.HYPHEN)}
     * 
     * @param property
     * @return
     */
    public String formatAsCurrency(String property) {
    	return formatAsCurrency(property, Constants.HYPHEN);
    }
    
    /**
     * 
     * @param property
     * @return
     */
    public String formatAsCurrency(String property, String defaultIfNull) {
    	try {
	        Object result = get(model, property);
	        
	        if (result != null && result instanceof BigDecimal) {
	        	return FormatUtil.formatCurrency((BigDecimal) result);
	        }
        } catch (Exception e) {
        	LOG.error(String.format("Error parsing currency from property [%s]", property), e); //$NON-NLS-1$
        }
    	
    	return defaultIfNull;    	
    }
    
    /**
     * 
     * @param property
     * @param defaultIfNull
     * @return
     */
    public String formatAsPercentage(String property, String defaultIfNull) {
    	try {
	        Object result = get(model, property);
	        
	        if (result != null && result instanceof BigDecimal) {
	        	return FormatUtil.formatPercentValue((BigDecimal) result);
	        }
        } catch (Exception e) {
        	LOG.error(String.format("Error parsing percentage from property [%s]", property), e); //$NON-NLS-1$
        }
    	
    	return defaultIfNull;
    }
    
    /**
     * 
     * @param property
     * @return
     */
    public JRDataSource getAsDataSource(String property) {
    	try {
	        Object result = get(model, property);
	        if (result instanceof Collection) {
	        	List<ModelWrapper> wrapped = new ArrayList<ModelWrapper>();
	        	for (Object source : (Collection<?>) result) {
	        		wrapped.add(new ModelWrapper(source));
	        	}
	        	
	        	return new CollectionDataSource(wrapped);
	        }
        } catch (Exception e) {
	        LOG.error("Could not build data source", e);
        }
    	
    	return null;
    }
    
    /**
     * 
     * @param object
     * @param property
     * @return
     * @throws Exception
     */
    private Object get(Object object, String property) throws Exception {
    	String children = null;
    	
    	if (property.startsWith(CALCULATED_TOTAL)) {
    		property = property.substring(property.indexOf(DOT) + 1);
    		object = calculatedTotal;
    	}
    	
    	if (property.contains(DOT)) {
    		children = property.substring(property.indexOf(DOT) + 1);
    		property = property.substring(0, property.indexOf(DOT));
    	}
    	
    	final String readMethodName = GET + property.substring(0, 1).toUpperCase() + property.substring(1);
    	LOG.debug("Looking for accessor method " + readMethodName); //$NON-NLS-1$
    	
    	Method readMethod = object.getClass().getMethod(readMethodName);
    	    	    	
    	Object result = readMethod.invoke(object);
    	
    	if (children != null && result != null) {
    		return get(result, children);
    	}
    	
    	return result;
    }
    
    public static void main(String[] args) {
    	System.out.println(PaymentType.TRADE_CREDIT instanceof Object);
    }
}
