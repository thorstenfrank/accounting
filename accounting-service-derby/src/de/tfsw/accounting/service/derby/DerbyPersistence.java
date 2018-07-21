/**
 * 
 */
package de.tfsw.accounting.service.derby;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.flywaydb.core.Flyway;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import de.tfsw.accounting.AccountingContext;
import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingInitService;
import de.tfsw.accounting.model.AbstractBaseEntity;

/**
 * exposes both external and bundle-internal DB access.
 * 
 * @author thorsten
 *
 */
@Component(service = {AccountingInitService.class, PersistenceAccess.class})
public class DerbyPersistence implements AccountingInitService, PersistenceAccess {
	
	private static final Logger LOG = LogManager.getLogger(DerbyPersistence.class);
	
	private static final String DRIVER_NAME = org.apache.derby.jdbc.EmbeddedDriver.class.getName();
	
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	
	@Deactivate
	protected void shutdownDB() {
		LOG.debug("Shutting down database");
		if (entityManager != null) {
			entityManager.close();
		}
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}
	
	@Override
	public String getDescription() {
		return "Accounting Service using Apache Derby DB: " + this.hashCode();
	}
	
	@Override
	public void init(AccountingContext context) {
		LOG.debug("Init DB service for context {}", context);
		
		final String dbURL = buildDbUrl(context.getDbLocation());
		LOG.debug("Using db location: {}", dbURL);
		
		setupDatabase(dbURL);
		initPersistence(dbURL);
	}
	
	@Override
	public AccountingContext importModelFromXml(String sourceXmlFile, String dbFileLocation) {
		LOG.warn("XML model import not yet implemented!");
		throw new AccountingException("XML import not yet implemented!");
	}
	
	/**
	 * 
	 */
	@Override
	public <E extends AbstractBaseEntity> E findById(Class<E> entityClass, Object id) {
		return entityManager.find(entityClass, id);
	}

	/**
	 * 
	 */
	@Override
	public <E extends AbstractBaseEntity> void save(E entity) {
		final EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		
		try {
			entityManager.persist(entity);
			tx.commit();
		} catch (PersistenceException e) {
			LOG.error("Error saving entity: " + entity.toString(), e);
			if (tx.isActive()) {
				tx.rollback();
			}
			throw new AccountingException("Error saving entity", e);
		}
	}

	private void setupDatabase(String dbLoc) {
		LOG.trace("Setting up database...");
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
		this.entityManagerFactory = persistenceProvider.createEntityManagerFactory("derby-eclipselink", map);
		this.entityManager = entityManagerFactory.createEntityManager();
	}
	
	private String buildDbUrl(final String dbLocation) {
		StringBuilder sb = new  StringBuilder("jdbc:derby:");
		sb.append(dbLocation);
		sb.append(";create=true");
		return sb.toString();
	}
}
