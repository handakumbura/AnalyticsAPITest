package com.dumiduh.das;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by dumiduh on 6/30/15.
 */
public class ListingAllTablesService {
    private AnalyticsAPIInvoker analyticsAPIInvoker;
    private AdminServiceInvoker adminServiceInvoker;

    @BeforeClass
    public void setup() {
        analyticsAPIInvoker = new AnalyticsAPIInvoker(IPFinder.getIP(), "9443");
        adminServiceInvoker = new AdminServiceInvoker();
        adminServiceInvoker.createMultiplStreams("admin","admin","highrollersstream","1.0.0",10);
    }

    @AfterClass
    public void teardown() {
        analyticsAPIInvoker = null;
        adminServiceInvoker= null;
    }

    @Test
    public void ListAllTablesTest() {

        String result = analyticsAPIInvoker.invokeListAllTables("admin","admin");
        Assert.assertEquals(result,"[\"ACTIVITYSTREAM\",\"HIGHROLLERSSTREAM0\",\"HIGHROLLERSSTREAM1\",\"HIGHROLLERSSTREAM2\",\"HIGHROLLERSSTREAM3\",\"HIGHROLLERSSTREAM4\",\"HIGHROLLERSSTREAM5\",\"HIGHROLLERSSTREAM6\",\"HIGHROLLERSSTREAM7\",\"HIGHROLLERSSTREAM8\",\"HIGHROLLERSSTREAM9\"]");
    }

}
