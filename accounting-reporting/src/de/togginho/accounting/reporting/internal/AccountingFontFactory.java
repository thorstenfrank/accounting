/**
 * 
 */
package de.togginho.accounting.reporting.internal;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.SimpleFontFamily;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;

/**
 * Font factory for all accounting documents generated via Jasperreports. 
 * 
 * @author thorsten frank
 *
 */
public class AccountingFontFactory implements ExtensionsRegistry, ExtensionsRegistryFactory {
		
	private static List<FontFamily> fontFamilies;
	
	/**
	 * @see net.sf.jasperreports.extensions.ExtensionsRegistryFactory#createRegistry(java.lang.String, net.sf.jasperreports.engine.JRPropertiesMap)
	 */
	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) {
		return this;
	}

	/**
	 * @see net.sf.jasperreports.extensions.ExtensionsRegistry#getExtensions(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getExtensions(Class<T> extensionType) {
		if (FontFamily.class.equals(extensionType) == false) {
			return null;
		}
		
		if (fontFamilies == null) {
			fontFamilies = new ArrayList<FontFamily>();
			
			SimpleFontFamily titillium = new SimpleFontFamily();
			titillium.setName("Titillium");
			
			titillium.setNormalFace(new AccountingFont("reports/fonts/Titillium-Regular.otf"));
			titillium.setBoldFace(new AccountingFont("reports/fonts/Titillium-Bold.otf"));
			titillium.setBoldItalicFace(new AccountingFont("reports/fonts/Titillium-BoldItalic.otf"));
			titillium.setItalicFace(new AccountingFont("reports/fonts/Titillium-RegularItalic.otf"));

			titillium.setPdfEmbedded(true);
			
			fontFamilies.add(titillium);
		}
		
		return (List<T>)fontFamilies;
	}

}
