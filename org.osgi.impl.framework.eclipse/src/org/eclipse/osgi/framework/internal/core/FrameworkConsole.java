/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.io.*;
import java.net.*;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class starts OSGi with a console for development use.
 *
 * FrameworkConsole provides a printStackTrace method to print Exceptions and their
 * nested Exceptions.
 */
public class FrameworkConsole implements Runnable {
	/** The stream to receive commands on  */
	protected Reader in;
	/** The stream to write command results to */
	protected PrintWriter out;
	/** The current bundle context */
	protected org.osgi.framework.BundleContext context;
	/** The current osgi instance */
	protected OSGi osgi;
	/** The command line arguments passed at launch time*/
	protected String[] args;
	/** The OSGi Command Provider */
	protected CommandProvider osgicp;
	/** A tracker containing the service object of all registered command providers */
	protected CommandProviderTracker cptracker;

	/** Default code page which must be supported by all JVMs */
	static String defaultEncoding = "iso8859-1"; //$NON-NLS-1$
	/** The current setting for code page */
	static String encoding = System.getProperty("file.encoding", defaultEncoding); //$NON-NLS-1$

	/** set to true if accepting commands from port */
	protected boolean useSocketStream = false;
	protected boolean disconnect = false;
	protected int port = 0;
	protected ServerSocket ss = null;
	protected ConsoleSocketGetter scsg = null;
	protected Socket s;

	/**
	 Constructor for FrameworkConsole.
	 It creates a service tracker to track CommandProvider registrations.
	 The console InputStream is set to System.in and the console PrintStream is set to System.out.
	 @param osgi - an instance of an osgi framework
	 @param args - any arguments passed on the command line when Launcher is started.
	 */
	public FrameworkConsole(OSGi osgi, String[] args) {

		getDefaultStreams();

		this.args = args;
		this.osgi = osgi;

		initialize();

	}

	/**
	 Constructor for FrameworkConsole.
	 It creates a service tracker to track CommandProvider registrations.
	 The console InputStream is set to System.in and the console PrintStream is set to System.out.
	 @param osgi - an instance of an osgi framework
	 @param args - any arguments passed on the command line when Launcher is started.
	 */
	public FrameworkConsole(OSGi osgi, int port, String[] args) {

		getSocketStream(port);

		this.useSocketStream = true;
		this.port = port;
		this.args = args;
		this.osgi = osgi;

		initialize();
	}

	/**
	 *  Open streams for system.in and system.out
	 */
	private void getDefaultStreams() {
		in = createBufferedReader(System.in);
		out = createPrintWriter(System.out);
	}

	/**
	 *  Open a socket and create input and output streams
	 *
	 * @param port number to listen on
	 */
	private void getSocketStream(int port) {
		try {
			System.out.println(NLS.bind(ConsoleMsg.CONSOLE_LISTENING_ON_PORT, String.valueOf(port))); 
			if (ss == null) {
				ss = new ServerSocket(port);
				scsg = new ConsoleSocketGetter(ss);
			}
			scsg.setAcceptConnections(true);
			s = scsg.getSocket();

			in = createBufferedReader(s.getInputStream());
			out = createPrintWriter(s.getOutputStream());
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return a BufferedReader from an InputStream.  Handle encoding.
	 *
	 * @param _in An InputStream to wrap with a BufferedReader
	 * @return a BufferedReader
	 */
	private BufferedReader createBufferedReader(InputStream _in) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(_in, encoding));
		} catch (UnsupportedEncodingException uee) {
			// if the encoding is not supported by the jvm, punt and use whatever encodiing there is
			reader = new BufferedReader(new InputStreamReader(_in));
		}
		return reader;
	}

	/**
	 * Return a PrintWriter from an OutputStream.  Handle encoding.
	 *
	 * @param _out An OutputStream to wrap with a PrintWriter
	 * @return a PrintWriter
	 */
	private PrintWriter createPrintWriter(OutputStream _out) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_out, encoding)), true);
		} catch (UnsupportedEncodingException uee) {
			// if the encoding is not supported by the jvm, punt and use whatever encodiing there is
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_out)), true);
		}
		return writer;
	}

	/**
	 *  Return the current output PrintWriter
	 * @return The currently active PrintWriter
	 */
	public PrintWriter getWriter() {
		return out;
	}

	/**
	 *  Return the current input BufferedReader
	 * @return The currently active BufferedReader
	 */
	public BufferedReader getReader() {
		return (BufferedReader) in;
	}

	/**
	 *  Return if the SocketSteam (telnet to the console) is being used 
	 * @return Return if the SocketSteam is being used 
	 */
	public boolean getUseSocketStream() {
		return useSocketStream;
	}

	/**
	 *  Initialize common things:
	 *  - system bundle context
	 *  - ServiceTracker to track CommandProvider registrations
	 *  - create OSGi CommandProvider object
	 */
	private void initialize() {

		context = osgi.getBundleContext();

		// set up a service tracker to track CommandProvider registrations
		cptracker = new CommandProviderTracker(context, CommandProvider.class.getName(), this);
		cptracker.open();

		// register the OSGi command provider
		osgicp = new FrameworkCommandProvider(osgi);

	}

	/**
	 * Begin doing the active part of the class' code. Starts up the console.
	 */
	public void run() {
		try {
			console(args);
			if (useSocketStream) {
				while (true) {
					getSocketStream(port);
					console();
				}
			}
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}

	/**
	 * Command Line Interface for OSGi. The method processes the initial commands
	 * and then reads and processes commands from the console InputStream.
	 * Command output is written to the console PrintStream. The method will
	 * loop reading commands from the console InputStream until end-of-file
	 * is reached. This method will then return.
	 *
	 * @param args Initial set of commands to execute.
	 * @throws IOException
	 */
	public void console(String args[]) throws IOException {
		// first handle any args passed in from launch
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				docommand(args[i]);
			}
		}
		console();
	}

	/**
	 * Command Line Interface for OSGi. The method processes the initial commands
	 * and then reads and processes commands from the console InputStream.
	 * Command output is written to the console PrintStream. The method will
	 * loop reading commands from the console InputStream until end-of-file
	 * is reached. This method will then return.
	 * @throws IOException
	 */
	protected void console() throws IOException {
		Object lock = new Object();
		disconnect = false;
		// wait to receive commands from console and handle them
		BufferedReader br = (BufferedReader) in;
		//cache the console prompt String
		String consolePrompt = "\r\n" + ConsoleMsg.CONSOLE_PROMPT; //$NON-NLS-1$
		boolean block = System.getProperty("osgi.dev") != null; //$NON-NLS-1$
		while (!disconnect) {
			out.print(consolePrompt);
			out.flush();

			String cmdline = null;
			if (block) {
				// bug 40066: avoid waiting on input stream - apparently generates contention with other native calls 
				try {
					synchronized (lock) {
						while (!br.ready())
							lock.wait(300);
					}
					cmdline = br.readLine();
				} catch (InterruptedException e) {
					// do nothing; probably got disconnected
				}
			} else
				cmdline = br.readLine();

			if (cmdline == null) {
				break;
			}

			docommand(cmdline);
		}
	}

	/**
	 *  Process the args on the command line.
	 *  This method invokes a CommandInterpreter to do the actual work.
	 *
	 *  @param cmdline a string containing the command line arguments
	 */
	protected void docommand(String cmdline) {
		if (cmdline != null && cmdline.length() > 0) {
			CommandInterpreter intcp = new FrameworkCommandInterpreter(cmdline, cptracker.getServices(), this);
			String command = intcp.nextArgument();
			if (command != null) {
				intcp.execute(command);
			}
		}
	}

	/**
	 * Disconnects from console if useSocketStream is set to true.  This
	 * will cause the console to close from a telnet session.
	 */

	void disconnect() throws IOException {
		disconnect = true;
		out.close();
		in.close();
		s.close();
	}

	/**
	 * Reads a string from standard input until user hits the Enter key.
	 *
	 * @return	The string read from the standard input without the newline character.
	 */
	public String getInput() {
		String input;
		try {
			/** The buffered input reader on standard in. */
			input = ((BufferedReader) in).readLine();
			System.out.println("<" + input + ">");  //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException e) {
			input = ""; //$NON-NLS-1$
		}
		return input;
	}

	/**
	 * ConsoleSocketGetter - provides a Thread that listens on the port
	 * for FrameworkConsole.  If acceptConnections is set to true then
	 * the thread will notify the getSocket method to return the socket.
	 * If acceptConnections is set to false then the client is notified
	 * that connections are not currently accepted and closes the socket.
	 */
	class ConsoleSocketGetter implements Runnable {

		/** The ServerSocket to accept connections from */
		ServerSocket server;
		/** The current socket to be returned by getSocket */
		Socket socket;
		/** if set to true then allow the socket to be returned by getSocket */
		boolean acceptConnections = true;
		/** Lock object to synchronize returning of the socket */
		Object lock = new Object();

		/**
		 * Constructor - sets the server and starts the thread to
		 * listen for connections.
		 *
		 * @param server a ServerSocket to accept connections from
		 */
		ConsoleSocketGetter(ServerSocket server) {
			this.server = server;
			Thread t = new Thread(this, "ConsoleSocketGetter"); //$NON-NLS-1$
			t.start();
		}

		public void run() {
			while (true) {

				try {
					socket = ss.accept();
					if (!acceptConnections) {
						PrintWriter o = createPrintWriter(socket.getOutputStream());
						o.println(ConsoleMsg.CONSOLE_TELNET_CONNECTION_REFUSED); 
						o.println(ConsoleMsg.CONSOLE_TELNET_CURRENTLY_USED);
						o.println(ConsoleMsg.CONSOLE_TELNET_ONE_CLIENT_ONLY); 
						o.close();
						socket.close();
					} else {
						synchronized (lock) {
							lock.notify();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		/**
		 * Method to get a socket connection from a client.
		 *
		 * @return - Socket from a connected client
		 */
		public Socket getSocket() throws InterruptedException {
			// wait for a socket to get assigned from the accepter thread
			synchronized (lock) {
				lock.wait();
			}
			setAcceptConnections(false);
			return socket;
		}

		/**
		 * Method to indicate if connections are accepted or not.  If set
		 * to false then the clients will be notified that connections
		 * are not accepted.
		 */
		public void setAcceptConnections(boolean acceptConnections) {
			this.acceptConnections = acceptConnections;
		}
	}

	class CommandProviderTracker extends ServiceTracker {

		FrameworkConsole con;

		CommandProviderTracker(org.osgi.framework.BundleContext context, String clazz, FrameworkConsole con) {
			super(context, clazz, null);
			this.con = con;
		}

		/**
		 * Default implementation of the <tt>ServiceTrackerCustomizer.addingService</tt> method.
		 *
		 * <p>This method is only called when this <tt>ServiceTracker</tt> object
		 * has been constructed with a <tt>null</tt> <tt>ServiceTrackerCustomizer</tt> argument.
		 *
		 * The default implementation returns the result of
		 * calling <tt>getService</tt>, on the
		 * <tt>BundleContext</tt> object with which this <tt>ServiceTracker</tt> object was created,
		 * passing the specified <tt>ServiceReference</tt> object.
		 *
		 * <p>This method can be overridden to customize
		 * the service object to be tracked for the service
		 * being added.
		 *
		 * @param reference Reference to service being added to this
		 * <tt>ServiceTracker</tt> object.
		 * @return The service object to be tracked for the service
		 * added to this <tt>ServiceTracker</tt> object.
		 */
		public Object addingService(ServiceReference reference) {
			CommandProvider cp = (CommandProvider) super.addingService(reference);
			return cp;
		}

		/**
		 * Return an array of service objects for all services
		 * being tracked by this <tt>ServiceTracker</tt> object.
		 *
		 * The array is sorted primarily by descending Service Ranking and
		 * secondarily by ascending Service ID.
		 *
		 * @return Array of service objects or <tt>null</tt> if no service
		 * are being tracked.
		 */
		public Object[] getServices() {
			ServiceReference[] serviceRefs = (ServiceReference[]) super.getServiceReferences();
			Util.dsort(serviceRefs, 0, serviceRefs.length);

			Object[] serviceObjects = new Object[serviceRefs.length];
			for (int i = 0; i < serviceRefs.length; i++) {
				serviceObjects[i] = FrameworkConsole.this.context.getService(serviceRefs[i]);
			}
			return serviceObjects;
		}

	}

}
