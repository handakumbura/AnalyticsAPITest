/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dumiduh.das;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 *
 * @author dumiduh
 */
public class IPFinder {

    public static String getIP()
    {
        String ip="127.0.0.1";

        try
        {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof InetAddress && !addr.isLoopbackAddress()) {
                        if(addr.getHostAddress().length()<16)
                        {
                            ip = addr.getHostAddress();
                        }
                    }

                }
            }
        }
        catch(SocketException ex)
        {
            ip="127.0.0.1";
        }

        return ip;

    }
}
