/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dumiduh.das;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.Header;

/**
 *
 * @author dumiduh
 */
public class AnalyticsAPIInvoker {
    private final String TABLE_EXISTS="/analytics/table_exists?tableName=";
    private final String TABLE_RECORD_COUNT="/analytics/tables/tableName/recordcount";
    private final String LIST_ALL_TABLES="/analytics/tables";
    private final String GET_TABLE_SCHEMA="/analytics/tables/tableName/schema";
    private String host;
    private String port;



    /**
     * wraps invocation of DAS Analytics REST API.
     *
     * @param host hostname of remote server
     * @param port port of remote server
     */
    public AnalyticsAPIInvoker(String host,String port)
    {
        this.host=host;
        this.port=port;
    }

    /**
     * invokes /analytics/table_exists?tableName
     *
     * @param tName Table name to be checked.
     * @param username a valid username found servers userstore.
     * @param pwd   a valid password found in servers userstore.
     * @return response of invocation as a string.
     */
    public String invokeTableExist(String tName,String username,String pwd)
    {
        String url = "https://"+host+":"+port+TABLE_EXISTS+tName;

        String jsonString = invoke(url, username, pwd,"body");
        return jsonString;

    }

    public String invokeListAllTables(String username,String pwd)
    {
        String url = "https://"+host+":"+port+LIST_ALL_TABLES;
        String jsonString = invoke(url, username, pwd,"body");
        return jsonString;
    }



    /**
     * invokes /analytics/tables/tableName/recordcount
     *
     * @param tName Table name to be checked.
     * @param username a valid username found servers userstore.
     * @param pwd a valid password found in servers userstore.
     * @return response of invocation as a string.
     */
    public String invokeTableRecordCount(String tName,String username,String pwd)
    {
        String url = "https://"+host+":"+port+TABLE_RECORD_COUNT.replaceAll("tableName", tName);
        String jsonString = invoke(url, username, pwd,"body");
        return jsonString;
    }


    public String invokeGetTableSchema(String tName,String username,String pwd)
    {
        String url = "https://"+host+":"+port+GET_TABLE_SCHEMA.replaceAll("tableName",tName);
        String jsonString = invoke(url, username, pwd,"body");
        return jsonString;
    }


    public String invokeRetrieveRecords(String tName,String username,String pwd, long from, long to, int start, int count)
    {
        String url = "https://localhost:9443/analytics/tables/";
        if(from==0L || to==0L)
        {
            url=url+tName;
        }
        else if((from!=0L || to!=0L ) && count==0)
        {
            url=url+tName+"/"+from+"/"+to;
        }
        else
        {
            url=url+tName+"/"+from+"/"+to+"/"+start+"/"+count;
        }


        String jsonString = invoke(url,username,pwd,"body");
        return jsonString;
    }

    public String getHeaders(String url,String username,String pwd)
    {
        String result =invoke(url,username,pwd,"header");
        return result;
    }

    /*
     * util methods
     */
    private String getBase64EncodedToken(String username,String pwd)
    {
        String value = username+":"+pwd;
        byte[] bytesEncoded = Base64.encodeBase64(value.getBytes());
        value = new String(bytesEncoded);

        return value;
    }

    private String invokePost(String url,String username,String pwd, String type,String payload)
    {
        TrustStrategyExt strategy = new TrustStrategyExt();

        String jsonString="";
        try {
            SSLSocketFactory sf = new SSLSocketFactory(strategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("https", Integer.parseInt(port), sf));
            ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);

            DefaultHttpClient client = new DefaultHttpClient(ccm);

            StringEntity stringEntity = new StringEntity(payload);
            HttpPost post = new HttpPost();
            post.setEntity(stringEntity);
            String header = "Basic "+getBase64EncodedToken(username, pwd);
            post.setHeader("Authorization", header);
            post.setHeader("Accept","application/json");
            post.setHeader("Content-Type","application/json");

            HttpResponse resp = client.execute(post);
            if(type.equals("body"))
            {
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(resp.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                jsonString=result.toString();
            }
            else if(type.equals("header"))
            {
                StringBuffer result = new StringBuffer();
                Header[] headers = resp.getAllHeaders();
                for(Header h : headers)
                {

                    result.append(h.getName()+" : "+h.getValue());
                }
                result.append("status code : "+resp.getStatusLine().getStatusCode());
                jsonString=result.toString();
            }

            client.close();


        }   catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        }catch (NumberFormatException ex)
        {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }


    private String invoke(String url,String username,String pwd, String type)
    {
        TrustStrategyExt strategy = new TrustStrategyExt();

        String jsonString="";
        try {
            SSLSocketFactory sf = new SSLSocketFactory(strategy,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("https", Integer.parseInt(port), sf));
            ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);

            DefaultHttpClient client = new DefaultHttpClient(ccm);
            HttpGet get = new HttpGet(url);
            String header = "Basic "+getBase64EncodedToken(username, pwd);
            get.setHeader("Authorization", header);

            HttpResponse resp = client.execute(get);
            if(type.equals("body"))
            {
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(resp.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                jsonString=result.toString();
            }
            else if(type.equals("header"))
            {
                StringBuffer result = new StringBuffer();
                Header[] headers = resp.getAllHeaders();
                for(Header h : headers)
                {

                    result.append(h.getName()+" : "+h.getValue());
                }
                result.append("status code : "+resp.getStatusLine().getStatusCode());
                jsonString=result.toString();
            }

            client.close();

        }   catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex)
        {
            Logger.getLogger(AnalyticsAPIInvoker.class.getName()).log(Level.SEVERE, null, ex);
        }


        return jsonString;
    }






}
