/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dumiduh.das;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author dumiduh
 */
public class TableExistsServiceTest {
private AdminServiceInvoker adminSInvoker;
    private AnalyticsAPIInvoker apiInvoker;

    @BeforeClass
public void setup()
{
    adminSInvoker = new AdminServiceInvoker();
    apiInvoker = new AnalyticsAPIInvoker(IPFinder.getIP(),"9443");
    adminSInvoker.createStreamDefinition("admin","admin","activitystream","1.0.0");
    adminSInvoker.persistStream("admin","admin","activitystream","1.0.0");
}

@AfterClass
public void teardown()
{
    adminSInvoker = null;
    apiInvoker = null;
}

@Test
public void invalidTableTest ()
{
    Assert.assertEquals(apiInvoker.invokeTableExist("INVALID", "admin", "admin"),"{\"status\":\"non-existent\",\"message\":\"Table : INVALID does not exist.\"}");
}

@Test
    public void validTableTest()
{
    Assert.assertEquals(apiInvoker.invokeTableExist("ACTIVITYSTREAM","admin","admin"),"{\"status\":\"success\",\"message\":\"Table : ACTIVITYSTREAM exists.\"}");
}
}
