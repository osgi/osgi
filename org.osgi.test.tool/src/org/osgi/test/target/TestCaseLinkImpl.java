/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.target;

import java.io.IOException;
import java.util.Date;
import org.osgi.framework.Bundle;
import org.osgi.test.service.TestCaseLink;
import org.osgi.test.shared.*;

/**
 * This class implements the link between the director and the test bundle on
 * the target, on the target side.
 */
public class TestCaseLinkImpl implements TestCaseLink {
	String						name;
	Bundle						bundle;
	org.osgi.test.shared.Queue	queue	= new org.osgi.test.shared.Queue();
	TargetLink					director;

	/**
	 * Constructor for the test case link.
	 */
	TestCaseLinkImpl(TargetLink director, Bundle bundle, String name) {
		this.bundle = bundle;
		this.name = name;
		this.director = director;
	}

	/**
	 * Interface from test bundle to send an object to its testcase on the
	 * directors side.
	 */
	public void send(Object msg) throws IOException {
		director.sendToRun(name, msg);
	}

	/**
	 * This method is called to log information in the testbundle. It will be
	 * compared with a reference output on the director.
	 */
	public void log(String log) throws IOException {
		System.out.println("Log: " + log);
		director.sendLog(name, new Log(new Date(), log));
	}

	/**
	 * method to call when an object send by the testcase on the directors
	 * framework needs to be received.
	 */
	public Object receive(long timeout) {
		return queue.pop(timeout);
	}

	/**
	 * Called when the testcase on the director has send a message to the
	 * testbundle on this target. The testbundle can wait for this message at
	 * receive. We will send this object to that guy.
	 */
	void push(Object o) {
		if (queue != null)
			queue.push(o);
	}

	public String getName() {
		return name;
	}

	public Bundle getBundle() {
		return bundle;
	}

	void close() {
		queue.releaseAll();
		if ( bundle.getLocation().indexOf("~keep~")<0)
		try {
			System.out.println("Closing " + bundle.getBundleId() + " "
					+ bundle.getLocation());
			bundle.uninstall();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
