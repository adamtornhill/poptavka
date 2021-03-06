/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eprovement.poptavka.dao;

import com.eprovement.poptavka.domain.common.DomainObject;
import com.eprovement.poptavka.domain.enums.OrderType;
import com.eprovement.poptavka.domain.common.ResultCriteria;
import com.eprovement.poptavka.util.search.Searcher;
import com.google.common.base.Preconditions;
import com.googlecode.genericdao.search.Search;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class GenericHibernateDao<T extends DomainObject> implements GenericDao<T> {

    private static final boolean FIND_LOCK_DEFAULT = false;

    /**
     * Persistent class of this DAO.
     */
    private Class<? extends T> persistentClass;

    /**
     * Used Entity Manager.
     */
    private EntityManager entityManager;

    /**
     * Creates new GenericHibernateDao with the \
     * {@link #persistentClass} being the class of generalized type (<T>).
     */
    public GenericHibernateDao() {
        this.persistentClass = (Class<T>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
    }

    /**
     * Creates new GenericHibernateDao.
     *
     * @param persistentClass
     */
    protected GenericHibernateDao(final Class<? extends T> persistentClass) {
        if (persistentClass == null) {
            throw new NullPointerException("The class given to persist must not be null!");
        }

        this.persistentClass = persistentClass;
    }

    /**
     * @param entityManager Sets entityManager.
     */
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Gets the entityManager.
     *
     * @return Returns entityManager.
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Uses the entity manager for internal purposes.
     * <p/>
     * If the {@code entityManager} is not set \
     * ( by none possible way ( ioc / manual setting)) \
     * it throws the detailed {@link IllegalStateException}.
     *
     * @return Returns entityManager.
     * @throws IllegalStateException if the {@code entityManager} is not set.
     */
    public EntityManager em() {
        if (entityManager == null) {
            throw new IllegalStateException("Entity manager is not set.");
        }

        return entityManager;
    }

    /**
     * @return Returns Hibernate entityManager.
     */
    protected org.hibernate.ejb.HibernateEntityManager getHibernateEntityManager() {
        return (org.hibernate.ejb.HibernateEntityManager) em();
    }

    /**
     * @return Returns Hibernate session
     */
    protected org.hibernate.Session getHibernateSession() {
        return getHibernateEntityManager().getSession();
    }

    /**
     * @return Returns persistentClass.
     */
    public Class<? extends T> getPersistentClass() {
        return persistentClass;
    }

    /**
     * Gets the simple name of entity class.
     *
     * @return
     */
    final protected String getEntityClassSimpleName() {
        return persistentClass.getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    public T findById(long id) {
        return findById(id, FIND_LOCK_DEFAULT);
    }

    @SuppressWarnings("unchecked")
    public T findById(long id, boolean lock) {
        final T entity = em().find(persistentClass, id);
        if (lock) {
            em().lock(entity, javax.persistence.LockModeType.WRITE);
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByIds(long[] ids, boolean lck) {
        final List<T> result = new java.util.ArrayList<>(ids.length);
        for (final long id : ids) {
            final T entity = findById(id, lck);
            result.add(entity);
        }
        return Collections.unmodifiableList(result);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        //Restrictions.eq("forLogOnly", null));
        return createQuery("select o from " + getPersistentClass().getName() + " o").getResultList();
    }

    @Override
    public List<T> findAll(ResultCriteria resultCriteria) {
        Preconditions.checkNotNull(resultCriteria);
        final Criteria entityCriteria = getHibernateSession().createCriteria(this.persistentClass);
        return buildResultCriteria(entityCriteria, resultCriteria).list();
    }

    @Transactional
    public long getCount() {
        Query query = getEntityManager().createQuery("select count(o) from "
                + getPersistentClass().getName() + " as o");
        return (Long) query.getResultList().get(0);
    }

    /**
     * Apply (or in other words "add") <code>resultCriteria</code> on <code>entityCriteria</code>.
     * Result criteria can be empty.
     *
     * @param entityCriteria criteria which will be augmented to create final criteria - both entity and result
     * @param resultCriteria result criteria will be applied as additional criteria on entity criteria
     *                       can be null, in that case, no additonal criteria are applied
     * @return criteria which are the result of both input criterias
     */
    protected Criteria buildResultCriteria(Criteria entityCriteria, ResultCriteria resultCriteria) {
        if (resultCriteria == null) {
            // no additional criteria
            return entityCriteria;
        }

        if (resultCriteria.getFirstResult() != null) {
            entityCriteria.setFirstResult(resultCriteria.getFirstResult());
        }
        if (resultCriteria.getMaxResults() != null) {
            entityCriteria.setMaxResults(resultCriteria.getMaxResults());
        }
        if (resultCriteria.getOrderByColumns() != null
                && CollectionUtils.isNotEmpty(resultCriteria.getOrderByColumns().keySet())) {

            for (Map.Entry<String, OrderType> orderBy : resultCriteria.getOrderByColumns().entrySet()) {
                final String orderByColumn = orderBy.getKey();
                if (StringUtils.isNotBlank(orderByColumn)) {
                    switch (orderBy.getValue()) {
                        case ASC:
                            entityCriteria.addOrder(Order.asc(orderByColumn));
                            break;
                        case DESC:
                            entityCriteria.addOrder(Order.desc(orderByColumn));
                            break;
                        default:
                            entityCriteria.addOrder(Order.asc(orderByColumn));
                    }
                }
            }
        }
        return entityCriteria;
    }


    /**
     * Set additional restrictions as specified by <code>resultCriteria</code> to the given <code>query</code>.
     * <p>
     * Currently only {@link com.eprovement.poptavka.domain.common.ResultCriteria#firstResult}
     * and {@link ResultCriteria#maxResults| are supported restrictions that can be applied!<br />
     * Ordering ({@link com.eprovement.poptavka.domain.common.ResultCriteria#orderByColumns}) cannot be applied
     * --> Collection returned by query must be sorted explicitely in memory.
     * </p>
     *
     * @param query          a query to which additional criteria should be applief
     * @param resultCriteria additional criteria to be applied to query
     * @return the same query which is further restricted by additional criteria <code>resultCriteria</code>
     * @throws IllegalStateException if given <code>resultCriteria</code> contains specification of ordering
     *
     * @see Searcher
     */
    public Query applyResultCriteria(Query query, ResultCriteria resultCriteria) {
        if (resultCriteria != null) {
            if (resultCriteria.getFirstResult() != null) {
                query.setFirstResult(resultCriteria.getFirstResult());
            }
            if (resultCriteria.getMaxResults() != null) {
                query.setMaxResults(resultCriteria.getMaxResults());
            }
        }

        return query;
    }

    @SuppressWarnings("unchecked")
    public T save(T entity) {
        return em().merge(entity);
    }

    /**
     * {@inheritDoc}
     */
    public T update(final T entity) {
        getHibernateSession().update(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public T saveOrUpdate(final T entity) {
        getHibernateSession().saveOrUpdate(entity);
        return entity;
    }



    /**
     * {@inheritDoc}
     */
    public void create(T entity) {
        em().persist(entity);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final T entity) {
        return em().contains(entity);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsById(final Long id) {
        return findById(id, false) != null;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(T entity) {
        em().remove(entity);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T refresh(T entity) {
        em().refresh(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        em().flush();
    }


    /**
     * Searches through database by given criteria.
     *
     * @param criterion
     * @return result of the search
     */
    @SuppressWarnings("unchecked")
    protected List<T> findByCriteria(ResultCriteria resultCriteria, Criterion... criterion) {
        Criteria crit = getHibernateSession().createCriteria(persistentClass);
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return buildResultCriteria(crit, resultCriteria).list();
    }

    /**
     * Creates named query.
     *
     * @param name
     * @return
     */
    final protected javax.persistence.Query createNamedQuery(final String name) {
        return em().createNamedQuery(name);
    }

    /**
     * Creates query.
     *
     * @param query
     * @return
     */
    final protected javax.persistence.Query createQuery(final String query) {
        return createJPAQuery(query);
    }

    /**
     * Creates JPA query.
     *
     * @param query
     * @return
     */
    final protected javax.persistence.Query createJPAQuery(final String query) {
        return em().createQuery(query);
    }

    /**
     * Creates hibernate query.
     *
     * @param query
     * @return
     */
    final protected org.hibernate.Query createHibernateQuery(final String query) {
        return getHibernateSession().createQuery(query);
    }

    /**
     * Returns single result of the query or null value if the result is empty.
     *
     * @param query
     * @return
     * @throws NonUniqueResultException if the query returns more than one result
     */
    @SuppressWarnings("unchecked")
    final protected T getQuerySingleOrNullResult(final javax.persistence.Query query) {
        final List<Object> resultList = query.getResultList();
        final int count = resultList.size();
        if (count == 0) {
            return null;
        }

        if (count > 1) {
            throw new NonUniqueResultException("there is only one code per " + getEntityClassSimpleName()
                    + " permitted: count=" + count);
        }

        return (T) resultList.get(0);
    }


    /**
     * {@inheritDoc}
     */
    public List runNamedQuery(String name, Map<String, ?> params) {
        Query query = getEntityManager().createNamedQuery(name);
        if (query == null) {
            throw new IllegalStateException("Query doesn't exist!");
        }

        if (params != null) {
            for (Map.Entry<String, ?> param : params.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
        }

        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public List runNamedQuery(String name, Map<String, ?> params,
            ResultCriteria resultCriteria) {
        Query query = getEntityManager().createNamedQuery(name);
        if (query == null) {
            throw new IllegalStateException("Query doesn't exist!");
        }

        if (params != null) {
            for (Map.Entry<String, ?> param : params.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
        }

        final List unsortedResult = applyResultCriteria(query, resultCriteria).getResultList();
        if (CollectionUtils.isNotEmpty(unsortedResult) && ResultCriteria.isOrderBySpecified(resultCriteria)) {
            // must be ordered
            final Search sortedSearch = new Search(unsortedResult.get(0).getClass());
            for (Map.Entry<String, OrderType> orderBy : resultCriteria.getOrderByColumns().entrySet()) {
                switch (orderBy.getValue()) {
                    case ASC:
                        sortedSearch.addSortAsc(orderBy.getKey());
                        break;
                    case DESC:
                        sortedSearch.addSortDesc(orderBy.getKey());
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown OrderType: " + orderBy.getValue());
                }
                return Searcher.searchCollection(unsortedResult, sortedSearch);
            }
        }

        return unsortedResult;
    }


    /**
     * {@inheritDoc}
     */
    public Object runNamedQueryForSingleResult(String name, Map<String, ?> params) {
        final List<?> resultList = runNamedQuery(name, params);
        final int count = resultList.size();
        if (count == 0) {
            return null;
        }

        if (count > 1) {
            throw new NonUniqueResultException("there is only one code per " + getEntityClassSimpleName()
                    + " permitted: count=" + count);
        }

        return resultList.get(0);
    }

    /**
     * {@inheritDoc}
     */
    public Object runNamedQueryForSingleResult(String name,
            Map<String, ?> params, ResultCriteria resultCriteria) {
        final List<?> resultList = runNamedQuery(name, params, resultCriteria);
        final int count = resultList.size();
        if (count == 0) {
            return null;
        }

        if (count > 1) {
            throw new NonUniqueResultException("there is only one code per " + getEntityClassSimpleName()
                    + " permitted: count=" + count);
        }

        return resultList.get(0);
    }

    /**
     * Utility. Conversion of collection to set.
     *
     * @param col
     * @return
     */
    public Set<T> toSet(Collection col) {
        return toSet(col, persistentClass);
    }

    /**
     * Utility. Conversion of collection to set.
     *
     * @param col
     * @return
     */
    static public <T> Set<T> toSet(Collection col, final Class<? extends T> cls) {
        final Set<T> set = new HashSet<>();
        for (Object o : col) {
            final T t = (T) o;
            set.add(t);
        }
        return set;
    }

    /**
     * Creates the new result list used to store dao method results.
     */
    protected List<T> createResultList() {
        return new java.util.LinkedList<>();
    }

    /**
     * Creates the new result set used to store dao method results.
     */
    protected Set<T> createResultSet() {
        return new java.util.HashSet<>();
    }

    /**
     * Returns the single result of the query.
     *
     * @param query
     * @return
     */
    protected T getSingleResult(javax.persistence.Query query) {
        List<T> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
