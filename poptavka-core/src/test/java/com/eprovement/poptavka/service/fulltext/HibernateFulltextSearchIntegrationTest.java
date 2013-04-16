package com.eprovement.poptavka.service.fulltext;

import com.eprovement.poptavka.base.integration.DBUnitIntegrationTest;
import com.eprovement.poptavka.base.integration.DataSet;
import com.eprovement.poptavka.domain.demand.Demand;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Juraj Martinka
 *         Date: 20.5.11
 */
@DataSet(path = {
        "classpath:com/eprovement/poptavka/domain/address/LocalityDataSet.xml",
        "classpath:com/eprovement/poptavka/domain/demand/CategoryDataSet.xml",
        "classpath:com/eprovement/poptavka/domain/demand/RatingDataSet.xml",
        "classpath:com/eprovement/poptavka/domain/user/UsersDataSet.xml",
        "classpath:com/eprovement/poptavka/domain/demand/DemandDataSet.xml",
        "classpath:com/eprovement/poptavka/domain/demand/DemandFulltextDataSet.xml" },
        dtd = "classpath:test.dtd")
// TODO RELEASE juraj: Fulltext index is not created correctly, probably because transaction is not really commited
// in integration tests
@Ignore
public class HibernateFulltextSearchIntegrationTest extends DBUnitIntegrationTest {

    @Autowired
    private FulltextSearchService fulltextSearchService;

    private static volatile boolean fulltextIndexInitialized = false;


    /**
     * Creates initial fulltext index - only once per test class due the performance reasons.
     */
    @Before
    public void createFulltextIndex() {
        if (!fulltextIndexInitialized) {
            fulltextSearchService.createInitialFulltextIndex();
            fulltextIndexInitialized = true;
        }
    }


    @Test
    public void testSearchBasic() {
        final List<Demand> foundDemands =
                this.fulltextSearchService.search(Demand.class, Demand.DEMAND_FULLTEXT_FIELDS, "diskr\u00e9tnos\u009D");
        Assert.assertTrue(CollectionUtils.isNotEmpty(foundDemands));
    }

    @Test
    public void testSearchCaseInsensitive() {
        final List<Demand> foundDemands =
                this.fulltextSearchService.search(Demand.class, Demand.DEMAND_FULLTEXT_FIELDS, "maxim\u00e1ln\u00ed");
        Assert.assertTrue(CollectionUtils.isNotEmpty(foundDemands));
    }

    @Test
    public void testSearchAccentInsensitive() {
        final List<Demand> foundDemands =
                this.fulltextSearchService.search(Demand.class, Demand.DEMAND_FULLTEXT_FIELDS, "pozaduju");
        Assert.assertTrue(CollectionUtils.isNotEmpty(foundDemands));
        Assert.assertEquals(2, foundDemands.size());


    }

}
