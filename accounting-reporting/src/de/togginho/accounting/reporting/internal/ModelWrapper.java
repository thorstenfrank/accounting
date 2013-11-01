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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import de.togginho.accounting.model.Address;
import de.togginho.accounting.model.User;

/**
 * @author thorsten
 *
 */
public class ModelWrapper {
	
	private static final String DOT = "."; //$NON-NLS-1$
	
	private static final Logger LOG = Logger.getLogger(ModelWrapper.class);
	
	private Object model;

	/**
     * @param model the model to set
     */
    public void setModel(Object model) {
    	this.model = model;
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
	        	return result instanceof String ? (String) result : result.toString();
	        }
        } catch (Exception e) {
        	LOG.error(String.format("Error reading property [%s] from model of type [%s]", property, model.getClass().getName()), e);
        }
    	
    	return null;
    }
    
    /**
     * 
     * @param property
     * @param binding
     * @return
     */
    public String bind(String property, String binding) {
    	return Messages.bind(getMessage(binding), get(property));
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
     * @param object
     * @param property
     * @return
     * @throws Exception
     */
    private Object get(Object object, String property) throws Exception {
    	String children = null;
    	
    	if (property.contains(DOT)) {
    		children = property.substring(property.indexOf(DOT) + 1);
    		property = property.substring(0, property.indexOf(DOT));
    	}
    	
    	PropertyDescriptor desc = new PropertyDescriptor(property, object.getClass());
    	Method readMethod = desc.getReadMethod();
    	
    	Object result = readMethod.invoke(object);
    	
    	if (children != null && result != null) {
    		return get(result, children);
    	}
    	
    	return result;
    }
    
    public static void main(String[] args) {
	    User user = new User();
	    user.setName("My User Name");
	    
	    Address address = new Address();
	    address.setCity("Da Ciddy");
	    user.setAddress(address);
	    
	    ModelWrapper wrapper = new ModelWrapper();
	    wrapper.setModel(user);
	    
	    System.out.println("Name: " + wrapper.get("name"));
	    System.out.println("City: " + wrapper.get("address.city"));
	    System.out.println(wrapper.bind("name", "Email"));
    }
}
