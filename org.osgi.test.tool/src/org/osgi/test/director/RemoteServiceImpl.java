/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.director;

import java.net.InetAddress;
import java.util.Properties;
import org.osgi.framework.*;
import org.osgi.test.service.RemoteService;

/**
 * Used by Discovery to implement a RemoteService.
 * 
 * A remote service is a reference to a host:port pair somewhere on the net.
 */
public class RemoteServiceImpl implements RemoteService {
	String				application;
	String				host;
	int					port;
	String				comment;
	long				modified;
	ServiceRegistration	registration;

	/**
	 * Default constructor.
	 * 
	 * @param application name of the application
	 * @param host name of the host
	 * @param port port number
	 */
	public RemoteServiceImpl(String application, String host, int port,
			String comment) {
		this.application = application;
		this.host = host;
		this.port = port;
		this.comment = comment;
		update();
	}

	public String getApplication() {
		return application;
	}

	public String getHost() {
		return host;
	}

	public String getComment() {
		return comment;
	}

	public int getPort() {
		return port;
	}

	public long getModified() {
		return modified;
	}

	public void update() {
		modified = System.currentTimeMillis();
	}

	public String toString() {
		return application + ":" + host + ":" + port + " " + comment;
	}

	public String getID() {
		return toString();
	}

	public int hashCode() {
		return application.hashCode() ^ host.hashCode() ^ port;
	}

	/**
	 * Check if objects are equal.
	 * 
	 * Equality is defined as equal application, host and port.
	 */
	public boolean equals(Object service) {
		if (service instanceof RemoteService) {
			RemoteServiceImpl other = (RemoteServiceImpl) service;
			return application.equals(other.application)
					&& host.equals(other.host) && port == other.port;
		}
		else
			return false;
	}

	/**
	 * Register the remote service.
	 */
	public void registerAt(BundleContext context) {
		Properties properties = new Properties();
		properties.put("application", getApplication());
		properties.put("host", getHost());
		properties.put("port", new Integer(getPort()));
		properties.put("comment", getComment());
		registration = context.registerService(RemoteService.class.getName(),
				this, properties);
	}

	/**
	 * Check if this service runs on the local machine.
	 */
	static InetAddress	localif;

	static public boolean isLocal(RemoteService rs) {
		if (localif == null)
			try {
				localif = InetAddress.getByName("127.0.0.1");
			}
			catch (Exception e) {
				e.printStackTrace();
			};
		try {
			InetAddress host = InetAddress.getLocalHost();
			InetAddress target = InetAddress.getByName(rs.getHost());
			return host.equals(target)
					|| (localif != null && target.equals(localif));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
