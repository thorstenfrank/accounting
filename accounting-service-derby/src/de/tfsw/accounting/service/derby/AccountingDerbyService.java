/**
 * 
 */
package de.tfsw.accounting.service.derby;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.flywaydb.core.Flyway;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import de.tfsw.accounting.DummyService;
import de.tfsw.accounting.model.Dummy;
import de.tfsw.accounting.service.spi.PersistenceStatusProvider;

/**
 * @author thorsten
 *
 */
@Component(service = {DummyService.class, PersistenceStatusProvider.class}, enabled = true, immediate = true)
public class AccountingDerbyService implements DummyService, PersistenceStatusProvider {

	private static final Logger LOG = LogManager.getLogger(AccountingDerbyService.class);
	
	private static final String DRIVER_NAME = EmbeddedDriver.class.getName();
	
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	
	@Activate
	protected void activate() {
		LOG.info("Firing up derby database");
		
		final String dbLoc = buildDbUrl();
		
		setupDatabase(dbLoc);
		initPersistence(dbLoc);
	}
		
	@Deactivate
	protected void deactivate() {
		LOG.info("Shutting down derby db");
		entityManager.close();
		entityManagerFactory.close();
		this.entityManager = null;
		this.entityManagerFactory = null;
	}
	
	@Override
	public String getStatus() {
		return "I think we're up...";
	}

	@Override
	public List<Dummy> getDummies() {
		return entityManager.createQuery("SELECT d FROM Dummy d", Dummy.class).getResultList();
	}

	@Override
	public void saveDummy(Dummy dummy) {
		
	}

	private void initPersistence(String dbLoc) {
		final Map<String, Object> map = new HashMap<>();
		map.put(PersistenceUnitProperties.JDBC_DRIVER, DRIVER_NAME);
		map.put(PersistenceUnitProperties.CLASSLOADER, getClass().getClassLoader());
		map.put(PersistenceUnitProperties.JDBC_URL, dbLoc);
		
		PersistenceProvider persistenceProvider = new PersistenceProvider();
		entityManagerFactory = persistenceProvider.createEntityManagerFactory("derby-eclipselink", map);
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	private void setupDatabase(String dbLoc) {
		Properties properties = new Properties();
		properties.put("flyway.driver", DRIVER_NAME);
		properties.put("flyway.url", dbLoc);
		//properties.put("flyway.user", "accounting");
		
		Flyway flyway = new Flyway(getClass().getClassLoader());
		flyway.configure(properties);
		flyway.migrate();
		
		LOG.debug("Flyway schema version: {}", flyway.info().current().getVersion());
	}
	
	/**
	 * 
	 * @return
	 */
	private String buildDbUrl() {
		StringBuilder sb = new  StringBuilder("jdbc:derby:");
		
		// On Windows, we need to strip away the leading slash, otherwise derby
		// cannot work with the connection URL
		String path = Platform.getInstanceLocation().getURL().getFile();
		if (isWindows() && path.startsWith("/")) {
			path = path.substring(1);
		}
		
		sb.append(path);
		sb.append("data;create=true");
		
		final String url = sb.toString();
		LOG.trace("Using db location: {}", url);
		
		return url;
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("win");
	}
}
