/**
 * 
 */
package de.tfsw.accounting.service.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingInitService;
import de.tfsw.accounting.service.spi.PersistenceStatusProvider;

/**
 * @author thorsten
 *
 */
@Component(service = AccountingInitService.class, enabled = true, immediate = true)
public class InitService implements AccountingInitService {
	
	private static final Logger LOG = LogManager.getLogger(InitService.class);
	
	@Reference
	private PersistenceStatusProvider persistenceStatusProvider;
	
	@Activate
	protected void activate() {
		LOG.info("InitService is up, persistence: {}", persistenceStatusProvider.getStatus());
	}
	
	@Deactivate
	protected void deactivate() {
		LOG.info("Service Core deactivating...");
	}
	
	
	@Override
	public void init(AccountingContext context) {
		LOG.debug("I WON'T INIT ANYTHING!");
	}
	
	@Override
	public AccountingContext importModelFromXml(String sourceXmlFile, String dbFileLocation) {
		LOG.debug("I WON'T IMPORT ANYTHING!");
		return null;
	}

}
