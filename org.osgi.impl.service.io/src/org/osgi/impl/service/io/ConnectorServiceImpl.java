/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.Connection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;
import javax.microedition.io.ConnectionNotFoundException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.io.*;
import org.osgi.util.tracker.ServiceTracker;

/**
 * ConnectorService implementation.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
class ConnectorServiceImpl implements ConnectorService {
	private ServiceTracker	factoryTracker;

	public ConnectorServiceImpl(BundleContext bc) {
		factoryTracker = new ServiceTracker(bc, ConnectionFactory.class
				.getName(), null);
		factoryTracker.open();
	}

	void close() {
		factoryTracker.close();
	}

	/**
	 * Opens a connection to the given uri with <tt>READ_WRITE</tt> access
	 * mode.
	 * 
	 * @param uri the URI for the connection.
	 * @throws IOException if and I/O error occurs
	 * @throws ConnectionNotFoundException if an appropriate
	 *         <tt>ConnectionFactory</tt> for the given scheme doesn't exist
	 * @throws IllegalArgumentException if the given uri is invalid
	 * @return A new <tt>Connection</tt> object to the given URI
	 */
	public Connection open(String uri) throws IOException {
		return open(uri, READ_WRITE);
	}

	/**
	 * Opens a connection to the given uri with timeouts set to false.
	 * 
	 * @param uri the URI for the connection.
	 * @param mode the access mode
	 * @throws IOException if and I/O error occurs
	 * @throws ConnectionNotFoundException if an appropriate
	 *         <tt>ConnectionFactory</tt> for the given scheme doesn't exist
	 * @throws IllegalArgumentException if the given uri is invalid
	 * @return A new <tt>Connection</tt> object to the given URI
	 */
	public Connection open(String uri, int mode) throws IOException {
		return open(uri, mode, false);
	}

	/**
	 * Creates a connection for the given uri. If an appropriate
	 * ConnectorFactory for the scheme of the uri exists it's used for creating
	 * the <tt>Connection
	 * </tt>, otherwise if an underlying
	 * <tt>javax.microedition.io.Connector
	 * </tt> implementation is present, the
	 * returned <tt>Connection</tt> is created with it.
	 * 
	 * @param uri the URI for the connection.
	 * @param mode the access mode
	 * @param timeout a flag to indicate that the caller wants timeout
	 *        exceptions
	 * @throws IOException if and I/O error occurs
	 * @throws ConnectionNotFoundException if an appropriate
	 *         <tt>ConnectionFactory</tt> for the given scheme doesn't exist
	 * @throws IllegalArgumentException if the given uri is invalid
	 * @return A new <tt>Connection</tt> object to the given URI
	 */
	public Connection open(String uri, int mode, boolean timeouts)
			throws IOException {
		if (uri == null) {
			throw new IllegalArgumentException("URL cannot be NULL!");
		}
		int colon = uri.indexOf(":");
		if (colon < 1) {
			throw new IllegalArgumentException("no : in URL");
		}
		String scheme = uri.substring(0, colon);
		ConnectionFactory prov = getConnectionFactory(scheme);
		Connection ret = null;
		if (prov != null) {
			ret = prov.createConnection(uri, mode, timeouts);
		}
		if (ret == null) {
			try {
				ret = Connector.open(uri, mode, timeouts);
			}
			catch (Exception ex) {
			}
		}
		if (ret != null) {
			return ret;
		}
		throw new ConnectionNotFoundException("Failed to create connection "
				+ uri);
	}

	private ConnectionFactory getConnectionFactory(String scheme) {
		ServiceReference[] cfRefs = factoryTracker.getServiceReferences();
		if (cfRefs == null || cfRefs.length == 0) {
			// no registered ConnectionFactory objects
			return null;
		}
		ServiceReference prospective = null;
		int pRanking = 0;
		for (int i = 0; i < cfRefs.length; i++) {
			String[] s = null;
			Object o = cfRefs[i].getProperty(ConnectionFactory.IO_SCHEME);
			if (o instanceof String) {
				s = new String[] {(String)o};
			}
			else if (o instanceof String[]) {
				s = (String[]) o;
			}

			if ((s != null) && contains(s, scheme)) {
				// found a ConnectionFactory object for the given scheme
				if (prospective == null) {
					prospective = cfRefs[i];
					pRanking = getRanking(cfRefs[i]);
				}
				else {
					int r = getRanking(cfRefs[i]);
					if (r > pRanking) {
						// found a ConnectionFactory with higher ranking
						prospective = cfRefs[i];
						pRanking = r;
					}
					else
						if (pRanking == r) {
							Long id1 = (Long) prospective
									.getProperty(Constants.SERVICE_ID);
							Long id2 = (Long) cfRefs[i]
									.getProperty(Constants.SERVICE_ID);
							if (id2.longValue() < id1.longValue()) {
								prospective = cfRefs[i];
							}
						}
				}
			}
		}
		// Must check for null because scheme can be absent
		if (prospective != null)
			return (ConnectionFactory) factoryTracker.getService(prospective);
		else
			return null;
	}

	private int getRanking(ServiceReference ref) {
		Object rank = ref.getProperty(Constants.SERVICE_RANKING);
		if (rank == null || !(rank instanceof Integer)) {
			return 0;
		}
		return ((Integer) rank).intValue();
	}

	private boolean contains(String[] a, String el) {
		for (int i = 0; i < a.length; i++) {
			if (a[i].equals(el)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Create and open an <tt>DataInputStream</tt> object for the specified
	 * name.
	 * 
	 * @param name the URI for the connection.
	 * @throws IOException if and I/O error occurs
	 * @throws ConnectionNotFoundException if the <tt>Connection</tt> object
	 *         can not be made or if no handler for the requested scheme can be
	 *         found.
	 * @throws IllegalArgumentException if the given uri is invalid
	 * @return A <tt>DataInputStream</tt> to the given URI
	 */
	public DataInputStream openDataInputStream(String name) throws IOException {
		Connection conn = open(name, READ);
		if (!(conn instanceof InputConnection)) {
			try {
				conn.close();
			}
			catch (IOException e) {
			}
			throw new IOException(
					"Connection does not implement InputConnection:"
							+ conn.getClass());
		}
		return ((InputConnection) conn).openDataInputStream();
	}

	/**
	 * Create and open an <tt>DataOutputStream</tt> object for the specified
	 * name.
	 * 
	 * @param name the URI for the connection.
	 * @throws IOException if and I/O error occurs
	 * @throws ConnectionNotFoundException if the <tt>Connection</tt> object
	 *         can not be made or if no handler for the requested scheme can be
	 *         found.
	 * @throws IllegalArgumentException if the given uri is invalid
	 * @return A <tt>DataOutputStream</tt> to the given URI
	 */
	public DataOutputStream openDataOutputStream(String name)
			throws IOException {
		Connection conn = open(name, WRITE);
		if (!(conn instanceof OutputConnection)) {
			try {
				conn.close();
			}
			catch (IOException e) {
			}
			throw new IOException(
					"Connection does not implement OutputConnection:"
							+ conn.getClass());
		}
		return ((OutputConnection) conn).openDataOutputStream();
	}

	/**
	 * Create and open an <tt>InputStream</tt> object for the specified name.
	 * 
	 * 
	 * @param name the URI for the connection.
	 * @throws IOException if and I/O error occurs
	 * @throws ConnectionNotFoundException if the <tt>Connection</tt> object
	 *         can not be made or if no handler for the requested scheme can be
	 *         found.
	 * @throws IllegalArgumentException if the given uri is invalid
	 * @return A <tt>InputStream</tt> to the given URI
	 */
	public InputStream openInputStream(String name) throws IOException {
		Connection conn = open(name, READ);
		if (!(conn instanceof InputConnection)) {
			try {
				conn.close();
			}
			catch (IOException e) {
			}
			throw new IOException(
					"Connection does not implement InputConnection:"
							+ conn.getClass());
		}
		return ((InputConnection) conn).openInputStream();
	}

	/**
	 * Create and open an <tt>OutputStream</tt> object for the specified name.
	 * 
	 * @param name the URI for the connection.
	 * @throws IOException if and I/O error occurs
	 * @throws ConnectionNotFoundException if the <tt>Connection</tt> object
	 *         can not be made or if no handler for the requested scheme can be
	 *         found.
	 * @throws IllegalArgumentException if the given uri is invalid
	 * @return A <tt>OutputStream</tt> to the given URI
	 */
	public OutputStream openOutputStream(String name) throws IOException {
		Connection conn = open(name, WRITE);
		if (!(conn instanceof OutputConnection)) {
			try {
				conn.close();
			}
			catch (IOException e) {
			}
			throw new IOException(
					"Connection does not implement OutputConnection:"
							+ conn.getClass());
		}
		return ((OutputConnection) conn).openOutputStream();
	}
}
