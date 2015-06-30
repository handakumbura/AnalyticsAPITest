package com.dumiduh.das;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by dumiduh on 6/30/15.
 */
public class RecordCountServiceTest {
    private AnalyticsAPIInvoker analyticsAPIInvoker;

@BeforeClass
public void setup()
{
    analyticsAPIInvoker = new AnalyticsAPIInvoker(IPFinder.getIP(),"9443");
}

@AfterClass
public void teardown()
{
    analyticsAPIInvoker=null;
}

@Test
public void invalidTableTest()
{
    String result = analyticsAPIInvoker.invokeTableRecordCount("TESTSTREAM","admin","admin");
    Assert.assertTrue(result.contains("does not exist"));
}

@Test
public void validTableTest()
{
    String result = analyticsAPIInvoker.invokeTableRecordCount("ACTIVITYSTREAM","admin","admin");
    Assert.assertEquals(result,"100");
}


}

