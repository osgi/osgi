/*
 * $Header$
 *
 * Copyright (c) 2002 - Acunia
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

package org.osgi.service.io;

import javax.microedition.io.*;
import java.io.*;

/**
 * A Connection Factory service is called by the implementation of the Connector Service
 * to create <tt>javax.microedition.io.Connection</tt> objects which implement
 * the scheme named by <tt>IO_SCHEME</tt>.
 *
 * When a <tt>ConnectorService.open</tt> method is called,
 * the implementation of the Connector Service will examine the specified
 * name for a scheme. The Connector Service will then look for a Connection Factory
 * service which is registered with the service property <tt>IO_SCHEME</tt>
 * which matches the scheme. The {@link #createConnection} method of the selected
 * Connection Factory will then be called to create the actual <tt>Connection</tt>
 * object.
 *
 * @version $Revision$
 */
public interface ConnectionFactory
{
    /**
     * Service property containing the scheme(s)
     * for which this Connection Factory can create <tt>Connection</tt> objects.
     * This property is of type <tt>String[]</tt>.
     */
    public static final String IO_SCHEME = "io.scheme";

    /**
     * Create a new <tt>Connection</tt> object for the specified URI.
     *
     * @param name The full URI passed to the <tt>ConnectorService.open</tt> method
     * @param mode The mode parameter passed to the <tt>ConnectorService.open</tt> method
     * @param timeouts The timeouts parameter passed to the <tt>ConnectorService.open</tt> method
     * @return A new <tt>javax.microedition.io.Connection</tt> object.
     * @throws IOException If a <tt>javax.microedition.io.Connection</tt> object can not not be created.
     */
    public Connection createConnection(String name, int mode, boolean timeouts) throws IOException;
}

