package org.osgi.impl.service.upnp.cp.event;

import java.io.IOException;
import java.net.*;
import java.util.Dictionary;

public class GenaServer extends Thread {
	private boolean			done	= false;
	public Socket			client;
	private Dictionary		dict;
	public EventServiceImpl	esi;
	private int				defaultPort;
	private ServerSocket	serverSock;

	public GenaServer(int port, EventServiceImpl esi) {
		defaultPort = port;
		this.esi = esi;
		boolean res = bindPort(defaultPort);
		if (!res) {
			while (true) {
				res = bindPort(defaultPort++);
				if (res) {
					break;
				}
			}
		}
		esi.setPort(getServerPort());
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
	int getServerPort() {
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

	public int getPort() {
		return getServerPort();
	}
}
