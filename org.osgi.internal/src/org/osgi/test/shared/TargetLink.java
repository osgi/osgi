/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.io.*;
import java.net.SocketException;
import java.util.Dictionary;

/**
 * This class is the network link between the target framework and the director.
 * This class is used by both! By reusing the class, all message types and
 * parameter marshalling can be confined to a single class.
 */
public class TargetLink extends Link {
	public final static int	PORT				= 4446;
	final static int		INSTALL				= 1;
	final static int		SEND_TO_TARGET		= 3;
	final static int		SEND_TO_RUN			= 4;
	final static int		SEND_LOG			= 7;
	final static int		STOPPED				= 8;
	final static int		UNINSTALL			= 9;
	final static int		TARGET_PROPERTIES	= 10;
	final static int		REBOOT				= 11;
	final static int		CLOSE_TARGET		= 12;
	final static int		UPDATE_FRAMEWORK	= 13;
	final static int		SET_TEST_PROPERTIES	= 14;

	public IRun				_run;
	public ITarget			_target;

	public TargetLink(IRun run) {
		super("run");
		_run = run;
	}

	public TargetLink(ITarget target) {
		super("target");
		_target = target;
	}

	//
	// Run -> Target
	//
	/**
	 * Called from director to target to install a new bundle.
	 * 
	 * @param name name of bundle
	 * @param in stream with jar
	 */
	public void install(String name, InputStream in) throws IOException {
		byte[] buffer = collect(in, 0);
		in.close();
		request(INSTALL, new Object[] {name, buffer});
	}

	/**
	 * Called from director to target to uninstall a bundle.
	 * 
	 * @param name name of bundle as given in install
	 */
	public void uninstall(String name) throws IOException {
		request(UNINSTALL, name);
	}

	/**
	 * Called from director to target to uninstall a bundle.
	 * 
	 * @param name name of bundle as given in install
	 */
	public void reboot(int cause) throws IOException {
		send(REBOOT, new Integer(cause));
	}

	/**
	 * Called from director to target to uninstall a bundle.
	 * 
	 * @param name name of bundle as given in install
	 */
	public void closeTarget() throws IOException {
		send(-1, null);
	}

	/**
	 * Called from director to update the framework, will close the link
	 * 
	 */
	public void updateFramework() throws IOException {
		send(UPDATE_FRAMEWORK, null);
	}

	/**
	 * Called from director to target to send an object to the testbundle.
	 * 
	 * @param name name of bundle as given in install
	 * @param msg payload
	 */
	public void sendToTarget(String name, Object msg) throws IOException {
		request(SEND_TO_TARGET, new Object[] {name, msg});
	}

	public void setTestProperties(Dictionary d) throws IOException {
		request(SET_TEST_PROPERTIES, new Object[] {d});
	}

	//
	// Target -> Run
	//
	/**
	 * Called from target to director to send an object to the owning testcase.
	 * 
	 * @param name name of bundle.
	 * @param msg payload
	 */
	public void sendToRun(String name, Object msg) throws IOException {
		request(SEND_TO_RUN, new Object[] {name, msg});
	}

	/**
	 * Called from target to director to log a message that will be verified.
	 * 
	 * @param name name of bundle
	 * @param log message to be logged and verified
	 */
	public void sendLog(String name, Log log) throws IOException {
		request(SEND_LOG, new Object[] {name, log});
	}

	/**
	 * Called from target to director to indicate that the bundle has given up
	 * the test link service indicating that it is done.
	 * 
	 * @param name name of bundle
	 */
	public void stopped(String name) throws IOException {
		send(STOPPED, name);
	}

	/**
	 * Called from the target to the director to indicate the properties of the
	 * target VM and Framework.
	 * 
	 * @param properties List of VM and Framework properties
	 */
	public void setTargetProperties(Dictionary properties) throws IOException {
		request(TARGET_PROPERTIES, properties);
	}

	//
	// Event loop
	//
	/**
	 * Event loop when a new message has arrived.
	 * 
	 * Called from Link superclass.
	 * 
	 * @param msg Message to dispatch
	 */
	public void deliver(Message msg) throws IOException {
		Object result = null;
		try {
			switch (msg.getType()) {
				//
				// Run -> Target
				//
				case INSTALL :
					Object[] parameters = (Object[]) msg.getContent();
					String name = (String) parameters[0];
					byte[] buffer = (byte[]) parameters[1];
					_target.install(name, new ByteArrayInputStream(buffer));
					break;
				case SEND_TO_TARGET :
					Object p[] = (Object[]) msg.getContent();
					_target.push((String) p[0], p[1]);
					break;
				case UNINSTALL :
					_target.uninstall((String) msg.getContent());
					break;
				case REBOOT :
					_target.reboot(((Integer) msg.getContent()).intValue());
					return;
				case UPDATE_FRAMEWORK :
					_target.updateFramework();
					return;
				case CLOSE_TARGET :
					close();
					return;
				case SET_TEST_PROPERTIES :
					p = (Object[]) msg.getContent();
					_target.setTestProperties((Dictionary) p[0]);
					break;
				//
				// Target -> run
				case SEND_TO_RUN :
					p = (Object[]) msg.getContent();
					_run.push((String) p[0], p[1]);
					break;
				case SEND_LOG :
					p = (Object[]) msg.getContent();
					_run.sendLog((String) p[0], (Log) p[1]);
					break;
				case TARGET_PROPERTIES :
					_run.setTargetProperties((Dictionary) msg.getContent());
					break;
				case STOPPED :
					// No reply expected
					_run.stopped((String) msg.getContent());
					return;
				default :
					debug("Invalid cmd for target link " + msg + " " + this,
							null);
			}
			msg.reply(OK, result);
		}
		catch (SocketException e) {
			try { close(); } catch( Exception ee ) {}
		}
		catch (Exception e) {
			e.printStackTrace();
			msg.reply(FAILED, e);
		}
	}

	/**
	 * Convenience method to turn an inputstream into a byte array. The method
	 * uses a recursive algorithm to minimize memory usage.
	 * 
	 * @param in stream with data
	 * @param offset where we are in the stream
	 * @returns byte array filled with data
	 */
	byte[] collect(InputStream in, int offset) throws IOException {
		byte[] result;
		byte[] buffer = new byte[10000];
		int size = in.read(buffer);
		if (size <= 0)
			return new byte[offset];
		else
			result = collect(in, offset + size);
		System.arraycopy(buffer, 0, result, offset, size);
		in.close();
		return result;
	}

	public void linkClosed() {
		try {
			if (_run != null)
				_run.linkClosed();
			else
				_target.linkClosed();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
