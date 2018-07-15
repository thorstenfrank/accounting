/**
 * 
 */
package de.tfsw.accounting.service.derby;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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

/**
 * @author thorsten
 *
 */
@Component(service = DummyService.class)
public class AccountingDerbyService implements DummyService {

	private static final Logger LOG = LogManager.getLogger(AccountingDerbyService.class);
	
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	
	@Activate
	protected void activate() {
		LOG.info(">>> Firing up derby db <<<");
		
		String dbLoc = Platform.getInstanceLocation().getURL().toString();
		dbLoc = dbLoc.substring(5);
		dbLoc = "jdbc:derby:" + dbLoc + "derbydb;create=true";
		LOG.info("Using [{}] as DB location", dbLoc);
		
		Properties properties = new Properties();
		properties.put("flyway.driver", "org.apache.derby.jdbc.EmbeddedDriver");
		properties.put("flyway.url", dbLoc);
		//properties.put("flyway.user", "accounting");
		Flyway flyway = new Flyway(getClass().getClassLoader());
		flyway.configure(properties);
		
		//flyway.setLocations("/db/migration");
		flyway.migrate();
		
		LOG.debug("Flyway target version: {}", flyway.info().current().getVersion());
		
		Map map = new HashMap<>();
		map.put(PersistenceUnitProperties.CLASSLOADER, getClass().getClassLoader());
		map.put(PersistenceUnitProperties.JDBC_URL, dbLoc);
		
		PersistenceProvider persistenceProvider = new PersistenceProvider();
		entityManagerFactory = persistenceProvider.createEntityManagerFactory("derby-eclipselink", map);
		entityManager = entityManagerFactory.createEntityManager();
		
		getDummies().forEach(dummy -> {
			LOG.trace("Dummy found (id / name): {} / {}", dummy.getId(), dummy.getName());
		});
	}
	
	@Deactivate
	protected void deactivate() {
		LOG.info(">>> Shutting down derby db <<<");
		entityManager.close();
		entityManagerFactory.close();
		this.entityManager = null;
		this.entityManagerFactory = null;
	}
	
	@Override
	public List<Dummy> getDummies() {
		return entityManager.createQuery("SELECT d FROM Dummy d", Dummy.class).getResultList();
	}

	@Override
	public void saveDummy(Dummy dummy) {
		
	}

}
