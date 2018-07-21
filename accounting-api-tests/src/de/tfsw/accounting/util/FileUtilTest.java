/**
 * 
 */
package de.tfsw.accounting.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @author thorsten
 *
 */
public class FileUtilTest {

	@Test
	public void test() {
		assertNotNull(FileUtil.defaultDataPath());
	}
}
