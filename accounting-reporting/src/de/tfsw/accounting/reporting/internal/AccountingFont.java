/**
 * 
 */
package de.tfsw.accounting.reporting.internal;

import java.awt.Font;
import java.io.InputStream;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.fonts.SimpleFontFace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link SimpleFontFace} can't deal with pathnames in an RCP application, so this simple subclass takes care of
 * locating the font path and creating the {@link Font} itself. 
 * 
 * @author thorsten frank
 *
 */
public class AccountingFont extends SimpleFontFace {

	/**
	 * 
	 */
	private static final Logger LOG = LogManager.getLogger(AccountingFont.class);
	
	private String fontPath;
	private Font font;
	
	/**
	 * 
	 * 
	 */
	AccountingFont(String pathToFontFile) {
		super(DefaultJasperReportsContext.getInstance());
		setTtf(pathToFontFile);
	}

	/**
	 * @see net.sf.jasperreports.engine.fonts.SimpleFontFace#setTtf(java.lang.String)
	 */
	@Override
	public void setTtf(String ttf) {
		ClassLoader loader = this.getClass().getClassLoader();
		
		LOG.debug("Loading font from " + loader.getResource(ttf).toString());
		
		try (InputStream is = loader.getResourceAsStream(ttf)) {
			this.fontPath = ttf;
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (Exception e) {
			LOG.error("Error loading font " + ttf, e);
		}
	}
	
	/**
	 * @see net.sf.jasperreports.engine.fonts.SimpleFontFace#getTtf()
	 */
	@Override
	public String getTtf() {
		return fontPath;
	}

	/**
	 * @see net.sf.jasperreports.engine.fonts.SimpleFontFace#getFont()
	 */
	@Override
	public Font getFont() {
		return font;
	}

	/**
	 * @see net.sf.jasperreports.engine.fonts.SimpleFontFace#getName()
	 */
	@Override
	public String getName() {
		return font != null ? font.getName() : null;
	}
}
