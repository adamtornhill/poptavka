package com.eprovement.poptavka.service.fulltext;

import com.google.common.base.Preconditions;
import com.eprovement.poptavka.domain.common.DomainObject;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Implementation which uses capabilities of Hibernate Search project.
 * See <a href="http://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/">
 *     http://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/</a>
 *
 * @author Juraj Martinka
 *         Date: 20.5.11
 */
@Transactional(readOnly = true)
public class HibernateFulltextSearchService implements FulltextSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateFulltextSearchService.class);

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public <T extends DomainObject> List<T> search(Class<T> entityClass, String[] propertyNames, String fulltextQuery) {
        final FullTextQuery fullTextQuery = createFulltextQuery(entityClass, propertyNames, fulltextQuery);

        // execute search
        return fullTextQuery.getResultList();
    }


    @Override
    public <T extends DomainObject> int searchCount(Class<T> entityClass, String[] propertyNames,
            String fulltextQuery) {
        final FullTextQuery fullTextQuery = createFulltextQuery(entityClass, propertyNames, fulltextQuery);

        // execute search
        return fullTextQuery.getResultSize();
    }


    /**
     * Call this method only if you know what you are doing!
     *
     * {@inheritDoc}
     *
     */
    @Override
    public void createInitialFulltextIndex() {
        LOGGER.info("Action=createFulltextIndex status=start");
        final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(this.entityManager);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
            LOGGER.info("Action=createFulltextIndex status=finish");
        } catch (InterruptedException e) {
            LOGGER.info("Action=createFulltextIndex status=error exception={}", e);
            throw new FulltextInitializationException("Error in fulltext index initialization: "
                    + e.getLocalizedMessage());
        }
    }


    //--------------------------------------------------- PRIVATE METHODS ----------------------------------------------
    private <T extends DomainObject> FullTextQuery createFulltextQuery(Class<T> entityClass, String[] propertyNames,
            String fulltextQuery) {
        Preconditions.checkArgument(entityClass != null, "Class for full-text must be specified");
        Preconditions.checkArgument(propertyNames != null && propertyNames.length > 0, "Names of fields that should"
                + " be searched by full-text must be NOT empty.");

        final FullTextEntityManager fullTextEntityManager =
                Search.getFullTextEntityManager(this.entityManager);

        // create native Lucene query using the query DSL
        // alternatively you can write the Lucene query using the Lucene query parser
        // or the Lucene programmatic API. The Hibernate Search DSL is recommended though
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(entityClass).get();
        org.apache.lucene.search.Query query = qb.keyword()
                .onFields(propertyNames)
                .matching(fulltextQuery)
                .createQuery();

        // wrap Lucene query in a javax.persistence.Query
        return fullTextEntityManager.createFullTextQuery(query, entityClass);
    }

}
