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
import org.osgi.framework.*;
import org.osgi.test.script.Tag;
import org.osgi.test.service.*;
import org.osgi.test.shared.*;

/**
 * Control the tests. Originally, the director had an applet and this class was
 * instantiated for each applet session. In 2001, the applet was removed and the
 * handler is now instantiated only once in the director.
 * <p>
 * The handler can start a test run (startRun). After this the getRun() will
 * return a valid Run object. The Run object can be used to do zero or more
 * testcases. At the end, the handle can stop the run (stopRun) and will gather
 * the test results. During a run, test results are stored in an XML oriented
 * object (Tag). Each aspect of the test run adds information to this history
 * object. The final result is registered on the web.
 */
public class Handler implements BundleListener {
	BundleContext			context;					// Framework interface
	TestCaseTracker			caseTracker;				// Holds wrappers
	// around the
	// testcases
	RemoteServiceTracker	targetTracker;				// Wrappers around the
	// targets
	HttpTracker				httpTracker;				// Registers a
	// servlet for
	// results + target
	Run						run;						// An active test run
	Hashtable				bundles	= new Hashtable();	// id -> id
	IApplet					applet	= new Dummy();
	Hashtable				lastHistory;				// TestCase -> Tag
	File					directory;					// Where test results
	// are stored
	String					lastRun;					// Name of last made
	// test result file.
	String					program;					// Test program
	String					campaign;					// Test campaign
	String					applicant;					// Applicant Id
	final static String		ALIAS	= "/test/director";

	/**
	 * Constructor for default handler.
	 * 
	 * @param socket socket to applet
	 * @param context Framework context
	 */
	Handler(BundleContext context) throws Exception {
		this.context = context;
		lastHistory = new Hashtable();
		context.addBundleListener(this);
		httpTracker = new HttpTracker(this, context);
		httpTracker.open();
		caseTracker = new TestCaseTracker(this, context);
		caseTracker.open();
		targetTracker = new RemoteServiceTracker(this, context);
		targetTracker.open();
		directory = context.getDataFile("history");
		directory.mkdir();
		applicant = getProperty("org.osgi.test.compliance.applicant",
				"[property org.osgi.test.compliance.applicant notset]");
		program = getProperty("org.osgi.test.compliance.program",
				"[property org.osgi.test.compliance.program notset]");
		campaign = getProperty("org.osgi.test.compliance.campaign",
				"[property org.osgi.test.compliance.campaign notset]");
	}

	/**
	 * Set the UI.
	 * 
	 * This UI is updated with changes when they happen.
	 */
	public void setApplet(IApplet applet) throws Exception {
		this.applet = applet;
		applet.bundlesChanged();
		applet.casesChanged();
		applet.targetsChanged();
	}

	/**
	 * Convenience method to return the current test run.
	 */
	public Run getRun() {
		return run;
	}

	/**
	 * Close this link and die.
	 */
	public void close() {
		context.removeBundleListener(this);
		stopRun();
		caseTracker.close();
		targetTracker.close();
	}

	/**
	 * Return the list of targets.
	 */
	public Vector getTargets() {
		return targetTracker.getTargets();
	}

	/**
	 * Return the current list of test cases.
	 */
	public Hashtable getTestCases() {
		return caseTracker.testcases;
	}

	/**
	 * Get a test case with the given name.
	 */
	public TestCase getTestCase(String name) {
		return caseTracker.getTestCase(name);
	}

	/**
	 * Start a new test run on the given host/port.
	 */
	public void startRun(String host, int port, int options) throws Exception {
		run = new Run(this, applet, host, port, options);
		Tag compliance = new Tag("compliance");
		compliance.addAttribute("applicant", applicant);
		compliance.addAttribute("campaign", campaign);
		compliance.addAttribute("program", program);
		run.top.addContent(compliance);
	}

	/**
	 * Stop the test run and create an XML file in the history directory with
	 * the time/date as name. These files are made available on the web in the
	 * HttpTracker.
	 */
	public String stopRun() {
		if (run != null) {
			try {
				java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat(
						"MMddHHmmss");
				lastRun = sf.format(new Date()) + ".xml";
				File file = new File(directory, lastRun);
				PrintWriter pw = new PrintWriter(new FileWriter(file));
				pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
				pw
						.println("<?xml-stylesheet type='text/xsl' title='Compact' href='http://www2.osgi.org/www/testresult-compact-1.xsl'?>");
				pw
						.println("<?xml-stylesheet type='text/xsl' title='Full' href='http://www2.osgi.org/www/testresult-full-1.xsl'?>");
				run.history.print(0, pw);
				pw.close();
			}
			catch (IOException e) {
			}
			run.close();
			run = null;
			applet.finished();
		}
		return lastRun;
	}

	/**
	 * Create a new RemoteService in the registry manually.
	 * 
	 * Sometimes hosts are not able to broadcast and can thus not reach the
	 * director. In those cases it is possible to add a host manually from the
	 * UI.
	 */
	public void connectManual(String host) {
		try {
			int port = 3191;
			StringTokenizer st = new StringTokenizer(host, ":");
			String name = st.nextToken();
			if (st.hasMoreTokens())
				port = Integer.parseInt(st.nextToken());
			if (context.getServiceReferences(RemoteService.class.getName(),
					"(&(application=org.osgi.test)(host=" + name + ")(port="
							+ port + "))") == null) {
				RemoteServiceImpl rs = new RemoteServiceImpl("org.osgi.test",
						name, port, "manual");
				rs.registerAt(context);
			}
			else
				System.out.println("Already exists " + host);
		}
		catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convenience method to return the file name part of a url without creating
	 * a URL.
	 */
	String name(String url) {
		int n = url.lastIndexOf('/');
		if (n >= 0)
			return url.substring(n + 1);
		else
			return url;
	}

	/**
	 * Callback from framework. Just update the UI.
	 */
	public void bundleChanged(BundleEvent event) {
		applet.bundlesChanged();
	}

	/**
	 * Copies the input url to an output filename or URL.
	 * 
	 * If the out parameters has a ':' in the 3 .. 10 th column, a URL is
	 * assumed, else a file name. If it is a URL, the output facility of the URL
	 * is used. This is not always possible but works for example for mailto:,
	 * http: and ftp:
	 */
	public void send(URL inurl, String out) throws IOException {
		OutputStream put = null;
		URLConnection connection = null;
		if (out.indexOf(":") < 2 || out.indexOf(":") > 10)
			put = new FileOutputStream(out);
		else {
			URL url = new URL(out);
			connection = url.openConnection();
			connection.setDoOutput(true);
			put = connection.getOutputStream();
		}
		InputStream in = inurl.openStream();
		byte buffer[] = new byte[1024];
		int size = in.read(buffer);
		while (size > 0) {
			put.write(buffer, 0, size);
			size = in.read(buffer);
		}
		in.close();
		put.close();
		if (connection != null && connection.getDoInput()) {
			try {
				in = connection.getInputStream();
				byte[] buf = new byte[1024];
				while (in.read(buf) > 0);
				in.close();
			}
			catch (UnknownServiceException e) {
				// Who cares??
			}
		}
	}

	/**
	 * Set progress percentage.
	 * 
	 * @param percentage percentage of progress.
	 */
	public void setProgess(int percent) throws IOException {
		applet.setProgress(percent);
	}

	/**
	 * Set message
	 * 
	 * @param message Message to send to applet
	 */
	public void setMessage(String message) throws IOException {
		applet.setMessage(message);
	}

	/**
	 * Set error
	 */
	public void setError(String message) throws IOException {
		applet.setError(message);
	}

	/**
	 * Called when the run is finished. We will update the applet.
	 */
	public void finished(IRun run) {
		this.run = null;
		applet.finished();
	}

	/**
	 * Create a new script editor.
	 */
	public ScriptEditor newScriptEditor(String name, URL path) {
		ScriptEditor editor = new ScriptEditor(name, path);
		editor.registration = context.registerService(TestCase.class.getName(),
				editor, null);
		return editor;
	}

	/**
	 * Get the description of a testcase. This includes lots of information of
	 * the service.
	 */
	public Tag getDescription(TestCase tc) {
		return caseTracker.getDescriptionTag(tc);
	}

	/**
	 * List all the files in the history directory.
	 */
	File[] getResults() {
		String[] names = directory.list();
		if (names == null)
			return null;
		File result[] = new File[names.length];
		for (int i = 0; i < names.length; i++) {
			result[i] = new File(directory, names[i]);
		}
		return result;
	}

	/**
	 * Get a particular test result.
	 */
	File getResult(String name) {
		return new File(directory, name);
	}

	/**
	 * Return the base url to the/a web server where the results can be found.
	 */
	public String getBaseURL() {
		String port = getProperty("org.osgi.service.http.port", null);
		if (port == null)
			port = "";
		else
			port = ":" + port;
		return "http://localhost" + port + ALIAS;
	}

	public String getProperty(String name, String def) {
		String result = System.getProperty(name);
		if (result == null) {
			result = context.getProperty(name);
		}
		return (result == null) ? def : result;
	}
}
