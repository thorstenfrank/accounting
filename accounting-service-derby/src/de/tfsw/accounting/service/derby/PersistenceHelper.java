package de.tfsw.accounting.service.derby;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.flywaydb.core.Flyway;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.model.AbstractBaseEntity;

/**
 * 
 * @author tfrank1
 *
 */
final class PersistenceHelper {
	
	private static final Logger LOG = LogManager.getLogger(PersistenceHelper.class);
	
	private static final String DRIVER_NAME = EmbeddedDriver.class.getName();
	
	private static final PersistenceHelper INSTANCE = new PersistenceHelper();
	
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	
	/**
	 * 
	 */
	static EntityManager init() {
		INSTANCE.initIfNecessary();
		return INSTANCE.entityManager;
	}
	
	/**
	 * 
	 * @param entity
	 */
	static <E extends AbstractBaseEntity> void save(E entity) {
		EntityTransaction tx = INSTANCE.entityManager.getTransaction();
		tx.begin();
		
		try {
			INSTANCE.entityManager.persist(entity);
			tx.commit();
		} catch (PersistenceException e) {
			LOG.error("Error saving entity: " + entity.toString(), e);
			if (tx.isActive()) {
				tx.rollback();
			}
			throw new AccountingException("Error saving entity", e);
		}
	}
	
	private void initIfNecessary() {
		if (entityManager == null) {
			LOG.info("Firing up derby database");
			final String dbLoc = buildDbUrl();
			
			setupDatabase(dbLoc);
			initPersistence(dbLoc);
		} else {
			LOG.trace("Persistence already up, skipping this step");
		}
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
	
	private void initPersistence(String dbLoc) {
		final Map<String, Object> map = new HashMap<>();
		map.put(PersistenceUnitProperties.JDBC_DRIVER, DRIVER_NAME);
		map.put(PersistenceUnitProperties.CLASSLOADER, getClass().getClassLoader());
		map.put(PersistenceUnitProperties.JDBC_URL, dbLoc);
		
		PersistenceProvider persistenceProvider = new PersistenceProvider();
		entityManagerFactory = persistenceProvider.createEntityManagerFactory("derby-eclipselink", map);
		entityManager = entityManagerFactory.createEntityManager();
	}
	
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
	
	/** */
	private PersistenceHelper() {
		
	}
}
