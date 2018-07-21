/**
 * 
 */
package de.tfsw.accounting.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author thorsten
 *
 */
public final class FileUtil {
	
	private static final Logger LOG = LogManager.getLogger(FileUtil.class);
	
	/**
	 * 
	 * @return {@link File#getAbsolutePath()}
	 */
	public static String defaultDataPath() {
		String result = System.getProperty("user.home"); // the fallback
		final String osgiInstanceArea = System.getProperty("osgi.instance.area");
		if (osgiInstanceArea != null) {
			try {
				final File instanceArea = new File(new URI(osgiInstanceArea));
				final File dbLoc = new File(instanceArea, "data");
				result = dbLoc.getAbsolutePath();
			} catch (URISyntaxException e) {
				LOG.warn("Error converting to URI: " + osgiInstanceArea, e);
			}			
		}
		
		LOG.trace("Default data path: {}", result);
		
		return result;
	}
	
	private FileUtil() {}
}
