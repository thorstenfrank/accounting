/**
 * 
 */
package de.tfsw.accounting.service.core;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author tfrank1
 *
 */
public final class ServiceLocator {

	public static <T> T getService(Class<T> clazz) {
		T service = null;
		
		Bundle bundle = FrameworkUtil.getBundle(ServiceLocator.class);
		if (bundle != null) {
			ServiceTracker<T, T> tracker = new ServiceTracker<>(bundle.getBundleContext(), clazz, null);
			tracker.open();
			try {
				service = tracker.waitForService(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("NO BUNDLE FOUND!");
		}
		return service;
	}
	
	private ServiceLocator() {
		
	}
}
