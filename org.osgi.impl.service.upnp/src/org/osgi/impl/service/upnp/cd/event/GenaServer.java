package org.osgi.impl.service.upnp.cd.event;

import java.io.IOException;
import java.net.*;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.upnp.cd.control.ControlImpl;

// HttpServer which listens to a particular port,accecepts requests, process it and sends the
// response back to the client.Uses HttpRegistry to check for the existence of resources/servlets
// and does necessary processing.
public class GenaServer extends Thread {
	private boolean			done	= false;	// Flag indicating when to
	// stop
	public Socket			client;
	private EventRegistry	eventRegistry;
	public ControlImpl		cti;
	private int				defaultPort;
	private ServerSocket	serverSock;
	public BundleContext	context;

	// Constructor for HttpServer which will be called by HttpService when it
	// starts.This server
	// will try to listen to UserPort supplied by HttpService or can be by
	// Framework .
	// If not supplied,uses the ServerConfiguration file to get the primary
	// port.If that also
	// failed, again uses the ServerConfiguration file to get the secondary port
	// and will try to
	// listen.If thats also not working,will come out by throwing an appropriate
	// exception.
	public GenaServer(int port, ControlImpl cti, BundleContext context,
			EventRegistry eventRegistry) {
		defaultPort = port;
		this.eventRegistry = eventRegistry;
		this.cti = cti;
		this.context = context;
		boolean res = bindPort(defaultPort);
		if (!res) {
			while (true) {
				res = bindPort(defaultPort++);
				if (res) {
					break;
				}
			}
		}
		eventRegistry.setPort(getServerPort());
	}

	// Starts server. Blocks on accept().When ever a client request comes , gets
	// a thread
	// from the thread pool if available,or creates a new thread and starts the
	// processing.After
	// each processing, checks the pool,if pool is not empty , relases the
	// thread else added to the
	// pool for waiting requests.
	public void run() {
		try {
			Processor pr = null;
			while (!done) {
				client = serverSock.accept(); // Listen for incoming requests.
				if (done) {
					if (client != null) {
						client.close();
						client = null;
					}
					if (serverSock != null) {
						serverSock.close();
						serverSock = null;
					}
					break;
				}
				if (client != null) {
					pr = new Processor(this, client);
					(new Thread(pr)).start();
				}
			}
		}
		catch (IOException e) {
			if (!done) {
				throw new RuntimeException("Dynamic server error");
			}
		}
	}

	// Stops the HTTP server. Closes the server socket,closes the client socket
	// .
	// releases all the threads from pool
	public void shutdown() throws IOException {
		done = true;
		if (done) {
			String hostname;
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			}
			catch (UnknownHostException e) {
				hostname = "";
			}
			client = new Socket(hostname, getServerPort());
			if (client != null) {
				client.close();
				client = null;
			}
			if (serverSock != null) {
				serverSock.close();
				serverSock = null;
			}
		}
	}

	// used to get the server port.If server socket is not null, returns the
	// port else returns -1
	public int getServerPort() {
		return (serverSock != null) ? serverSock.getLocalPort() : -1;
	}

	// This methods tries to bind the serverSocket to a given port, if not
	// returns false
	private boolean bindPort(int port) {
		try {
			serverSock = new ServerSocket(port);
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	// used to get the server port.If server socket is not null, returns the
	// port else returns -1
	public String getServerIP() {
		try {
			return (serverSock != null) ? InetAddress.getLocalHost()
					.getHostAddress()
					+ ":" + serverSock.getLocalPort() : "-1";
		}
		catch (Exception e) {
			return null;
		}
	}
}
