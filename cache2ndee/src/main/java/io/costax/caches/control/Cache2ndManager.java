package io.costax.caches.control;

import io.costax.users.boundary.Users;
import org.hibernate.Session;

import javax.ejb.Stateless;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Provides an API for querying/managing the second level cache regions.
 *
 * @see org.hibernate.Cache
 */
@Stateless
public class Cache2ndManager {

    @PersistenceContext
    EntityManager em;

    /**
     * Clear the cache.
     */
    public void evictAllCache() {
        final Cache cache = em.getEntityManagerFactory().getCache();
        cache.evictAll();
    }

    /**
     * Remove the data for entities of the specified class (and its
     * subclasses) from the cache.
     *
     * @param entityClass entity class
     */
    public void evictCacheForEntity(Class<?> entityClass) {
        final Cache cache = em.getEntityManagerFactory().getCache();
        cache.evict(entityClass);
    }

    /**
     * Remove the data for the given entity from the cache.
     *
     * @param entityClass entity class
     * @param primaryKey  primary key
     */
    public void evictCacheForInstance(Class<?> entityClass, Object primaryKey) {
        final Cache cache = em.getEntityManagerFactory().getCache();
        cache.evict(entityClass, primaryKey);
    }

    /**
     * Whether the cache contains data for the given entity.
     *
     * @param entityClass entity class
     * @param primaryKey  primary key
     * @return boolean indicating whether the entity is in the cache
     */
    public boolean contains(Class<?> entityClass, Object primaryKey) {
        final Cache cache = em.getEntityManagerFactory().getCache();
        return cache.contains(entityClass, primaryKey);
    }

    /**
     * Evict all data from the named cache region.
     * <p>
     * The regions are create in the Query execution eg.
     * <p>
     * {@code .setHint(QueryHints.HINT_CACHE_REGION, "home")}
     *
     * @param regionName regions Name that should be clean
     * @see {@link Users#list()}
     */
    public void evictRegion(String regionName) {
        final org.hibernate.Cache cache = em.unwrap(Session.class).getSessionFactory().getCache();
        // clean regions home of the cache
        cache.evictRegion(regionName);

        // NOTE:
        // How to create the regions? See the Users#list() method please...
    }

    /**
     * Evict data from all cache regions.
     */
    public void evictAllRegions() {
        final org.hibernate.Cache cache = em.unwrap(Session.class).getSessionFactory().getCache();
        // clean all region
        cache.evictAllRegions();
    }


}
