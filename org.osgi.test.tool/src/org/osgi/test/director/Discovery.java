/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.director;

import java.io.InterruptedIOException;
import java.net.*;
import java.util.*;
import org.osgi.framework.BundleContext;
import org.osgi.test.service.RemoteService;

/**
 * The Discovery class keeps track remote services on the local net and
 * registers these in the registry.
 * 
 * A local net service broadcasts regularly a message with:
 * 
 * <pre>
 * 
 *  
 *  		application=&lt;app&gt; host=&lt;host&gt; port=&lt;port&gt;
 *  	
 *  
 * </pre>
 * 
 * This class picks these messages up and parses them. If a service is detected
 * it is registered as a RemoteService object in the framework registry. As a
 * property, the application is set to the received application. When a
 * broadcast has not been received for a certain amount of time, the service is
 * removed again. This allows interested parties to listen to remote services in
 * the framework.
 */
public class Discovery extends Thread {
	public final static int	PORT		= 2001;
	BundleContext			context;
	DatagramSocket			listener;
	boolean					cont		= true;
	Hashtable				services	= new Hashtable();	// RemoteService
	// ->
	// RemoteServiceImpl
	static final long		LEASE		= 20000;			// ms == 20 secs

	/**
	 * Create a discovery object.
	 * 
	 * @param context Framework context
	 */
	public Discovery(BundleContext context) {
		this.context = context;
		start();
	}

	/**
	 * Close the discovery.
	 */
	public void close() {
		cont = false;
		DatagramSocket l = listener;
		if (l != null)
			l.close();
		listener = null;
	}

	/**
	 * Update the service in the registry.
	 * 
	 * If the service already exists, the time is updated. Else it is registered
	 * in the framework. The parameter is a newly created service and is used to
	 * find the same service in the local hashtable.
	 * 
	 * @service service a newly created service
	 */
	void updateService(RemoteServiceImpl service) {
		RemoteServiceImpl actual = (RemoteServiceImpl) services.get(service);
		if (actual == null)
			addRemote(service);
		else
			actual.update();
	}

	public void addRemote(RemoteServiceImpl service) {
		log("Adding " + service + " App = " + service.getApplication(), null);
		service.registerAt(context);
		services.put(service, service);
	}

	/**
	 * A service was not found during a certain time, remove it from the
	 * registry.
	 * 
	 * @param service service to be removed.
	 */
	void removeService(RemoteService service) {
		RemoteServiceImpl actual = (RemoteServiceImpl) services.get(service);
		if (actual != null) {
			log("Removing " + service, null);
			actual.registration.unregister();
			services.remove(actual);
		}
	}

	/**
	 * Thread run.
	 * 
	 * Listen to datagrams on port 2001 and parse these. The listening is done
	 * with a time out. In this timeout the existing services are checked and
	 * removed if their lease expired.
	 */
	public void run() {
		try {
			int port;
			String application;
			String comment;
			String host;
			try {
				listener = new DatagramSocket(PORT);
			}
			catch( BindException e ) {
				log("DatagramSockeet for target discovery already in use (" + PORT  + ")", null );
				return;
			}
			listener.setSoTimeout(25000);
			log("Discovery starts.", null);
			while (cont) {
				try {
					verify();
					DatagramPacket packet = new DatagramPacket(new byte[256],
							256);
					listener.receive(packet);
					String msg = new String(packet.getData(), 0, packet
							.getLength());
					StringTokenizer st = new StringTokenizer(msg, " =\n\r\t");
					application = null;
					comment = "";
					host = packet.getAddress().getHostAddress();
					port = -1;
					while (st.hasMoreTokens()) {
						String key = st.nextToken();
						String val = st.nextToken();
						if (key.equals("application"))
							application = val;
						else
							if (key.equals("host"))
								;
							else
								if (key.equals("port"))
									port = Integer.parseInt(val);
								else
									if (key.equals("comment"))
										comment = val;
					}
					if (host != null && application != null && port > 0) {
						RemoteServiceImpl service = new RemoteServiceImpl(
								application, host, port, comment);
						updateService(service);
					}
					else
						log("Invalid remote service request from "
								+ packet.getAddress(), null);
				}
				catch (InterruptedIOException e) {
				}
				catch (Exception e) {
					if (cont)
						log("Receving remote service packets, ignoring", e);
				}
			}
			listener.close();
			listener = null;
		}
		catch (Exception e) {
			log("Main discover loop exit", e);
		}
		log("Discovery quits.", null);
	}

	/**
	 * Check loop to see if services expired their lease.
	 */
	void verify() {
		long now = System.currentTimeMillis();
		Hashtable s = new Hashtable();
		for (Enumeration e = services.keys(); e.hasMoreElements();) {
			RemoteServiceImpl service = (RemoteServiceImpl) e.nextElement();
			if (service.getModified() + LEASE < now)
				removeService(service);
			else
				s.put(service, service);
		}
		services = s;
	}

	/**
	 * Temporary log.
	 */
	void log(String s, Exception e) {
		System.out.println(s);
		if (cont && e != null)
			e.printStackTrace();
	}
}
