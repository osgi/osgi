/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.director;

import java.io.*;
import java.util.Vector;
import org.osgi.test.script.Tag;
import org.osgi.test.service.TestBundle;
import org.osgi.test.shared.Log;

/**
 * Represents the testbundle on the testcase side.
 * 
 * A testcase can download a bundle in the target framework. This class
 * represents this remote bundle in the local framework. It implements the
 * org.osgi.test.service.TestBundle interface for this purpose.
 */
class TestBundleImpl implements TestBundle {
	final static int			LOOKAHEAD	= 4;
	Run							run;											// Link
	// to
	// director
	String						name;											// Name
	// of
	// bundle
	org.osgi.test.shared.Queue	queue		= new org.osgi.test.shared.Queue(); // for
	// send
	boolean						installed	= true;							// When
	// installed
	boolean						finished;										// When
	// finished
	int							errors		= 0;								// # of
	// errors
	Vector						input		= new Vector();					// Look
	// ahead
	// for
	// reference
	// output
	BufferedReader				ref;
	InputStream					refIn;

	public TestBundleImpl(Run run, String name, InputStream ref)
			throws IOException {
		this.run = run;
		this.name = name;
		this.refIn = ref;
		if (ref != null)
			this.ref = new BufferedReader(new InputStreamReader(ref,
					"ISO-8859-1"));
		else
			this.ref = new BufferedReader(new StringReader(""));
	}

	/**
	 * Return the name of the bundle.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Send an object to the other side.
	 */
	public void send(Object o) throws IOException {
		run.target.sendToTarget(name, o);
	}

	/**
	 * Receive an object from the other side.
	 */
	public Object receive(long timeout) {
		return queue.pop(timeout);
	}

	/**
	 * Push an object on the receive queue.
	 */
	void push(Object o) {
		queue.push(o);
	}

	/**
	 * Return number of errors.
	 */
	public int getCompareErrors() {
		return errors;
	}

	/**
	 * Log a message from the target.
	 */
	void log(Log log) throws IOException {
		Tag logtag = new Tag("log");
		logtag.addAttribute("time", log.getDate());
		logtag.addAttribute("name", name);
		run.history.addContent(logtag);
		log(logtag, log.getString());
	}

	/**
	 * Uninstall the bundle on the target.
	 */
	public void uninstall() throws IOException {
		boolean doit;
		synchronized (this) {
			doit = installed;
			installed = false;
		}
		if (doit) {
			run.target.uninstall(name);
		}
	}

	/**
	 * Stop and clear the receive queue (making receive return).
	 */
	public void abort() {
		queue.abort();
	}

	/**
	 * Indicate that the bundle is stopped on the target.
	 */
	public void stopped() {
		stopped_sync();
	}

	public synchronized void stopped_sync() {
		installed = false;
		notify();
	}

	/**
	 * Wait until the bundle is stopped on the target.
	 */
	public synchronized void sync(boolean log) {
		try {
			int n = 10;
			while (installed) {
				wait(60000);
				System.out.println("Waiting for " + name + " " + installed);
				if (n-- == 0)
					break;
			}
			//if ( log ) ??? should always do this?? - pkr mar 2003
			errors += linesLeft();
			if (refIn != null)
				refIn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the remaining reference log and reports the number of lines
	 * left.
	 */
	public int linesLeft() throws IOException {
		String s = ref.readLine();
		int lines = 0;
		while (s != null) {
			s = Log.cleanup(s);
			if (!skip(s)) {
				run.reportMessage("++ " + s);
				lines++;
			}
			s = ref.readLine();
		}
		return lines;
	}

	/**
	 * Processes a log message.
	 * 
	 * Log messages are compared to the reference output. The comparing will do
	 * a lookahead so it can skip lines that do not match.
	 */
	void log(Tag logtag, String log) throws IOException {
		logtag.addContent(new Tag("raw-result", log));
		log = Log.cleanup(log);
		if (skip(log)) {
			logtag.addAttribute("match", "true");
			return;
		}
		for (int i = 0; i < LOOKAHEAD; i++) {
			String ref = getLine(i);
			if (ref == null) {
				logtag.addAttribute("eof", "ref");
				break;
			}
			if (ref.equals(log)) {
				for (int j = 0; j < i; j++) {
					String skip = (String) input.elementAt(0);
					logtag.addContent(new Tag("skipped", skip));
					run.reportMessage("+ " + skip);
					errors++;
					input.removeElementAt(0);
				}
				logtag.addContent(new Tag("reference", ref));
				logtag.addAttribute("match", "true");
				input.removeElementAt(0);
				return;
			}
		}
		logtag.addAttribute("match", "false");
		run.reportMessage("- " + log);
		errors++;
	}

	/**
	 * Get the next line of the reference input, taking prior lookahead into
	 * calculation.
	 */
	String getLine(int i) throws IOException {
		String line = null;
		if (i >= input.size()) {
			line = ref.readLine();
			while (line != null) {
				line = Log.cleanup(line);
				if (!skip(line))
					break;
				line = ref.readLine();
			}
			if (line != null)
				input.addElement(line);
			//else
			//	System.out.println( "TBI: Read null from ref" );
		}
		else
			line = (String) input.elementAt(i);
		return line;
	}

	/**
	 * Cleanup the line and see if it needs to be skipped.
	 */
	boolean skip(String log) {
		boolean result = log == null || log.length() == 0;
		return result;
	}
}
