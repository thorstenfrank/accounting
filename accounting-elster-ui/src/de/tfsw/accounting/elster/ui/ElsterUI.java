package de.tfsw.accounting.elster.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.tfsw.accounting.elster.ElsterService;

/**
 * The activator class controls the plug-in life cycle
 */
public class ElsterUI extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "de.tfsw.accounting.elster.ui"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static ElsterUI plugin;
	
	/**
	 * 
	 */
	private ElsterServiceProvider elsterServiceProvider;
	
	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ElsterUI getDefault() {
		return plugin;
	}
	
	/**
	 * 
	 * @param provider
	 */
	protected void registerElsterServiceProvider(ElsterServiceProvider provider) {
		this.elsterServiceProvider = provider;
	}
	
	/**
	 * 
	 * @param provider
	 */
	protected void unregisterElsterServiceProvider(ElsterServiceProvider provider) {
		if (this.elsterServiceProvider == provider) {
			this.elsterServiceProvider = null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ElsterService getElsterService() {
		return elsterServiceProvider != null ? elsterServiceProvider.getElsterService() : null;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * 
	 * @return
	 */
	public static IEclipsePreferences getPreferences() {
		return InstanceScope.INSTANCE.getNode(PLUGIN_ID);
	}
}
