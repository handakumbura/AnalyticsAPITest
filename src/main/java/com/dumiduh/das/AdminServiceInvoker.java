package com.dumiduh.das;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.wso2.carbon.analytics.stream.persistence.EventStreamPersistenceAdminServiceEventStreamPersistenceAdminServiceExceptionException;
import org.wso2.carbon.analytics.stream.persistence.EventStreamPersistenceAdminServiceStub;
import org.wso2.carbon.event.receiver.admin.EventReceiverAdminServiceStub;
import org.wso2.carbon.event.stream.admin.internal.EventStreamAdminServiceStub;

import java.io.File;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dumiduh on 6/29/15.
 */
public class AdminServiceInvoker {
    private final String STREAMADMINURL;
    private final String RECEIVERURL;
    private final String PERSISTURL;

    public AdminServiceInvoker()
    {
        STREAMADMINURL = "https://"+IPFinder.getIP()+":9443/services/EventStreamAdminService";
        RECEIVERURL = "https://"+IPFinder.getIP()+":9443/services/EventReceiverAdminService";
        PERSISTURL = "https://"+IPFinder.getIP()+":9443/services/EventStreamPersistenceAdminService";

        String trustStore = new File("").getAbsolutePath()+"/src/test/resources/client-truststore.jks";
        System.setProperty("javax.net.ssl.trustStore",  trustStore );
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    }

    /**
     *
     * @param us - username
     * @param pwd - password
     * @param streamName - stream name
     * @param version -  version
     * @return - stream definition creation outcome
     */
    public  boolean createStreamDefinition(String us, String pwd, String streamName, String version)
    {
        boolean status = false;
        try {
            ConfigurationContext configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);

            EventStreamAdminServiceStub stub = new EventStreamAdminServiceStub(configContext, STREAMADMINURL);

            ServiceClient client = stub._getServiceClient();
            Options option = client.getOptions();

            option.setProperty(HTTPConstants.COOKIE_STRING, null);
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(us);
            auth.setPassword(pwd);
            auth.setPreemptiveAuthentication(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
            option.setManageSession(true);

            EventStreamAdminServiceStub.AddEventStreamDefinitionAsDto addDto = new EventStreamAdminServiceStub.AddEventStreamDefinitionAsDto();
            EventStreamAdminServiceStub.EventStreamDefinitionDto dto =  new EventStreamAdminServiceStub.EventStreamDefinitionDto();
            dto.setName(streamName);
            dto.setVersion(version);
            dto.setDescription("desc");
            dto.setNickName("nick");

            EventStreamAdminServiceStub.EventStreamAttributeDto attribute1 = new EventStreamAdminServiceStub.EventStreamAttributeDto();
            attribute1.setAttributeName("entry");
            attribute1.setAttributeType("string");

            EventStreamAdminServiceStub.EventStreamAttributeDto[] payload = new EventStreamAdminServiceStub.EventStreamAttributeDto[]{attribute1};
            dto.setPayloadData(payload);

            addDto.setEventStreamDefinitionDto(dto);

            EventStreamAdminServiceStub.AddEventStreamDefinitionAsDtoResponse response = stub.addEventStreamDefinitionAsDto(addDto);
            status = response.get_return();
            System.out.println("putting thread to sleep for 10s to allow the stream definition to deploy");
            Thread.sleep(10000);

        } catch (AxisFault ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;
    }


    /**
     *
     * @param us - username
     * @param pwd - password
     * @param streamName - stream name
     * @param version -  version
     * @return - receiver creation outcome
     */
    public boolean createReceiver(String us, String pwd, String streamName, String version)
    {
        boolean status = false;
        ConfigurationContext configContext;
        try {
            configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem( null, null);
            EventReceiverAdminServiceStub stub = new EventReceiverAdminServiceStub(configContext, RECEIVERURL);
            ServiceClient client = stub._getServiceClient();
            Options option = client.getOptions();

            option.setProperty(HTTPConstants.COOKIE_STRING, null);
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(us);
            auth.setPassword(pwd);
            auth.setPreemptiveAuthentication(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
            option.setManageSession(true);

            EventReceiverAdminServiceStub.DeployWso2EventReceiverConfiguration res = new EventReceiverAdminServiceStub.DeployWso2EventReceiverConfiguration();
            res.setEventAdapterType("wso2event");
            res.setStreamNameWithVersion(streamName+":"+version);
            res.setEventReceiverName(streamName+"adapter");
            res.setMappingEnabled(false);

            EventReceiverAdminServiceStub.DeployWso2EventReceiverConfigurationResponse resp = stub.deployWso2EventReceiverConfiguration(res);
            status = resp.get_return();
            System.out.println("putting thread to sleep for 10s to allow the receiver to deploy");
            Thread.sleep(10000);
        } catch (AxisFault ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        }



        return status;
    }



    public void persistStream(String us, String pwd,String streamName,String version)
    {

        ConfigurationContext configContext;
        try {
            configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem( null, null);
            EventStreamPersistenceAdminServiceStub stub = new EventStreamPersistenceAdminServiceStub(configContext,PERSISTURL);
            ServiceClient client = stub._getServiceClient();
            Options option = client.getOptions();


            option.setProperty(HTTPConstants.COOKIE_STRING, null);
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(us);
            auth.setPassword(pwd);
            auth.setPreemptiveAuthentication(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
            option.setManageSession(true);

            EventStreamPersistenceAdminServiceStub.AnalyticsTable table = new EventStreamPersistenceAdminServiceStub.AnalyticsTable();
            EventStreamPersistenceAdminServiceStub.AnalyticsTableRecord rec1 = new EventStreamPersistenceAdminServiceStub.AnalyticsTableRecord();
            rec1.setColumnName("entry");
            rec1.setColumnType("STRING");
            rec1.setPersist(true);
            table.addAnalyticsTableRecords(rec1);
            table.setStreamVersion(version);
            table.setTableName(streamName);
            table.setPersist(true);

            EventStreamPersistenceAdminServiceStub.AddAnalyticsTable add = new EventStreamPersistenceAdminServiceStub.AddAnalyticsTable();
            add.setAnalyticsTable(table);
            stub.addAnalyticsTable(add);
            System.out.println("putting thread to sleep for 10s to allow the eventsink to deploy");
            Thread.sleep(10000);


        } catch (AxisFault ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EventStreamPersistenceAdminServiceEventStreamPersistenceAdminServiceExceptionException ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    public void createMultiplStreams(String us, String pwd, String streamNamePrefix, String version, int noOfStreams)
    {

        for(int x=0;x< noOfStreams;x++)
        {
            String name = streamNamePrefix+x;
            createStreamDefinition(us,pwd,name,version);
            persistStream(us,pwd,name,version);
        }
        System.out.println("created/persisted " + noOfStreams +" streams.");
    }

    public boolean removeEventStream(String us, String pwd,String streamName, String version)
    {
        boolean status = false;
        try {
            ConfigurationContext configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);

            EventStreamAdminServiceStub stub = new EventStreamAdminServiceStub(configContext, STREAMADMINURL);

            ServiceClient client = stub._getServiceClient();
            Options option = client.getOptions();

            option.setProperty(HTTPConstants.COOKIE_STRING, null);
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(us);
            auth.setPassword(pwd);
            auth.setPreemptiveAuthentication(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
            option.setManageSession(true);

            EventStreamAdminServiceStub.RemoveEventStreamDefinition removeEventStreamDefinition = new EventStreamAdminServiceStub.RemoveEventStreamDefinition();
            removeEventStreamDefinition.setEventStreamName(streamName);
            removeEventStreamDefinition.setEventStreamVersion(version);

            EventStreamAdminServiceStub.RemoveEventStreamDefinitionResponse response = stub.removeEventStreamDefinition(removeEventStreamDefinition);
            status = response.get_return();

            System.out.println("putting thread to sleep for 5s to allow artifact un-deployment");
            Thread.sleep(5000);

        } catch (AxisFault ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(AdminServiceInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return status;
    }

}
