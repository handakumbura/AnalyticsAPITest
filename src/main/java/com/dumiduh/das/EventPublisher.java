package com.dumiduh.das;

import org.wso2.carbon.databridge.agent.thrift.Agent;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.conf.AgentConfiguration;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.AuthenticationException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dumiduh on 6/29/15.
 */
public class EventPublisher {
private String thriftURL;
    public EventPublisher()
    {
        thriftURL = "tcp://"+IPFinder.getIP()+":7611";
        String trustStore = new File("").getAbsolutePath()+"/src/test/resources/client-truststore.jks";
        System.setProperty("javax.net.ssl.trustStore",  trustStore );
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    }

    public long publishEvents(String streamName, String version, String us, String pwd, int eventCount)
    {
        long startingTime=0L;
        AgentConfiguration agentConfiguration = new AgentConfiguration();
        Agent agent = new Agent(agentConfiguration);

        try {
            DataPublisher dataPublisher = new DataPublisher(thriftURL, us, pwd, agent);
            System.out.println("Sending data...");
            Event event;

            for(int x=0;x<eventCount;x++) {
                if(x==0)
                {
                    startingTime=System.currentTimeMillis();
                }
                event = new Event(streamName+":"+version, startingTime,
                        null, null, new Object[] { "val " + x });
                dataPublisher.publish(event);

            }
            //increase sleep time if event count is large
            Thread.sleep(10000);
            dataPublisher.stop();
            agent.shutdown();

        } catch (AgentException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(EventPublisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(EventPublisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(EventPublisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransportException ex) {
            Logger.getLogger(EventPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }

        return startingTime;
    }
}
