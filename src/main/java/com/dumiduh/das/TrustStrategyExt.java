/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dumiduh.das;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.TrustStrategy;

/**
 *
 * @author dumiduh
 */
public class TrustStrategyExt implements TrustStrategy{

    public boolean isTrusted(X509Certificate[] xcs, String string) throws CertificateException {


        return true;
    }

}
