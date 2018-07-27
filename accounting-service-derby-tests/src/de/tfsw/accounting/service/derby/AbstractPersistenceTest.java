package de.tfsw.accounting.service.derby;

import org.junit.BeforeClass;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingInitService;
import de.tfsw.accounting.util.FileUtil;

public abstract class AbstractPersistenceTest {

	static final String TEST_USER_NAME = "JUnit User";
	static final String TEST_DB_PATH = FileUtil.defaultDataPath();
	
	@BeforeClass
	public static void initPersistence() {
		ServiceLocator.getService(AccountingInitService.class)
			.init(new AccountingContext(TEST_USER_NAME, TEST_DB_PATH));
	}
}
