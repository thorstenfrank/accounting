/**
 * 
 */
package de.tfsw.accounting.service;

import java.util.Collection;
import java.util.Set;

import com.db4o.query.Predicate;

import de.tfsw.accounting.model.AbstractBaseEntity;

/**
 * @author tfrank1
 *
 */
public interface Persistence {

	<T> Set<T> runFindQuery(Predicate<T> predicate);

	<T> Set<T> runFindQuery(Class<T> targetType);

	void deleteEntity(AbstractBaseEntity entity);

	void storeEntities(Collection<? extends AbstractBaseEntity> entities);

	void storeEntity(final AbstractBaseEntity entity);

	void shutDown();

	void init(String dbFileName);
}
