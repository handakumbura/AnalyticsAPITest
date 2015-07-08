package com.dumiduh.das;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by dumiduh on 6/30/15.
 */
public class GetTableSchemaTest {
    private AnalyticsAPIInvoker analyticsAPIInvoker;
    private AdminServiceInvoker adminServiceInvoker;

    @BeforeClass
    public void setup() {
        analyticsAPIInvoker = new AnalyticsAPIInvoker(IPFinder.getIP(), "9443");
    }

    @AfterClass
    public void teardown() {
        analyticsAPIInvoker = null;
        adminServiceInvoker= null;

        //if the das server is started as part of the test it is killed.
        if(System.getProperty("killdasserver").equals("true")) {
            String killserver = "sh " + new File("").getAbsolutePath() + "/src/test/resources/teardown.sh "+System.getProperty("dashome");
            ShellExecutor executor = new ShellExecutor(killserver);
            executor.execute();
        }
    }

    @Test
    public void existingTableSchemaTest()
    {
        String result = analyticsAPIInvoker.invokeGetTableSchema("ACTIVITYSTREAM","admin","admin");
        Assert.assertEquals(result,"{\"columns\":{\"entry\":{\"type\":\"STRING\",\"isScoreParam\":false,\"isIndex\":false}},\"primaryKeys\":[]}");
    }

}
