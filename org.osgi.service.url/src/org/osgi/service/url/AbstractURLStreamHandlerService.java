/*
 * $Header$
 *
 * Copyright (c) 2002 - IBM Corporation
 * All Rights Reserved.
 * 	
 * These materials have been contributed to the OSGi Alliance
 * as "MEMBER LICENSED MATERIALS" as defined in, and
 * subject to the terms of, the OSGi Member Agreement by and between the OSGi Alliance and
 * IBM, specifically including but not limited to, the license
 * rights and warranty disclaimers as set forth in Sections 3.2 and 12.1
 * thereof.
 *
 * All company, brand and product names contained within this document may be
 * trademarks that are the sole property of the respective owners.
 *
 * The above notice must be included on all copies of this document that are
 * made.
 */

package org.osgi.service.url;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLConnection;
import java.net.InetAddress;

/**
 * Abstract implementation of the <tt>URLStreamHandlerService</tt> interface.
 * All the methods simply invoke the corresponding methods on <tt>java.net.URLStreamHandler</tt>
 * except for <tt>parseURL</tt> and <tt>setURL</tt>, which use the <tt>URLStreamHandlerSetter</tt>
 * parameter.  Subclasses of this abstract class should not need to override the <tt>setURL</tt> and
 * <tt>parseURL(URLStreamHandlerSetter,...)</tt> methods.
 *
 * @version $Revision$
 */
public abstract class AbstractURLStreamHandlerService extends URLStreamHandler
implements URLStreamHandlerService
{
    /**
     * @see "java.net.URLStreamHandler.openConnection"
     */
    public abstract URLConnection openConnection(URL u) throws java.io.IOException;

    /**
     * The <tt>URLStreamHandlerSetter</tt> object passed to the parseURL method.
     */
    protected URLStreamHandlerSetter realHandler;

    /**
     * Parse a URL using the <tt>URLStreamHandlerSetter</tt> object.
     * This method sets the <tt>realHandler</tt> field with the specified
     * <tt>URLStreamHandlerSetter</tt> object and then calls
     * <tt>parseURL(URL,String,int,int)</tt>.
     * @param realHandler The object on which the <tt>setURL</tt> method must be invoked for
     *        the specified URL.
     * @see "java.net.URLStreamHandler.parseURL"
     */
    public void parseURL(URLStreamHandlerSetter realHandler,
                         URL u, String spec, int start, int limit)
    {
        this.realHandler = realHandler;
        parseURL(u, spec, start, limit);
    }

    /**
     * This method calls <tt>super.toExternalForm</tt>.
     *
     * @see "java.net.URLStreamHandler.toExternalForm"
     */
    public String toExternalForm(URL u)
    {
        return super.toExternalForm(u);
    }

    /**
     * This method calls <tt>super.equals(URL,URL)</tt>.
     *
     * @see "java.net.URLStreamHandler.equals(URL,URL)"
     */
    public boolean equals(URL u1, URL u2)
    {
        return super.equals(u1, u2);
    }

    /**
     * This method calls <tt>super.getDefaultPort</tt>.
     *
     * @see "java.net.URLStreamHandler.getDefaultPort"
     */
    public int getDefaultPort()
    {
        return super.getDefaultPort();
    }

    /**
     * This method calls <tt>super.getHostAddress</tt>.
     *
     * @see "java.net.URLStreamHandler.getHostAddress"
     */
    public InetAddress getHostAddress(URL u)
    {
        return super.getHostAddress(u);
    }

    /**
     * This method calls <tt>super.hashCode(URL)</tt>.
     *
     * @see "java.net.URLStreamHandler.hashCode(URL)"
     */
    public int hashCode(URL u)
    {
        return super.hashCode(u);
    }

    /**
     * This method calls <tt>super.hostsEqual</tt>.
     *
     * @see "java.net.URLStreamHandler.hostsEqual"
     */
    public boolean hostsEqual(URL u1, URL u2)
    {
        return super.hostsEqual(u1, u2);
    }

    /**
     * This method calls <tt>super.sameFile</tt>.
     *
     * @see "java.net.URLStreamHandler.sameFile"
     */
    public boolean sameFile(URL u1, URL u2)
    {
        return super.sameFile(u1, u2);
    }

    /**
     * This method calls <tt>realHandler.setURL(URL,String,String,int,String,String)</tt>.
     *
     * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String)"
     * @deprecated This method is only for compatibility with handlers written
     *             for JDK 1.1.
     */
    protected void setURL(URL u, String proto, String host, int port,
                          String file, String ref)
    {
        realHandler.setURL(u, proto, host, port, file, ref);
    }

    /**
     * This method calls <tt>realHandler.setURL(URL,String,String,int,String,String,String,String)</tt>.
     *
     * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String,String,String)"
     */
    protected void setURL(URL u, String proto, String host, int port,
                          String auth, String user, String path,
                          String query, String ref)
    {
        realHandler.setURL(u, proto, host, port, auth, user, path,
                           query, ref);
    }
}

