/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.director;

import java.io.*;
import java.net.*;
import java.util.*;

import org.osgi.test.script.Tag;
import org.osgi.test.service.*;
import org.osgi.test.shared.*;

/**
 * Represents a test run.
 * 
 * This class will be created by the handler on request of the ui or script.
 * <p>
 * When we start the testcase we handout the TestRun interface which is us.
 * Through this interface, the testcase can create bundles in the target and
 * send/receive objects with the target. When the testcase returns, we will
 * clean up their mess.
 * <p>
 * During the run, we maintain a history in the Tag object.
 */
public class Run implements IRun, TestRun {
	Handler		handler;						// Our boss
	TargetLink	target;						// Link to the target, only
	// open during testcase exec.
	int			progress;						// Where we currently are
	Hashtable	testbundles	= new Hashtable();	// name -> test link
	int			options;						// Bitmap of options
	String		host;							// Host name
	int			port;							// Port on which it runs
	IApplet		applet;						// UI
	Tag			history;						// Current history
	Tag			top;							// Top of current histiry
	long		lasttime;						// Separates testcases at least
	// 2 secs
	boolean		hadProperties;					// Waits until properties

	// are send after closing
	// link.
	/**
	 * Create run for a handler, applet, host, port and set of testcases.
	 * 
	 * @param handler associated handler
	 * @param applet link to applet
	 * @param host host of target
	 * @param port port of org.osgi.test application
	 * @param cases list of test cases
	 * @param options bitmap of options given by applet, defined in IApplet
	 */
	Run(Handler handler, IApplet applet, String host, int port, int options)
			throws Exception {
		this.host = host;
		this.port = port;
		this.handler = handler;
		this.options = options;
		this.applet = applet;
		if (applet == null)
			this.applet = new Dummy();
		top = history = new Tag("run");
		history.addAttribute("host", host);
		history.addAttribute("port", "" + port);
		history.addAttribute("time", new Date());
	}

	/**
	 * Execute a testcase. A testcase can be a normal testcase or a script. The
	 * target is opened when it is a normal testcase and a testcase is initiated
	 * on the target.
	 */
	public Tag doTestCase(TestCase tc) throws Exception {
		Tag testcase = new Tag("testcase");
		this.history.addContent(testcase);
		testcase.addAttribute("starting", new Date());
		testcase.addAttribute("name", tc.getName());
		testcase.addContent(handler.getDescription(tc));
		
		if (tc instanceof ScriptEditor) {
			ScriptEditor editor = (ScriptEditor) tc;
			return editor.execute();
		}
		int errors = 0;
		System.out
				.println("\r\n----------------------------------------------\r\n"
						+ tc.getName());
		//
		// Some test cases dont run when there is not at least a
		// second in between.
		//
		long passed = System.currentTimeMillis() - lasttime;
		if (passed < 2000)
			Thread.sleep(2000 - passed);

		int i = 0;
		while(true) {
			try {
				target = new TargetLink(this);
				target.open(new Socket(host, port));
				if (isOption(IHandler.OPTION_FOREVER)) {
					target.setTimeout(5 * 60000);
					history.addContent(new Tag("forever"));
				}
				updateTestProperties(target);
				// Stack the history se we can call this method recursively
				break;
			}
			catch (ConnectException e) {
				if ( i++ == 9 ) {
					// We have a bad error, better bail out.
					testcase.addAttribute("exception", e.toString() );
					return testcase;
				}
				System.out.println("Could not connect to: " + host + ":" + port + " (try " + i + ")");
				Thread.sleep(2000);
			}
		}
		

		Tag previous = this.history;
		this.history = testcase;
		try {
			errors += tc.test(this);
			if (errors == -1)
				testcase.addAttribute("absent", "true");
		}
		catch (Throwable ee) {
			history.addContent(new Tag("exception", "" + ee));
		}
		if (target.isOpen()) {
			for (Enumeration e = testbundles.elements(); e.hasMoreElements();) {
				TestBundleImpl testbundle = (TestBundleImpl) e.nextElement();
				try {
					testbundle.uninstall();
				}
				catch (Exception ee) {/* disaster recovery */
				}
			}
			for (Enumeration e = testbundles.elements(); e.hasMoreElements();) {
				TestBundleImpl testbundle = (TestBundleImpl) e.nextElement();
				testbundle.sync(errors != -1); // only log when not absent
				if (errors >= 0)
					errors += testbundle.getCompareErrors();
			}
			target.closeTarget();
			target.close();
			target = null;
			lasttime = System.currentTimeMillis();
		}
		testbundles = new Hashtable();
		testcase.addAttribute("ended", new Date());
		if (errors > 0)
			testcase.addAttribute("errors", "" + errors);
		this.history = previous;
		if (errors == -1)
			applet.setResult(tc, 0);
		else
			applet.setResult(tc, errors);
		return testcase;
	}

	private void updateTestProperties(TargetLink target) throws IOException,
			FileNotFoundException {
		String targetProperties = System.getProperty(IRun.TEST_PROPERTIES_FILE);
		if (targetProperties != null) {
			File tp = new File(targetProperties);
			if (tp.exists()) {
				Properties properties = new Properties();
				properties.load(new FileInputStream(tp));
				target.setTestProperties(properties);
			}
		}
	}

	/**
	 * Close the current run.
	 * 
	 * All testcases are aborted and the run finishes as quickly as possible.
	 * Obviously we need support from the testcase to make this work.
	 */
	public void close() {
		try {
			if (target != null && target.isOpen()) {
				target.closeTarget();
				target.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Report progress to the applet. We assume that the testcase will report
	 * progress relative to itself so we recalculate it to handle n testcases
	 * and then report it to the applet.
	 * 
	 * @param progress progress percentage per testcase
	 */
	public void reportProgress(int progress) throws IOException {
		applet.setProgress(progress);
	}

	/**
	 * Report a message to the applet.
	 */
	public void reportMessage(String msg) throws IOException {
		applet.setMessage(msg);
		history.addContent(new Tag("message", msg));
	}

	/**
	 * Create a test bundle in the target and compare the reference output.
	 * 
	 * @param name Name of the testbundle.
	 * @param jar stream with bundle jar
	 * @param refOut stream with reference output
	 * @returns A new testbundle
	 */
	static InputStream	j;
	static InputStream	r;

	public TestBundle createTestBundle(String name, InputStream jar,
			InputStream reference) throws IOException {
		Tag install = new Tag("install");
		install.addAttribute("name", name);
		install.addAttribute("starting", new Date());
		history.addContent(install);
		TestBundleImpl testbundle = new TestBundleImpl(this, name, reference);
		try {
			testbundles.put(name, testbundle);
			target.install(name, jar);
			install.addAttribute("installed", new Date());
			return testbundle;
		}
		catch (IOException e) {
			testbundles.remove(name);
			throw e;
		}
	}

	/**
	 * Notification from target that the testbundle stopped.
	 * 
	 * @param name name of the testbundle
	 */
	public void stopped(String name) {
		TestBundleImpl testbundle = getTestBundle(name);
		Tag stopped = new Tag("stopped");
		stopped.addAttribute("name", name);
		stopped.addAttribute("time", new Date());
		if (testbundle != null) {
			testbundle.stopped();
		}
		else
			stopped.addAttribute("existed", "false");
		history.addContent(stopped);
	}

	/**
	 * Send an object to the testbundle.
	 */
	public void push(String name, Object msg) {
		TestBundleImpl testbundle = getTestBundle(name);
		testbundle.push(msg);
	}

	/**
	 * Called from the targetlink to set the properties as defined in the target
	 * for the VM and the framework.
	 */
	public synchronized void setTargetProperties(Dictionary properties)
			throws IOException {
		if (!hadProperties) {
			hadProperties = true;
			applet.setTargetProperties(properties);
			Tag p = new Tag("target-properties");
			p.addAttribute("time", new Date());
			for (Enumeration e = properties.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = properties.get(key).toString();
				Tag pp = new Tag("property", new String[] {"key", key});
				pp.addContent(value);
				p.addContent(pp);
			}
			top.addContent(p);
		}
		notifyAll(); // Might wait to just reboot
	}

	/**
	 * Send a log message to the testbundle.
	 * 
	 * @param name Name of the testbundle.
	 * @param log log object
	 */
	public void sendLog(String name, Log log) throws IOException {
		TestBundleImpl testbundle = getTestBundle(name);
		testbundle.log(log);
		if (isOption(IHandler.OPTION_LOGGING))
			applet.setMessage("[" + name + "] " + log);
		else
			applet.alive(log.getString());
		applet.step(log.getString());
	}

	/**
	 * Check the option in the mask.
	 * 
	 * @param mask mask as defined in IApplet
	 */
	boolean isOption(int mask) {
		return (mask & options) != 0;
	}

	/**
	 * Convenience method to get the testbundle.
	 */
	TestBundleImpl getTestBundle(String name) {
		return (TestBundleImpl) testbundles.get(name);
	}

	/**
	 * Update the framework in the target.
	 */
	public synchronized void updateTarget() throws IOException {
		try {
			target = new TargetLink(this);
			target.open(new Socket(host, port));
			wait(15000);
			target.updateFramework();
			target.close();
		}
		catch (Exception e) {
			reportMessage("Cannot update the target " + e);
			e.printStackTrace();
		}
	}

	/**
	 * reboot the framework in the target.
	 * 
	 * To avoid confusion, we wait until the target has send us its properties.
	 * This is synchronized.
	 */
	public synchronized void rebootTarget(int cause) throws IOException {
		try {
			target = new TargetLink(this);
			target.open(new Socket(host, port));
			wait(15000);
			target.reboot(cause);
			target.close();
		}
		catch (Exception e) {
			reportMessage("Cannot update the target " + e);
			e.printStackTrace();
		}
	}

	public void linkClosed() {
	}
}
