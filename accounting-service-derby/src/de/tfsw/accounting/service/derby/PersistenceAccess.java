package de.tfsw.accounting.service.derby;

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
	 * @param entity
	 * @param <E>
	 */
	<E extends AbstractBaseEntity> void save(E entity);
}
