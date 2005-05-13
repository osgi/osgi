/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2002, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.io;

import javax.microedition.io.*;
import java.io.*;

/**
 * A Connection Factory service is called by the implementation of the Connector
 * Service to create <code>javax.microedition.io.Connection</code> objects which
 * implement the scheme named by <code>IO_SCHEME</code>.
 * 
 * When a <code>ConnectorService.open</code> method is called, the implementation
 * of the Connector Service will examine the specified name for a scheme. The
 * Connector Service will then look for a Connection Factory service which is
 * registered with the service property <code>IO_SCHEME</code> which matches the
 * scheme. The {@link #createConnection}method of the selected Connection
 * Factory will then be called to create the actual <code>Connection</code>
 * object.
 * 
 * @version $Revision$
 */
public interface ConnectionFactory {
	/**
	 * Service property containing the scheme(s) for which this Connection
	 * Factory can create <code>Connection</code> objects. This property is of
	 * type <code>String[]</code>.
	 */
	public static final String	IO_SCHEME	= "io.scheme";

	/**
	 * Create a new <code>Connection</code> object for the specified URI.
	 * 
	 * @param name The full URI passed to the <code>ConnectorService.open</code>
	 *        method
	 * @param mode The mode parameter passed to the
	 *        <code>ConnectorService.open</code> method
	 * @param timeouts The timeouts parameter passed to the
	 *        <code>ConnectorService.open</code> method
	 * @return A new <code>javax.microedition.io.Connection</code> object.
	 * @throws IOException If a <code>javax.microedition.io.Connection</code>
	 *         object can not not be created.
	 */
	public Connection createConnection(String name, int mode, boolean timeouts)
			throws IOException;
}
