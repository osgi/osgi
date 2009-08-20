/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A link between two processes.
 * 
 * This is a base class to be used as a link between two processes.
 * Serialization is used for communication protocol.
 * <p>
 * This class is in the domain of CORBA, RMI, Voyager etc. These are not used so
 * that no extra components are introduced which all have their issues. E.g.
 * CORBA/RMI creates lots and lots of classes and have all kind of side effects.
 * RMI is not supported in Internet Explorer, Voyager needs licensing etc. So
 * therefore this is a simple point-to-point connection. It is intended to be
 * extended by a semantic class that implements the methods and marshalling.
 * <p>
 * The operation is quite simple. The link class is opened with a socket. The
 * other side of the socket should also be a socket. When the link is opened, it
 * will start a thread that waits for incoming messages. These are dispatched in
 * the subclass or treated as a reply. See the Message class for details.
 */
public class Link extends Thread {
	private DataOutputStream	_out;							// -> to other
																 // link
	private DataInputStream		_in;							// <- from
																   // other link
	Socket						_socket;						// Socket to
																  // allow
																  // closing
	boolean						_open;							// Are we
																	 // open?
	Hashtable					_requests	= new Hashtable();	// Outstanding
																	// requests
	Thread						_delivery;
	Queue						_queue;
	PrintStream					_debug;
	public static final int		OK			= 0;
	public static final int		FAILED		= -1;
	public int					TIMEOUT		= 1200000;			// ms 30
																	   // secs

	public Link(String name) {
		super(name);
		//setDebug( getClass().getName() );
	}

	/**
	 * Answer if link is currently open.
	 * 
	 * @returns true if open, false if closed.
	 */
	public boolean isOpen() {
		return _open;
	}

	public InetAddress getAddress() {
		return _socket.getInetAddress();
	}

	/**
	 * Open the link to the other side.
	 * 
	 * Create a thread that will
	 * 
	 * @param socket open socket to other link
	 */
	public void open(Socket socket) throws Exception {
		debug("open start", null);
		_socket = socket;
		_out = new DataOutputStream(socket.getOutputStream());
		_open = true;
		setDaemon(true);
		_queue = new Queue();
		_delivery = new Thread() {
			public void run() {
				while (_open)
					try {
						Message msg = (Message) _queue.pop(2000);
						if (msg != null)
							deliver(msg);
					}
					catch (IOException e) {
						System.out.println("Error in open(Socket) in "
								+ getName());
						e.printStackTrace();
					}
			}
		};
		_delivery.setDaemon(true);
		_delivery.start();
		synchronized (_socket) {
			try {
				start();
				_socket.wait(30000);
			}
			catch (InterruptedException e) {
			}
		}
		debug("open done", null);
	}

	/**
	 * Close the link.
	 * 
	 * This is a null operation if the link IS already closed. All threads
	 * waiting for a reply will be canceled. We ignore any exceptions here
	 * because we are cleaning up. If things, fail, tough luck.
	 */
	public void close() {
		if (_open) {
			_open = false;
			debug("starting to close", null);
			try {
				_socket.close();
				for (Enumeration e = _requests.elements(); e.hasMoreElements();) {
					Message message = (Message) e.nextElement();
					debug("canceling", message);
					message.cancel();
				}
				debug("closing log now", null);
				if (_debug != null)
					_debug.close();
				_debug = null;
			}
			catch (IOException e) {
				debug("close", e);
			}
		}
	}

	/**
	 * Send a message to the other side. A message is defined as an integer for
	 * the type and some type of object that MUST be serializable. This is an
	 * asynchronous notification.
	 * 
	 * @param type Type of the message
	 * @param object Whatever as long as it is serializable
	 */
	protected void send(int type, Object object) throws IOException {
		Message msg = new Message(type, object);
		send(msg);
	}

	/**
	 * Perform a request to the other side and wait for a reply. If the message
	 * on the other side threw an exception, then we will throw a LinkException
	 * which contains that remote exception. An exception is also thrown when
	 * the reply is not received within the timeout.
	 * 
	 * @param type Type of message
	 * @param object Serializable parameter
	 * @returns Reply
	 */
	protected Object request(int type, Object object) throws IOException {
		Message msg = new Message(type, object);
		Message reply = request(msg, TIMEOUT);
		if (reply == null) {
			debug("request time out", msg);
			throw new IOException("Time out on receiving msg");
		}
		if (reply.getType() == OK)
			return reply.getContent();
		else {
			debug("Not OK reply, msg=", msg);
			debug("Not OK reply, reply=", reply);
			throw new LinkException((Exception) reply.getContent());
		}
	}

	/**
	 * Entry for thread that listens to incoming messages.
	 * 
	 * This run will keep on running as long as the _open flag is true. To quit
	 * the link, the _open flag should be false and the socket should be closed.
	 * <p>
	 * We deliver each message in a new thread. Not very efficient, but in this
	 * case not that relevant.
	 */
	public void run() {
		try {
			debug("run", null);
			_in = new DataInputStream(_socket.getInputStream());
			//
			// Sync the open method with out start.
			//
			synchronized (_socket) {
				_socket.notify();
			}
			while (_open) {
				int sz = _in.readInt();
				if (sz < 0) {
					System.err.println("Closing because we read " + sz
							+ " as sz");
					close();
					break;
				}
				byte[] data = new byte[sz];
				for (int i = 0; i < sz; i++) {
					int c = _in.read();
					if (c < 0) {
						System.err.println("Closing because we read " + sz
								+ " as sz");
						close();
						break;
					}
					data[i] = (byte) c;
				}
				final Message msg = new Message(data);
				if (msg.getType() == -1) {
					close();
					break;
				}
				debug("rx", msg);
				msg.setLink(this);
				if (msg._responseID != 0) {
					//
					// This is a response
					//
					Object key = key(msg._responseID);
					synchronized (_requests) {
						Message request = (Message) _requests.get(key);
						if (request != null)
							request.replyReceived(msg);
						else
							unexpectedResponse(msg);
					}
				}
				else {
					//
					// This is a message that needs to be delivered
					// to our owner. This is done in a separate
					// thread to prevent deadlocks.
					//
					_queue.push(msg);
				}
			}
		}
		catch (EOFException e) {
			debug("end of sessions other site", e);
		}
		catch (SocketException e) { /* debug( "end of session this site", e ); */
		}
		catch (Exception e) {
			debug("rx loop", e);
			e.printStackTrace();
		}
		linkClosed();
	}

	protected void linkClosed() {
	}

	/**
	 * This is when we get a response but nobody is waiting for it.
	 * 
	 * @param msg orphanaged response
	 */
	public void unexpectedResponse(Message msg) {
		debug("unexpected", msg);
	}

	/**
	 * Request a response from this message.
	 * 
	 * @param msg Message to send
	 * @param timout if 0, send a notification, if >0 wait for response
	 */
	public Message request(Message msg, int timeout) throws IOException {
		if (!_open) {
			debug("request (not open)", msg);
			throw new IOException("Link is not open");
		}
		msg.setLink(this);
		if (timeout == 0) {
			send(msg);
			return null;
		}
		Object key = null;
		try {
			//
			// Wait for response. We have a list of message in
			// in that are waiting. We add the messages and
			// then wait at the message which will be notified
			// by our run message when a response comes in.
			//
			key = key(msg._requestID);
			_requests.put(key, msg);
			send(msg);
			return msg.getResponse(timeout);
		}
		finally {
			// No pollution
			_requests.remove(key);
		}
	}

	/**
	 * Send a message to the other side.
	 */
	synchronized void send(Message msg) throws IOException {
			byte[] data = msg.asBytes();
			debug("tx", msg + "  " + data.length);
			_out.writeInt(data.length);
			_out.write(data);
			_out.flush();
	}

	/**
	 * Key generator to make an object key from request/reply transaction id.
	 */
	Object key(int key) {
		return new Integer(key);
	}

	/**
	 * This method should be overridden by the subclass to implement the message
	 * semantics.
	 * 
	 * @param message Just received request message
	 */
	protected void deliver(Message message) throws IOException {
		message.reply(-1, "NO RECEIVER");
	}

	public void setDebug(String log) {
		try {
			_debug = new PrintStream(new FileOutputStream(log));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	static int	_n;

	public void debug(String type, Object msg) {
		PrintStream debug = _debug;
		if (debug != null) {
			synchronized (debug) {
				debug.println(_n++ + ":" + type + ": " + msg + ":" + this);
				debug.flush();
			}
		}
	}

	public void setTimeout(int timeout) {
		TIMEOUT = timeout;
	}

	public String toString() {
		return getName() + "(" + System.currentTimeMillis() + ")";
	}
}
