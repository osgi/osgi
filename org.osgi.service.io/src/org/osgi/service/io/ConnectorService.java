/*
 * $Header$
 *
 * Copyright (c) 2002 - Acunia
 * Copyright (c) 2002 - IBM Corporation
 * All Rights Reserved.
 * 	
 * These materials have been contributed to the Open Services Gateway
 * Initiative (OSGi) as "MEMBER LICENSED MATERIALS" as defined in, and
 * subject to the terms of, the OSGi Member Agreement by and between OSGi and
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

package org.osgi.service.io;

import java.io.*;
import javax.microedition.io.*;

/**
 * The Connector Service should be called to create and open
 * <tt>javax.microedition.io.Connection</tt> objects.
 *
 * When an <tt>open*</tt> method is called,
 * the implementation of the Connector Service will examine the specified
 * name for a scheme. The Connector Service will then look for a Connection Factory
 * service which is registered with the service property <tt>IO_SCHEME</tt>
 * which matches the scheme. The <tt>createConnection</tt> method of the selected
 * Connection Factory will then be called to create the actual <tt>Connection</tt>
 * object.
 *
 * <p>If more than one Connection Factory service is registered for a particular scheme, the service
 * with the highest ranking (as specified in its <tt>service.ranking</tt> property) is
 * called.
 * If there is a tie in ranking, the service with the lowest
 * service ID (as specified in its <tt>service.id</tt> property), that is
 * the service that was registered first, is called.
 * This is the same algorithm used by <tt>BundleContext.getServiceReference</tt>.
 *
 * @version $Revision$
 */
public interface ConnectorService
{
    /**
     * Read access mode.
     *
     * @see "javax.microedition.io.Connector.READ"
     */
    public static final int READ = Connector.READ;

    /**
     * Write access mode.
     *
     * @see "javax.microedition.io.Connector.WRITE"
     */
    public static final int WRITE = Connector.WRITE;

    /**
     * Read/Write access mode.
     *
     * @see "javax.microedition.io.Connector.READ_WRITE"
     */
    public static final int READ_WRITE = Connector.READ_WRITE;

    /**
     * Create and open a <tt>Connection</tt> object for the specified name.
     *
     * @param name The URI for the connection.
     * @return A new <tt>javax.microedition.io.Connection</tt> object.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws javax.microedition.io.ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     * @see "javax.microedition.io.Connector.open(String name)"
     */
    public Connection open(String name) throws IOException;

    /**
     * Create and open a <tt>Connection</tt> object for the specified name and access mode.
     *
     * @param name The URI for the connection.
     * @param mode The access mode.
     * @return A new <tt>javax.microedition.io.Connection</tt> object.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws javax.microedition.io.ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     * @see "javax.microedition.io.Connector.open(String name, int mode)"
     */
    public Connection open(String name, int mode) throws IOException;

    /**
     * Create and open a <tt>Connection</tt> object for the specified name, access mode and timeouts.
     *
     * @param name The URI for the connection.
     * @param mode The access mode.
     * @param timeouts A flag to indicate that the caller wants timeout exceptions.
     * @return A new <tt>javax.microedition.io.Connection</tt> object.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws javax.microedition.io.ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     * @see "javax.microedition.io.Connector.open(String name, int mode, boolean timeouts)"
     */
    public Connection open(String name, int mode, boolean timeouts) throws IOException;

    /**
     * Create and open an <tt>InputStream</tt> object for the specified name.
     *
     * @param name The URI for the connection.
     * @return An <tt>InputStream</tt> object.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws javax.microedition.io.ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     * @see "javax.microedition.io.Connector.openInputStream(String name)"
     */
    public InputStream openInputStream(String name) throws IOException;

    /**
     * Create and open a <tt>DataInputStream</tt> object for the specified name.
     *
     * @param name The URI for the connection.
     * @return A <tt>DataInputStream</tt> object.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws javax.microedition.io.ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     * @see "javax.microedition.io.Connector.openDataInputStream(String name)"
     */
    public DataInputStream openDataInputStream(String name) throws IOException;

    /**
     * Create and open an <tt>OutputStream</tt> object for the specified name.
     *
     * @param name The URI for the connection.
     * @return An <tt>OutputStream</tt> object.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws javax.microedition.io.ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     * @see "javax.microedition.io.Connector.openOutputStream(String name)"
     */
    public OutputStream openOutputStream(String name) throws IOException;

    /**
     * Create and open a <tt>DataOutputStream</tt> object for the specified name.
     *
     * @param name The URI for the connection.
     * @return A <tt>DataOutputStream</tt> object.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws javax.microedition.io.ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     * @see "javax.microedition.io.Connector.openDataOutputStream(String name)"
     */
    public DataOutputStream openDataOutputStream(String name) throws IOException;
}
