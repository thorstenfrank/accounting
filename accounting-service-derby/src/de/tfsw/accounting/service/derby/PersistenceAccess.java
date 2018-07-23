package de.tfsw.accounting.service.derby;

import java.util.Set;

import de.tfsw.accounting.model.AbstractBaseEntity;

/**
 * Bundle-internal service.
 *
 */
public interface PersistenceAccess {
	
	/**
	 * 
	 * @param entityClass
	 * @param id
	 * @param <E>
	 * @return
	 */
	<E extends AbstractBaseEntity> E findById(Class<E> entityClass, Object id);
	
	/**
	 * 
	 * @param entityType
	 * @return
	 */
	<E extends AbstractBaseEntity> Set<E> findAll(Class<E> entityType);
	
	/**
	 * 
	 * @param entity
	 * @param <E>
	 */
	<E extends AbstractBaseEntity> void save(E entity);
}
