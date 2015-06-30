package com.dumiduh.das;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by dumiduh on 6/29/15.
 */
public class RecordsOfTableServiceTest {
private AdminServiceInvoker adminSInvoker;
    private AnalyticsAPIInvoker analyticsAPIInvoker;
    private EventPublisher eventPublisher;
    private String callWithTblNameResult;
    private long startingTimeStamp;
    @BeforeClass
    public void setup()
    {
        analyticsAPIInvoker = new AnalyticsAPIInvoker(IPFinder.getIP(),"9443");
        adminSInvoker = new AdminServiceInvoker();
        adminSInvoker.createReceiver("admin","admin","activitystream","1.0.0");

        //publishing required events and getting start timestamp
        eventPublisher = new EventPublisher();
        startingTimeStamp = eventPublisher.publishEvents("activitystream","1.0.0","admin","admin",100);
    }

    @AfterClass
    public void teardown()
    {
        adminSInvoker=null;
        analyticsAPIInvoker=null;
        eventPublisher=null;
    }

    @Test
    public void callWithOnlyTableNameTest()
    {
        callWithTblNameResult = analyticsAPIInvoker.invokeRetrieveRecords("activitystream","admin","admin",0L,0L,0,0);
        Assert.assertTrue(callWithTblNameResult.contains("val 99"));

    }

    @Test
    public void timestampFilterTest1()
    {
        String result = analyticsAPIInvoker.invokeRetrieveRecords("activitystream","admin","admin",startingTimeStamp+5000L,startingTimeStamp+10000L,0,0);
        Assert.assertFalse(result.contains("val 99"));
    }

    @Test
    public void timestampFilterTest2()
    {
        String result = analyticsAPIInvoker.invokeRetrieveRecords("activitystream","admin","admin",startingTimeStamp,startingTimeStamp+1000L,0,0);
        Assert.assertTrue(result.contains("val 99"));
    }

    @Test
    public void paginatedResultTest1()
    {
        String result = analyticsAPIInvoker.invokeRetrieveRecords("activitystream","admin","admin",startingTimeStamp,startingTimeStamp+1000L,0,1);
        Assert.assertTrue(result.contains("val 0"),"may fail due events not arriving at server in proper order");
    }

    @Test
    public void paginatedResultTest2()
    {
        String result = analyticsAPIInvoker.invokeRetrieveRecords("activitystream","admin","admin",startingTimeStamp,startingTimeStamp+1000L,0,100);
        Assert.assertTrue(result.contains("val 99"));
    }

    @Test
    public void paginatedResultTest3()
    {
        String result = analyticsAPIInvoker.invokeRetrieveRecords("activitystream","admin","admin",startingTimeStamp,startingTimeStamp+1000L,1,10);
        int t = countSubstring("val ",result);
        Assert.assertEquals(t,10);
    }


    @Test(dependsOnMethods = {"callWithOnlyTableNameTest"})
    public void negativeScenario1()
    {
        Assert.assertFalse(callWithTblNameResult.contains("val 200"),"failed due to existence of non published event");

    }

    @Test
    public void negativeScenario2()
    {
        String result = analyticsAPIInvoker.invokeRetrieveRecords("activitystream","admin","admin",-1000000000000L,-2000000000000L,0,0);
        Assert.assertEquals(result,"[]", "failed due to returning non empty json array for negative time stamp");
    }

    @Test
    public void negativeScenario3()
    {
        String result = analyticsAPIInvoker.getHeaders("https://localhost:9443/analytics/tables/ACTIVITYSTREAM/test/test","admin","admin");
        Assert.assertTrue(result.contains("404"),"failed due to not returning 404 for char timestamps");

    }

    //util method
    private int countSubstring(String subStr, String str){
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }

}
