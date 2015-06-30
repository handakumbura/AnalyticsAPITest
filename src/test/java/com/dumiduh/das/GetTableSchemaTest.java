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

        //if the test is getting run non independently(pom profile) comment out the below 3 lines.
        String killserver = "sh "+new File("").getAbsolutePath()+"/src/test/resources/teardown.sh";
        ShellExecutor executor = new ShellExecutor(killserver);
        executor.execute();
    }

    @Test
    public void existingTableSchemaTest()
    {
        String result = analyticsAPIInvoker.invokeGetTableSchema("ACTIVITYSTREAM","admin","admin");
        Assert.assertEquals(result,"{\"columns\":{\"entry\":{\"type\":\"STRING\",\"isScoreParam\":false,\"isIndex\":false}},\"primaryKeys\":[]}");
    }

}
